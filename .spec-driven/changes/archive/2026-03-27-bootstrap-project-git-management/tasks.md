# Tasks: bootstrap-project-git-management

## Implementation

- [x] Add proposal and delta specs for the project-attached Git management
  baseline, including branch listing, recent commits, clean-only branch
  switching, and detached HEAD restrictions.
- [x] Add backend Git-management behavior for bound projects using the existing
  repository anchor and local Git CLI boundary.
- [x] Add frontend Git-management presentation and branch-switch workflow inside
  the existing Projects experience.
- [x] Preserve clear success, failure, unbound, dirty-working-tree, and
  detached-HEAD states in the Git-management flow.

## Testing

- [x] Backend: `mvn -q test`
- [x] Frontend: `bun run test`
- [x] Frontend: `bun run build`
- [x] Frontend: `bun run lint`
- [x] Verify Git-management workflows are exercised through backend-backed
  `/service/*` paths where feasible.

## Verification

- [x] Verify the change stays within local Git management scope and does not
  silently expand into branch creation, remote operations, worktree management,
  merge support, or sandbox behavior.
- [x] Verify projects without repository bindings remain valid and continue to
  show a clear unbound state.
- [x] Verify detached HEAD remains a visible restricted state instead of being
  treated as a normal branch-switch target.
