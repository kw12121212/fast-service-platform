# AI Tool Orchestration Contract

### Requirement: Repository Defines AI Tool-Orchestration Contract
The repository MUST define a machine-readable contract that teaches AI contributors how to orchestrate repository-owned tooling for supported platform workflows.

#### Scenario: An AI contributor chooses a workflow entrypoint
- GIVEN an AI contributor needs to perform a supported platform workflow
- WHEN it reads the repository-owned AI tool-orchestration contract
- THEN it can identify the default repository-owned entrypoint for that workflow
- AND it can determine what prerequisite assets or guidance it should inspect before invoking the tool

### Requirement: AI Orchestration Contract Prefers Repository-Owned Tooling
The AI tool-orchestration contract MUST state that AI contributors SHOULD prefer repository-owned tooling entrypoints and compatible wrappers before attempting to reconstruct workflow behavior themselves.

#### Scenario: An AI contributor considers bypassing repository tooling
- GIVEN an AI contributor wants to complete assembly, verification, advisory, lifecycle, or upgrade work
- WHEN it checks the repository-owned orchestration contract
- THEN it sees that repository-owned tooling is the default execution path
- AND it does not need to infer a direct implementation path from scattered repository source files

### Requirement: AI Orchestration Contract Defines Fallback And Failure Semantics
The AI tool-orchestration contract MUST describe when an AI contributor may fall back from the unified tooling facade to a compatible wrapper and when it SHOULD stop and surface a blocker instead of guessing.

#### Scenario: The unified tooling facade cannot complete a workflow
- GIVEN an AI contributor invokes a supported platform workflow through the default repository-owned entrypoint
- WHEN that entrypoint is unavailable or returns a blocker that the contract classifies as non-recoverable
- THEN the orchestration contract tells the contributor whether a compatible wrapper is allowed
- AND if no compatible fallback is allowed, the contributor stops and reports the blocker instead of inventing a replacement workflow

### Requirement: AI Orchestration Contract Covers Supported Workflow Categories
The AI tool-orchestration contract MUST cover the repository's supported assembly, generated-app verification, release advisory, upgrade-target selection, upgrade evaluation, and upgrade execution workflow categories.

#### Scenario: An AI contributor plans an upgrade workflow
- GIVEN an AI contributor needs to prepare or perform a derived-app upgrade task
- WHEN it reads the repository-owned orchestration contract
- THEN it can identify the supported sequence across target lookup, advisory, evaluation, and execution instead of treating those steps as unrelated commands
