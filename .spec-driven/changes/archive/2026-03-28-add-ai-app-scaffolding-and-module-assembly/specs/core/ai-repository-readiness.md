# AI Repository Readiness

## MODIFIED Requirements

### Requirement: Repository Provides AI-Oriented Application Derivation Quickstart
The repository MUST provide an AI-oriented derivation quickstart that explains how to create an independent application from the platform, what to read first, and which repository-owned commands validate the derived output.

#### Scenario: An AI contributor starts a new application from the platform
- GIVEN an AI contributor wants to derive an application instead of modifying the current baseline app
- WHEN it reads the repository's AI quickstart path
- THEN it can find the derivation workflow, the relevant machine-readable indexes, and the validation entrypoints for generated output

### Requirement: Repository Provides Machine-Readable Module And Assembly Assets
The repository MUST provide machine-readable assets that describe the platform's module catalog and the application-assembly contract for derived applications.

#### Scenario: An AI contributor selects modules for a derived application
- GIVEN an AI contributor needs to choose which platform capabilities belong in a new application
- WHEN it reads the repository-owned structured assets
- THEN it can identify required platform core, optional built-in modules, dependency relationships, default assembly behavior, and expected validation steps

### Requirement: Repository Provides Validation Entrypoints For Derived Applications
The repository MUST provide stable repository-owned validation entrypoints for scaffolded or assembled application outputs.

#### Scenario: A contributor validates a newly scaffolded application
- GIVEN a contributor has generated a derived application from the platform
- WHEN they run the documented validation path for generated output
- THEN they can verify that the scaffolded application satisfies the platform's assembly contract through repository-owned commands
