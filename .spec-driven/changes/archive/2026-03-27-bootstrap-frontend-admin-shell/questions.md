# Questions: bootstrap-frontend-admin-shell

## Open

<!-- No open questions -->

## Resolved

- [x] Q: Should the first frontend change target PC admin only or include mobile H5?
  Context: The UI form factor changes routing, layout, and interaction structure.
  A: Limit this change to the PC admin frontend.
- [x] Q: Should the first frontend only provide a shell or make the minimum business pages visible?
  Context: This determines whether the change is merely structural or truly satisfies the V1 deliverable path.
  A: The minimum business pages must be visible in this change.
- [x] Q: Should the first frontend integrate directly with the backend or rely on mocks first?
  Context: This affects whether the first frontend establishes a real end-to-end baseline.
  A: Integrate directly with the current backend.
- [x] Q: What frontend data-access level should this change use?
  Context: The repository needs a stable pattern for AI-driven frontend evolution.
  A: Use a more complete data-fetching convention instead of ad hoc page-level requests.
- [x] Q: What local frontend runtime and package-manager constraints apply?
  Context: The scaffold should match the agreed local environment before implementation starts.
  A: Use Node 24 via `nvm` and the latest `bun`.
