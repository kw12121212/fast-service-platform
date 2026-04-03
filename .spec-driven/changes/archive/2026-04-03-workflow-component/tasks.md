# Tasks: workflow-component

## Implementation

- [x] Add minimal backend ticket-workflow support for workflow detail, bounded action execution, reassignment, and history persistence needed by the real example
- [x] Define `WorkflowDescriptor`, `WorkflowActionDescriptor`, and workflow-instance input contracts for a single-record workflow panel
- [x] Specify the V1 visible workflow capabilities: current state, assignee metadata, bounded action list including `reassign`, required comment input, and history list
- [x] Specify that action execution is handed off through a caller-owned callback instead of being performed inside the component
- [x] Specify that workflow mutation outcomes use the existing frontend mutation feedback convention
- [x] Register the workflow component as a reusable platform capability in the relevant platform spec areas
- [x] Provide a minimal real workflow example page or example area in the current runnable admin frontend

## Testing

- [x] Add backend tests covering workflow detail, bounded actions, required comment handling, and reassignment history
- [x] Add frontend tests covering descriptor rendering, bounded action visibility, comment handoff, and caller-owned action execution
- [x] Add frontend tests covering workflow error display through the existing mutation feedback convention
- [x] Run `mvn -q test`
- [x] Run `bun run test`
- [x] Run `bun run lint`
- [x] Run `bun run build`

## Verification

- [x] Verify the workflow component remains render-only and does not fetch backend data itself
- [x] Verify the descriptor stays within the V1 single-record workflow boundary rather than expanding into a generic BPM engine
- [x] Verify the new workflow capability is visible as a reusable platform component rather than a page-private implementation
- [x] Verify the current runnable admin frontend exposes a real workflow example backed by the existing backend path
