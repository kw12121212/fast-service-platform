package com.fastservice.platform.backend.project;

record ProjectWorktreeSandboxRecord(
        String initImageScriptPathOverride,
        String initProjectScriptPathOverride,
        String imageStatus,
        String imageFailureMessage,
        String containerStatus,
        String containerFailureMessage) {
}
