package com.fastservice.platform.backend.project;

import java.util.List;

record ProjectWorktreeSummary(
        String path,
        boolean main,
        ProjectGitHeadState headState,
        String branch,
        String workingTreeState,
        boolean hasUpstream,
        boolean hasUnpushedCommits,
        boolean stale) {

    boolean dirty() {
        return "DIRTY".equals(workingTreeState);
    }

    boolean deletionAllowed() {
        return !main && !stale && !dirty() && hasUpstream && !hasUnpushedCommits;
    }

    List<String> mergeTargetBranches(List<String> availableBranches) {
        if (main || stale || headState != ProjectGitHeadState.BRANCH || branch == null) {
            return List.of();
        }

        return availableBranches.stream()
                .filter(candidate -> !candidate.equals(branch))
                .toList();
    }

    boolean mergeAllowed(List<String> availableBranches) {
        return mergeRestriction(availableBranches) == null;
    }

    String mergeRestriction(List<String> availableBranches) {
        if (main) {
            return "Main repository worktree cannot be used as a merge source";
        }
        if (stale) {
            return "Stale worktree records cannot be used as a merge source";
        }
        if (headState != ProjectGitHeadState.BRANCH || branch == null) {
            return "Detached HEAD worktree cannot be used as a merge source";
        }
        if (dirty()) {
            return "Worktree has uncommitted changes";
        }
        if (mergeTargetBranches(availableBranches).isEmpty()) {
            return "No merge target branches are available for this worktree";
        }
        return null;
    }

    String deletionRestriction() {
        if (main) {
            return "Main repository worktree cannot be removed from the project view";
        }
        if (stale) {
            return "Stale worktree records must be pruned before removal";
        }
        if (dirty()) {
            return "Worktree has uncommitted changes";
        }
        if (!hasUpstream) {
            return "Worktree has no upstream branch";
        }
        if (hasUnpushedCommits) {
            return "Worktree has unpushed commits";
        }
        return null;
    }
}
