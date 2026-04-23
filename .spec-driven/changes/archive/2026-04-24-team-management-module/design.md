# Design: team-management-module

## Approach

Implement team management as a descriptor-driven management module following the established pattern used by `department-directory`, `leave-request`, and other existing modules.

**Backend:** Add new SQL tables (`team`, `team_member`, `team_project_binding`, `team_role`, `team_member_role`) and a new `team_service` to `services.sql`. Implement `TeamServiceImpl` in a new `team/` domain package. The service handles team CRUD, membership management, team-project binding, and team-scoped role assignment.

**Frontend:** Create a management-module descriptor (`team-directory.management-module.json`) that uses `dynamic-form` for team create/edit and `dynamic-report` for the team list. The descriptor integrates through existing platform slots (admin routes, navigation).

**Module registry:** Register `team-management` as an optional business module depending on `user-management` and `role-permission-management`.

## Key Decisions

1. **Descriptor-driven over hand-coded** — Team management uses the existing descriptor-driven pipeline with dynamic form/report components. This follows the established repository pattern, reduces custom code, and keeps the team module consistent with other generated modules.

2. **Parallel team_role table** — Team-scoped roles (Scrum Master, Product Owner, Developer, Observer) live in a dedicated `team_role` / `team_member_role` table structure, separate from the platform RBAC `app_role` / `app_permission` tables. This avoids scope creep in the RBAC model and keeps team roles a team-concern.

3. **User-profile-baseline kept separate** — This change references `app_user` as-is (id, username, display_name, email). Avatar and extended profile fields are deferred to `user-profile-baseline`.

4. **AI employee deferred** — The `team_member` table references `user_id` from `app_user`. AI employees will be added later via `ai-employee-model`, which can extend the membership model without restructuring the team tables.

## Alternatives Considered

- **Hand-coded module** — Would provide more flexibility for complex team UI (member management pages, role assignment workflows) but would create a parallel implementation pattern and more code to maintain. Rejected in favor of consistency with the descriptor-driven pipeline.

- **Extend existing RBAC with team scope** — Would reuse the existing `app_role` / `app_permission` infrastructure by adding a scope dimension. Rejected because team roles serve a different purpose (project-sprint collaboration roles vs. platform authorization) and would complicate the clean RBAC model.

- **Single team_member table with embedded role** — Would store role as a column on `team_member` rather than a separate `team_member_role` join table. Rejected because members may hold multiple team roles simultaneously (e.g., Developer + Scrum Master), and the join table model supports that without schema changes.
