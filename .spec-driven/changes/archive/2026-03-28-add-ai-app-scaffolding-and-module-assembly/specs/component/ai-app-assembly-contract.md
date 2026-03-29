# AI App Assembly Contract

## ADDED Requirements

### Requirement: Derived Applications Use A Machine-Readable Application Manifest
The system MUST define a machine-readable application manifest contract for derived applications.
The manifest MUST let a contributor identify the target application and the selected platform modules for assembly.

#### Scenario: An AI agent describes a new application to be derived
- GIVEN an AI agent needs to derive an independent application from the platform
- WHEN it prepares the application-assembly input
- THEN it can express the target application identity and selected modules through the repository's machine-readable manifest contract

### Requirement: Platform Provides A Machine-Readable Module Registry
The system MUST provide a machine-readable module registry that exposes the available platform core and optional modules, their assembly roles, and their dependency expectations.

#### Scenario: An AI agent decides which modules it can include
- GIVEN an AI agent wants to choose modules without inferring from source directories
- WHEN it reads the repository's module registry
- THEN it can identify available modules, their classification, and any declared dependency expectations needed for assembly

### Requirement: Repository Generates An Independent Application Skeleton
The system MUST provide a repository-owned scaffolding and assembly path that generates an independent monolithic application skeleton from the application manifest and selected modules.

#### Scenario: A contributor scaffolds a new application from the platform
- GIVEN a contributor has provided a valid application-assembly input
- WHEN they run the repository-owned scaffolding and assembly path
- THEN the repository generates an independent application skeleton outside the current baseline runtime workspace
- AND the generated output reflects the selected modules instead of always copying the full default application

### Requirement: Scaffolded Output Includes Repository-Approved Validation Guidance
The system MUST define how a scaffolded application is validated after assembly.

#### Scenario: A contributor validates a generated application
- GIVEN a contributor has generated a new application from the platform
- WHEN they consult the generated output and repository documentation
- THEN they can identify the repository-approved validation commands or entrypoints for that derived application
