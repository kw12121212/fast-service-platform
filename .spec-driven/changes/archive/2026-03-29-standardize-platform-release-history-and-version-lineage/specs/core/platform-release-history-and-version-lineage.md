# Platform Release History And Version Lineage

## ADDED Requirements

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
