# Tasks: complete-rbac-management-baseline

## Implementation

- [x] Add delta specs for the minimum manageable RBAC backend and frontend baseline.
- [x] Extend the backend access-control service contract with the additional listing/query behaviors needed for RBAC management, including `listRolesForUser`.
- [x] Implement and test the backend RBAC query and assignment flows on top of the current identity model.
- [x] Upgrade the frontend role-permission page from manual role-id inspection to a backend-backed RBAC management page.
- [x] Keep the frontend data-access pattern consistent with the rest of the admin shell for RBAC reads and writes.

## Testing

- [x] Verify the proposal artifacts and delta specs with `spec-driven.js verify`.
- [x] Run `$HOME/.sdkman/candidates/maven/current/bin/mvn -q test` in `backend/`.
- [x] Run `bun run lint`, `bun run test`, and `bun run build` in `frontend/`.

## Verification

- [x] Confirm the platform now exposes a minimum manageable RBAC component baseline instead of a read-only role-id lookup page.
