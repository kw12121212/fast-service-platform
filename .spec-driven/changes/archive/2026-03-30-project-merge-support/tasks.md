# Tasks: project-merge-support

## Implementation

- [x] Add backend project-merge-support behavior for exposing merge context for managed linked worktrees and validating merge source or target restrictions.
- [x] Add backend project-merge execution behavior that merges a managed linked-worktree branch into another existing local branch and fails safely on conflicts.
- [x] Extend the existing Projects experience to show merge candidates, restricted states, and merge submission controls for linked worktrees.
- [x] Extend the existing Projects experience to refresh visible Git and worktree context after merge success or failure.

## Testing

- [x] Add backend tests covering successful merge, dirty-source rejection, invalid-source rejection, invalid-target rejection, and conflict failure cleanup.
- [x] Add frontend tests covering merge availability, restricted merge states, successful merge feedback, and conflict failure feedback.
- [x] Run backend verification with `./scripts/verify-backend.sh`.
- [x] Run frontend verification with `./scripts/verify-frontend.sh`.

## Verification

- [x] Verify the implemented behavior stays project-scoped and does not silently expand into remote Git hosting, rebase, cherry-pick, or sandbox behavior.
- [x] Verify merge conflicts fail without leaving the repository in an in-progress merge state.
- [x] Verify the final implementation matches the proposal and delta specs.
