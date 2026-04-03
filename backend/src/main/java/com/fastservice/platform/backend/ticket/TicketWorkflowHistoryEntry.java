package com.fastservice.platform.backend.ticket;

record TicketWorkflowHistoryEntry(
        long id,
        String action,
        String fromState,
        String toState,
        long actorUserId,
        String actorDisplayName,
        Long previousAssigneeUserId,
        String previousAssigneeDisplayName,
        Long nextAssigneeUserId,
        String nextAssigneeDisplayName,
        String comment) {
}
