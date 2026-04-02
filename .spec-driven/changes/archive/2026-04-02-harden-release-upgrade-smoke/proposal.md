# harden-release-upgrade-smoke

## What

Add repository-owned runnable smoke fixtures and a verification suite for the
platform upgrade workflow. The fixtures provide a concrete derived-app at
`0.0.0-bootstrap` origin that can be used to prove the `upgrade-targets`,
`upgrade-advisory`, `upgrade-evaluate`, and `upgrade-execute --dry-run`
commands all behave correctly against a real versioned example.

## Why

The platform already defines complete release history, supported upgrade paths,
advisory assets, and Java-owned upgrade tooling. However these definitions are
only validated at the contract level — no repository-owned runnable fixture
proves the actual tool invocations return correct, actionable output. The next
trust gap is runtime proof: the upgrade workflow should be demonstrably
runnable and verifiable, not just structurally defined.

## Scope

**In scope:**
- A minimal upgrade smoke fixture directory representing a derived app
  originated from `fast-service-platform/0.0.0-bootstrap`
- A repository-owned upgrade smoke suite definition analogous to
  `docs/ai/compatibility/app-assembly-suite.json`
- Verification that `upgrade-targets` correctly lists `0.1.0-dev` as a
  supported target from `0.0.0-bootstrap`
- Verification that `upgrade-advisory` returns the `0.0.0 → 0.1.0` advisory
  without error
- Verification that `upgrade-evaluate` identifies the bootstrap fixture as
  compatible (happy path)
- Verification that `upgrade-execute --dry-run` produces an upgrade plan
  without writing files
- Verification that an invalid source release is rejected with an actionable
  error message

**Out of scope:**
- `upgrade-execute --apply` (file-writing end-to-end)
- Multi-hop upgrade path testing
- Adding new platform releases
- Modifying existing advisory content or release history

## Unchanged Behavior

- Existing `upgrade-targets`, `upgrade-advisory`, `upgrade-evaluate`, and
  `upgrade-execute` command behavior must not change
- `app-assembly-suite.json` and its fixtures must not be modified
- `demo/baseline-demo` lifecycle metadata must not be modified
- `platform-release-history.json` content must not change
