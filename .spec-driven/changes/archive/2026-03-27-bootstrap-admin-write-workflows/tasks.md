# Tasks: bootstrap-admin-write-workflows

## Implementation

- [x] Add delta specs that define minimum admin write workflows and the updated V1 deliverable contract.
- [x] Extend the frontend data-access layer with a consistent mutation convention for supported backend write actions.
- [x] Add user and project creation workflows to the existing admin pages with visible submission, success, and failure states.
- [x] Add kanban and ticket creation workflows that respect the current project-scoped backend contracts.
- [x] Add ticket state-transition controls that follow the current minimal kanban flow and refresh backend-backed page data after successful changes.
- [x] Preserve current read behavior for unsupported write areas such as role-permission management.

## Testing

- [x] Verify the proposal artifacts and delta specs with `spec-driven.js verify`.
- [x] Run `bun run lint`, `bun run test`, and `bun run build` in `frontend/`.
- [x] Validate the supported write workflows against the running backend `/service/*` paths.

## Verification

- [x] Confirm the first admin baseline is operable for the supported domains instead of limited to read-only inspection.
- [x] Confirm the V1 generation contract now reflects baseline management workflows for the current core entities.
