# Derived App Lifecycle And Upgrade Contract

### Requirement: Repository Defines A Machine-Readable Derived-App Lifecycle Contract
The repository MUST define a machine-readable contract for describing the lifecycle state of a derived application after assembly.

#### Scenario: A contributor inspects lifecycle semantics
- GIVEN a contributor wants to understand how a derived application identifies its platform origin
- WHEN they inspect the repository-owned lifecycle assets
- THEN they can find a machine-readable contract that defines the required lifecycle metadata and its meaning

### Requirement: Derived Applications Declare Platform Origin Metadata
Derived applications MUST expose machine-readable metadata that identifies the platform release, lifecycle contract version, and baseline identity they were generated from.

#### Scenario: A contributor checks which platform release produced an application
- GIVEN a contributor has a previously generated application
- WHEN they inspect its lifecycle metadata
- THEN they can determine which platform release and baseline identity the generated application came from

### Requirement: Repository Defines Upgrade Compatibility Inputs
The repository MUST define the machine-readable inputs used to evaluate whether a derived application is compatible with a later platform release, including the standardized release-history / lineage assets, the release-delta and advisory assets for candidate targets, and the supported upgrade-path declarations that explain which source-to-target combinations the repository recognizes.

#### Scenario: A contributor evaluates upgrade eligibility
- GIVEN a contributor wants to know whether a derived application can be upgraded
- WHEN they inspect the repository-owned lifecycle and upgrade assets
- THEN they can identify the declared compatibility inputs instead of inferring them from implementation-specific scripts

#### Scenario: A contributor inspects release advisory inputs
- GIVEN a contributor wants to understand why an upgrade target may require manual review
- WHEN they inspect the repository-owned lifecycle and upgrade assets
- THEN they can identify the machine-readable release advisory inputs that describe changed contracts, impacted modules, and recommended follow-up checks

#### Scenario: A contributor evaluates multiple candidate targets
- GIVEN a contributor has a derived application from an older platform release
- WHEN they inspect the repository-owned lifecycle and upgrade assets
- THEN they can identify which target releases are declared as supported candidates instead of inferring upgrade eligibility from the current release alone

### Requirement: Repository Provides A Repository-Owned Upgrade Evaluation Path
The repository MUST provide repository-owned entrypoints that evaluate derived-application upgrade compatibility, surface release advisory details for repository-declared target releases, provide release lookup guidance for selecting a supported target, and provide a controlled upgrade execution path.

#### Scenario: A contributor runs the upgrade evaluation flow
- GIVEN a contributor has a derived application and a target platform release
- WHEN they run the documented repository-owned upgrade evaluation path
- THEN they can determine whether the derived application satisfies the repository-defined upgrade contract for that target

#### Scenario: A contributor reads the current platform advisory
- GIVEN a contributor wants to understand the current platform release delta before upgrading a derived application
- WHEN they run the documented repository-owned advisory entrypoint
- THEN they can identify the changed contracts, impacted modules, compatibility posture, and recommended next actions for that release

#### Scenario: A contributor performs a dry-run upgrade planning step
- GIVEN a contributor wants to understand how the repository-owned upgrade path would change a derived application
- WHEN they run the documented execution path in dry-run mode
- THEN they can inspect a machine-readable upgrade plan that identifies auto-applied items and manual-intervention items before any upgrade changes are committed

#### Scenario: A contributor executes a repository-owned upgrade path
- GIVEN a contributor has accepted the repository-owned upgrade plan
- WHEN they run the documented execution path
- THEN the repository applies the supported upgrade actions, reports any remaining manual-intervention items, and identifies the required post-upgrade validation commands

#### Scenario: A contributor selects a supported upgrade target
- GIVEN a contributor has a derived application and needs to choose a valid platform target release
- WHEN they run the documented repository-owned release lookup path
- THEN they can identify the supported target releases and the declared path semantics before running upgrade evaluation or execution

### Requirement: Lifecycle And Upgrade Contracts Are Implementation-Independent
The repository MUST define derived-app lifecycle, advisory, and upgrade execution semantics independently from any single implementation language or script structure.

#### Scenario: A contributor evaluates whether lifecycle semantics depend on one toolchain
- GIVEN a contributor reviews the lifecycle and upgrade definition
- WHEN they inspect what counts as the normative contract
- THEN they can identify the required machine-readable inputs and observable results without depending on Node-specific or Java-specific internals

#### Scenario: A contributor evaluates whether execution semantics depend on one toolchain
- GIVEN a contributor reviews the lifecycle and upgrade definition
- WHEN they inspect what counts as the normative contract
- THEN they can identify the required machine-readable inputs, upgrade-plan outputs, and observable results without depending on Node-specific or Java-specific internals
