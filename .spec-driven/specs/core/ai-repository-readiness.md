# AI Repository Readiness

### Requirement: Repository Provides AI Contribution Quickstart
The repository MUST provide an AI-oriented quickstart that identifies required references, common task locations, the standard verification entrypoints for repository changes, the required local `Lealone` dependency bootstrap, the repository-owned path for deriving an independent application from the platform, and the discoverable entrypoint for any repository-owned baseline demo and its regeneration guide.

#### Scenario: An AI contributor starts a common repository task
- GIVEN an AI contributor needs to extend or modify the repository
- WHEN it opens the AI contribution quickstart
- THEN it can identify what to read first, where common backend and frontend work lives, which repository commands validate the change, and how the local `Lealone` dependency should be prepared

#### Scenario: An AI contributor starts a new application from the platform
- GIVEN an AI contributor wants to derive an application instead of modifying the current baseline app
- WHEN it reads the repository's AI quickstart path
- THEN it can find the derivation workflow, the relevant machine-readable indexes, and the validation entrypoints for generated output

#### Scenario: A contributor looks for the repository-owned demo entrypoint
- GIVEN a contributor wants to run or explain the repository-owned baseline demo
- WHEN they read the repository quickstart or root guidance
- THEN they can find the committed demo location and its guide without searching through implementation directories

### Requirement: Repository Provides Machine-Readable AI Context
The repository MUST provide a machine-readable AI context manifest that exposes the current stack baseline, required guidance files, workspace boundaries, verification commands, hard repository constraints, the required local `Lealone` source dependency checkout and install path, the machine-readable assets used for structured solution definition and later application derivation, the machine-readable assets that classify generated template boundaries and customization zones, the lifecycle / upgrade assets needed to evaluate existing derived applications, the advisory assets needed to explain platform release deltas, the release-history / lineage assets needed to select supported upgrade targets, the execution assets needed to plan or apply derived-app upgrades, the AI tool-orchestration assets needed to teach AI contributors how to choose and sequence repository-owned tooling, and the unified repository-owned tooling façade that serves as the default invocation surface for those workflows, while distinguishing frontend `Node/bun` requirements from Java-owned platform tooling requirements.

#### Scenario: A tool-driven agent loads repository context
- GIVEN an AI agent can consume structured repository metadata
- WHEN it reads the machine-readable AI context asset
- THEN it can determine the repository's required references, runtime assumptions, directory responsibilities, validation entrypoints, non-negotiable constraints, required local `Lealone` dependency bootstrap, and application-derivation assets without inferring them from prose alone

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

#### Scenario: A tool-driven agent loads solution-input context
- GIVEN an AI agent needs to move from business intent to assembly preparation
- WHEN it reads the machine-readable AI context asset
- THEN it can identify the structured solution input contract, related schema assets, and the playbook that explains how to map solution input into manifest-driven assembly

#### Scenario: A tool-driven agent loads generated-output template context
- GIVEN an AI agent needs to understand which generated files are safe to customize or overwrite
- WHEN it reads the machine-readable AI context asset
- THEN it can identify the structured template-layer contract, related examples, and the repository guidance for customization boundaries without inferring them from implementation details

### Requirement: Repository Provides High-Frequency Change Playbooks
The repository MUST provide repository-owned playbooks for the current high-frequency change scenarios, including structured solution-input definition, template-slot and customization-boundary guidance, derived-application lifecycle, release advisory, release-history / lineage lookup, upgrade evaluation, upgrade execution, and AI tool-orchestration guidance, so contributors can follow repeatable extension patterns instead of reconstructing them from scattered source files.

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

#### Scenario: A contributor prepares a structured AI solution input
- GIVEN a contributor wants to describe a new application before choosing modules
- WHEN they read the corresponding repository playbook
- THEN they can identify the required structured input fields, the expected mapping boundary to `app-manifest`, and the repository-owned follow-up workflow for assembly

### Requirement: Repository Provides Automated Validation Entrypoints
The repository MUST provide stable automated entrypoints for backend validation, frontend validation, full-stack validation, and derived-application assembly validation.

#### Scenario: A contributor validates a repository change
- GIVEN a contributor has modified the repository
- WHEN they run the documented validation entrypoints
- THEN they can execute backend checks, frontend checks, the expected full-stack validation path, and the derived-application assembly checks through repository-owned commands

### Requirement: Repository Distinguishes Fast Backend Validation From Heavy Runtime Validation
The repository MUST document which backend validation entrypoint is the default fast-feedback baseline and which separate entrypoint exercises heavier engineering-runtime behavior such as real sandbox execution.

#### Scenario: A contributor chooses a backend validation path
- GIVEN a contributor needs to validate a repository change
- WHEN they inspect the documented repository-owned validation commands
- THEN they can distinguish the default backend baseline from the heavier sandbox-runtime validation path
- AND they can determine which path includes real sandbox runtime execution

### Requirement: Repository Provides Automated Full-Stack Smoke Path
The repository MUST provide an automated smoke-validation path that exercises the current backend-frontend integration boundary through the active `/service/*` route contract.

#### Scenario: A contributor verifies integration behavior
- GIVEN the repository's current backend and frontend are available in the local environment
- WHEN the contributor runs the documented smoke-validation entrypoint
- THEN the repository validates the current end-to-end integration path through `/service/*` instead of limiting verification to isolated backend or frontend checks

### Requirement: Repository Exposes Derived-App Runtime Smoke Validation Guidance
The repository MUST expose the derived-app runtime smoke validation path as a first-class repository validation entrypoint in its AI-facing guidance so contributors can tell when runtime proof is required in addition to generated-app contract verification.

#### Scenario: A contributor chooses how to validate a derived application
- GIVEN a contributor has generated or regenerated a derived application
- WHEN they inspect the repository's quickstart, AI context, or related playbooks
- THEN they can identify the derived-app runtime smoke entrypoint
- AND they can distinguish it from generated-app contract verification and main-workspace full-stack smoke validation

### Requirement: Repository Provides AI-Oriented Troubleshooting Guidance
The repository MUST provide troubleshooting guidance for the current local setup so contributors can resolve common environment, dependency, and integration failures without guessing.

#### Scenario: A contributor hits a common local failure
- GIVEN a contributor encounters a known setup or validation failure in the current repository environment
- WHEN they consult the repository troubleshooting guidance
- THEN they can identify the likely cause and the repository-approved recovery path for that failure class

#### Scenario: A contributor hits a missing local dependency failure
- GIVEN a contributor encounters a missing local dependency failure in the current repository environment
- WHEN they consult the repository troubleshooting guidance
- THEN they can identify `vendor/lealone` as the required source checkout and the repository-approved recovery path
- AND they are not told that `vendor/lealone-platform` is required for the current runnable baseline

### Requirement: Repository Provides Machine-Readable Module And Assembly Assets
The repository MUST provide machine-readable assets that describe the platform's solution-input model, module catalog, and application-assembly contract for derived applications.

#### Scenario: An AI contributor selects modules for a derived application
- GIVEN an AI contributor needs to choose which platform capabilities belong in a new application
- WHEN it reads the repository-owned structured assets
- THEN it can identify required platform core, optional built-in modules, dependency relationships, default assembly behavior, and expected validation steps

#### Scenario: An AI contributor moves from solution input to module selection
- GIVEN an AI contributor needs to translate business intent into a derived application
- WHEN it reads the repository-owned structured assets
- THEN it can identify the higher-level solution input layer before reading module-level and manifest-level assembly facts

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
