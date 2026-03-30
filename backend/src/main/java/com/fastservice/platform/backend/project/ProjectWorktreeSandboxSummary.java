package com.fastservice.platform.backend.project;

record ProjectWorktreeSandboxSummary(
        boolean supported,
        String restriction,
        String imageStatus,
        String imageReference,
        String imageFailureMessage,
        String imageInitScriptPath,
        String imageInitScriptSource,
        boolean imageActionAllowed,
        String imageActionRestriction,
        String containerStatus,
        String containerName,
        String containerFailureMessage,
        String projectInitScriptPath,
        String projectInitScriptSource,
        boolean containerCreateAllowed,
        String containerCreateRestriction,
        boolean containerDeleteAllowed,
        String containerDeleteRestriction) {
}
