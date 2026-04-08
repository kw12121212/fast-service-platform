## MODIFIED Requirements

### Requirement: Repository Defines AI Tool-Orchestration Contract
Previously: The repository MUST define a machine-readable contract that teaches AI contributors how to orchestrate repository-owned tooling for supported platform workflows, including the step that maps structured solution input into manifest-driven assembly.

The repository MUST define a machine-readable contract that teaches AI contributors how to orchestrate repository-owned tooling for supported platform workflows, including the repository-owned planning step that turns structured solution input into explicit module and manifest-preparation decisions before manifest-driven assembly.

#### Scenario: An AI contributor starts from a structured solution input
- GIVEN an AI contributor has prepared a repository-defined solution input
- WHEN it reads the orchestration contract
- THEN it can identify the expected sequence from solution definition to repository-owned planning output, then to manifest preparation, and then to repository-owned assembly tooling
