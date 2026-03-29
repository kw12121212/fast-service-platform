# Derived App Lifecycle And Upgrade Contract

## MODIFIED Requirements

### Requirement: Repository Defines Upgrade Compatibility Inputs
Previously:
The repository MUST define the machine-readable inputs used to evaluate whether a derived application is compatible with a later platform release, including the release-delta and advisory assets that explain what changed and what should be checked next.

The repository MUST define the machine-readable inputs used to evaluate whether a derived application is compatible with a later platform release, including the standardized release-history / lineage assets, the release-delta and advisory assets for candidate targets, and the supported upgrade-path declarations that explain which source-to-target combinations the repository recognizes.

#### Scenario: A contributor evaluates multiple candidate targets
- GIVEN a contributor has a derived application from an older platform release
- WHEN they inspect the repository-owned lifecycle and upgrade assets
- THEN they can identify which target releases are declared as supported candidates instead of inferring upgrade eligibility from the current release alone

### Requirement: Repository Provides A Repository-Owned Upgrade Evaluation Path
Previously:
The repository MUST provide repository-owned entrypoints that evaluate derived-application upgrade compatibility, surface release advisory details about the current platform delta, and provide a controlled upgrade execution path.

The repository MUST provide repository-owned entrypoints that evaluate derived-application upgrade compatibility, surface release advisory details for repository-declared target releases, provide release lookup guidance for selecting a supported target, and provide a controlled upgrade execution path.

#### Scenario: A contributor selects a supported upgrade target
- GIVEN a contributor has a derived application and needs to choose a valid platform target release
- WHEN they run the documented repository-owned release lookup path
- THEN they can identify the supported target releases and the declared path semantics before running upgrade evaluation or execution
