package com.fastservice.platform.backend.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Comparator;

import com.fastservice.platform.backend.common.db.JdbcSupport;

abstract class ProjectServiceTestSupport {

    protected Path createFakePlatformToolRepoRoot() throws IOException {
        Path repoRoot = Files.createTempDirectory("fsp-platform-tool-repo-");
        Path scriptsDir = Files.createDirectories(repoRoot.resolve("scripts"));
        Path docsAiDir = Files.createDirectories(repoRoot.resolve("docs/ai"));
        Path sourceDocsAiDir = Path.of("../docs/ai").toAbsolutePath().normalize();
        Files.copy(sourceDocsAiDir.resolve("module-registry.json"), docsAiDir.resolve("module-registry.json"));
        Files.copy(sourceDocsAiDir.resolve("app-assembly-contract.json"), docsAiDir.resolve("app-assembly-contract.json"));
        Files.writeString(
                scriptsDir.resolve("platform-tool.sh"),
                "#!/usr/bin/env bash\n"
                        + "set -euo pipefail\n"
                        + "group=\"${1:-}\"\n"
                        + "command=\"${2:-}\"\n"
                        + "case \"${group}/${command}\" in\n"
                        + "  assembly/scaffold)\n"
                        + "    manifest=\"${3:-}\"\n"
                        + "    output=\"${4:-}\"\n"
                        + "    if grep -q '\"id\"[[:space:]]*:[[:space:]]*\"fail-assembly\"' \"$manifest\"; then\n"
                        + "      printf 'Repository-owned assembly tooling failed\\n'\n"
                        + "      exit 1\n"
                        + "    fi\n"
                        + "    mkdir -p \"$output\"\n"
                        + "    cp \"$manifest\" \"$output/app-manifest.json\"\n"
                        + "    printf 'Repository-owned assembly tooling scaffolded app into %s\\n' \"$output\"\n"
                        + "    ;;\n"
                        + "  generated-app/verify)\n"
                        + "    target=\"${3:-}\"\n"
                        + "    if [[ -f \"$target/.fail-generated-app-verify\" ]]; then\n"
                        + "      printf 'Repository-owned generated-app verification failed for %s\\n' \"$target\"\n"
                        + "      exit 1\n"
                        + "    fi\n"
                        + "    printf 'Repository-owned generated-app verification passed for %s\\n' \"$target\"\n"
                        + "    ;;\n"
                        + "  generated-app/smoke)\n"
                        + "    target=\"${3:-}\"\n"
                        + "    if [[ -f \"$target/.fail-runtime-smoke\" ]]; then\n"
                        + "      printf 'Derived-app runtime smoke failed during proxy reachability for %s\\n' \"$target\"\n"
                        + "      exit 1\n"
                        + "    fi\n"
                        + "    printf 'Derived-app runtime smoke verification passed for %s through frontend /service proxy.\\n' \"$target\"\n"
                        + "    ;;\n"
                        + "  *)\n"
                        + "    printf 'Unexpected command: %s %s\\n' \"$group\" \"$command\" >&2\n"
                        + "    exit 1\n"
                        + "    ;;\n"
                        + "esac\n",
                StandardCharsets.UTF_8);
        scriptsDir.resolve("platform-tool.sh").toFile().setExecutable(true, false);
        return repoRoot;
    }

    protected Path createGitRepository() throws Exception {
        Path remoteRepositoryDir = Files.createTempDirectory("fsp-project-remote-");
        runGit(remoteRepositoryDir, "init", "--bare");
        Path repositoryDir = Files.createTempDirectory("fsp-project-repository-");
        runGit(repositoryDir, "init");
        runGit(repositoryDir, "config", "user.name", "Fast Service Tests");
        runGit(repositoryDir, "config", "user.email", "tests@fastservice.local");
        runGit(repositoryDir, "checkout", "-b", "repo-test");
        Files.writeString(repositoryDir.resolve("README.md"), "repository binding test\n", StandardCharsets.UTF_8);
        Files.writeString(
                repositoryDir.resolve("init-image.sh"),
                "#!/bin/sh\nprintf 'image ready\\n' > image-ready.txt\n",
                StandardCharsets.UTF_8);
        Files.writeString(
                repositoryDir.resolve("init-project.sh"),
                "#!/bin/sh\nprintf 'project ready\\n' > project-ready.txt\n",
                StandardCharsets.UTF_8);
        runGit(repositoryDir, "add", "README.md", "init-image.sh", "init-project.sh");
        runGit(repositoryDir, "commit", "-m", "Initial platform repo");
        runGit(repositoryDir, "remote", "add", "origin", remoteRepositoryDir.toString());
        runGit(repositoryDir, "push", "-u", "origin", "repo-test");
        Files.writeString(repositoryDir.resolve("README.md"), "repository binding test\nsecond commit\n", StandardCharsets.UTF_8);
        runGit(repositoryDir, "add", "README.md");
        runGit(repositoryDir, "commit", "-m", "Second platform repo commit");
        runGit(repositoryDir, "push", "origin", "repo-test");
        runGit(repositoryDir, "checkout", "-b", "feature/api-preview");
        Files.writeString(repositoryDir.resolve("api-preview.txt"), "preview worktree branch\n", StandardCharsets.UTF_8);
        runGit(repositoryDir, "add", "api-preview.txt");
        runGit(repositoryDir, "commit", "-m", "Add API preview branch");
        runGit(repositoryDir, "push", "-u", "origin", "feature/api-preview");
        runGit(repositoryDir, "checkout", "repo-test");
        runGit(repositoryDir, "branch", "feature-preview");
        return repositoryDir;
    }

