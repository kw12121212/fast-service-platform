# Tasks: project-worktree-management

## Implementation

- [x] Add backend delta specs for project-scoped worktree listing, creation, deletion, repair, and prune behavior.
- [x] Add frontend delta specs for Projects-experience worktree visibility, restricted states, and mutation feedback.
- [x] Define the required sibling-directory location and sanitized branch-name path behavior for created worktrees.
- [x] Define the branch-to-worktree uniqueness rule and the detached-HEAD restrictions for worktree management.
- [x] Define the deletion safety boundary covering clean state, pushed-state requirements, and the no-upstream restriction.
- [x] Define how repair and prune are surfaced as project-scoped maintenance operations without widening the platform boundary to merge or sandbox support.

## Testing

- [x] Backend tests cover bound, unbound, detached-HEAD, duplicate-branch, clean-delete, dirty-delete, ahead-of-upstream, and no-upstream worktree scenarios.
- [x] Frontend tests cover worktree inventory display, restricted action states, successful mutations, and rejected mutations in the Projects experience.
- [x] Repository validation still passes with the new worktree management behavior included in the current backend and frontend baseline.

## Verification

- [x] `node /home/code/.agents/skills/spec-driven-brainstorm/scripts/spec-driven.js verify project-worktree-management` passes
- [x] Confirm the final proposal keeps worktree management project-scoped and does not silently expand into merge or sandbox behavior
