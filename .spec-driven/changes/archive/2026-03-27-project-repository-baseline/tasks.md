# Tasks: project-repository-baseline

## Implementation

- [x] Extend the project-domain proposal and delta specs to define optional project-to-repository binding behavior.
- [x] Add backend project repository binding behavior and repository summary read behavior through backend-backed project workflows.
- [x] Add frontend project repository binding and repository summary presentation in the existing Projects experience.
- [x] Preserve clear empty, success, and failure states for projects with and without repository bindings.

## Testing

- [x] Backend: `mvn -q test`
- [x] Frontend: `bun run test`
- [x] Frontend: `bun run build`
- [x] Frontend: `bun run lint`
- [x] Verify repository binding flows through backend-backed `/service/*` paths instead of local-only mocks where feasible.

## Verification

- [x] Verify the change stays within project-to-repository baseline scope and does not silently expand into worktree, merge, sandbox, or remote-hosting features.
- [x] Verify a project can remain valid without a repository binding and that existing project, ticket, kanban, user, and RBAC flows still behave the same.
