# Tasks: project-derived-app-verification

## Implementation

- [x] Finalize delta specs for the project-scoped derived-app verification contract, backend behavior, and frontend experience.
- [x] Add backend project-scoped verification orchestration that validates request boundaries and delegates to repository-owned validation entrypoints.
- [x] Add frontend Projects experience support for project-scoped verification, including request submission and visible restricted and outcome states.
- [x] Keep the first verification path scoped to project-triggered validation only, without silently adding upgrade support, solution-input planning, or unrelated lifecycle history behavior.

## Testing

- [x] Backend: `mvn -q test`
- [x] Frontend: `bun run test`
- [x] Frontend: `bun run build`
- [x] Frontend: `bun run lint`
- [x] Verify the project-scoped verification path still drives repository-owned validation behavior and backend-backed `/service/*` flows where feasible.

## Verification

- [x] Verify unbound projects and other restricted states do not expose normal verification execution.
- [x] Verify existing direct repository-owned verification commands remain valid and unchanged.
- [x] Verify scope remains within the first project-scoped verification milestone step.
