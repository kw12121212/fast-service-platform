# Design: bootstrap-admin-write-workflows

## Approach

Add the first mutation-capable admin workflows on top of the existing backend-connected frontend baseline.
The change will stay narrow: it will only cover write behaviors that fit the current backend contracts and page structure, and it will use a consistent frontend mutation pattern so later AI-generated pages can reuse the same operational shape.
The admin experience should become operable without trying to solve full application authoring in one step.

## Key Decisions

- Prioritize write workflows over adding new platform domains.
  Rationale: the current baseline already spans the minimum V1 domains, but it is still weak as a usable management console.
- Limit the change to workflows that already align with the current backend service surface.
  Rationale: this reduces backend expansion risk and keeps the proposal grounded in the repository's present implementation reality.
- Treat this as a frontend-led operability change, not a full backend capability expansion.
  Rationale: the backend already exposes create and move operations for the main delivery entities, so the next gap is making those operations usable from the admin shell.
- Keep role and permission management read-focused in this iteration.
  Rationale: the current role page only exposes permission lookup by role id, and expanding into role authoring or assignment would widen scope beyond the cleanest first write-workflow milestone.
- Standardize post-submit behavior around explicit backend refresh and visible success or failure feedback.
  Rationale: that pattern is easier for AI to extend safely than optimistic local state reconciliation across multiple related resources.
- Preserve the existing AI-friendly page, component, and API-boundary layout.
  Rationale: the repository explicitly values clear structure and low hidden coupling.

## Alternatives Considered

- Start with the devops component set instead of improving the current admin baseline.
  Rejected because the existing V1 shell is still mostly read-only, so adding higher-order platform modules now would stack new scope on top of an underpowered baseline.
- Implement full CRUD for every minimum V1 page.
  Rejected because edit, delete, bulk operations, and recovery flows would add a large amount of behavior before the first write pattern is proven.
- Include role creation, permission creation, and user-role assignment in the same change.
  Rejected because the current page and backend read model do not yet provide a clean management flow for those relationships.
- Rely on local optimistic updates after mutations.
  Rejected because explicit backend refresh keeps the source of truth visible and avoids fragile cross-page client-side state bookkeeping in the first write-capable milestone.
