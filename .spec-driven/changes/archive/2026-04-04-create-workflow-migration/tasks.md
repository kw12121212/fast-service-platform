# Tasks: create-workflow-migration

## Implementation

- [x] Add `KanbanStateMachineServiceImpl.java` adapter class delegating to `KanbanStateMachine` static methods
- [x] Add `CREATE SERVICE kanban_state_machine_service (isTransitionAllowed, ensureTransition)` to `services.sql` before `ticket_service`
- [x] Add `CREATE WORKFLOW ticket_workflow_service (getWorkflow, executeWorkflowAction)` to `services.sql` before `ticket_service`
- [x] Remove `getWorkflow` and `executeWorkflowAction` from `ticket_service` DDL in `services.sql`
- [x] Remove `getWorkflow`, `getworkflow`, `executeWorkflowAction`, `executeworkflowaction` methods from `TicketServiceImpl.java`
- [x] Remove `workflowService` field and import from `TicketServiceImpl.java`
- [x] Delete `TicketWorkflowService.java`
- [x] Trigger backend startup with `LLM_PROVIDER` configured to generate `TicketWorkflowServiceImpl`; inspect generated code
- [x] If generated JSON shape does not match `WorkflowInstance`, write `TicketWorkflowJsonAdapter.java` and wire it into the generated service
- [x] Update `frontend/src/lib/api/hooks.ts`: change `ticket_service/getWorkflow` → `ticket_workflow_service/getWorkflow` and `ticket_service/executeWorkflowAction` → `ticket_workflow_service/executeWorkflowAction`
- [x] Update `workflow-component.md` spec to add platform convention: backend workflow services MUST be defined via `CREATE WORKFLOW` DDL

## Testing

- [x] Write integration test asserting `ticket_workflow_service/getWorkflow` returns JSON matching `WorkflowInstance` shape (ticketId, ticketKey, title, state, assignee, availableActions, history)
- [x] Write integration test asserting valid state transitions succeed (TODO→IN_PROGRESS, IN_PROGRESS→DONE)
- [x] Write integration test asserting invalid transitions are rejected with an error
- [x] Write integration test asserting `executeWorkflowAction` with blank comment is rejected
- [x] Write integration test asserting reassign action requires `assigneeUserId`
- [x] Backend lint and build pass
- [x] Frontend lint and type-check pass

## Verification

- [x] `ticket_service` DDL no longer contains `getWorkflow` or `executeWorkflowAction`
- [x] `TicketWorkflowService.java` no longer exists in the repository
- [x] `ticket_workflow_service` is registered as `CREATE WORKFLOW` in `services.sql`
- [x] `kanban_state_machine_service` is registered before `ticket_workflow_service` in `services.sql`
- [x] All workflow integration tests pass against the generated implementation
- [x] Frontend tickets page and workflow panel function correctly end-to-end
- [x] `workflow-component.md` spec reflects the `CREATE WORKFLOW` backend convention
