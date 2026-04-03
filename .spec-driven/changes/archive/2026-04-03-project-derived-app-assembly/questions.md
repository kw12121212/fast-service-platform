# Questions: project-derived-app-assembly

## Open

<!-- No open questions -->

## Resolved

- [x] Q: Should the first project-scoped assembly path allow any explicit absolute output directory, or only the bound repository root or a repository-derived location?
  Context: This determines how tightly the first lifecycle path is coupled to the current bound repository and how much output-location validation the backend must enforce.
  A: Allow any explicit absolute output directory. The first lifecycle step should preserve the existing repository-owned assembly contract and avoid inventing implicit project-derived path rules.

- [x] Q: Should the first project-scoped assembly path target only the main bound repository context, or should managed linked worktrees also be valid sources in the initial release?
  Context: This changes how the project lifecycle surface composes with existing worktree-management behavior.
  A: Limit the first release to the bound project's main repository context. Managed linked worktrees are left for later lifecycle expansion.

- [x] Q: Does the first project-scoped assembly path need persistent run history, or is a current request and latest outcome surface sufficient for the initial milestone step?
  Context: This affects how much project-scoped lifecycle state the first implementation must store and expose.
  A: The first release only needs the current request and latest outcome surface. Persistent run history is out of scope.
