# Derived App Lifecycle And Upgrade Contract

## MODIFIED Requirements

### Requirement: Repository Defines Upgrade Compatibility Inputs
Previously:
The repository MUST define the machine-readable inputs used to evaluate whether a derived application is compatible with a later platform release.

The repository MUST define the machine-readable inputs used to evaluate whether a derived application is compatible with a later platform release, including the release-delta and advisory assets that explain what changed and what should be checked next.

#### Scenario: A contributor inspects release advisory inputs
- GIVEN a contributor wants to understand why an upgrade target may require manual review
- WHEN they inspect the repository-owned lifecycle and upgrade assets
- THEN they can identify the machine-readable release advisory inputs that describe changed contracts, impacted modules, and recommended follow-up checks

### Requirement: Repository Provides A Repository-Owned Upgrade Evaluation Path
Previously:
The repository MUST provide a repository-owned entrypoint that evaluates or prepares derived-application upgrades against the lifecycle and upgrade contract.

The repository MUST provide repository-owned entrypoints that evaluate derived-application upgrade compatibility and that surface release advisory details about the current platform delta.

#### Scenario: A contributor reads the current platform advisory
- GIVEN a contributor wants to understand the current platform release delta before upgrading a derived application
- WHEN they run the documented repository-owned advisory entrypoint
- THEN they can identify the changed contracts, impacted modules, compatibility posture, and recommended next actions for that release
