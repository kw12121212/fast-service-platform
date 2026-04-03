# Engineering Support Workflows

## Goal

Close the core project-scoped engineering workflow gaps around real local repositories so the platform can safely manage repository state, parallel work branches, merges, and sandbox execution from within the product boundary.

## Done Criteria

- Bound projects can expose repository state, managed worktrees, merge support, and sandbox state through repository-owned platform workflows.
- Contributors can verify that these workflows are project-scoped rather than generic standalone Git utilities.
- All planned changes in this milestone are archived.

## Planned Changes

- project-worktree-management
- project-merge-support
- project-sandbox-environment

## Dependencies / Risks

- These workflows operate on real local repositories and runtime environments, so safety restrictions and failure visibility must remain explicit.
- Sandbox support depends on host `podman` availability and narrow runtime validation boundaries.

## Status

- Declared: complete
