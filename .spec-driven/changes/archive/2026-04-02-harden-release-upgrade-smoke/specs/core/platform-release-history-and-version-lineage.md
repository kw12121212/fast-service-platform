# Delta: platform-release-history-and-version-lineage

## ADDED Requirements

### Requirement: Repository Provides A Runnable Upgrade Smoke Suite
The repository MUST provide a repository-owned upgrade smoke suite that
verifies the observable behavior of the `upgrade-targets`, `upgrade-advisory`,
`upgrade-evaluate`, and `upgrade-execute --dry-run` commands against at least
one concrete versioned fixture.

#### Scenario: A contributor runs the upgrade smoke suite
- GIVEN the repository contains an upgrade smoke suite definition and a
  concrete derived-app fixture at a historical source release
- WHEN they run the repository-owned upgrade smoke suite entrypoint
- THEN every declared case passes or fails with a fixture id and actionable
  message, and the suite exits non-zero if any case fails

### Requirement: Repository Provides At Least One Upgrade Smoke Fixture
The repository MUST provide at least one concrete derived-app fixture that
represents an application originated from a recognized historical source
release, suitable for exercising the repository-owned upgrade workflow
commands.

#### Scenario: A contributor inspects the upgrade smoke fixture
- GIVEN a contributor wants to understand what a upgradeable derived-app looks
  like at the historical source release
- WHEN they inspect the repository-owned upgrade smoke fixture
- THEN they can find a minimal but complete lifecycle metadata file declaring
  the historical source platform release id, with no ambiguous or
  fabricated field values

### Requirement: Upgrade Smoke Suite Covers Rejection Of Invalid Source Releases
The upgrade smoke suite MUST include at least one case verifying that a
derived-app fixture with an unrecognized source release id is rejected with
an actionable error.

#### Scenario: A contributor runs evaluate against an invalid source fixture
- GIVEN a derived-app fixture declares a source platform release id that is
  not in the repository-owned release history
- WHEN they run `upgrade-evaluate` against that fixture
- THEN the command exits non-zero and the error output includes a recognizable
  message identifying the unrecognized release id
