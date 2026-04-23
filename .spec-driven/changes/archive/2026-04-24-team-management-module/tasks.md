# Tasks: team-management-module

## Implementation

- [x] Add team, team_member, team_project_binding, team_role, and team_member_role table definitions to `backend/src/main/resources/sql/tables.sql`
- [x] Add team_service operations (createTeam, updateTeam, deleteTeam, listTeams, addMember, removeMember, listMembers, bindProject, unbindProject, listProjectTeams, assignTeamRole, removeTeamRole) to `backend/src/main/resources/sql/services.sql`
- [x] Implement `TeamServiceImpl` in `backend/src/main/java/com/fastservice/platform/backend/team/`
- [x] Create the management-module descriptor `docs/ai/management-modules/team-directory.management-module.json` using dynamic-form and dynamic-report components
- [x] Register `team-management` module in `docs/ai/module-registry.json` with dependencies on user-management and role-permission-management
- [x] Add team-management to relevant assembly profiles (baseline-v1, project-admin, project-repository, project-kanban) in `docs/ai/module-registry.json`
- [x] Add team-management to frontend module selection in `frontend/src/app/module-selection.ts`
- [x] Add team route entry and navigation item in `frontend/src/app/router.tsx` and `frontend/src/app/navigation.ts`
- [x] Add team demo data (sample teams, memberships, project bindings) to `backend/src/main/resources/sql/demo.sql`
- [x] Register the Lealone service router for `team_service` in the backend bootstrap

## Testing

- [x] Run `bun run lint --prefix frontend` — frontend lint and validation for the team module
- [x] Run `mvn test -f backend/pom.xml` — backend unit tests covering team CRUD, membership, project binding, and team roles
- [x] Run `bun run test --prefix frontend` — frontend unit tests for the team management page

## Verification

- [x] Verify implementation matches proposal scope
- [x] Run `./scripts/verify-backend.sh` — full backend verification including integration checks
- [x] Run `bun run build --prefix frontend` — frontend build succeeds with team module included
- [x] Run `bun run lint --prefix frontend` — frontend linting passes
