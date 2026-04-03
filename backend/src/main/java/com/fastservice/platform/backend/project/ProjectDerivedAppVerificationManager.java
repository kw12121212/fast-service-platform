package com.fastservice.platform.backend.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fastservice.platform.backend.common.db.JdbcSupport;
import com.fastservice.platform.backend.common.json.SimpleJson;

final class ProjectDerivedAppVerificationManager {

    private static final String STATUS_AVAILABLE = "AVAILABLE";
    private static final String STATUS_RESTRICTED = "RESTRICTED";
    private static final String OUTCOME_SUCCESS = "SUCCESS";
    private static final String OUTCOME_FAILED = "FAILED";
    private static final String STEP_NOT_RUN = "NOT_RUN";
    private static final String CATEGORY_VALIDATION = "REQUEST_VALIDATION";
    private static final String CATEGORY_COMBINED = "COMBINED_VALIDATION";
    private static final String CATEGORY_GENERATED_APP_VERIFICATION = "GENERATED_APP_VERIFICATION";
    private static final String CATEGORY_RUNTIME_SMOKE = "RUNTIME_SMOKE";
    private static final String SOURCE_CONTEXT_TYPE = "BOUND_MAIN_REPOSITORY";
    private static final String TARGET_CONTEXT_TYPE = "LATEST_SUCCESSFUL_ASSEMBLY_OUTPUT";

    String readVerificationContext(long projectId, String repositoryRootPath) {
        VerificationRecord persisted = readRecord(projectId);
        return buildContextJson(projectId, repositoryRootPath, persisted);
    }

    String requestVerification(long projectId, String repositoryRootPath) {
        String sourceRepositoryPath = normalizeAbsolutePath(repositoryRootPath, "Repository path").toString();
        AssemblyTarget target = resolveAssemblyTarget(projectId);

        if (target.outputDirectory() == null) {
            String message = target.restriction();
            persistOutcome(projectId, new VerificationRecord(
                    STATUS_RESTRICTED,
                    true,
                    message,
                    sourceRepositoryPath,
                    OUTCOME_FAILED,
                    CATEGORY_VALIDATION,
                    message,
                    null,
                    STEP_NOT_RUN,
                    "Generated-app verification was not started.",
                    STEP_NOT_RUN,
                    "Runtime smoke was not started.",
                    Instant.now().toString()));
            throw new IllegalStateException(message);
        }

        Path targetDirectory;
        try {
            targetDirectory = normalizeAbsolutePath(target.outputDirectory(), "Latest successful assembly output directory");
        } catch (IllegalArgumentException error) {
            persistOutcome(projectId, new VerificationRecord(
                    STATUS_RESTRICTED,
                    true,
                    error.getMessage(),
                    sourceRepositoryPath,
                    OUTCOME_FAILED,
                    CATEGORY_VALIDATION,
                    error.getMessage(),
                    target.outputDirectory(),
                    STEP_NOT_RUN,
                    "Generated-app verification was not started.",
                    STEP_NOT_RUN,
                    "Runtime smoke was not started.",
                    Instant.now().toString()));
            throw error;
        }

        if (!Files.isDirectory(targetDirectory)) {
            String message = "Latest successful project-scoped assembly output is no longer available: " + targetDirectory;
            persistOutcome(projectId, new VerificationRecord(
                    STATUS_RESTRICTED,
                    true,
                    message,
                    sourceRepositoryPath,
                    OUTCOME_FAILED,
                    CATEGORY_VALIDATION,
                    message,
                    targetDirectory.toString(),
                    STEP_NOT_RUN,
                    "Generated-app verification was not started.",
                    STEP_NOT_RUN,
                    "Runtime smoke was not started.",
                    Instant.now().toString()));
            throw new IllegalStateException(message);
        }

        RepoRootResolution repoRoot = resolveRepoRoot();
        if (repoRoot.root() == null) {
            String message = repoRoot.restriction();
            persistOutcome(projectId, new VerificationRecord(
                    STATUS_RESTRICTED,
                    true,
                    message,
                    sourceRepositoryPath,
                    OUTCOME_FAILED,
                    CATEGORY_GENERATED_APP_VERIFICATION,
                    message,
                    targetDirectory.toString(),
                    STEP_NOT_RUN,
                    "Generated-app verification was not started.",
                    STEP_NOT_RUN,
                    "Runtime smoke was not started.",
                    Instant.now().toString()));
            throw new IllegalStateException(message);
        }

        CommandResult generatedAppVerification = runPlatformTool(repoRoot.root(), "generated-app", "verify", targetDirectory);
        if (generatedAppVerification.exitCode() != 0) {
            String message = normalizeProcessMessage(
                    generatedAppVerification.output(),
                    "Repository-owned generated-app verification failed");
            persistOutcome(projectId, new VerificationRecord(
                    STATUS_AVAILABLE,
                    false,
                    null,
                    sourceRepositoryPath,
                    OUTCOME_FAILED,
                    CATEGORY_GENERATED_APP_VERIFICATION,
                    message,
                    targetDirectory.toString(),
                    OUTCOME_FAILED,
                    message,
                    STEP_NOT_RUN,
                    "Runtime smoke was not started because generated-app verification failed.",
                    Instant.now().toString()));
            throw new IllegalStateException(message);
        }

        CommandResult runtimeSmoke = runPlatformTool(repoRoot.root(), "generated-app", "smoke", targetDirectory);
        if (runtimeSmoke.exitCode() != 0) {
            String message = normalizeProcessMessage(
                    runtimeSmoke.output(),
                    "Repository-owned derived-app runtime smoke failed");
            persistOutcome(projectId, new VerificationRecord(
                    STATUS_AVAILABLE,
                    false,
                    null,
                    sourceRepositoryPath,
                    OUTCOME_FAILED,
                    CATEGORY_RUNTIME_SMOKE,
                    message,
                    targetDirectory.toString(),
                    OUTCOME_SUCCESS,
                    "Generated-app verification completed through repository-owned tooling.",
                    OUTCOME_FAILED,
                    message,
                    Instant.now().toString()));
            throw new IllegalStateException(message);
        }

        persistOutcome(projectId, new VerificationRecord(
                STATUS_AVAILABLE,
                false,
                null,
                sourceRepositoryPath,
                OUTCOME_SUCCESS,
                CATEGORY_COMBINED,
                "Generated-app verification and runtime smoke completed through repository-owned tooling.",
                targetDirectory.toString(),
                OUTCOME_SUCCESS,
                "Generated-app verification completed through repository-owned tooling.",
                OUTCOME_SUCCESS,
                "Derived-app runtime smoke completed through repository-owned tooling.",
                Instant.now().toString()));
        return buildContextJson(projectId, repositoryRootPath, readRecord(projectId));
    }

