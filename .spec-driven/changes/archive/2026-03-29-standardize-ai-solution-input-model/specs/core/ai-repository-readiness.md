## MODIFIED Requirements

### Requirement: Repository Provides Machine-Readable AI Context
Previously: The repository MUST provide a machine-readable AI context manifest that exposes the current stack baseline, required guidance files, workspace boundaries, verification commands, hard repository constraints, the machine-readable assets used for application derivation, the lifecycle / upgrade assets needed to evaluate existing derived applications, the advisory assets needed to explain platform release deltas, the release-history / lineage assets needed to select supported upgrade targets, the execution assets needed to plan or apply derived-app upgrades, the AI tool-orchestration assets needed to teach AI contributors how to choose and sequence repository-owned tooling, and the unified repository-owned tooling façade that serves as the default invocation surface for those workflows, while distinguishing frontend `Node/bun` requirements from Java-owned platform tooling requirements.

The repository MUST provide a machine-readable AI context manifest that exposes the current stack baseline, required guidance files, workspace boundaries, verification commands, hard repository constraints, the machine-readable assets used for structured solution definition and later application derivation, the lifecycle / upgrade assets needed to evaluate existing derived applications, the advisory assets needed to explain platform release deltas, the release-history / lineage assets needed to select supported upgrade targets, the execution assets needed to plan or apply derived-app upgrades, the AI tool-orchestration assets needed to teach AI contributors how to choose and sequence repository-owned tooling, and the unified repository-owned tooling façade that serves as the default invocation surface for those workflows, while distinguishing frontend `Node/bun` requirements from Java-owned platform tooling requirements.

#### Scenario: A tool-driven agent loads solution-input context
- GIVEN an AI agent needs to move from business intent to assembly preparation
- WHEN it reads the machine-readable AI context asset
- THEN it can identify the structured solution input contract, related schema assets, and the playbook that explains how to map solution input into manifest-driven assembly

### Requirement: Repository Provides High-Frequency Change Playbooks
Previously: The repository MUST provide repository-owned playbooks for the current high-frequency change scenarios, including derived-application lifecycle, release advisory, release-history / lineage lookup, upgrade evaluation, upgrade execution, and AI tool-orchestration guidance, so contributors can follow repeatable extension patterns instead of reconstructing them from scattered source files.

The repository MUST provide repository-owned playbooks for the current high-frequency change scenarios, including structured solution-input definition, derived-application lifecycle, release advisory, release-history / lineage lookup, upgrade evaluation, upgrade execution, and AI tool-orchestration guidance, so contributors can follow repeatable extension patterns instead of reconstructing them from scattered source files.

#### Scenario: A contributor prepares a structured AI solution input
- GIVEN a contributor wants to describe a new application before choosing modules
- WHEN they read the corresponding repository playbook
- THEN they can identify the required structured input fields, the expected mapping boundary to `app-manifest`, and the repository-owned follow-up workflow for assembly

### Requirement: Repository Provides Machine-Readable Module And Assembly Assets
Previously: The repository MUST provide machine-readable assets that describe the platform's module catalog and the application-assembly contract for derived applications.

The repository MUST provide machine-readable assets that describe the platform's solution-input model, module catalog, and application-assembly contract for derived applications.

#### Scenario: An AI contributor moves from solution input to module selection
- GIVEN an AI contributor needs to translate business intent into a derived application
- WHEN it reads the repository-owned structured assets
- THEN it can identify the higher-level solution input layer before reading module-level and manifest-level assembly facts
