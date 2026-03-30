package com.fastservice.platform.backend.project;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

    String createWorktree(String repositoryPath, String branchName) {
        if (branchName == null || branchName.isBlank()) {
            throw new IllegalArgumentException("Branch name is required");
        }

        Path repositoryRootPath = resolveRepositoryRoot(repositoryPath);
        ProjectRepositorySummary repositorySummary = inspectResolvedRepositoryRoot(repositoryRootPath);
        requireNonDetachedWorktreeState(repositorySummary, "Cannot create worktrees while repository is in detached HEAD state");

        if (!repositorySummary.availableBranches().contains(branchName)) {
            throw new IllegalArgumentException("Branch does not exist locally: " + branchName);
        }

        if (repositorySummary.worktrees().stream().anyMatch(worktree -> branchName.equals(worktree.branch()))) {
            throw new IllegalStateException("Branch already has a worktree: " + branchName);
        }

        Path worktreeRoot = repositoryRootPath.resolveSibling(repositoryRootPath.getFileName() + "-worktrees");
        Path worktreePath = worktreeRoot.resolve(sanitizeBranchName(branchName)).normalize().toAbsolutePath();
        if (Files.exists(worktreePath)) {
            throw new IllegalStateException("Worktree path already exists: " + worktreePath);
        }

        try {
            Files.createDirectories(worktreeRoot);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create worktree root directory", e);
        }

        runGitCommandOrThrow(
                repositoryRootPath,
                "Unable to create project worktree",
                "worktree",
                "add",
                worktreePath.toString(),
                branchName);
        return worktreePath.toString();
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

    String deleteWorktree(String repositoryPath, String worktreePath) {
        Path repositoryRootPath = resolveRepositoryRoot(repositoryPath);
        ProjectRepositorySummary repositorySummary = inspectResolvedRepositoryRoot(repositoryRootPath);
        Path requestedWorktreePath = normalizeAbsolutePath(worktreePath, "Worktree path");
        ProjectWorktreeSummary selectedWorktree = repositorySummary.worktrees().stream()
                .filter(worktree -> Path.of(worktree.path()).normalize().toAbsolutePath().equals(requestedWorktreePath))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Worktree is not managed by the bound repository: " + worktreePath));

        if (selectedWorktree.main()) {
            throw new IllegalArgumentException("Cannot delete the main repository worktree");
        }
        if (selectedWorktree.stale()) {
            throw new IllegalStateException("Cannot delete stale worktree records; run prune instead");
        }
        if (selectedWorktree.dirty()) {
            throw new IllegalStateException("Cannot delete worktree while it has uncommitted changes");
        }
        if (!selectedWorktree.hasUpstream()) {
            throw new IllegalStateException("Cannot delete worktree without an upstream branch");
        }
        if (selectedWorktree.hasUnpushedCommits()) {
            throw new IllegalStateException("Cannot delete worktree while it has unpushed commits");
        }

        runGitCommandOrThrow(
                repositoryRootPath,
                "Unable to delete project worktree",
                "worktree",
                "remove",
                requestedWorktreePath.toString());
        return requestedWorktreePath.toString();
    }

    String mergeWorktree(String repositoryPath, String worktreePath, String targetBranch) {
        if (targetBranch == null || targetBranch.isBlank()) {
            throw new IllegalArgumentException("Target branch is required");
        }

        Path repositoryRootPath = resolveRepositoryRoot(repositoryPath);
        ProjectRepositorySummary repositorySummary = inspectResolvedRepositoryRoot(repositoryRootPath);
        Path requestedWorktreePath = normalizeAbsolutePath(worktreePath, "Worktree path");
        ProjectWorktreeSummary sourceWorktree = repositorySummary.worktrees().stream()
                .filter(worktree -> Path.of(worktree.path()).normalize().toAbsolutePath().equals(requestedWorktreePath))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Worktree is not managed by the bound repository: " + worktreePath));

        String mergeRestriction = sourceWorktree.mergeRestriction(repositorySummary.availableBranches());
        if (mergeRestriction != null) {
            throw new IllegalStateException(mergeRestriction);
        }
        if (!repositorySummary.availableBranches().contains(targetBranch)) {
            throw new IllegalArgumentException("Target branch does not exist locally: " + targetBranch);
        }
        if (targetBranch.equals(sourceWorktree.branch())) {
            throw new IllegalArgumentException("Target branch must differ from source branch: " + targetBranch);
        }

        ProjectWorktreeSummary targetWorktree = repositorySummary.worktrees().stream()
                .filter(worktree -> !worktree.stale())
                .filter(worktree -> worktree.headState() == ProjectGitHeadState.BRANCH)
                .filter(worktree -> targetBranch.equals(worktree.branch()))
                .findFirst()
                .orElse(null);

        Path mergeExecutionPath = repositoryRootPath;
        if (targetWorktree != null) {
            if (targetWorktree.dirty()) {
                throw new IllegalStateException("Cannot merge into target branch while its worktree has uncommitted changes");
            }
            mergeExecutionPath = Path.of(targetWorktree.path()).normalize().toAbsolutePath();
        } else {
            ProjectWorktreeSummary mainWorktree = repositorySummary.worktrees().stream()
                    .filter(ProjectWorktreeSummary::main)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Main repository worktree is unavailable"));
            if (mainWorktree.dirty()) {
                throw new IllegalStateException("Cannot prepare merge target while the main repository worktree has uncommitted changes");
            }
            if (!targetBranch.equals(repositorySummary.branch())) {
                runGitCommandOrThrow(repositoryRootPath, "Unable to prepare merge target branch", "checkout", targetBranch);
            }
        }

        try {
            runGitCommandOrThrow(
                    mergeExecutionPath,
                    "Unable to merge project worktree branch",
                    "merge",
                    "--no-ff",
                    "--no-edit",
                    sourceWorktree.branch());
        } catch (IllegalStateException error) {
            boolean aborted = abortMergeIfNeeded(mergeExecutionPath);
            if (isMergeConflictMessage(error.getMessage())) {
                throw new IllegalStateException(
                        "Merge conflict detected while merging "
                                + sourceWorktree.branch()
                                + " into "
                                + targetBranch
                                + (aborted ? "; the platform aborted the in-progress merge"
                                        : "; the platform could not complete the merge"));
            }
            throw error;
        }

        return targetBranch;
    }

    String repairWorktrees(String repositoryPath) {
        Path repositoryRootPath = resolveRepositoryRoot(repositoryPath);
        ProjectRepositorySummary repositorySummary = inspectResolvedRepositoryRoot(repositoryRootPath);
        requireNonDetachedWorktreeState(repositorySummary, "Cannot repair worktrees while repository is in detached HEAD state");
        runGitCommandOrThrow(repositoryRootPath, "Unable to repair project worktree metadata", "worktree", "repair");
        return repositoryRootPath.toString();
    }

    String pruneWorktrees(String repositoryPath) {
        Path repositoryRootPath = resolveRepositoryRoot(repositoryPath);
        runGitCommandOrThrow(
                repositoryRootPath,
                "Unable to prune stale project worktree metadata",
                "worktree",
                "prune",
                "--expire=now");
        return repositoryRootPath.toString();
    }

    private Path resolveRepositoryRoot(String repositoryPath) {
        Path requestedPath = normalizeAbsolutePath(repositoryPath, "Repository path");

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
        List<ProjectWorktreeSummary> worktrees = readWorktrees(repositoryRootPath);
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
                recentCommits,
                worktrees);
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

    private String runGitCommandOrThrow(Path workingPath, String failurePrefix, String... args) {
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
            if (output.isBlank()) {
                throw new IllegalStateException(failurePrefix);
            }
            throw new IllegalStateException(failurePrefix + ": " + output);
        }

        return output;
    }

    private boolean abortMergeIfNeeded(Path workingPath) {
        String mergeHead = runGitCommand(workingPath, true, "rev-parse", "-q", "--verify", "MERGE_HEAD");
        if (mergeHead.isBlank()) {
            return false;
        }

        runGitCommandOrThrow(workingPath, "Unable to abort conflicted merge", "merge", "--abort");
        return true;
    }

    private boolean isMergeConflictMessage(String message) {
        if (message == null || message.isBlank()) {
            return false;
        }

        return message.contains("CONFLICT")
                || message.contains("Automatic merge failed")
                || message.contains("Merge conflict");
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

    private List<ProjectWorktreeSummary> readWorktrees(Path repositoryRootPath) {
        String output = runGitCommand(repositoryRootPath, false, "worktree", "list", "--porcelain");
        if (output.isBlank()) {
            return List.of();
        }

        List<ProjectWorktreeSummary> worktrees = new ArrayList<>();
        Path worktreePath = null;
        String branch = null;
        ProjectGitHeadState headState = ProjectGitHeadState.BRANCH;
        boolean stale = false;

        for (String line : output.split("\\R")) {
            if (line.isBlank()) {
                if (worktreePath != null) {
                    worktrees.add(buildWorktreeSummary(repositoryRootPath, worktreePath, branch, headState, stale));
                }
                worktreePath = null;
                branch = null;
                headState = ProjectGitHeadState.BRANCH;
                stale = false;
                continue;
            }

            if (line.startsWith("worktree ")) {
                worktreePath = Path.of(line.substring("worktree ".length())).normalize().toAbsolutePath();
                continue;
            }
            if (line.startsWith("branch ")) {
                String branchRef = line.substring("branch ".length()).trim();
                branch = branchRef.startsWith("refs/heads/") ? branchRef.substring("refs/heads/".length()) : branchRef;
                continue;
            }
            if ("detached".equals(line)) {
                headState = ProjectGitHeadState.DETACHED;
                branch = null;
                continue;
            }
            if (line.startsWith("prunable")) {
                stale = true;
            }
        }

        if (worktreePath != null) {
            worktrees.add(buildWorktreeSummary(repositoryRootPath, worktreePath, branch, headState, stale));
        }
        return Collections.unmodifiableList(worktrees);
    }

    private ProjectWorktreeSummary buildWorktreeSummary(
            Path repositoryRootPath,
            Path worktreePath,
            String branch,
            ProjectGitHeadState headState,
            boolean stale) {
        boolean main = repositoryRootPath.equals(worktreePath);
        boolean pathExists = Files.exists(worktreePath);
        if (!pathExists) {
            stale = true;
        }

        String workingTreeState = "UNAVAILABLE";
        boolean hasUpstream = false;
        boolean hasUnpushedCommits = false;
        if (!stale) {
            workingTreeState = runGitCommand(worktreePath, false, "status", "--porcelain").isBlank() ? "CLEAN" : "DIRTY";
            if (headState == ProjectGitHeadState.BRANCH && branch != null) {
                String upstreamRef = runGitCommand(
                        worktreePath,
                        true,
                        "rev-parse",
                        "--abbrev-ref",
                        "--symbolic-full-name",
                        "@{upstream}");
                hasUpstream = !upstreamRef.isBlank();
                if (hasUpstream) {
                    String aheadCount = runGitCommand(worktreePath, true, "rev-list", "--count", "@{upstream}..HEAD");
                    hasUnpushedCommits = !aheadCount.isBlank() && Integer.parseInt(aheadCount) > 0;
                }
            }
        }

        return new ProjectWorktreeSummary(
                worktreePath.toString(),
                main,
                headState,
                branch,
                workingTreeState,
                hasUpstream,
                hasUnpushedCommits,
                stale);
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

    private void requireNonDetachedWorktreeState(ProjectRepositorySummary repositorySummary, String message) {
        if (repositorySummary.headState() == ProjectGitHeadState.DETACHED) {
            throw new IllegalStateException(message);
        }
    }

    private String sanitizeBranchName(String branchName) {
        String sanitized = branchName.replaceAll("[^A-Za-z0-9._-]+", "-");
        if (sanitized.isBlank()) {
            throw new IllegalArgumentException("Branch name cannot be converted into a filesystem-safe worktree path");
        }
        return sanitized;
    }
}
