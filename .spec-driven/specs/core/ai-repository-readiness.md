# AI Repository Readiness

### Requirement: Repository Provides AI Contribution Quickstart
The repository MUST provide an AI-oriented quickstart that identifies required references, common task locations, the standard verification entrypoints for repository changes, and the repository-owned path for deriving an independent application from the platform.

#### Scenario: An AI contributor starts a common repository task
- GIVEN an AI contributor needs to extend or modify the repository
- WHEN it opens the AI contribution quickstart
- THEN it can identify what to read first, where common backend and frontend work lives, and which repository commands validate the change

#### Scenario: An AI contributor starts a new application from the platform
- GIVEN an AI contributor wants to derive an application instead of modifying the current baseline app
- WHEN it reads the repository's AI quickstart path
- THEN it can find the derivation workflow, the relevant machine-readable indexes, and the validation entrypoints for generated output

### Requirement: Repository Provides Machine-Readable AI Context
The repository MUST provide a machine-readable AI context manifest that exposes the current stack baseline, required guidance files, workspace boundaries, verification commands, hard repository constraints, and the machine-readable assets used for application derivation.

#### Scenario: A tool-driven agent loads repository context
- GIVEN an AI agent can consume structured repository metadata
- WHEN it reads the machine-readable AI context asset
- THEN it can determine the repository's required references, runtime assumptions, directory responsibilities, validation entrypoints, non-negotiable constraints, and application-derivation assets without inferring them from prose alone

### Requirement: Repository Provides High-Frequency Change Playbooks
The repository MUST provide repository-owned playbooks for the current high-frequency change scenarios so contributors can follow repeatable extension patterns instead of reconstructing them from scattered source files.

#### Scenario: A contributor plans a common repository change
- GIVEN a contributor needs to perform a common backend, frontend, integration-oriented, or app-derivation change
- WHEN they read the corresponding playbook
- THEN they can identify the typical files, sequence of work, and validation steps for that change type

### Requirement: Repository Provides Automated Validation Entrypoints
The repository MUST provide stable automated entrypoints for backend validation, frontend validation, full-stack validation, and derived-application assembly validation.

#### Scenario: A contributor validates a repository change
- GIVEN a contributor has modified the repository
- WHEN they run the documented validation entrypoints
- THEN they can execute backend checks, frontend checks, the expected full-stack validation path, and the derived-application assembly checks through repository-owned commands

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

### Requirement: Repository Provides Machine-Readable Module And Assembly Assets
The repository MUST provide machine-readable assets that describe the platform's module catalog and the application-assembly contract for derived applications.

#### Scenario: An AI contributor selects modules for a derived application
- GIVEN an AI contributor needs to choose which platform capabilities belong in a new application
- WHEN it reads the repository-owned structured assets
- THEN it can identify required platform core, optional built-in modules, dependency relationships, default assembly behavior, and expected validation steps

### Requirement: Repository Exposes Compatibility Assets As First-Class AI Inputs
The repository MUST expose the app assembly standard, schema assets, compatibility fixtures, validation entrypoints, and generated-app verification contract as first-class AI-readable inputs.

#### Scenario: An AI contributor prepares to implement against the assembly standard
- GIVEN an AI contributor needs to understand how conformance is checked
- WHEN it reads the repository's AI readiness path
- THEN it can identify the normative assembly contract assets, the generated-app verification contract, the compatibility suite, and the validation commands before reading a specific implementation

### Requirement: Validation Entrypoints Cover Contract Compatibility
The repository MUST provide validation entrypoints that check contract compatibility for app assembly implementations in addition to validating the current reference implementation.

#### Scenario: A contributor validates an implementation against the standard
- GIVEN a contributor has an app assembly implementation to validate
- WHEN they run the repository-owned compatibility validation path
- THEN they can determine whether the implementation satisfies the standard's observable contract rather than only whether one repository script still works

### Requirement: Repository Documents Compatible Assembly Implementations
The repository MUST identify the available compatible assembly implementations and how contributors can invoke them through repository-owned tooling.

#### Scenario: A contributor chooses an assembly implementation
- GIVEN a contributor wants to derive an application from the platform
- WHEN they read the repository's AI-ready assembly guidance
- THEN they can identify the available compatible implementations and the repository-owned way to invoke each one

### Requirement: Repository Provides Validation Paths For Each Compatible Implementation
The repository MUST provide repository-owned validation paths that let contributors verify each compatible app assembly implementation and generated-app verifier path against the standard contract.

#### Scenario: A contributor validates the Java implementation
- GIVEN a contributor has used the Java CLI assembly path
- WHEN they run the documented validation flow
- THEN they can verify the Java implementation against the standard compatibility expectations

#### Scenario: A contributor validates the generated-app verifier path
- GIVEN a contributor wants to understand whether a generated application passes repository-owned validation
- WHEN they inspect the repository's validation guidance
- THEN they can identify the current reference verifier path and the contract it is expected to satisfy
