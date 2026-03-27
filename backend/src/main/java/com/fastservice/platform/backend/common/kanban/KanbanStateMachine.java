package com.fastservice.platform.backend.common.kanban;

public final class KanbanStateMachine {

    private KanbanStateMachine() {
    }

    public static void ensureTransition(String currentState, String targetState) {
        KanbanState current = KanbanState.from(currentState);
        KanbanState target = KanbanState.from(targetState);
        if (!isAllowed(current, target)) {
            throw new IllegalArgumentException("Unsupported kanban transition: " + current + " -> " + target);
        }
    }

    public static boolean isAllowed(KanbanState current, KanbanState target) {
        if (current == target) {
            return true;
        }
        return switch (current) {
        case TODO -> target == KanbanState.IN_PROGRESS;
        case IN_PROGRESS -> target == KanbanState.DONE;
        case DONE -> false;
        };
    }
}
