# AI Repository Readiness

## MODIFIED Requirements

### Requirement: Repository Provides Machine-Readable AI Context
Previously:
The repository MUST provide a machine-readable AI context manifest that exposes the current stack baseline, required guidance files, workspace boundaries, verification commands, hard repository constraints, the machine-readable assets used for application derivation, and the lifecycle / upgrade assets needed to evaluate existing derived applications.

The repository MUST provide a machine-readable AI context manifest that exposes the current stack baseline, required guidance files, workspace boundaries, verification commands, hard repository constraints, the machine-readable assets used for application derivation, the lifecycle / upgrade assets needed to evaluate existing derived applications, and the advisory assets needed to explain current platform release deltas.

#### Scenario: A tool-driven agent loads release advisory context
- GIVEN an AI agent needs to understand what changed in the current platform release
- WHEN it reads the machine-readable AI context asset
- THEN it can identify the release advisory assets, schemas, playbooks, and repository-owned advisory entrypoints without inferring them from prose alone

### Requirement: Repository Provides High-Frequency Change Playbooks
Previously:
The repository MUST provide repository-owned playbooks for the current high-frequency change scenarios, including derived-application lifecycle and upgrade evaluation, so contributors can follow repeatable extension patterns instead of reconstructing them from scattered source files.

The repository MUST provide repository-owned playbooks for the current high-frequency change scenarios, including derived-application lifecycle, release advisory, and upgrade evaluation, so contributors can follow repeatable extension patterns instead of reconstructing them from scattered source files.

#### Scenario: A contributor prepares an upgrade advisory review
- GIVEN a contributor needs to understand what changed in the current platform release before upgrading a derived application
- WHEN they read the corresponding repository playbook
- THEN they can identify the relevant release advisory assets, the expected repository-owned advisory entrypoints, and the recommended follow-up checks
