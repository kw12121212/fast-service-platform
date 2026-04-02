# Platform Release History And Version Lineage

### Requirement: Repository Defines A Machine-Readable Platform Release History
The repository MUST define a machine-readable history asset that lists the platform releases the repository recognizes for lifecycle, advisory, and upgrade workflows.

#### Scenario: A contributor inspects known platform releases
- GIVEN a contributor wants to know which platform releases participate in repository-owned lifecycle workflows
- WHEN they inspect the repository-owned release history assets
- THEN they can identify the recognized releases instead of inferring them from scattered advisory files or commit history

### Requirement: Repository Defines Version Lineage Relationships
The repository MUST define machine-readable lineage relationships between recognized platform releases, including each release's parent or origin within the supported history.

#### Scenario: A contributor inspects release ancestry
- GIVEN a contributor wants to know how one release relates to an earlier release
- WHEN they inspect the repository-owned release history assets
- THEN they can identify the declared lineage relationship without inferring it from version strings alone

### Requirement: Repository Declares Supported Upgrade Paths
The repository MUST declare which source-to-target release combinations are supported for derived-application upgrade workflows.

#### Scenario: A contributor checks whether an upgrade path is supported
- GIVEN a contributor has a derived application from a known source release
- WHEN they inspect the repository-owned release history assets or run the repository-owned lookup path
- THEN they can determine whether the requested target release is declared as supported, unsupported, or outside the repository's support window

### Requirement: Repository Associates Advisory Assets With Releases
The repository MUST associate each recognized release with the advisory assets that explain its observable delta and follow-up guidance.

#### Scenario: A contributor reviews advisory history
- GIVEN a contributor wants to understand what changed at a specific target release
- WHEN they inspect the repository-owned release history assets
- THEN they can identify the advisory asset or advisory archive entry associated with that release

### Requirement: Repository Provides A Repository-Owned Release Lookup Path
The repository MUST provide a repository-owned entrypoint that surfaces recognized releases, supported upgrade targets, and lineage metadata for upgrade target selection.

#### Scenario: A contributor looks up valid target releases
- GIVEN a contributor has a derived application from a known platform release
- WHEN they run the documented repository-owned release lookup path
- THEN they can identify the supported target releases, lineage context, and advisory references before running upgrade evaluation or execution

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
- GIVEN a contributor wants to understand what an upgradeable derived-app looks
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
