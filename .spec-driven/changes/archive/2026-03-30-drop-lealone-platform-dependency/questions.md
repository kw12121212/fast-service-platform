# Questions: drop-lealone-platform-dependency

## Open

<!-- No open questions -->

## Resolved

- [x] Q: Should this migration also introduce the upstream `agent` or AI app platform capabilities?
  Context: This determines whether the dependency migration also expands the repository's product scope.
  A: No. The migration should stay focused on aligning the current repository with `Lealone` and should not add those capabilities for now.

- [x] Q: Should `vendor/lealone-platform` remain a required local source repository after the migration?
  Context: This determines the local dependency contract, install script, troubleshooting guidance, and AI-readable setup instructions.
  A: No. The repository should move to a Lealone-only dependency contract, and `vendor/lealone-platform` should no longer be required.
