# Tasks: bootstrap-backend-enterprise-component-core

## Implementation

- [x] Create the `backend/` single-module Maven Java 25 project foundation aligned with Lealone-Platform startup and dependency conventions.
- [x] Add backend schema and service-definition inputs for user, role, permission, software project, ticket, and kanban domains.
- [x] Implement combined menu-level and function-level authorization in the RBAC baseline.
- [x] Implement the minimum project-ticket and kanban-ticket domain relationships with minimal kanban state flow.
- [x] Implement the first backend component services required to support the V1 minimum enterprise-management baseline.
- [x] Add optional demo-data support using SQL and Java initialization together.
- [x] Add reserved backend structure for later Git/worktree/merge/sandbox component expansion without implementing those features yet.

## Testing

- [x] Verify the proposal artifacts and delta specs with `spec-driven.js verify`.
- [x] Run backend checks appropriate to the initial stack, including build validation plus unit, service, and integration tests.
- [x] Verify demo data and core services work from a clean backend startup.

## Verification

- [x] Confirm the backend core satisfies the V1 minimum deliverables needed before frontend scaffold work starts.