    private String buildContextJson(long projectId, String repositoryRootPath, VerificationRecord persisted) {
        Map<String, Object> payload = new LinkedHashMap<>();
        RepoRootResolution repoRoot = resolveRepoRoot();
        AssemblyTarget target = resolveAssemblyTarget(projectId);
        boolean repositoryBound = repositoryRootPath != null && !repositoryRootPath.isBlank();
        boolean restricted = !repositoryBound || repoRoot.root() == null || target.outputDirectory() == null || target.restriction() != null;
        payload.put("available", !restricted);
        payload.put("status", restricted ? STATUS_RESTRICTED : STATUS_AVAILABLE);
        payload.put("restricted", restricted);
        payload.put("restriction", buildRestriction(repositoryRootPath, repoRoot, target));
        payload.put("sourceRepositoryPath", repositoryBound ? normalizeAbsolutePath(repositoryRootPath, "Repository path").toString() : null);
        payload.put("sourceContext", Map.of("type", SOURCE_CONTEXT_TYPE));
        Map<String, Object> targetContext = new LinkedHashMap<>();
        targetContext.put("type", TARGET_CONTEXT_TYPE);
        targetContext.put("outputDirectory", target.outputDirectory());
        payload.put("targetContext", targetContext);
        payload.put("latestOutcome", latestOutcomeMap(persisted));
        return SimpleJson.stringify(payload);
    }

    private String buildRestriction(String repositoryRootPath, RepoRootResolution repoRoot, AssemblyTarget target) {
        if (repositoryRootPath == null || repositoryRootPath.isBlank()) {
            return "Bind a repository first to run project-scoped derived-app verification.";
        }
        if (repoRoot.root() == null) {
            return repoRoot.restriction();
        }
        return target.restriction();
    }