    protected Path createDetachedHeadRepository() throws Exception {
        Path repositoryDir = createGitRepository();
        String headCommit = runGitAndReadOutput(repositoryDir, "rev-parse", "HEAD");
        runGit(repositoryDir, "checkout", headCommit);
        return repositoryDir;
    }

    protected Path createConflictingGitRepository() throws Exception {
        Path remoteRepositoryDir = Files.createTempDirectory("fsp-project-conflict-remote-");
        runGit(remoteRepositoryDir, "init", "--bare");
        Path repositoryDir = Files.createTempDirectory("fsp-project-conflict-repository-");
        runGit(repositoryDir, "init");
        runGit(repositoryDir, "config", "user.name", "Fast Service Tests");
        runGit(repositoryDir, "config", "user.email", "tests@fastservice.local");
        runGit(repositoryDir, "checkout", "-b", "repo-test");
        Files.writeString(repositoryDir.resolve("README.md"), "conflict repository\n", StandardCharsets.UTF_8);
        Files.writeString(repositoryDir.resolve("conflict.txt"), "base\n", StandardCharsets.UTF_8);
        runGit(repositoryDir, "add", "README.md", "conflict.txt");
        runGit(repositoryDir, "commit", "-m", "Initial conflicting repository");
        runGit(repositoryDir, "remote", "add", "origin", remoteRepositoryDir.toString());
        runGit(repositoryDir, "push", "-u", "origin", "repo-test");
        runGit(repositoryDir, "checkout", "-b", "feature/conflict");
        Files.writeString(repositoryDir.resolve("conflict.txt"), "feature change\n", StandardCharsets.UTF_8);
        runGit(repositoryDir, "add", "conflict.txt");
        runGit(repositoryDir, "commit", "-m", "Feature side conflict change");
        runGit(repositoryDir, "push", "-u", "origin", "feature/conflict");
        runGit(repositoryDir, "checkout", "repo-test");
        Files.writeString(repositoryDir.resolve("conflict.txt"), "target change\n", StandardCharsets.UTF_8);
        runGit(repositoryDir, "add", "conflict.txt");
        runGit(repositoryDir, "commit", "-m", "Target side conflict change");
        runGit(repositoryDir, "push", "origin", "repo-test");
        runGit(repositoryDir, "branch", "feature-preview");
        return repositoryDir;
    }

    protected void runGit(Path repositoryDir, String... args) throws Exception {
        String output = runGitAndReadOutput(repositoryDir, args);
        if (output.startsWith("fatal:")) {
            throw new IOException("Git command failed: " + output);
        }
    }

    protected String runGitAndReadOutput(Path repositoryDir, String... args) throws Exception {
        String[] command = new String[args.length + 3];
        command[0] = "git";
        command[1] = "-C";
        command[2] = repositoryDir.toString();
        System.arraycopy(args, 0, command, 3, args.length);
        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Git command failed: " + output);
        }
        return output;
    }

    protected String runGitAndReadOutputAllowFailure(Path repositoryDir, String... args) throws Exception {
        String[] command = new String[args.length + 3];
        command[0] = "git";
        command[1] = "-C";
        command[2] = repositoryDir.toString();
        System.arraycopy(args, 0, command, 3, args.length);
        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
        process.waitFor();
        return output;
    }

    protected void upsertSandboxScriptOverrides(
            long projectId,
            String worktreePath,
            String initImageOverride,
            String initProjectOverride) throws SQLException {
        String updateSql = """
                UPDATE project_worktree_sandbox
                SET init_image_script_path_override = ?,
                    init_project_script_path_override = ?
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
                ) VALUES(?, ?, ?, ?, null, null, null, null)
                """;
        try (Connection connection = JdbcSupport.getConnection()) {
            int updatedRows;
            try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
                statement.setString(1, initImageOverride);
                statement.setString(2, initProjectOverride);
                statement.setLong(3, projectId);
                statement.setString(4, worktreePath);
                updatedRows = statement.executeUpdate();
            }
            if (updatedRows == 0) {
                try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
                    statement.setLong(1, projectId);
                    statement.setString(2, worktreePath);
                    statement.setString(3, initImageOverride);
                    statement.setString(4, initProjectOverride);
                    statement.executeUpdate();
                }
            }
        }
    }

    protected boolean podmanResourceExists(String kind, String identifier) throws Exception {
        Process process = new ProcessBuilder("podman", kind, "exists", identifier)
                .redirectErrorStream(true)
                .start();
        process.getInputStream().readAllBytes();
        int exitCode = process.waitFor();
        return exitCode == 0;
    }

    protected String runPodmanAndReadOutput(String... args) throws Exception {
        String[] command = new String[args.length + 1];
        command[0] = "podman";
        System.arraycopy(args, 0, command, 1, args.length);
        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Podman command failed: " + output);
        }
        return output;
    }

    protected String escapeJson(String value) {
        return value.replace("\\", "\\\\");
    }

    protected void deleteRecursively(Path path) throws IOException {
        if (Files.notExists(path)) {
            return;
        }

        try (var stream = Files.walk(path)) {
            stream.sorted(Comparator.reverseOrder()).forEach(entry -> {
                try {
                    Files.deleteIfExists(entry);
                } catch (IOException e) {
                    throw new IllegalStateException("Unable to delete path: " + entry, e);
                }
            });
        }
    }
}
