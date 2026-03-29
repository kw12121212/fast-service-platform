# AI Repository Readiness

### Requirement: Repository Provides AI Contribution Quickstart
The repository MUST provide an AI-oriented quickstart that identifies required references, common task locations, the standard verification entrypoints for repository changes, and the repository-owned path for deriving an independent application from the platform.

#### Scenario: An AI contributor starts a common repository task
- GIVEN an AI contributor needs to extend or modify the repository
- WHEN it opens the AI contribution quickstart
- THEN it can identify what to read first, where common backend and frontend work lives, and which repository commands validate the change

#### Scenario: An AI contributor starts a new application from the platform
- GIVEN an AI contributor wants to derive an application instead of modifying the current baseline app
- WHEN it reads the repository's AI quickstart path
- THEN it can find the derivation workflow, the relevant machine-readable indexes, and the validation entrypoints for generated output

### Requirement: Repository Provides Machine-Readable AI Context
The repository MUST provide a machine-readable AI context manifest that exposes the current stack baseline, required guidance files, workspace boundaries, verification commands, hard repository constraints, the machine-readable assets used for application derivation, the lifecycle / upgrade assets needed to evaluate existing derived applications, the advisory assets needed to explain platform release deltas, the release-history / lineage assets needed to select supported upgrade targets, the execution assets needed to plan or apply derived-app upgrades, the AI tool-orchestration assets needed to teach AI contributors how to choose and sequence repository-owned tooling, and the unified repository-owned tooling façade that serves as the default invocation surface for those workflows, while distinguishing frontend `Node/bun` requirements from Java-owned platform tooling requirements.

#### Scenario: A tool-driven agent loads repository context
- GIVEN an AI agent can consume structured repository metadata
- WHEN it reads the machine-readable AI context asset
- THEN it can determine the repository's required references, runtime assumptions, directory responsibilities, validation entrypoints, non-negotiable constraints, and application-derivation assets without inferring them from prose alone

#### Scenario: A tool-driven agent loads lifecycle context
- GIVEN an AI agent needs to understand whether a generated application can be upgraded
- WHEN it reads the machine-readable AI context asset
- THEN it can identify the lifecycle contract, relevant schemas, playbooks, and repository-owned upgrade evaluation entrypoints without inferring them from prose alone

#### Scenario: A tool-driven agent loads release advisory context
- GIVEN an AI agent needs to understand what changed in the current platform release
- WHEN it reads the machine-readable AI context asset
- THEN it can identify the release advisory assets, schemas, playbooks, and repository-owned advisory entrypoints without inferring them from prose alone

#### Scenario: A tool-driven agent loads upgrade execution context
- GIVEN an AI agent needs to prepare or run a derived-app upgrade
- WHEN it reads the machine-readable AI context asset
- THEN it can identify the upgrade plan contract, playbooks, repository-owned execution entrypoints, and post-upgrade validation paths without inferring them from prose alone

#### Scenario: A tool-driven agent loads release lineage context
- GIVEN an AI agent needs to choose a valid upgrade target for a derived application
- WHEN it reads the machine-readable AI context asset
- THEN it can identify the release-history / lineage contract, relevant schemas, playbooks, and repository-owned target-selection entrypoints without inferring them from prose alone

#### Scenario: A tool-driven agent loads unified tooling context
- GIVEN an AI agent needs to invoke repository-owned assembly or lifecycle tooling
- WHEN it reads the machine-readable AI context asset
- THEN it can identify the unified façade entrypoint and its role as the default invocation surface before falling back to more specific wrappers

#### Scenario: A tool-driven agent loads AI orchestration context
- GIVEN an AI agent needs to understand how repository-owned tooling should be chosen and sequenced
- WHEN it reads the machine-readable AI context asset
- THEN it can identify the orchestration contract, related playbooks, default entrypoints, and allowed fallback behavior without inferring them from prose alone

#### Scenario: A tool-driven agent distinguishes frontend runtime from platform tooling runtime
- GIVEN an AI agent reads the machine-readable AI context asset
- WHEN it identifies the repository's runtime expectations
- THEN it can see that frontend workflows still use Node/bun
- AND it can see that repository-owned platform tooling workflows use Java-owned implementations

### Requirement: Repository Provides High-Frequency Change Playbooks
The repository MUST provide repository-owned playbooks for the current high-frequency change scenarios, including derived-application lifecycle, release advisory, release-history / lineage lookup, upgrade evaluation, upgrade execution, and AI tool-orchestration guidance, so contributors can follow repeatable extension patterns instead of reconstructing them from scattered source files.

#### Scenario: A contributor plans a common repository change
- GIVEN a contributor needs to perform a common backend, frontend, integration-oriented, or app-derivation change
- WHEN they read the corresponding playbook
- THEN they can identify the typical files, sequence of work, and validation steps for that change type

#### Scenario: A contributor plans a derived-app upgrade
- GIVEN a contributor needs to evaluate or prepare an upgrade for a derived application
- WHEN they read the corresponding repository playbook
- THEN they can identify the required metadata, the expected repository-owned entrypoints, and the validation steps for that lifecycle task

#### Scenario: A contributor prepares an upgrade advisory review
- GIVEN a contributor needs to understand what changed in the current platform release before upgrading a derived application
- WHEN they read the corresponding repository playbook
- THEN they can identify the relevant release advisory assets, the expected repository-owned advisory entrypoints, and the recommended follow-up checks

