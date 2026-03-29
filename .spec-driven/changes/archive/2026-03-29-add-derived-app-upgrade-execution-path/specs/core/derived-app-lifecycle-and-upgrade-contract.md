# Derived App Lifecycle And Upgrade Contract

## MODIFIED Requirements

### Requirement: Repository Provides A Repository-Owned Upgrade Evaluation Path
Previously:
The repository MUST provide repository-owned entrypoints that evaluate derived-application upgrade compatibility and that surface release advisory details about the current platform delta.

The repository MUST provide repository-owned entrypoints that evaluate derived-application upgrade compatibility, surface release advisory details about the current platform delta, and provide a controlled upgrade execution path.

#### Scenario: A contributor performs a dry-run upgrade planning step
- GIVEN a contributor wants to understand how the repository-owned upgrade path would change a derived application
- WHEN they run the documented execution path in dry-run mode
- THEN they can inspect a machine-readable upgrade plan that identifies auto-applied items and manual-intervention items before any upgrade changes are committed

#### Scenario: A contributor executes a repository-owned upgrade path
- GIVEN a contributor has accepted the repository-owned upgrade plan
- WHEN they run the documented execution path
- THEN the repository applies the supported upgrade actions, reports any remaining manual-intervention items, and identifies the required post-upgrade validation commands

### Requirement: Lifecycle And Upgrade Contracts Are Implementation-Independent
Previously:
The repository MUST define derived-app lifecycle and upgrade semantics independently from any single implementation language or script structure.

The repository MUST define derived-app lifecycle, advisory, and upgrade execution semantics independently from any single implementation language or script structure.

#### Scenario: A contributor evaluates whether execution semantics depend on one toolchain
- GIVEN a contributor reviews the lifecycle and upgrade definition
- WHEN they inspect what counts as the normative contract
- THEN they can identify the required machine-readable inputs, upgrade-plan outputs, and observable results without depending on Node-specific or Java-specific internals
