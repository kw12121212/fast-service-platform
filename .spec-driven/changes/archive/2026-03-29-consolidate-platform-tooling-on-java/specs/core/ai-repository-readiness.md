## MODIFIED Requirements

### Requirement: Repository Provides Machine-Readable AI Context
Previously: The repository MUST provide a machine-readable AI context manifest that exposes the current stack baseline, required guidance files, workspace boundaries, verification commands, hard repository constraints, the machine-readable assets used for application derivation, the lifecycle / upgrade assets needed to evaluate existing derived applications, the advisory assets needed to explain platform release deltas, the release-history / lineage assets needed to select supported upgrade targets, the execution assets needed to plan or apply derived-app upgrades, the AI tool-orchestration assets needed to teach AI contributors how to choose and sequence repository-owned tooling, and the unified repository-owned tooling façade that serves as the default invocation surface for those workflows.

The repository MUST provide a machine-readable AI context manifest that exposes the current stack baseline, required guidance files, workspace boundaries, verification commands, hard repository constraints, the machine-readable assets used for application derivation, the lifecycle / upgrade assets needed to evaluate existing derived applications, the advisory assets needed to explain platform release deltas, the release-history / lineage assets needed to select supported upgrade targets, the execution assets needed to plan or apply derived-app upgrades, the AI tool-orchestration assets needed to teach AI contributors how to choose and sequence repository-owned tooling, and the unified repository-owned tooling façade that serves as the default invocation surface for those workflows, while distinguishing frontend `Node/bun` requirements from Java-owned platform tooling requirements.

#### Scenario: A tool-driven agent distinguishes frontend runtime from platform tooling runtime
- GIVEN an AI agent reads the machine-readable AI context asset
- WHEN it identifies the repository's runtime expectations
- THEN it can see that frontend workflows still use Node/bun
- AND it can see that repository-owned platform tooling workflows use Java-owned implementations

### Requirement: Repository Documents Compatible Assembly Implementations
Previously: The repository MUST identify the available compatible assembly and generated-app verifier implementations and how contributors can invoke them through repository-owned tooling.

The repository MUST identify the repository-owned Java assembly and generated-app verifier implementations and how contributors can invoke them through repository-owned tooling, while no longer describing Node as the repository-owned tooling implementation runtime for those workflows.

#### Scenario: A contributor chooses a repository-owned implementation
- GIVEN a contributor wants to derive or validate an application through the repository-owned tooling path
- WHEN they read the repository's AI-ready tooling guidance
- THEN they can identify Java-owned repository tooling as the default implementation runtime for those workflows
