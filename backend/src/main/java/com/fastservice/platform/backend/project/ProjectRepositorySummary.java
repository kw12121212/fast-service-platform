package com.fastservice.platform.backend.project;

record ProjectRepositorySummary(String rootPath, String branch, boolean dirty, String latestCommitSummary) {

    String workingTreeState() {
        return dirty ? "DIRTY" : "CLEAN";
    }
}
