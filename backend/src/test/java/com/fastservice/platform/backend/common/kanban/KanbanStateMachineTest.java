package com.fastservice.platform.backend.common.kanban;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class KanbanStateMachineTest {

    @Test
    void allowsMinimalForwardFlow() {
        assertTrue(KanbanStateMachine.isAllowed(KanbanState.TODO, KanbanState.IN_PROGRESS));
        assertTrue(KanbanStateMachine.isAllowed(KanbanState.IN_PROGRESS, KanbanState.DONE));
    }

    @Test
    void rejectsBackwardFlow() {
        assertFalse(KanbanStateMachine.isAllowed(KanbanState.DONE, KanbanState.IN_PROGRESS));
        assertThrows(IllegalArgumentException.class, () -> KanbanStateMachine.ensureTransition("DONE", "TODO"));
    }
}
