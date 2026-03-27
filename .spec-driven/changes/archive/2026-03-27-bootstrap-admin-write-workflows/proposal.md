# bootstrap-admin-write-workflows

## What

Evolve the first admin frontend from a read-only baseline into a minimally operable management console for the current V1 enterprise domains.
Define and implement the first supported admin write workflows so contributors can create core records and advance ticket delivery state through the existing backend service surface.

## Why

The repository already proves that the frontend can render the minimum V1 pages against the current backend, but the baseline still behaves mostly like a live demo.
Without write workflows, the platform does not yet demonstrate the management behavior that AI is expected to assemble into a usable internal application.
This change also establishes a reusable mutation pattern for future AI-generated admin pages, which is a better next step than expanding into new component areas before the current baseline is operable.

## Scope

In scope:
- Define minimum admin write workflows for the current V1 pages that already map cleanly to backend service operations.
- Support creating users, software projects, kanban boards, and tickets from the admin frontend.
- Support moving tickets through the current minimal ticket state flow from the admin frontend.
- Extend the frontend data-access convention so supported write actions use a consistent request, feedback, and refresh pattern instead of page-specific ad hoc handling.
- Keep the affected pages directly backed by the current backend `/service/*` paths.
- Update the V1 generation contract so the baseline deliverables are evaluated as manageable workflows rather than read-only views.

Out of scope:
- Full CRUD maturity such as edit, delete, archive, restore, bulk operations, or inline table editing.
- New business domains or new platform component areas.
- Git repository management, worktree management, code merge support, or sandbox-environment implementation.
- Role and permission authoring workflows or user-role assignment UX in this change.
- Introducing new external software libraries beyond the existing project boundary.

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):
- The platform remains focused on AI-generated monolithic enterprise internal management applications.
- The existing minimum V1 domain set remains the platform baseline.
- The current backend core stays the source of truth for admin data instead of falling back to frontend-only mock state.