#### Scenario: A contributor prepares a derived-app upgrade execution
- GIVEN a contributor needs to apply a repository-owned upgrade path to a derived application
- WHEN they read the corresponding repository playbook
- THEN they can identify the machine-readable plan inputs, execution entrypoints, manual-intervention checkpoints, and post-upgrade verification steps

#### Scenario: A contributor prepares an upgrade target selection
- GIVEN a contributor needs to choose a supported release target before evaluating or executing an upgrade
- WHEN they read the corresponding repository playbook
- THEN they can identify the relevant release-history assets, repository-owned lookup entrypoints, and supported-path selection guidance

#### Scenario: A contributor prepares an AI tool-driven workflow
- GIVEN a contributor wants an AI agent to use repository-owned tooling for a supported platform workflow
- WHEN they read the corresponding repository playbook
- THEN they can identify the recommended command sequence, expected inputs, and failure-handling guidance instead of relying on ad hoc prompt instructions

### Requirement: Repository Provides Automated Validation Entrypoints
The repository MUST provide stable automated entrypoints for backend validation, frontend validation, full-stack validation, and derived-application assembly validation.

#### Scenario: A contributor validates a repository change
- GIVEN a contributor has modified the repository
- WHEN they run the documented validation entrypoints
- THEN they can execute backend checks, frontend checks, the expected full-stack validation path, and the derived-application assembly checks through repository-owned commands

### Requirement: Repository Provides Automated Full-Stack Smoke Path
The repository MUST provide an automated smoke-validation path that exercises the current backend-frontend integration boundary through the active `/service/*` route contract.

#### Scenario: A contributor verifies integration behavior
- GIVEN the repository's current backend and frontend are available in the local environment
- WHEN the contributor runs the documented smoke-validation entrypoint
- THEN the repository validates the current end-to-end integration path through `/service/*` instead of limiting verification to isolated backend or frontend checks

### Requirement: Repository Provides AI-Oriented Troubleshooting Guidance
The repository MUST provide troubleshooting guidance for the current local setup so contributors can resolve common environment, dependency, and integration failures without guessing.

#### Scenario: A contributor hits a common local failure
- GIVEN a contributor encounters a known setup or validation failure in the current repository environment
- WHEN they consult the repository troubleshooting guidance
- THEN they can identify the likely cause and the repository-approved recovery path for that failure class

### Requirement: Repository Provides Machine-Readable Module And Assembly Assets
The repository MUST provide machine-readable assets that describe the platform's module catalog and the application-assembly contract for derived applications.

#### Scenario: An AI contributor selects modules for a derived application
- GIVEN an AI contributor needs to choose which platform capabilities belong in a new application
- WHEN it reads the repository-owned structured assets
- THEN it can identify required platform core, optional built-in modules, dependency relationships, default assembly behavior, and expected validation steps

### Requirement: Repository Exposes Compatibility Assets As First-Class AI Inputs
The repository MUST expose the app assembly standard, schema assets, expanded compatibility fixtures, validation entrypoints, and generated-app verification contract as first-class AI-readable inputs.

#### Scenario: An AI contributor prepares to implement against the assembly standard
- GIVEN an AI contributor needs to understand how conformance is checked
- WHEN it reads the repository's AI readiness path
- THEN it can identify the normative assembly contract assets, the generated-app verification contract, the compatibility suite, and the validation commands before reading a specific implementation

#### Scenario: An AI contributor inspects expanded compatibility coverage
- GIVEN an AI contributor needs to understand how wide the repository-owned compatibility target is
- WHEN it reads the repository's AI readiness path
- THEN it can identify that the compatibility assets include multiple representative module combinations and invalid edge cases instead of only a minimal baseline

### Requirement: Validation Entrypoints Cover Contract Compatibility
The repository MUST provide validation entrypoints that check contract compatibility for app assembly implementations in addition to validating the current reference implementation.

#### Scenario: A contributor validates an implementation against the standard
- GIVEN a contributor has an app assembly implementation to validate
- WHEN they run the repository-owned compatibility validation path
- THEN they can determine whether the implementation satisfies the standard's observable contract rather than only whether one repository script still works

### Requirement: Repository Documents Compatible Assembly Implementations
The repository MUST identify the repository-owned Java assembly and generated-app verifier implementations and how contributors can invoke them through repository-owned tooling, while no longer describing Node as the repository-owned tooling implementation runtime for those workflows.

#### Scenario: A contributor chooses a repository-owned implementation
- GIVEN a contributor wants to derive or validate an application through the repository-owned tooling path
- WHEN they read the repository's AI-ready tooling guidance
- THEN they can identify Java-owned repository tooling as the default implementation runtime for those workflows

### Requirement: Repository Provides Validation Paths For Each Compatible Implementation
The repository MUST provide repository-owned validation paths that let contributors verify each compatible app assembly implementation and generated-app verifier implementation against the standard contract.

#### Scenario: A contributor validates the Java implementation
- GIVEN a contributor has used the Java CLI assembly path
- WHEN they run the documented validation flow
- THEN they can verify the Java implementation against the standard compatibility expectations

#### Scenario: A contributor validates the generated-app verifier path
- GIVEN a contributor wants to understand whether a generated application passes repository-owned validation
- WHEN they inspect the repository's validation guidance
- THEN they can identify the current reference verifier path and the contract it is expected to satisfy

#### Scenario: A contributor validates the Java generated-app verifier
- GIVEN a contributor wants to validate a derived application through the Java verifier path
- WHEN they run the documented repository-owned validation flow
- THEN they can verify the generated application through the Java verifier against the same generated-app verification contract
