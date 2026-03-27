# Design: complete-rbac-management-baseline

## Approach

Build the missing RBAC management baseline by extending the existing access-control service contract and wiring the frontend role-permission page to that fuller backend surface.
The change should follow the same pattern already used by the other minimum domains: backend-supported create and list flows, frontend-backed management views, and no frontend-only invented state.
The result should feel like a minimum reusable component baseline, not a one-off demo page.

## Key Decisions

- Keep the scope at the minimum management baseline instead of full RBAC maturity.
  Rationale: the platform needs a dependable reusable floor first, not an overbuilt authorization studio.
- Reuse the current access-control service area rather than creating a separate identity-management module.
  Rationale: the backend already establishes RBAC as part of the current minimum enterprise component core.
- Bring the frontend role page up to the same operational standard as the other minimum admin pages.
  Rationale: the current manual role-id lookup page is inconsistent with the rest of the baseline and too weak for platform reuse.
- Preserve the existing menu-level and function-level permission model.
  Rationale: the active backend spec already defines that granularity as part of the core contract.
- Use `listRolesForUser` as the first relationship-query direction.
  Rationale: user-centered role inspection is enough for the minimum management baseline and keeps the first RBAC query surface smaller than exposing both directions at once.

## Alternatives Considered

- Leave the frontend role page as a thin diagnostic page and only expand the backend.
  Rejected because the platform baseline is meant to provide reusable frontend and backend components together.
- Jump directly to a role-permission matrix or other advanced RBAC management UI.
  Rejected because that would add significant UX and relationship complexity before the minimum management baseline is complete.
- Postpone RBAC work and move to engineering-support components next.
  Rejected because identity and authorization are more central to enterprise component reuse than the currently unimplemented engineering-support area.
