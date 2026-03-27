# Project Foundation

### Requirement: Repository Bootstrap Documentation
The repository MUST provide root documentation that explains the platform goal, the supported backend and frontend stack, and the intended workspace layout.

#### Scenario: Root documentation is available
- GIVEN a contributor opens the repository root
- WHEN they read the main project documentation
- THEN they can identify the project mission, technical baseline, and planned structure without reading source code

### Requirement: Agent Collaboration Rules
The repository MUST provide agent-readable instructions that identify required references, spec-driven workflow expectations, and AI-friendly engineering constraints.

#### Scenario: An AI agent starts work in the repository
- GIVEN an AI agent is asked to change the project
- WHEN it reads the repository guidance files
- THEN it can determine which documents to read first and which conventions to follow before making changes

### Requirement: Reserved Full-Stack Workspace Layout
The repository MUST reserve separate backend and frontend workspaces and describe the responsibility of each workspace.

#### Scenario: A contributor plans implementation work
- GIVEN a contributor is deciding where new code should live
- WHEN they inspect the repository structure
- THEN they can distinguish the backend service workspace from the frontend application workspace
