# complete-rbac-management-baseline

## What

Complete the first manageable identity and RBAC component baseline across backend and frontend.
Add the minimum role, permission, and user-role management behaviors needed to make the current role-permission area a real reusable enterprise component rather than a read-only placeholder.

## Why

The repository now has a usable baseline for users, projects, kanban boards, and tickets, but the identity and RBAC area is still incomplete.
The backend already exposes some RBAC write operations, yet it does not provide a balanced management surface, and the frontend still depends on manually entering a role id to inspect permissions.
For an AI-oriented enterprise component platform, identity and authorization are core reusable building blocks. Leaving RBAC weaker than the other minimum domains reduces the platform's reuse value.

## Scope

In scope:
- Extend the backend access-control component with the minimum additional query surface needed for role and permission management.
- Add `listRolesForUser` as the first relationship-query view for the minimum RBAC baseline.
- Keep role creation, permission creation, permission-to-role assignment, and role-to-user assignment as supported baseline management behaviors.
- Replace the current frontend role page's manual role-id inspection pattern with a minimum manageable RBAC page.
- Make the frontend RBAC page show the available roles and permissions through current backend-backed workflows instead of relying on unsupported ad hoc assumptions.
- Keep RBAC aligned with the current menu-level and function-level permission model.

Out of scope:
- A full policy engine, conditional authorization rules, or organization-level access modeling.
- Advanced RBAC visualization such as matrix editors, bulk assignment UX, or complex filtering.
- New enterprise domains outside the current identity and authorization area.
- Engineering-support component implementation.

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):
- The current minimum enterprise-management domain set remains unchanged.
- The RBAC baseline continues to use menu-level and function-level permission granularity.
- The rest of the admin shell remains directly backed by the current backend core.
