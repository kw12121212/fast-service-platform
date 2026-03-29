# AI Repository Readiness

## MODIFIED Requirements

### Requirement: Repository Provides Machine-Readable AI Context
Previously:
The repository MUST provide a machine-readable AI context manifest that exposes the current stack baseline, required guidance files, workspace boundaries, verification commands, hard repository constraints, and the machine-readable assets used for application derivation.

The repository MUST provide a machine-readable AI context manifest that exposes the current stack baseline, required guidance files, workspace boundaries, verification commands, hard repository constraints, the machine-readable assets used for application derivation, and the lifecycle / upgrade assets needed to evaluate existing derived applications.

#### Scenario: A tool-driven agent loads lifecycle context
- GIVEN an AI agent needs to understand whether a generated application can be upgraded
- WHEN it reads the machine-readable AI context asset
- THEN it can identify the lifecycle contract, relevant schemas, playbooks, and repository-owned upgrade evaluation entrypoints without inferring them from prose alone

### Requirement: Repository Provides High-Frequency Change Playbooks
Previously:
The repository MUST provide repository-owned playbooks for the current high-frequency change scenarios so contributors can follow repeatable extension patterns instead of reconstructing them from scattered source files.

The repository MUST provide repository-owned playbooks for the current high-frequency change scenarios, including derived-application lifecycle and upgrade evaluation, so contributors can follow repeatable extension patterns instead of reconstructing them from scattered source files.

#### Scenario: A contributor plans a derived-app upgrade
- GIVEN a contributor needs to evaluate or prepare an upgrade for a derived application
- WHEN they read the corresponding repository playbook
- THEN they can identify the required metadata, the expected repository-owned entrypoints, and the validation steps for that lifecycle task
