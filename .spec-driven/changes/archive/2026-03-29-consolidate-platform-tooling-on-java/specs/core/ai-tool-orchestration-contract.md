## MODIFIED Requirements

### Requirement: AI Orchestration Contract Prefers Repository-Owned Tooling
Previously: The AI tool-orchestration contract MUST state that AI contributors SHOULD prefer repository-owned tooling entrypoints and compatible wrappers before attempting to reconstruct workflow behavior themselves.

The AI tool-orchestration contract MUST state that AI contributors SHOULD prefer repository-owned Java tooling entrypoints and compatible wrappers before attempting to reconstruct workflow behavior themselves.

#### Scenario: An AI contributor chooses the default runtime for a platform workflow
- GIVEN an AI contributor needs to perform a repository-owned platform workflow
- WHEN it checks the orchestration contract
- THEN it sees Java-owned repository tooling as the default execution runtime for that workflow
- AND it does not treat Node implementation paths as the repository-approved tooling runtime for platform workflows

### Requirement: AI Orchestration Contract Defines Fallback And Failure Semantics
Previously: The AI tool-orchestration contract MUST describe when an AI contributor may fall back from the unified tooling facade to a compatible wrapper and when it SHOULD stop and surface a blocker instead of guessing.

The AI tool-orchestration contract MUST describe when an AI contributor may fall back from the unified tooling facade to a compatible Java wrapper and when it SHOULD stop and surface a blocker instead of guessing.
