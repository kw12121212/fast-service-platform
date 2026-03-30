package com.fastservice.platform.backend.project;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fastservice.platform.backend.common.db.JdbcSupport;

final class ProjectSandboxManager {

    private static final long PODMAN_TIMEOUT_SECONDS = 180;
    private static final long CONTAINER_STARTUP_TIMEOUT_MILLIS = 30_000;
    private static final long CONTAINER_STARTUP_POLL_MILLIS = 250;
    private static final String DEFAULT_IMAGE_SCRIPT_PATH = "init-image.sh";
    private static final String DEFAULT_PROJECT_SCRIPT_PATH = "init-project.sh";
    private static final String DEFAULT_BASE_IMAGE = "docker.io/library/debian:12-slim";

    Map<String, ProjectWorktreeSandboxRecord> readSandboxRecords(long projectId) {
        String sql = """
                SELECT worktree_path,
                       init_image_script_path_override,
                       init_project_script_path_override,
                       image_status,
                       image_failure_message,
                       container_status,
                       container_failure_message
                FROM project_worktree_sandbox
                WHERE project_id = ?
                """;
        Map<String, ProjectWorktreeSandboxRecord> records = new HashMap<>();
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, projectId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    records.put(
                            normalizeAbsolutePath(rs.getString("worktree_path"), "Worktree path").toString(),
                            new ProjectWorktreeSandboxRecord(
                                    rs.getString("init_image_script_path_override"),
                                    rs.getString("init_project_script_path_override"),
                                    rs.getString("image_status"),
                                    rs.getString("image_failure_message"),
                                    rs.getString("container_status"),
                                    rs.getString("container_failure_message")));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to read project sandbox state", e);
        }
        return records;
    }

    ProjectWorktreeSandboxSummary summarize(
            long projectId,
            Path repositoryRootPath,
            ProjectWorktreeSummary worktree,
            ProjectWorktreeSandboxRecord record) {
        if (!isPodmanAvailable()) {
            return new ProjectWorktreeSandboxSummary(
                    false,
                    "Podman is not available on this host",
                    "MISSING",
                    deriveImageReference(projectId, worktree.path()),
                    null,
                    effectiveImageScriptPath(record),
                    scriptPathSource(record == null ? null : record.initImageScriptPathOverride()),
                    false,
                    "Podman is not available on this host",
                    "INACTIVE",
                    deriveContainerName(projectId, worktree.path()),
                    null,
                    effectiveProjectScriptPath(record),
                    scriptPathSource(record == null ? null : record.initProjectScriptPathOverride()),
                    false,
                    "Podman is not available on this host",
                    false,
                    "Podman is not available on this host");
        }

        String baseRestriction = sandboxRestriction(worktree);
        String imageReference = deriveImageReference(projectId, worktree.path());
        String containerName = deriveContainerName(projectId, worktree.path());
        String imageStatus = inspectImageStatus(imageReference, record);
        String containerStatus = inspectContainerStatus(containerName, record);
        String imageFailureMessage = "FAILED".equals(imageStatus) && record != null ? record.imageFailureMessage() : null;
        String containerFailureMessage = "FAILED".equals(containerStatus) && record != null ? record.containerFailureMessage() : null;
        boolean imageActionAllowed = baseRestriction == null && !"ACTIVE".equals(containerStatus);
        String imageActionRestriction = null;
        if (!imageActionAllowed) {
            imageActionRestriction = baseRestriction != null
                    ? baseRestriction
                    : "Destroy the active sandbox container before rebuilding the image";
        }

        boolean containerCreateAllowed = baseRestriction == null
                && "READY".equals(imageStatus)
                && !"ACTIVE".equals(containerStatus);
        String containerCreateRestriction = null;
        if (!containerCreateAllowed) {
            if (baseRestriction != null) {
                containerCreateRestriction = baseRestriction;
            } else if (!"READY".equals(imageStatus)) {
                containerCreateRestriction = "Create or rebuild the sandbox image before starting a container";
            } else {
                containerCreateRestriction = "A sandbox container is already active for this worktree";
            }
        }

        boolean containerDeleteAllowed = baseRestriction == null && "ACTIVE".equals(containerStatus);
        String containerDeleteRestriction = containerDeleteAllowed ? null
                : baseRestriction != null ? baseRestriction : "No active sandbox container exists for this worktree";

        return new ProjectWorktreeSandboxSummary(
                baseRestriction == null,
                baseRestriction,
                imageStatus,
                imageReference,
                imageFailureMessage,
                effectiveImageScriptPath(record),
                scriptPathSource(record == null ? null : record.initImageScriptPathOverride()),
                imageActionAllowed,
                imageActionRestriction,
                containerStatus,
                containerName,
                containerFailureMessage,
                effectiveProjectScriptPath(record),
                scriptPathSource(record == null ? null : record.initProjectScriptPathOverride()),
                containerCreateAllowed,
                containerCreateRestriction,
                containerDeleteAllowed,
                containerDeleteRestriction);
    }

    String createImage(long projectId, String repositoryPath, String worktreePath) {
        ensurePodmanAvailable();
        Path repositoryRootPath = normalizeAbsolutePath(repositoryPath, "Repository path");
        Path normalizedWorktreePath = normalizeAbsolutePath(worktreePath, "Worktree path");
        requireSupportedWorktree(repositoryRootPath, normalizedWorktreePath);
        ProjectWorktreeSandboxRecord record = readSandboxRecord(projectId, normalizedWorktreePath.toString());
        String imageReference = deriveImageReference(projectId, normalizedWorktreePath.toString());
        String containerName = deriveContainerName(projectId, normalizedWorktreePath.toString());
        String scriptPath = effectiveImageScriptPath(record);
        Path relativeScriptPath = resolveRelativeScriptPath(scriptPath, "Image initialization script");

        if (podmanResourceExists("container", "exists", containerName)) {
            throw new IllegalStateException("Destroy the active sandbox container before rebuilding the image");
        }
        removeImageIfPresent(imageReference);
        upsertSandboxRow(projectId, normalizedWorktreePath.toString(), record, "MISSING", null, "INACTIVE", null);

        Path buildContext = copyWorktreeToBuildContext(repositoryRootPath, normalizedWorktreePath);
        try {
            Path scriptInContext = buildContext.resolve(relativeScriptPath).normalize();
            if (!Files.exists(scriptInContext)) {
                String message = "Sandbox image initialization script does not exist: " + scriptPath;
                upsertSandboxRow(projectId, normalizedWorktreePath.toString(), record, "FAILED", message, "INACTIVE", null);
                throw new IllegalStateException(message);
            }

            writeContainerfile(buildContext, relativeScriptPath);
            runPodmanOrThrow(
                    "Unable to build sandbox image",
                    "build",
                    "--pull=missing",
                    "-t",
                    imageReference,
                    buildContext.toString());
            upsertSandboxRow(projectId, normalizedWorktreePath.toString(), record, "READY", null, "INACTIVE", null);
            return imageReference;
        } catch (IllegalStateException error) {
            if (!"FAILED".equals(readSandboxRecord(projectId, normalizedWorktreePath.toString()).imageStatus())) {
                upsertSandboxRow(
                        projectId,
                        normalizedWorktreePath.toString(),
                        record,
                        "FAILED",
                        error.getMessage(),
                        "INACTIVE",
                        null);
            }
            throw error;
        } finally {
            deleteRecursively(buildContext);
        }
    }

    String createContainer(long projectId, String repositoryPath, String worktreePath) {
        ensurePodmanAvailable();
        Path repositoryRootPath = normalizeAbsolutePath(repositoryPath, "Repository path");
        Path normalizedWorktreePath = normalizeAbsolutePath(worktreePath, "Worktree path");
        requireSupportedWorktree(repositoryRootPath, normalizedWorktreePath);
        ProjectWorktreeSandboxRecord record = readSandboxRecord(projectId, normalizedWorktreePath.toString());
        String imageReference = deriveImageReference(projectId, normalizedWorktreePath.toString());
        String containerName = deriveContainerName(projectId, normalizedWorktreePath.toString());
        String scriptPath = effectiveProjectScriptPath(record);
        Path relativeScriptPath = resolveRelativeScriptPath(scriptPath, "Project initialization script");

        if (!podmanResourceExists("image", "exists", imageReference)) {
            throw new IllegalStateException("Sandbox image does not exist for this worktree");
        }
        if (podmanResourceExists("container", "exists", containerName)) {
            throw new IllegalStateException("A sandbox container is already active for this worktree");
        }

        String containerCommand = "/bin/sh " + shellQuote("./" + relativeScriptPath.toString().replace('\\', '/'))
                + " && tail -f /dev/null";
        try {
            runPodmanOrThrow(
                    "Unable to create sandbox container",
                    "run",
                    "-d",
                    "--name",
                    containerName,
                    imageReference,
                    "/bin/sh",
                    "-c",
                    containerCommand);
            awaitContainerStartup(containerName);
            upsertSandboxContainerState(projectId, normalizedWorktreePath.toString(), record, "ACTIVE", null);
            return containerName;
        } catch (IllegalStateException error) {
            String failureMessage = error.getMessage();
            removeContainerIfPresent(containerName);
            upsertSandboxContainerState(projectId, normalizedWorktreePath.toString(), record, "FAILED", failureMessage);
            throw error;
        }
    }

    String deleteContainer(long projectId, String repositoryPath, String worktreePath) {
        ensurePodmanAvailable();
        Path repositoryRootPath = normalizeAbsolutePath(repositoryPath, "Repository path");
        Path normalizedWorktreePath = normalizeAbsolutePath(worktreePath, "Worktree path");
        requireSupportedWorktree(repositoryRootPath, normalizedWorktreePath);
        ProjectWorktreeSandboxRecord record = readSandboxRecord(projectId, normalizedWorktreePath.toString());
        String containerName = deriveContainerName(projectId, normalizedWorktreePath.toString());
        if (!podmanResourceExists("container", "exists", containerName)) {
            throw new IllegalStateException("No active sandbox container exists for this worktree");
        }

        runPodmanOrThrow("Unable to destroy sandbox container", "rm", "-f", containerName);
        upsertSandboxContainerState(projectId, normalizedWorktreePath.toString(), record, "INACTIVE", null);
        return containerName;
    }

    void cleanupWorktreeResources(long projectId, String worktreePath) {
        Path normalizedWorktreePath = normalizeAbsolutePath(worktreePath, "Worktree path");
        removeContainerIfPresent(deriveContainerName(projectId, normalizedWorktreePath.toString()));
        removeImageIfPresent(deriveImageReference(projectId, normalizedWorktreePath.toString()));
        deleteSandboxRow(projectId, normalizedWorktreePath.toString());
    }

    void pruneMissingWorktreeResources(long projectId, List<ProjectWorktreeSummary> visibleWorktrees) {
        Map<String, ProjectWorktreeSandboxRecord> records = readSandboxRecords(projectId);
        for (String worktreePath : records.keySet()) {
            boolean stillVisible = visibleWorktrees.stream()
                    .anyMatch(worktree -> normalizeAbsolutePath(worktree.path(), "Worktree path").toString().equals(worktreePath));
            if (!stillVisible) {
                cleanupWorktreeResources(projectId, worktreePath);
            }
        }
    }

    private ProjectWorktreeSandboxRecord readSandboxRecord(long projectId, String worktreePath) {
        return readSandboxRecords(projectId).get(worktreePath);
    }

    private void upsertSandboxContainerState(
            long projectId,
            String worktreePath,
            ProjectWorktreeSandboxRecord record,
            String containerStatus,
            String containerFailureMessage) {
        String imageStatus = record == null ? null : record.imageStatus();
        String imageFailureMessage = record == null ? null : record.imageFailureMessage();
        upsertSandboxRow(projectId, worktreePath, record, imageStatus, imageFailureMessage, containerStatus, containerFailureMessage);
    }

    private void upsertSandboxRow(
            long projectId,
            String worktreePath,
            ProjectWorktreeSandboxRecord existingRecord,
            String imageStatus,
            String imageFailureMessage,
            String containerStatus,
            String containerFailureMessage) {
        String updateSql = """
                UPDATE project_worktree_sandbox
                SET init_image_script_path_override = ?,
                    init_project_script_path_override = ?,
                    image_status = ?,
                    image_failure_message = ?,
                    container_status = ?,
                    container_failure_message = ?
                WHERE project_id = ? AND worktree_path = ?
                """;
        String insertSql = """
                INSERT INTO project_worktree_sandbox(
                    project_id,
                    worktree_path,
                    init_image_script_path_override,
                    init_project_script_path_override,
                    image_status,
                    image_failure_message,
                    container_status,
                    container_failure_message
                ) VALUES(?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = JdbcSupport.getConnection()) {
            int updatedRows;
            try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
                statement.setString(1, existingRecord == null ? null : existingRecord.initImageScriptPathOverride());
                statement.setString(2, existingRecord == null ? null : existingRecord.initProjectScriptPathOverride());
                statement.setString(3, imageStatus);
                statement.setString(4, imageFailureMessage);
                statement.setString(5, containerStatus);
                statement.setString(6, containerFailureMessage);
                statement.setLong(7, projectId);
                statement.setString(8, worktreePath);
                updatedRows = statement.executeUpdate();
            }
            if (updatedRows == 0) {
                try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
                    statement.setLong(1, projectId);
                    statement.setString(2, worktreePath);
                    statement.setString(3, existingRecord == null ? null : existingRecord.initImageScriptPathOverride());
                    statement.setString(4, existingRecord == null ? null : existingRecord.initProjectScriptPathOverride());
                    statement.setString(5, imageStatus);
                    statement.setString(6, imageFailureMessage);
                    statement.setString(7, containerStatus);
                    statement.setString(8, containerFailureMessage);
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to persist sandbox state", e);
        }
    }

    private void deleteSandboxRow(long projectId, String worktreePath) {
        String sql = "DELETE FROM project_worktree_sandbox WHERE project_id = ? AND worktree_path = ?";
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, projectId);
            statement.setString(2, worktreePath);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to delete sandbox state", e);
        }
    }

    private String sandboxRestriction(ProjectWorktreeSummary worktree) {
        if (worktree.main()) {
            return "Main repository worktree cannot be used as a sandbox source";
        }
        if (worktree.stale()) {
            return "Stale worktree records cannot be used as a sandbox source";
        }
        if (worktree.headState() != ProjectGitHeadState.BRANCH || worktree.branch() == null) {
            return "Detached HEAD worktree cannot be used as a sandbox source";
        }
        return null;
    }

    private String effectiveImageScriptPath(ProjectWorktreeSandboxRecord record) {
        if (record != null && record.initImageScriptPathOverride() != null && !record.initImageScriptPathOverride().isBlank()) {
            return record.initImageScriptPathOverride();
        }
        return DEFAULT_IMAGE_SCRIPT_PATH;
    }

    private String effectiveProjectScriptPath(ProjectWorktreeSandboxRecord record) {
        if (record != null && record.initProjectScriptPathOverride() != null && !record.initProjectScriptPathOverride().isBlank()) {
            return record.initProjectScriptPathOverride();
        }
        return DEFAULT_PROJECT_SCRIPT_PATH;
    }

    private String scriptPathSource(String overridePath) {
        return overridePath == null || overridePath.isBlank() ? "DEFAULT" : "WORKTREE_PROPERTY";
    }

    private String inspectImageStatus(String imageReference, ProjectWorktreeSandboxRecord record) {
        if (podmanResourceExists("image", "exists", imageReference)) {
            return "READY";
        }
        if (record != null && "FAILED".equals(record.imageStatus())) {
            return "FAILED";
        }
        return "MISSING";
    }

    private String inspectContainerStatus(String containerName, ProjectWorktreeSandboxRecord record) {
        if (podmanResourceExists("container", "exists", containerName)) {
            String status = runPodman(true, "inspect", "--format", "{{.State.Status}}", containerName);
            if ("running".equals(status)) {
                return "ACTIVE";
            }
            return "FAILED";
        }
        if (record != null && "FAILED".equals(record.containerStatus())) {
            return "FAILED";
        }
        return "INACTIVE";
    }

    private String deriveImageReference(long projectId, String worktreePath) {
        String suffix = stableSuffix(projectId, worktreePath);
        return "localhost/fsp-sandbox-" + suffix + ":latest";
    }

    private String deriveContainerName(long projectId, String worktreePath) {
        return "fsp-sandbox-" + stableSuffix(projectId, worktreePath);
    }

    private String stableSuffix(long projectId, String worktreePath) {
        String fileName = Path.of(worktreePath).getFileName() == null ? "worktree" : Path.of(worktreePath).getFileName().toString();
        String normalizedName = fileName.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]+", "-");
        String hash = Integer.toUnsignedString((projectId + ":" + worktreePath).hashCode(), 16);
        return normalizedName + "-" + hash;
    }

    private void writeContainerfile(Path buildContext, Path relativeScriptPath) {
        String normalizedScriptPath = "./" + relativeScriptPath.toString().replace('\\', '/');
        String containerfile = """
                FROM %s
                ENV DEBIAN_FRONTEND=noninteractive
                WORKDIR /workspace
                COPY . /workspace
                RUN ["/bin/sh", "%s"]
                CMD ["tail", "-f", "/dev/null"]
                """.formatted(DEFAULT_BASE_IMAGE, normalizedScriptPath);
        try {
            Files.writeString(buildContext.resolve("Containerfile"), containerfile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create sandbox Containerfile", e);
        }
    }

    private Path copyWorktreeToBuildContext(Path repositoryRootPath, Path worktreePath) {
        try {
            Path buildContextRoot = repositoryRootPath.getParent();
            Path buildContext = buildContextRoot == null
                    ? Files.createTempDirectory("fsp-sandbox-build-")
                    : Files.createTempDirectory(buildContextRoot, "fsp-sandbox-build-");
            Files.walkFileTree(worktreePath, new CopyTreeVisitor(worktreePath, buildContext));
            return buildContext;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to prepare sandbox build context", e);
        }
    }

    private Path resolveRelativeScriptPath(String scriptPath, String label) {
        Path path = Path.of(scriptPath).normalize();
        if (path.isAbsolute()) {
            throw new IllegalStateException(label + " must be configured as a path relative to the worktree root: " + scriptPath);
        }
        if (path.startsWith("..")) {
            throw new IllegalStateException(label + " must stay inside the worktree root: " + scriptPath);
        }
        return path;
    }

    private void awaitContainerStartup(String containerName) {
        long deadline = System.currentTimeMillis() + CONTAINER_STARTUP_TIMEOUT_MILLIS;
        while (System.currentTimeMillis() < deadline) {
            String status = runPodman(true, "inspect", "--format", "{{.State.Status}}", containerName);
            if ("running".equals(status)) {
                return;
            }
            if ("exited".equals(status) || "stopped".equals(status)) {
                String exitCode = runPodman(true, "inspect", "--format", "{{.State.ExitCode}}", containerName);
                String logs = runPodman(true, "logs", containerName);
                String detail = logs.isBlank() ? "exit code " + exitCode : logs;
                throw new IllegalStateException("Sandbox container initialization failed: " + detail);
            }
            sleep(CONTAINER_STARTUP_POLL_MILLIS);
        }
        throw new IllegalStateException("Timed out waiting for sandbox container initialization");
    }

    private void removeContainerIfPresent(String containerName) {
        if (podmanResourceExists("container", "exists", containerName)) {
            runPodmanOrThrow("Unable to remove sandbox container", "rm", "-f", containerName);
        }
    }

    private void removeImageIfPresent(String imageReference) {
        if (podmanResourceExists("image", "exists", imageReference)) {
            runPodmanOrThrow("Unable to remove sandbox image", "rmi", "-f", imageReference);
        }
    }

    private boolean isPodmanAvailable() {
        try {
            return !runPodman(true, "--version").isBlank();
        } catch (IllegalStateException error) {
            return false;
        }
    }

    private void ensurePodmanAvailable() {
        if (!isPodmanAvailable()) {
            throw new IllegalStateException("Podman is not available on this host");
        }
    }

    private boolean podmanResourceExists(String kind, String existsCommand, String identifier) {
        List<String> command = List.of("podman", kind, existsCommand, identifier);
        Process process;
        try {
            process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to start podman command", e);
        }

        try {
            process.getInputStream().readAllBytes();
            if (!process.waitFor(PODMAN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new IllegalStateException("Podman command timed out");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read podman command output", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for podman command", e);
        }
        return process.exitValue() == 0;
    }

    private String runPodman(boolean allowFailure, String... args) {
        List<String> command = new ArrayList<>();
        command.add("podman");
        command.addAll(List.of(args));

        Process process;
        try {
            process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to start podman command", e);
        }

        String output;
        try {
            output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            if (!process.waitFor(PODMAN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new IllegalStateException("Podman command timed out");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read podman command output", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for podman command", e);
        }

        if (process.exitValue() != 0 && !allowFailure) {
            throw new IllegalStateException(output.isBlank() ? "Podman command failed" : output);
        }
        return process.exitValue() == 0 ? output : "";
    }

    private String runPodmanOrThrow(String failurePrefix, String... args) {
        try {
            return runPodman(false, args);
        } catch (IllegalStateException error) {
            String detail = error.getMessage();
            if (detail == null || detail.isBlank()) {
                throw new IllegalStateException(failurePrefix, error);
            }
            throw new IllegalStateException(failurePrefix + ": " + detail, error);
        }
    }

    private Path normalizeAbsolutePath(String pathValue, String label) {
        if (pathValue == null || pathValue.isBlank()) {
            throw new IllegalArgumentException(label + " is required");
        }
        Path path = Path.of(pathValue).normalize();
        if (!path.isAbsolute()) {
            throw new IllegalArgumentException(label + " must be absolute: " + pathValue);
        }
        return path.toAbsolutePath();
    }

    private String shellQuote(String value) {
        return "'" + value.replace("'", "'\"'\"'") + "'";
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for sandbox container state", e);
        }
    }

    private void requireSupportedWorktree(Path repositoryRootPath, Path worktreePath) {
        ProjectRepositorySummary repositorySummary = new GitRepositoryInspector().inspect(repositoryRootPath.toString());
        ProjectWorktreeSummary worktree = repositorySummary.worktrees().stream()
                .filter(candidate -> normalizeAbsolutePath(candidate.path(), "Worktree path").equals(worktreePath))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Worktree is not managed by the bound repository: " + worktreePath));
        String restriction = sandboxRestriction(worktree);
        if (restriction != null) {
            throw new IllegalStateException(restriction);
        }
    }

    private void deleteRecursively(Path path) {
        if (path == null || Files.notExists(path)) {
            return;
        }
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.deleteIfExists(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.deleteIfExists(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new IllegalStateException("Unable to clean sandbox build context", e);
        }
    }

    private static final class CopyTreeVisitor extends SimpleFileVisitor<Path> {

        private final Path sourceRoot;
        private final Path targetRoot;

        private CopyTreeVisitor(Path sourceRoot, Path targetRoot) {
            this.sourceRoot = sourceRoot;
            this.targetRoot = targetRoot;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            if (shouldSkip(dir)) {
                return FileVisitResult.SKIP_SUBTREE;
            }
            Path target = targetRoot.resolve(sourceRoot.relativize(dir).toString());
            Files.createDirectories(target);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (!shouldSkip(file)) {
                Path target = targetRoot.resolve(sourceRoot.relativize(file).toString());
                if (target.getParent() != null) {
                    Files.createDirectories(target.getParent());
                }
                Files.copy(file, target);
            }
            return FileVisitResult.CONTINUE;
        }

        private boolean shouldSkip(Path path) {
            Path relative = sourceRoot.relativize(path);
            if (relative.getNameCount() == 0) {
                return false;
            }
            return ".git".equals(relative.getName(0).toString());
        }
    }
}