    private Map<String, Object> latestOutcomeMap(VerificationRecord persisted) {
        if (persisted == null || persisted.latestOutcomeStatus() == null || persisted.latestOutcomeStatus().isBlank()) {
            return null;
        }
        Map<String, Object> latestOutcome = new LinkedHashMap<>();
        Map<String, Object> generatedAppVerification = new LinkedHashMap<>();
        generatedAppVerification.put("status", persisted.latestGeneratedAppVerificationStatus());
        generatedAppVerification.put("message", persisted.latestGeneratedAppVerificationMessage());
        Map<String, Object> runtimeSmoke = new LinkedHashMap<>();
        runtimeSmoke.put("status", persisted.latestRuntimeSmokeStatus());
        runtimeSmoke.put("message", persisted.latestRuntimeSmokeMessage());
        latestOutcome.put("status", persisted.latestOutcomeStatus());
        latestOutcome.put("category", persisted.latestOutcomeCategory());
        latestOutcome.put("message", persisted.latestOutcomeMessage());
        latestOutcome.put("targetOutputDirectory", persisted.latestTargetOutputDirectory());
        latestOutcome.put("generatedAppVerification", generatedAppVerification);
        latestOutcome.put("runtimeSmoke", runtimeSmoke);
        latestOutcome.put("updatedAt", persisted.updatedAt());
        return latestOutcome;
    }

