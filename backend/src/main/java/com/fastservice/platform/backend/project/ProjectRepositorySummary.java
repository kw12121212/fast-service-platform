package com.fastservice.platform.backend.project;

import java.util.List;

record ProjectRepositorySummary(
        String rootPath,
        ProjectGitHeadState headState,
        String branch,
        boolean dirty,
        String latestCommitSummary,
        List<String> availableBranches,
        List<ProjectGitCommitSummary> recentCommits) {

    String workingTreeState() {
        return dirty ? "DIRTY" : "CLEAN";
    }
}
