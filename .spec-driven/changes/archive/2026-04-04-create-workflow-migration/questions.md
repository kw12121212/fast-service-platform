# Questions: create-workflow-migration

## Open

<!-- No open questions -->

## Resolved

- [x] Q: Should the hand-written `TicketWorkflowService.java` be deleted or kept as fallback?
  Context: Determines whether we fully trust LLM generation or maintain a manual fallback.
  A: Delete it. Full replacement — no fallback.

- [x] Q: How should `KanbanStateMachine` state transition rules reach the LLM during workflow generation?
  Context: The LLM needs to know the allowed transitions to generate correct workflow logic.
  A: Register `KanbanStateMachine` as a Lealone SERVICE so `genWorkflowCode` includes its DDL as context automatically.

- [x] Q: What is the scope of this migration — ticket workflow only, or all workflow services?
  Context: Determines whether this is a one-off or establishes a platform-level convention.
  A: All workflow-enabled services going forward. Ticket is the first migration target.

- [x] Q: What happens if the LLM-generated JSON does not align with the frontend `WorkflowInstance` type?
  Context: The frontend must receive an identical contract; regeneration may produce different field names.
  A: Add a server-side adapter/converter — do not change the frontend.

- [x] Q: How is the generated class re-verified when schema changes require regeneration?
  Context: Without a verification gate, silent regression in generated code is possible.
  A: Integration tests are the verification gate. Delete the generated `.java`/`.class` files, restart, and re-run tests to confirm the new generation is correct.
