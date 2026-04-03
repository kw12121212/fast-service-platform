package com.fastservice.platform.backend.ticket;

enum TicketWorkflowAction {
    SUBMIT,
    APPROVE,
    REJECT,
    REASSIGN;

    static TicketWorkflowAction from(String raw) {
        return TicketWorkflowAction.valueOf(raw.trim().toUpperCase());
    }
}
