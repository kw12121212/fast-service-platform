package com.fastservice.platform.backend.project;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

final class GitRepositoryInspector {

    private static final long GIT_TIMEOUT_SECONDS = 10;

    ProjectRepositorySummary inspect(String repositoryPath) {
        if (repositoryPath == null || repositoryPath.isBlank()) {
            throw new IllegalArgumentException("Repository path is required");
        }

        Path requestedPath = Path.of(repositoryPath).normalize();
        if (!requestedPath.isAbsolute()) {
            throw new IllegalArgumentException("Repository path must be absolute: " + repositoryPath);
        }

        String repositoryRoot = runGitCommand(requestedPath, false, "rev-parse", "--show-toplevel");
        Path repositoryRootPath = Path.of(repositoryRoot).normalize().toAbsolutePath();
        String branch = runGitCommand(repositoryRootPath, false, "rev-parse", "--abbrev-ref", "HEAD");
        boolean dirty = !runGitCommand(repositoryRootPath, false, "status", "--porcelain").isBlank();
        String latestCommitSummary = runGitCommand(repositoryRootPath, true, "log", "-1", "--pretty=format:%h %s");
        if (latestCommitSummary.isBlank()) {
            latestCommitSummary = "No commits yet";
        }

        return new ProjectRepositorySummary(repositoryRootPath.toString(), branch, dirty, latestCommitSummary);
    }

    private String runGitCommand(Path workingPath, boolean allowFailure, String... args) {
        List<String> command = new ArrayList<>();
        command.add("git");
        command.add("-C");
        command.add(workingPath.toString());
        command.addAll(List.of(args));

        Process process;
        try {
            process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to start git command", e);
        }

        String output;
        try {
            output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            if (!process.waitFor(GIT_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new IllegalStateException("Git command timed out for path: " + workingPath);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read git command output", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for git command", e);
        }

        if (process.exitValue() != 0) {
            if (allowFailure) {
                return "";
            }
            throw new IllegalArgumentException("Repository path is not a valid Git repository: " + workingPath);
        }

        return output;
    }
}
