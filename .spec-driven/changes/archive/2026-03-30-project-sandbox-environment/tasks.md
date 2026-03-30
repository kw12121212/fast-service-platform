# Tasks: project-sandbox-environment

## Implementation

- [x] Add backend project-sandbox context behavior for managed linked worktrees, including image state, container state, script-path source, and restricted-state visibility.
- [x] Add backend sandbox image lifecycle behavior for linked worktrees, including default script paths and worktree-level script-path overrides for `init-image.sh`.
- [x] Add backend temporary container lifecycle behavior for linked worktrees, including existing-image preconditions, single-active-container enforcement, and `init-project.sh` execution.
- [x] Extend the existing Projects experience to show linked-worktree sandbox context, restricted states, and image/container actions.
- [x] Extend the existing Projects experience to refresh visible sandbox state after image creation, container creation, or container destruction.

## Testing

- [x] Add backend tests covering image creation, script-path override handling, container creation, duplicate active-container rejection, missing-image rejection, and initialization failure handling.
- [x] Add frontend tests covering sandbox availability, restricted sandbox states, image creation feedback, container lifecycle feedback, and failure visibility.
- [x] Run backend verification with `./scripts/verify-backend.sh`.
- [x] Run frontend verification with `./scripts/verify-frontend.sh`.
- [x] Run full-stack verification with `./scripts/verify-fullstack.sh`.

## Verification

- [x] Verify the implemented behavior stays linked-worktree-scoped and does not silently expand into main-worktree sandboxing, multiple active containers, or generic orchestration behavior.
- [x] Verify non-zero `init-image.sh` or `init-project.sh` exits produce clear failure states instead of false success.
- [x] Verify the final implementation matches the proposal and delta specs.
