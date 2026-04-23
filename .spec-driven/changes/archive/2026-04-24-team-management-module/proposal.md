# team-management-module

## What

Introduce a team management module that provides Team CRUD, team membership management, team-project binding, and team-scoped roles for human and AI members. This is implemented as a descriptor-driven management module reusing the existing dynamic form, dynamic report, and platform component pipeline.

## Why

This is the V2 foundation milestone. Every downstream agile milestone — work items, sprints, boards, dashboards, releases, and collaboration — depends on teams and people existing. The platform currently has individual users (`app_user`) and RBAC but no team or group organizational structure. Without teams, there is no way to assign work, plan sprints, or scope collaboration.

## Scope

**In scope:**
- Team entity with name, description, and status
- Team membership: add/remove users to/from teams with a member role
- Team-project binding: associate teams with one or more projects
- Team-scoped roles (Scrum Master, Product Owner, Developer, Observer) in a parallel `team_role` table, separate from platform RBAC
- Backend SQL tables, service operations, and Java implementation
- Descriptor-driven frontend module using dynamic form and dynamic report components
- Module registry entry for team management
- Demo data covering teams, memberships, and team-project bindings

**Out of scope:**
- User profile enhancements (avatar, extended fields) — deferred to `user-profile-baseline`
- AI employee model and API integration — deferred to `ai-employee-model` and `ai-api-integration-surface`
- Hierarchical team structures (team-of-teams, departments)
- Cross-organization or multi-tenant team sharing
- Capacity planning or availability tracking
- Modifications to existing RBAC tables or behavior

## Unchanged Behavior

- Existing `app_user` CRUD and RBAC management workflows continue unchanged.
- Existing project, ticket, and kanban modules continue unchanged.
- The descriptor-driven generation pipeline is not modified — team management is a consumer of that pipeline, not an extension of it.
- Module selection and assembly profiles for existing modules continue unchanged.
