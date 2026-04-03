# Tasks: project-derived-app-assembly

## Implementation

- [x] Finalize delta specs for the project-scoped derived-app assembly contract, backend behavior, and frontend experience.
- [x] Add backend project-scoped assembly orchestration that accepts `app-manifest` input, validates request boundaries, and delegates to repository-owned assembly tooling.
- [x] Add frontend Projects experience support for project-scoped assembly, including request submission and visible restricted and outcome states.
- [x] Keep the first assembly path scoped to project-triggered assembly only, without silently adding verification, smoke, upgrade, or solution-input planning behavior.

## Testing

- [x] Backend: `mvn -q test`
- [x] Frontend: `bun run test`
- [x] Frontend: `bun run build`
- [x] Frontend: `bun run lint`
- [x] Verify the project-scoped assembly path still drives repository-owned tooling and backend-backed `/service/*` flows rather than local-only mocks where feasible.

## Verification

- [x] Verify unbound projects and other restricted states do not expose normal assembly execution.
- [x] Verify existing direct `./scripts/platform-tool.sh assembly scaffold ...` usage remains valid and unchanged.
- [x] Verify scope remains within the first project-scoped assembly milestone step.
