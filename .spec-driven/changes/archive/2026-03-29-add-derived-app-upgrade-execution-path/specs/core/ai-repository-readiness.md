# AI Repository Readiness

## MODIFIED Requirements

### Requirement: Repository Provides Machine-Readable AI Context
Previously:
The repository MUST provide a machine-readable AI context manifest that exposes the current stack baseline, required guidance files, workspace boundaries, verification commands, hard repository constraints, the machine-readable assets used for application derivation, the lifecycle / upgrade assets needed to evaluate existing derived applications, and the advisory assets needed to explain current platform release deltas.

The repository MUST provide a machine-readable AI context manifest that exposes the current stack baseline, required guidance files, workspace boundaries, verification commands, hard repository constraints, the machine-readable assets used for application derivation, the lifecycle / upgrade assets needed to evaluate existing derived applications, the advisory assets needed to explain current platform release deltas, and the execution assets needed to plan or apply derived-app upgrades.

#### Scenario: A tool-driven agent loads upgrade execution context
- GIVEN an AI agent needs to prepare or run a derived-app upgrade
- WHEN it reads the machine-readable AI context asset
- THEN it can identify the upgrade plan contract, playbooks, repository-owned execution entrypoints, and post-upgrade validation paths without inferring them from prose alone

### Requirement: Repository Provides High-Frequency Change Playbooks
Previously:
The repository MUST provide repository-owned playbooks for the current high-frequency change scenarios, including derived-application lifecycle, release advisory, and upgrade evaluation, so contributors can follow repeatable extension patterns instead of reconstructing them from scattered source files.

The repository MUST provide repository-owned playbooks for the current high-frequency change scenarios, including derived-application lifecycle, release advisory, upgrade evaluation, and upgrade execution, so contributors can follow repeatable extension patterns instead of reconstructing them from scattered source files.

#### Scenario: A contributor prepares a derived-app upgrade execution
- GIVEN a contributor needs to apply a repository-owned upgrade path to a derived application
- WHEN they read the corresponding repository playbook
- THEN they can identify the machine-readable plan inputs, execution entrypoints, manual-intervention checkpoints, and post-upgrade verification steps
