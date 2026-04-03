# Tasks: project-derived-app-assembly

## Implementation

- [x] Add backend project-derived-app-assembly context for bound and unbound software projects, including normal and restricted state reporting.
- [x] Add backend project-scoped assembly request handling that validates `app-manifest` input and explicit absolute output directories before delegating to repository-owned assembly tooling.
- [x] Add backend outcome reporting that exposes the latest visible assembly result and distinguishes invalid input from assembly execution failure.
- [x] Extend the existing Projects experience to show assembly availability, restricted states, manifest submission, output-directory input, and visible assembly outcomes.
- [x] Refresh project assembly state in the frontend after successful project-scoped assembly requests.

## Testing

- [x] Add backend tests covering bound-project context, unbound-project restrictions, valid assembly request handling, invalid input rejection, and execution-failure reporting.
- [x] Add frontend tests covering assembly availability, restricted states, successful submission feedback, and execution-failure feedback in the Projects experience.
- [x] Run backend verification with `./scripts/verify-backend.sh`.
- [x] Run frontend verification with `./scripts/verify-frontend.sh`.

## Verification

- [x] Verify the final implementation preserves repository-owned assembly tooling boundaries instead of re-implementing assembly logic in the UI or project layer.
- [x] Verify the first release remains scoped to the main bound repository context and does not silently expand into linked-worktree source selection.
- [x] Verify the final implementation matches the proposal and delta specs.
