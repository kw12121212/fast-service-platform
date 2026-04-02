# Design: harden-release-upgrade-smoke

## Approach

Mirror the existing `app-assembly-suite.json` pattern for upgrade smoke
validation:

1. Create a minimal upgrade fixture directory at
   `docs/ai/compatibility/upgrade-smoke-fixture/` containing only the files
   the upgrade tooling reads: `docs/ai/derived-app-lifecycle.json` and the
   other required normative asset references. The fixture declares
   `sourcePlatform.releaseId = fast-service-platform/0.0.0-bootstrap`.

2. Define `docs/ai/compatibility/upgrade-smoke-suite.json` to describe the
   expected outcomes for each upgrade command against the fixture: targets,
   advisory, evaluate (compatible), dry-run execute (plan output), and the
   invalid-source rejection case.

3. Add a `run-upgrade-smoke-suite` command to `PlatformTooling.java` that
   iterates through the suite fixtures, invokes the relevant upgrade sub-
   command internally, and asserts the expected observable results. Failures
   are reported with the fixture id, command, and actionable message.

4. Add `scripts/run-upgrade-smoke-suite.sh` as the shell entry point.

5. Add the smoke suite run to the `verify` task so it is validated as part of
   the standard repository check.

## Key Decisions

- **Fixture is a directory, not a single JSON file** — the upgrade tooling
  reads multiple files relative to a "generated app dir" path; the fixture
  must mirror that structure so the real tool path is exercised.

- **Only `--dry-run` for execute** — `--apply` would write managed assets
  into the fixture directory, complicating cleanup and blurring the line
  between smoke validation and actual upgrade execution. Dry-run output is
  sufficient to prove the planning stage works.

- **Suite JSON follows `app-assembly-suite.json` conventions** — consistent
  structure makes the validation approach familiar and lets future suites
  extend the same pattern.

- **Invalid-source case uses a standalone lifecycle file** — the invalid
  fixture only needs to set an unrecognized `sourcePlatform.releaseId`; it
  does not need all normative assets.

## Alternatives Considered

- **Shell-script-only smoke runner** — simpler to add but harder to make
  failure output actionable and harder to integrate with the existing Java
  tooling verification model. Rejected in favor of consistency with
  PlatformTooling.

- **Reuse `demo/baseline-demo` as the fixture** — that app is already on
  `0.1.0-dev`, so it cannot test the `0.0.0-bootstrap → 0.1.0-dev` upgrade
  path. A dedicated fixture is required.
