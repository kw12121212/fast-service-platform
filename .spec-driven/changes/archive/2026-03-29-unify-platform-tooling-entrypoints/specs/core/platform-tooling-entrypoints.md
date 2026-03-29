# Platform Tooling Entrypoints

## ADDED Requirements

### Requirement: Repository Provides A Unified Tooling Façade
The repository MUST provide a unified repository-owned tooling façade for platform assembly, verification, advisory, lifecycle, and upgrade workflows.

#### Scenario: A contributor discovers the default tooling surface
- GIVEN a contributor wants to invoke a repository-owned platform workflow
- WHEN they inspect the repository-owned tooling guidance
- THEN they can identify a unified façade instead of reconstructing the workflow from many unrelated script names

### Requirement: Unified Tooling Façade Preserves Existing Workflow Coverage
The unified tooling façade MUST expose the current repository-owned assembly, verification, advisory, lifecycle, and upgrade workflow categories.

#### Scenario: A contributor invokes lifecycle tooling through the façade
- GIVEN a contributor needs to inspect upgrade targets, advisory output, or upgrade execution behavior
- WHEN they use the unified tooling façade
- THEN they can reach the corresponding repository-owned workflow without depending on direct knowledge of the underlying wrapper script name

### Requirement: Repository Retains Compatible Wrapper Entrypoints
The repository MUST retain compatible wrapper entrypoints for the existing script paths during the transition to the unified tooling façade.

#### Scenario: A contributor still invokes an older wrapper
- GIVEN a contributor or generated application still uses an existing wrapper path
- WHEN they invoke that wrapper
- THEN the repository continues to provide the expected workflow behavior instead of breaking the previously documented path
