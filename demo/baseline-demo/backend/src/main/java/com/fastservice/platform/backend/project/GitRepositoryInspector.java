package com.fastservice.platform.backend.project;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

final class GitRepositoryInspector {

    private static final long GIT_TIMEOUT_SECONDS = 10;
    private static final int RECENT_COMMIT_LIMIT = 5;

    ProjectRepositorySummary inspect(String repositoryPath) {
        Path repositoryRootPath = resolveRepositoryRoot(repositoryPath);
        return inspectResolvedRepositoryRoot(repositoryRootPath);
    }

    String switchBranch(String repositoryPath, String branchName) {
        if (branchName == null || branchName.isBlank()) {
            throw new IllegalArgumentException("Branch name is required");
        }

        Path repositoryRootPath = resolveRepositoryRoot(repositoryPath);
        ProjectRepositorySummary repositorySummary = inspectResolvedRepositoryRoot(repositoryRootPath);

        if (repositorySummary.headState() == ProjectGitHeadState.DETACHED) {
            throw new IllegalStateException("Cannot switch branches while repository is in detached HEAD state");
        }

        if (repositorySummary.dirty()) {
            throw new IllegalStateException("Cannot switch branches while working tree is dirty");
        }

        if (!repositorySummary.availableBranches().contains(branchName)) {
            throw new IllegalArgumentException("Branch does not exist locally: " + branchName);
        }

        if (branchName.equals(repositorySummary.branch())) {
            return branchName;
        }

        runGitCommand(repositoryRootPath, false, "checkout", branchName);
        ProjectRepositorySummary updatedRepository = inspectResolvedRepositoryRoot(repositoryRootPath);
        return updatedRepository.branch();
    }

    private Path resolveRepositoryRoot(String repositoryPath) {
        if (repositoryPath == null || repositoryPath.isBlank()) {
            throw new IllegalArgumentException("Repository path is required");
        }

        Path requestedPath = Path.of(repositoryPath).normalize();
        if (!requestedPath.isAbsolute()) {
            throw new IllegalArgumentException("Repository path must be absolute: " + repositoryPath);
        }

        String repositoryRoot = runGitCommand(requestedPath, false, "rev-parse", "--show-toplevel");
        return Path.of(repositoryRoot).normalize().toAbsolutePath();
    }

    private ProjectRepositorySummary inspectResolvedRepositoryRoot(Path repositoryRootPath) {
        String symbolicBranch = runGitCommand(repositoryRootPath, true, "symbolic-ref", "--quiet", "--short", "HEAD");
        ProjectGitHeadState headState = symbolicBranch.isBlank() ? ProjectGitHeadState.DETACHED : ProjectGitHeadState.BRANCH;
        String branch = symbolicBranch.isBlank() ? null : symbolicBranch;
        boolean dirty = !runGitCommand(repositoryRootPath, false, "status", "--porcelain").isBlank();
        List<String> availableBranches = splitNonBlankLines(
                runGitCommand(repositoryRootPath, false, "for-each-ref", "--format=%(refname:short)", "refs/heads"));
        List<ProjectGitCommitSummary> recentCommits = readRecentCommits(repositoryRootPath);
        String latestCommitSummary = recentCommits.isEmpty() ? "No commits yet"
                : recentCommits.getFirst().hash() + " " + recentCommits.getFirst().summary();
        if (latestCommitSummary.isBlank()) {
            latestCommitSummary = "No commits yet";
        }

        return new ProjectRepositorySummary(
                repositoryRootPath.toString(),
                headState,
                branch,
                dirty,
                latestCommitSummary,
                availableBranches,
                recentCommits);
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

    private List<ProjectGitCommitSummary> readRecentCommits(Path repositoryRootPath) {
        String output = runGitCommand(
                repositoryRootPath,
                true,
                "log",
                "-n",
                Integer.toString(RECENT_COMMIT_LIMIT),
                "--pretty=format:%h\t%s");
        if (output.isBlank()) {
            return List.of();
        }

        List<ProjectGitCommitSummary> commits = new ArrayList<>();
        for (String line : splitNonBlankLines(output)) {
            int separatorIndex = line.indexOf('\t');
            if (separatorIndex < 0) {
                commits.add(new ProjectGitCommitSummary(line, ""));
                continue;
            }
            commits.add(new ProjectGitCommitSummary(
                    line.substring(0, separatorIndex),
                    line.substring(separatorIndex + 1)));
        }
        return Collections.unmodifiableList(commits);
    }

    private List<String> splitNonBlankLines(String output) {
        if (output.isBlank()) {
            return List.of();
        }

        List<String> values = new ArrayList<>();
        for (String line : output.split("\\R")) {
            if (!line.isBlank()) {
                values.add(line);
            }
        }
        return Collections.unmodifiableList(values);
    }
}
