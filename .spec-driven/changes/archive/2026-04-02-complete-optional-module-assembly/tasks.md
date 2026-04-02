# Tasks: complete-optional-module-assembly

## Implementation

- [x] Add `module-selection.ts` to the frontend source with a typed constant for project, ticket, and kanban flags; default all three to true (baseline-v1)
- [x] Update `router.tsx` to conditionally include the projects, tickets, and kanban routes based on `moduleSelection` flags
- [x] Update `navigation.ts` to conditionally include the delivery-management nav items based on `moduleSelection` flags
- [x] Split `tables.sql` into a core section and per-module sections (software_project, kanban_board, ticket)
- [x] Split `services.sql` into a core section and per-module service blocks (project_service, kanban_service, ticket_service)
- [x] Update `DemoDataSupport.java` to skip instantiation of module-specific service impls when their flag is not active
- [x] Update the assembly tooling to emit a `module-selection.ts` with the correct per-module booleans based on the selected profile from the app manifest
- [x] Update the assembly tooling to emit only the relevant SQL sections for the selected profile
- [x] Add a `core-admin` profile assembly fixture to the compatibility suite inputs

## Testing

- [x] Lint passes (frontend and backend)
- [x] Frontend unit tests pass with full-module config (baseline-v1)
- [x] Frontend unit tests pass with core-only config (no project/ticket/kanban routes or nav items rendered)
- [x] Backend unit tests pass
- [x] Compatibility suite passes for baseline-v1 profile (full module set, no regression)
- [x] Compatibility suite passes for core-admin profile (omitted-module routes, nav, tables, services absent from output)

## Verification

- [x] Verify the generated `module-selection.ts` for core-admin profile contains `project: false`, `ticket: false`, `kanban: false`
- [x] Verify the router for a core-admin assembly exposes no `/projects`, `/tickets`, or `/kanban` routes
- [x] Verify the nav for a core-admin assembly contains no project, ticket, or kanban items
- [x] Verify the emitted SQL for a core-admin assembly contains no `software_project`, `kanban_board`, or `ticket` table definitions
- [x] Verify the emitted SQL for a core-admin assembly contains no `project_service`, `kanban_service`, or `ticket_service` registrations
- [x] Verify the baseline-v1 assembly output is unchanged (all routes, nav, tables, services present)
- [x] Verify implementation matches proposal scope — no runtime toggling introduced
