# Tasks: harden-release-upgrade-smoke

## Implementation

- [x] Create `docs/ai/compatibility/upgrade-smoke-fixture/` directory with a
      minimal `docs/ai/derived-app-lifecycle.json` declaring
      `sourcePlatform.releaseId = fast-service-platform/0.0.0-bootstrap` and
      all other required lifecycle fields
- [x] Create `docs/ai/compatibility/upgrade-smoke-fixture/` sibling asset
      stubs for the normative files the upgrade tooling references
      (`docs/ai/platform-release-history.json`, etc.) — use symlinks or
      relative references pointing to the platform-owned originals if the
      tooling supports relative paths; otherwise copy the minimum required
      fields
- [x] Create `docs/ai/compatibility/upgrade-smoke-suite.json` defining the
      expected observable outcomes for: `upgrade-targets` (happy path),
      `upgrade-advisory` (happy path), `upgrade-evaluate` (compatible),
      `upgrade-execute --dry-run` (plan produced), and invalid-source
      rejection (error includes recognizable message)
- [x] Add `run-upgrade-smoke-suite` command handler to `PlatformTooling.java`
      that reads the suite JSON, runs each case against the real upgrade
      sub-command logic, and reports pass/fail with fixture id and actionable
      message
- [x] Add `scripts/run-upgrade-smoke-suite.sh` shell entry point delegating
      to `PlatformTooling.java run-upgrade-smoke-suite`
- [x] Register the upgrade smoke suite run in the repository `verify` task
      (same location where `run-compatibility-suite` is invoked)

## Testing

- [x] Run `./scripts/run-upgrade-smoke-suite.sh` and confirm all cases pass
- [x] Manually invoke `./scripts/platform-tool.sh upgrade targets
      docs/ai/compatibility/upgrade-smoke-fixture` and confirm output matches
      suite expectation
- [x] Manually invoke `./scripts/platform-tool.sh upgrade evaluate
      docs/ai/compatibility/upgrade-smoke-fixture` and confirm compatible
      result
- [x] Manually invoke `./scripts/platform-tool.sh upgrade execute
      docs/ai/compatibility/upgrade-smoke-fixture --dry-run` and confirm plan
      output without file writes
- [x] Confirm the invalid-source fixture case returns a non-zero exit code
      with an actionable error message

## Verification

- [x] All upgrade smoke suite cases pass via `run-upgrade-smoke-suite.sh`
- [x] `./scripts/platform-tool.sh verify` (or equivalent full-verify
      entrypoint) completes without error, including the new smoke suite
- [x] No changes to `demo/baseline-demo` lifecycle metadata
- [x] No changes to `docs/ai/platform-release-history.json`
- [x] No changes to existing `app-assembly-suite.json` or its fixtures
