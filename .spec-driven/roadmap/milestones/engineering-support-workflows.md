# Engineering Support Workflows

## Goal

Close the core project-scoped engineering workflow gaps around real local repositories so the platform can safely manage repository state, parallel work branches, merges, and sandbox execution from within the product boundary.

## In Scope

- Project-scoped worktree management for bound repositories
- Project-scoped merge support for managed linked worktrees
- Project-scoped sandbox image and container management

## Out of Scope

- A general-purpose Git client unrelated to bound project workflows
- Remote deployment orchestration or CI/CD management
- Runtime environments beyond the repository-owned sandbox path

## Done Criteria

- Bound projects can expose repository state, managed worktrees, merge support, and sandbox state through repository-owned platform workflows.
- Contributors can verify that these workflows are project-scoped rather than generic standalone Git utilities.
- All planned changes in this milestone are archived.

## Planned Changes

- `project-worktree-management` - Declared: complete - Project-scoped management of linked Git worktrees for bound repositories
- `project-merge-support` - Declared: complete - Project-scoped merge support for managed linked worktree branches
- `project-sandbox-environment` - Declared: complete - Project-scoped sandbox image and container management through podman

## Dependencies

- These workflows depend on repository binding and local Git state remaining repository-owned platform concerns.
- Sandbox execution depends on host `podman` availability and repository-owned initialization entrypoints.

## Risks

- These workflows operate on real local repositories and runtime environments, so safety restrictions and failure visibility must remain explicit.
- Sandbox support depends on host `podman` availability and narrow runtime validation boundaries.

## Status

- Declared: complete

## Notes

- This milestone established the engineering-support baseline that later project-scoped lifecycle work builds on.
