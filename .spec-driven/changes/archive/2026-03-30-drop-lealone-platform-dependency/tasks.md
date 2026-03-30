# Tasks: drop-lealone-platform-dependency

## Implementation

- [x] Update backend runtime dependencies, bootstrap imports, and committed generated executors to use the current `Lealone` modules and package names instead of legacy `Lealone-Platform` paths.
- [x] Update repository-owned generation templates and assembly tooling outputs so regenerated code uses the same `Lealone` import and dependency conventions.
- [x] Remove `vendor/lealone-platform` from local dependency bootstrap scripts, troubleshooting guidance, AI context assets, and repository documentation.
- [x] Update committed baseline demo assets and mirrored backend references so the demo follows the same Lealone-only dependency contract.
- [x] Update the affected main and delta specs to describe `Lealone` as the backend foundation and dependency boundary.

## Testing

- [x] Run `./scripts/install-lealone-source-deps.sh` with the migrated Lealone-only dependency path.
- [x] Run `./scripts/verify-backend.sh`.
- [x] Run `./scripts/verify-frontend.sh`.
- [x] Run `./scripts/verify-fullstack.sh`.

## Verification

- [x] Verify repository-owned docs, AI context assets, and troubleshooting guidance consistently describe only `vendor/lealone` as the required source dependency.
- [x] Verify no current backend runtime path, committed generated executor, or baseline demo backend still depends on `com.lealone.plugins.*` runtime imports for the migrated Lealone path.
- [x] Verify implementation matches the approved proposal and updated specs without adding upstream agent capabilities.
