# Project Foundation Delta

## MODIFIED Requirements

### Requirement: Repository Bootstrap Documentation
Previously: The repository MUST provide root documentation that explains the platform goal, the supported backend and frontend stack, and the intended workspace layout.

The repository MUST provide root documentation that explains the platform goal, the supported backend and frontend stack, the intended workspace layout, and the primary AI-oriented repository entrypoints for contribution and verification.

#### Scenario: Root documentation is available
- GIVEN a contributor opens the repository root
- WHEN they read the main project documentation
- THEN they can identify the project mission, technical baseline, and planned structure without reading source code
- AND they can identify where AI-oriented repository guidance and verification entrypoints begin

### Requirement: Agent Collaboration Rules
Previously: The repository MUST provide agent-readable instructions that identify required references, spec-driven workflow expectations, and AI-friendly engineering constraints.

The repository MUST provide agent-readable instructions that identify required references, spec-driven workflow expectations, AI-friendly engineering constraints, and where AI contributors can find repository-owned change guidance and validation entrypoints.

#### Scenario: An AI agent starts work in the repository
- GIVEN an AI agent is asked to change the project
- WHEN it reads the repository guidance files
- THEN it can determine which documents to read first and which conventions to follow before making changes
- AND it can locate the repository-owned readiness assets that describe common change paths and validation commands
