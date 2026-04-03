# Tasks: project-derived-app-upgrade-support

## Implementation

- [x] Finalize delta specs for project-scoped derived-app upgrade support behavior in the backend and frontend.
- [x] Add backend project-scoped upgrade support orchestration that exposes availability, validates request boundaries, and delegates target lookup, advisory review, upgrade evaluation, and `upgrade-execute --dry-run` to repository-owned upgrade entrypoints.
- [x] Add frontend Projects experience support for project-scoped upgrade support, including supported target visibility, advisory/evaluation/dry-run request submission, and visible restricted and outcome states.
- [x] Keep the first project-scoped upgrade support path scoped to upgrade readiness and dry-run planning, without silently adding unrelated lifecycle history, solution-input planning, or broad project-local non-dry-run upgrade execution behavior.

## Testing

- [x] Backend: `mvn -q test`
- [x] Frontend: `bun run test`
- [x] Frontend: `bun run build`
- [x] Frontend: `bun run lint`
- [x] Verify the project-scoped upgrade support path still drives repository-owned upgrade behavior and backend-backed `/service/*` flows where feasible.

## Verification

- [x] Verify unbound projects and other restricted states do not expose normal upgrade support execution.
- [x] Verify existing direct repository-owned upgrade commands remain valid and unchanged.
- [x] Verify scope remains within the first project-scoped upgrade-support milestone step, including `upgrade-execute --dry-run` but excluding non-dry-run execution.
