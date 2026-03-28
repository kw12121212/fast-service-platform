# AI Repository Readiness

### Requirement: Repository Provides AI Contribution Quickstart
The repository MUST provide an AI-oriented quickstart that identifies required references, common task locations, and the standard verification entrypoints for repository changes.

#### Scenario: An AI contributor starts a common repository task
- GIVEN an AI contributor needs to extend or modify the repository
- WHEN it opens the AI contribution quickstart
- THEN it can identify what to read first, where common backend and frontend work lives, and which repository commands validate the change

### Requirement: Repository Provides Machine-Readable AI Context
The repository MUST provide a machine-readable AI context manifest that exposes the current stack baseline, required guidance files, workspace boundaries, verification commands, and hard repository constraints.

#### Scenario: A tool-driven agent loads repository context
- GIVEN an AI agent can consume structured repository metadata
- WHEN it reads the machine-readable AI context asset
- THEN it can determine the repository's required references, runtime assumptions, directory responsibilities, validation entrypoints, and non-negotiable constraints without inferring them from prose alone

### Requirement: Repository Provides High-Frequency Change Playbooks
The repository MUST provide repository-owned playbooks for the current high-frequency change scenarios so contributors can follow repeatable extension patterns instead of reconstructing them from scattered source files.

#### Scenario: A contributor plans a common repository change
- GIVEN a contributor needs to perform a common backend, frontend, or integration-oriented change
- WHEN they read the corresponding playbook
- THEN they can identify the typical files, sequence of work, and validation steps for that change type

### Requirement: Repository Provides Automated Validation Entrypoints
The repository MUST provide stable automated entrypoints for backend validation, frontend validation, and full-stack validation.

#### Scenario: A contributor validates a repository change
- GIVEN a contributor has modified the repository
- WHEN they run the documented validation entrypoints
- THEN they can execute backend checks, frontend checks, and the expected full-stack validation path through repository-owned commands

### Requirement: Repository Provides Automated Full-Stack Smoke Path
The repository MUST provide an automated smoke-validation path that exercises the current backend-frontend integration boundary through the active `/service/*` route contract.

#### Scenario: A contributor verifies integration behavior
- GIVEN the repository's current backend and frontend are available in the local environment
- WHEN the contributor runs the documented smoke-validation entrypoint
- THEN the repository validates the current end-to-end integration path through `/service/*` instead of limiting verification to isolated backend or frontend checks

### Requirement: Repository Provides AI-Oriented Troubleshooting Guidance
The repository MUST provide troubleshooting guidance for the current local setup so contributors can resolve common environment, dependency, and integration failures without guessing.

#### Scenario: A contributor hits a common local failure
- GIVEN a contributor encounters a known setup or validation failure in the current repository environment
- WHEN they consult the repository troubleshooting guidance
- THEN they can identify the likely cause and the repository-approved recovery path for that failure class