    private AssemblyTarget resolveAssemblyTarget(long projectId) {
        String sql = """
                SELECT latest_outcome_status,
                       latest_output_directory
                FROM project_derived_app_assembly
                WHERE project_id = ?
                """;
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, projectId);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return new AssemblyTarget(null, "Run project-scoped derived-app assembly successfully before requesting verification.");
                }
                String latestOutcomeStatus = rs.getString("latest_outcome_status");
                String latestSuccessfulOutputDirectory = rs.getString("latest_output_directory");
                if (!OUTCOME_SUCCESS.equals(latestOutcomeStatus)
                        || latestSuccessfulOutputDirectory == null
                        || latestSuccessfulOutputDirectory.isBlank()) {
                    return new AssemblyTarget(null, "Run project-scoped derived-app assembly successfully before requesting verification.");
                }
                Path targetDirectory;
                try {
                    targetDirectory = normalizeAbsolutePath(latestSuccessfulOutputDirectory, "Latest successful assembly output directory");
                } catch (IllegalArgumentException error) {
                    return new AssemblyTarget(latestSuccessfulOutputDirectory, error.getMessage());
                }
                if (!Files.isDirectory(targetDirectory)) {
                    return new AssemblyTarget(
                            targetDirectory.toString(),
                            "Latest successful project-scoped assembly output is no longer available: " + targetDirectory);
                }
                return new AssemblyTarget(targetDirectory.toString(), null);
            }
        } catch (SQLException error) {
            throw new IllegalStateException("Unable to read latest successful project derived-app assembly output", error);
        }
    }

    private static Path normalizeAbsolutePath(String pathValue, String label) {
        if (pathValue == null || pathValue.isBlank()) {
            throw new IllegalArgumentException(label + " is required");
        }
        Path path = Path.of(pathValue).normalize();
        if (!path.isAbsolute()) {
            throw new IllegalArgumentException(label + " must be absolute: " + pathValue);
        }
        return path.toAbsolutePath();
    }

    private static RepoRootResolution resolveRepoRoot() {
        String configured = System.getProperty("fsp.repo-root", System.getenv("FSP_REPO_ROOT"));
        if (configured != null && !configured.isBlank()) {
            Path repoRoot = Path.of(configured).toAbsolutePath().normalize();
            if (!Files.exists(repoRoot.resolve("scripts/platform-tool.sh"))) {
                return new RepoRootResolution(
                        null,
                        "Configured repository root does not contain scripts/platform-tool.sh: " + repoRoot);
            }
            return new RepoRootResolution(repoRoot, null);
        }

        Path current = Path.of("").toAbsolutePath().normalize();
        while (current != null) {
            if (Files.exists(current.resolve("scripts/platform-tool.sh"))) {
                return new RepoRootResolution(current, null);
            }
            current = current.getParent();
        }
        return new RepoRootResolution(
                null,
                "Repository-owned generated-app validation tooling is not available from the current backend runtime.");
    }

    private static CommandResult runPlatformTool(Path repoRoot, String group, String command, Path targetDirectory) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "bash",
                    repoRoot.resolve("scripts/platform-tool.sh").toString(),
                    group,
                    command,
                    targetDirectory.toString());
            processBuilder.directory(repoRoot.toFile());
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            String output = new String(process.getInputStream().readAllBytes()).trim();
            int exitCode = process.waitFor();
            return new CommandResult(exitCode, output);
        } catch (IOException error) {
            throw new IllegalStateException("Unable to start repository-owned generated-app validation tooling", error);
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for repository-owned generated-app validation tooling", error);
        }
    }

    private static String normalizeProcessMessage(String output, String fallback) {
        if (output == null || output.isBlank()) {
            return fallback;
        }
        return output;
    }

    private VerificationRecord readRecord(long projectId) {
        String sql = """
                SELECT status,
                       restricted,
                       restriction,
                       source_repository_path,
                       latest_outcome_status,
                       latest_outcome_category,
                       latest_outcome_message,
                       latest_target_output_directory,
                       latest_generated_app_verification_status,
                       latest_generated_app_verification_message,
                       latest_runtime_smoke_status,
                       latest_runtime_smoke_message,
                       updated_at
                FROM project_derived_app_verification
                WHERE project_id = ?
                """;
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, projectId);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new VerificationRecord(
                        rs.getString("status"),
                        rs.getBoolean("restricted"),
                        rs.getString("restriction"),
                        rs.getString("source_repository_path"),
                        rs.getString("latest_outcome_status"),
                        rs.getString("latest_outcome_category"),
                        rs.getString("latest_outcome_message"),
                        rs.getString("latest_target_output_directory"),
                        rs.getString("latest_generated_app_verification_status"),
                        rs.getString("latest_generated_app_verification_message"),
                        rs.getString("latest_runtime_smoke_status"),
                        rs.getString("latest_runtime_smoke_message"),
                        rs.getString("updated_at"));
            }
        } catch (SQLException error) {
            throw new IllegalStateException("Unable to read project derived-app verification state", error);
        }
    }

    private void persistOutcome(long projectId, VerificationRecord record) {
        String updateSql = """
                UPDATE project_derived_app_verification
                SET status = ?,
                    restricted = ?,
                    restriction = ?,
                    source_repository_path = ?,
                    latest_outcome_status = ?,
                    latest_outcome_category = ?,
                    latest_outcome_message = ?,
                    latest_target_output_directory = ?,
                    latest_generated_app_verification_status = ?,
                    latest_generated_app_verification_message = ?,
                    latest_runtime_smoke_status = ?,
                    latest_runtime_smoke_message = ?,
                    updated_at = ?
                WHERE project_id = ?
                """;
        String insertSql = """
                INSERT INTO project_derived_app_verification(
                    project_id,
                    status,
                    restricted,
                    restriction,
                    source_repository_path,
                    latest_outcome_status,
                    latest_outcome_category,
                    latest_outcome_message,
                    latest_target_output_directory,
                    latest_generated_app_verification_status,
                    latest_generated_app_verification_message,
                    latest_runtime_smoke_status,
                    latest_runtime_smoke_message,
                    updated_at
                ) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = JdbcSupport.getConnection()) {
            int updatedRows;
            try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
                bindRecord(statement, record, false, projectId);
                updatedRows = statement.executeUpdate();
            }
            if (updatedRows == 0) {
                try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
                    bindRecord(statement, record, true, projectId);
                    statement.executeUpdate();
                }
            }
        } catch (SQLException error) {
            throw new IllegalStateException("Unable to persist project derived-app verification state", error);
        }
    }

    private void bindRecord(PreparedStatement statement, VerificationRecord record, boolean includeProjectIdFirst, long projectId)
            throws SQLException {
        int index = 1;
        if (includeProjectIdFirst) {
            statement.setLong(index++, projectId);
        }
        statement.setString(index++, record.status());
        statement.setBoolean(index++, record.restricted());
        statement.setString(index++, record.restriction());
        statement.setString(index++, record.sourceRepositoryPath());
        statement.setString(index++, record.latestOutcomeStatus());
        statement.setString(index++, record.latestOutcomeCategory());
        statement.setString(index++, record.latestOutcomeMessage());
        statement.setString(index++, record.latestTargetOutputDirectory());
        statement.setString(index++, record.latestGeneratedAppVerificationStatus());
        statement.setString(index++, record.latestGeneratedAppVerificationMessage());
        statement.setString(index++, record.latestRuntimeSmokeStatus());
        statement.setString(index++, record.latestRuntimeSmokeMessage());
        statement.setString(index++, record.updatedAt());
        if (!includeProjectIdFirst) {
            statement.setLong(index, projectId);
        }
    }

    private static final class AssemblyTarget {
        private final String outputDirectory;
        private final String restriction;

        private AssemblyTarget(String outputDirectory, String restriction) {
            this.outputDirectory = outputDirectory;
            this.restriction = restriction;
        }

        private String outputDirectory() {
            return outputDirectory;
        }

        private String restriction() {
            return restriction;
        }
    }

    private static final class CommandResult {
        private final int exitCode;
        private final String output;

        private CommandResult(int exitCode, String output) {
            this.exitCode = exitCode;
            this.output = output;
        }

        private int exitCode() {
            return exitCode;
        }

        private String output() {
            return output;
        }
    }

    private static final class VerificationRecord {
        private final String status;
        private final boolean restricted;
        private final String restriction;
        private final String sourceRepositoryPath;
        private final String latestOutcomeStatus;
        private final String latestOutcomeCategory;
        private final String latestOutcomeMessage;
        private final String latestTargetOutputDirectory;
        private final String latestGeneratedAppVerificationStatus;
        private final String latestGeneratedAppVerificationMessage;
        private final String latestRuntimeSmokeStatus;
        private final String latestRuntimeSmokeMessage;
        private final String updatedAt;

        private VerificationRecord(
                String status,
                boolean restricted,
                String restriction,
                String sourceRepositoryPath,
                String latestOutcomeStatus,
                String latestOutcomeCategory,
                String latestOutcomeMessage,
                String latestTargetOutputDirectory,
                String latestGeneratedAppVerificationStatus,
                String latestGeneratedAppVerificationMessage,
                String latestRuntimeSmokeStatus,
                String latestRuntimeSmokeMessage,
                String updatedAt) {
            this.status = status;
            this.restricted = restricted;
            this.restriction = restriction;
            this.sourceRepositoryPath = sourceRepositoryPath;
            this.latestOutcomeStatus = latestOutcomeStatus;
            this.latestOutcomeCategory = latestOutcomeCategory;
            this.latestOutcomeMessage = latestOutcomeMessage;
            this.latestTargetOutputDirectory = latestTargetOutputDirectory;
            this.latestGeneratedAppVerificationStatus = latestGeneratedAppVerificationStatus;
            this.latestGeneratedAppVerificationMessage = latestGeneratedAppVerificationMessage;
            this.latestRuntimeSmokeStatus = latestRuntimeSmokeStatus;
            this.latestRuntimeSmokeMessage = latestRuntimeSmokeMessage;
            this.updatedAt = updatedAt;
        }

        private String status() {
            return status;
        }

        private boolean restricted() {
            return restricted;
        }

        private String restriction() {
            return restriction;
        }

        private String sourceRepositoryPath() {
            return sourceRepositoryPath;
        }

        private String latestOutcomeStatus() {
            return latestOutcomeStatus;
        }

        private String latestOutcomeCategory() {
            return latestOutcomeCategory;
        }

        private String latestOutcomeMessage() {
            return latestOutcomeMessage;
        }

        private String latestTargetOutputDirectory() {
            return latestTargetOutputDirectory;
        }

        private String latestGeneratedAppVerificationStatus() {
            return latestGeneratedAppVerificationStatus;
        }

        private String latestGeneratedAppVerificationMessage() {
            return latestGeneratedAppVerificationMessage;
        }

        private String latestRuntimeSmokeStatus() {
            return latestRuntimeSmokeStatus;
        }

        private String latestRuntimeSmokeMessage() {
            return latestRuntimeSmokeMessage;
        }

        private String updatedAt() {
            return updatedAt;
        }
    }

    private static final class RepoRootResolution {
        private final Path root;
        private final String restriction;

        private RepoRootResolution(Path root, String restriction) {
            this.root = root;
            this.restriction = restriction;
        }

        private Path root() {
            return root;
        }

        private String restriction() {
            return restriction;
        }
    }
}
