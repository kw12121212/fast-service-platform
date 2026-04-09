# AI Tool Orchestration Contract

### Requirement: Repository Defines AI Tool-Orchestration Contract
The repository MUST define a machine-readable contract that teaches AI contributors how to orchestrate repository-owned tooling for supported platform workflows, including the repository-owned planning step that turns structured solution input into explicit module and manifest-preparation decisions, the optional repository-owned recommendation step that may guide manifest shaping, and the bounded descriptor-driven management-module generation step that may precede manifest-driven assembly.

#### Scenario: An AI contributor chooses a workflow entrypoint
- GIVEN an AI contributor needs to perform a supported platform workflow
- WHEN it reads the repository-owned AI tool-orchestration contract
- THEN it can identify the default repository-owned entrypoint for that workflow
- AND it can determine what prerequisite assets or guidance it should inspect before invoking the tool

#### Scenario: An AI contributor starts from a structured solution input
- GIVEN an AI contributor has prepared a repository-defined solution input
- WHEN it reads the orchestration contract
- THEN it can identify the expected sequence from solution definition to repository-owned planning output, then to optional repository-owned recommendation guidance, then to optional bounded descriptor-driven module generation, then to manifest preparation, and then to repository-owned assembly tooling

### Requirement: AI Orchestration Contract Prefers Repository-Owned Tooling
The AI tool-orchestration contract MUST state that AI contributors SHOULD prefer repository-owned Java tooling entrypoints and compatible wrappers before attempting to reconstruct workflow behavior themselves.

#### Scenario: An AI contributor chooses the default runtime for a platform workflow
- GIVEN an AI contributor needs to perform a repository-owned platform workflow
- WHEN it checks the orchestration contract
- THEN it sees Java-owned repository tooling as the default execution runtime for that workflow
- AND it does not treat Node implementation paths as the repository-approved tooling runtime for platform workflows

### Requirement: AI Orchestration Contract Defines Fallback And Failure Semantics
The AI tool-orchestration contract MUST describe when an AI contributor may fall back from the unified tooling facade to a compatible Java wrapper and when it SHOULD stop and surface a blocker instead of guessing.

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
