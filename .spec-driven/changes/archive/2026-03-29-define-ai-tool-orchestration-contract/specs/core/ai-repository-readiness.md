## MODIFIED Requirements

### Requirement: Repository Provides Machine-Readable AI Context
Previously: The repository MUST provide a machine-readable AI context manifest that exposes the current stack baseline, required guidance files, workspace boundaries, verification commands, hard repository constraints, the machine-readable assets used for application derivation, the lifecycle / upgrade assets needed to evaluate existing derived applications, the advisory assets needed to explain platform release deltas, the release-history / lineage assets needed to select supported upgrade targets, the execution assets needed to plan or apply derived-app upgrades, and the unified repository-owned tooling façade that serves as the default invocation surface for those workflows.

The repository MUST provide a machine-readable AI context manifest that exposes the current stack baseline, required guidance files, workspace boundaries, verification commands, hard repository constraints, the machine-readable assets used for application derivation, the lifecycle / upgrade assets needed to evaluate existing derived applications, the advisory assets needed to explain platform release deltas, the release-history / lineage assets needed to select supported upgrade targets, the execution assets needed to plan or apply derived-app upgrades, the AI tool-orchestration assets needed to teach AI contributors how to choose and sequence repository-owned tooling, and the unified repository-owned tooling façade that serves as the default invocation surface for those workflows.

#### Scenario: A tool-driven agent loads AI orchestration context
- GIVEN an AI agent needs to understand how repository-owned tooling should be chosen and sequenced
- WHEN it reads the machine-readable AI context asset
- THEN it can identify the orchestration contract, related playbooks, default entrypoints, and allowed fallback behavior without inferring them from prose alone

### Requirement: Repository Provides High-Frequency Change Playbooks
Previously: The repository MUST provide repository-owned playbooks for the current high-frequency change scenarios, including derived-application lifecycle, release advisory, release-history / lineage lookup, upgrade evaluation, and upgrade execution, so contributors can follow repeatable extension patterns instead of reconstructing them from scattered source files.

The repository MUST provide repository-owned playbooks for the current high-frequency change scenarios, including derived-application lifecycle, release advisory, release-history / lineage lookup, upgrade evaluation, upgrade execution, and AI tool-orchestration guidance, so contributors can follow repeatable extension patterns instead of reconstructing them from scattered source files.

#### Scenario: A contributor prepares an AI tool-driven workflow
- GIVEN a contributor wants an AI agent to use repository-owned tooling for a supported platform workflow
- WHEN they read the corresponding repository playbook
- THEN they can identify the recommended command sequence, expected inputs, and failure-handling guidance instead of relying on ad hoc prompt instructions
