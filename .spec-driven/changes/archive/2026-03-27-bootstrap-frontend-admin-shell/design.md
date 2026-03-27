# Design: bootstrap-frontend-admin-shell

## Approach

Create a frontend admin application that is immediately runnable and useful, but still narrow enough to match the current V1 backend baseline.
The frontend will prioritize a clear admin-shell structure first, then map each required V1 domain into a visible page backed by the current backend services.
The frontend implementation will use a stronger data-access convention than page-local fetch calls so that AI can later extend the application without rewriting the integration approach.

## Key Decisions

- Limit the first frontend change to PC admin experience only.
  Rationale: V1 already targets enterprise internal management applications, and the backend baseline is aligned to a desktop-style admin console.
- Build visible minimum business pages instead of only a generic shell.
  Rationale: a shell without the required pages would not satisfy the V1 minimum deliverable goal.
- Directly integrate with the current backend instead of using frontend-only mocks.
  Rationale: this change should establish the first real end-to-end path across backend and frontend.
- Use a more complete data-fetching convention rather than scattered request code in route components.
  Rationale: AI-driven iteration benefits from a predictable data layer boundary and reusable query patterns.
- Keep route, page, layout, and data-access concerns separated.
  Rationale: the repository explicitly values AI-friendly structure and low hidden coupling.
- Use Node 24 through `nvm` and the latest `bun` as the package manager for frontend bootstrap.
  Rationale: the user has already fixed the local toolchain direction, and the frontend scaffold should match that environment.
- Preserve a practical admin-first visual language rather than over-designing the first shell.
  Rationale: the first frontend needs to be credible, maintainable, and easy for AI to extend before it needs a broader design system.

## Alternatives Considered

- Create only the frontend build shell and defer all business pages.
  Rejected because the user explicitly wants the minimum business pages to be visible in this change.
- Use mocks for the first frontend milestone.
  Rejected because it would delay the first true backend-frontend integration path.
- Add mobile H5 support in the same change.
  Rejected because that would expand the UI and navigation model beyond the current V1 backend baseline.
- Keep data loading local to each page.
  Rejected because it would work against the goal of AI-friendly consistency and later extension.
