# AI Repository Readiness

## MODIFIED Requirements

### Requirement: Repository Provides Machine-Readable AI Context
Previously:
The repository MUST provide a machine-readable AI context manifest that exposes the current stack baseline, required guidance files, workspace boundaries, verification commands, hard repository constraints, the machine-readable assets used for application derivation, the lifecycle / upgrade assets needed to evaluate existing derived applications, the advisory assets needed to explain current platform release deltas, and the execution assets needed to plan or apply derived-app upgrades.

The repository MUST provide a machine-readable AI context manifest that exposes the current stack baseline, required guidance files, workspace boundaries, verification commands, hard repository constraints, the machine-readable assets used for application derivation, the lifecycle / upgrade assets needed to evaluate existing derived applications, the advisory assets needed to explain platform release deltas, the release-history / lineage assets needed to select supported upgrade targets, and the execution assets needed to plan or apply derived-app upgrades.

#### Scenario: A tool-driven agent loads release lineage context
- GIVEN an AI agent needs to choose a valid upgrade target for a derived application
- WHEN it reads the machine-readable AI context asset
- THEN it can identify the release-history / lineage contract, relevant schemas, playbooks, and repository-owned target-selection entrypoints without inferring them from prose alone

### Requirement: Repository Provides High-Frequency Change Playbooks
Previously:
The repository MUST provide repository-owned playbooks for the current high-frequency change scenarios, including derived-application lifecycle, release advisory, upgrade evaluation, and upgrade execution, so contributors can follow repeatable extension patterns instead of reconstructing them from scattered source files.

The repository MUST provide repository-owned playbooks for the current high-frequency change scenarios, including derived-application lifecycle, release advisory, release-history / lineage lookup, upgrade evaluation, and upgrade execution, so contributors can follow repeatable extension patterns instead of reconstructing them from scattered source files.

#### Scenario: A contributor prepares an upgrade target selection
- GIVEN a contributor needs to choose a supported release target before evaluating or executing an upgrade
- WHEN they read the corresponding repository playbook
- THEN they can identify the relevant release-history assets, repository-owned lookup entrypoints, and supported-path selection guidance
