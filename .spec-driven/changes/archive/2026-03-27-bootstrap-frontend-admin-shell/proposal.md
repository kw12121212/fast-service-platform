# bootstrap-frontend-admin-shell

## What

Bootstrap the frontend workspace as the first runnable PC enterprise admin application.
Create a Vite 8 + React 19 + shadcn/ui + Tailwind CSS 4 frontend that directly connects to the current backend core and exposes the minimum V1 admin pages through a coherent admin shell.

## Why

The repository now has a runnable backend core, but it still lacks the frontend application that makes the V1 minimum deliverables visible and usable.
Without the admin frontend, the platform cannot demonstrate the end-to-end enterprise-management baseline that AI is expected to generate.

## Scope

In scope:
- Create the first real frontend project structure under `frontend/`.
- Bootstrap a runnable PC admin application using the agreed frontend stack.
- Add the admin shell structure with layout, navigation, and route organization.
- Provide visible minimum pages for dashboard, user management, role-permission management, software project management, ticket management, and kanban management.
- Directly integrate the frontend with the current backend core.
- Establish a more complete frontend data-fetching convention instead of ad hoc page-level requests.
- Keep the frontend structure AI-friendly, with clear boundaries for routes, UI components, and data access.

Out of scope:
- Mobile H5 frontend.
- Public site, marketing site, or portal-oriented UI.
- Full design-system maturity beyond what the first admin shell needs.
- Advanced frontend state workflows not required by the V1 minimum pages.
- Frontend support for later Git/worktree/merge/sandbox modules beyond reserved navigation space if needed.

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):
- The platform remains focused on monolithic enterprise internal management applications.
- Human input remains limited to natural language with optional prototype images and UI-only reference sites.
- The backend core and its V1 minimum enterprise domains remain the source of truth for the first end-to-end baseline.
