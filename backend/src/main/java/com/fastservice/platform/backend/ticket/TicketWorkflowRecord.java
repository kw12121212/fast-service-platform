package com.fastservice.platform.backend.ticket;

record TicketWorkflowRecord(
        long ticketId,
        String ticketKey,
        String title,
        String state,
        long assigneeUserId,
        String assigneeUsername,
        String assigneeDisplayName) {
}
