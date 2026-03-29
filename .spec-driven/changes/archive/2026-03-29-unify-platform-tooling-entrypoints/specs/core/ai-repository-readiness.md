# AI Repository Readiness

## MODIFIED Requirements

### Requirement: Repository Provides Machine-Readable AI Context
Previously:
The repository MUST provide a machine-readable AI context manifest that exposes the current stack baseline, required guidance files, workspace boundaries, verification commands, hard repository constraints, the machine-readable assets used for application derivation, the lifecycle / upgrade assets needed to evaluate existing derived applications, the advisory assets needed to explain platform release deltas, the release-history / lineage assets needed to select supported upgrade targets, and the execution assets needed to plan or apply derived-app upgrades.

The repository MUST provide a machine-readable AI context manifest that exposes the current stack baseline, required guidance files, workspace boundaries, verification commands, hard repository constraints, the machine-readable assets used for application derivation, the lifecycle / upgrade assets needed to evaluate existing derived applications, the advisory assets needed to explain platform release deltas, the release-history / lineage assets needed to select supported upgrade targets, the execution assets needed to plan or apply derived-app upgrades, and the unified repository-owned tooling façade that serves as the default invocation surface for those workflows.

#### Scenario: A tool-driven agent loads unified tooling context
- GIVEN an AI agent needs to invoke repository-owned assembly or lifecycle tooling
- WHEN it reads the machine-readable AI context asset
- THEN it can identify the unified façade entrypoint and its role as the default invocation surface before falling back to more specific wrappers
