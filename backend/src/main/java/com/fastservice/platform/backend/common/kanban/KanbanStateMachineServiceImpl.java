package com.fastservice.platform.backend.common.kanban;

public class KanbanStateMachineServiceImpl {

    public boolean istransitionallowed(String fromState, String toState) {
        return isTransitionAllowed(fromState, toState);
    }

    public void ensuretransition(String fromState, String toState) {
        ensureTransition(fromState, toState);
    }

    public boolean isTransitionAllowed(String fromState, String toState) {
        KanbanState from = KanbanState.from(fromState);
        KanbanState to = KanbanState.from(toState);
        return KanbanStateMachine.isAllowed(from, to);
    }

    public void ensureTransition(String fromState, String toState) {
        KanbanStateMachine.ensureTransition(fromState, toState);
    }
}
