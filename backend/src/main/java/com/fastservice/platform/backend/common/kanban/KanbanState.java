package com.fastservice.platform.backend.common.kanban;

public enum KanbanState {
    TODO,
    IN_PROGRESS,
    DONE;

    public static KanbanState from(String value) {
        return KanbanState.valueOf(value.trim().toUpperCase());
    }
}
