## MODIFIED Requirements

### Requirement: Repository Provides A Unified Tooling Façade
Previously: The repository MUST provide a unified repository-owned tooling façade for platform assembly, verification, advisory, lifecycle, and upgrade workflows.

The repository MUST provide a unified repository-owned tooling façade for platform assembly, verification, advisory, lifecycle, and upgrade workflows, and that façade MUST invoke Java-owned repository tooling implementations for those platform workflows.

#### Scenario: A contributor invokes a platform workflow through the façade
- GIVEN a contributor uses the unified tooling façade for a repository-owned platform workflow
- WHEN the façade dispatches the requested workflow
- THEN it invokes the repository's Java-owned tooling path for that workflow instead of depending on a Node implementation path

### Requirement: Repository Retains Compatible Wrapper Entrypoints
Previously: The repository MUST retain compatible wrapper entrypoints for the existing script paths during the transition to the unified tooling façade.

The repository MUST retain compatible wrapper entrypoints for the existing script paths, and those wrappers MUST behave as thin compatibility layers over the Java-owned platform tooling path rather than as separate Node-owned implementations.

#### Scenario: A contributor still invokes an older wrapper
- GIVEN a contributor or generated application still uses an existing wrapper path
- WHEN they invoke that wrapper
- THEN the wrapper reaches the same Java-owned repository workflow behavior exposed through the unified façade
