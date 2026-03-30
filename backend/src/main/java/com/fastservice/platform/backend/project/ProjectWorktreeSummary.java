package com.fastservice.platform.backend.project;

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
