# Tasks: bootstrap-frontend-admin-shell

## Implementation

- [x] Create the `frontend/` runnable Vite 8 + React 19 admin project foundation using Node 24 via `nvm` and the latest `bun`.
- [x] Add the first admin shell layout, route structure, and navigation model for the PC management console.
- [x] Implement visible minimum pages for dashboard, user management, role-permission management, software project management, ticket management, and kanban management.
- [x] Add the frontend data-access convention and API integration layer that directly connects to the current backend core.
- [x] Wire the minimum pages to current backend behavior so the admin frontend can display real data.
- [x] Add frontend structure that remains extensible for later enterprise modules without overbuilding the first shell.

## Testing

- [x] Verify the proposal artifacts and delta specs with `spec-driven.js verify`.
- [x] Run frontend checks appropriate to the initial stack, including install validation, build validation, and automated tests if introduced by the scaffold.
- [x] Verify the minimum admin pages render and the frontend can reach the backend data path in the local development setup.

## Verification

- [x] Confirm the frontend now satisfies the V1 requirement for a visible admin home page and visible minimum business pages on top of the existing backend core.
