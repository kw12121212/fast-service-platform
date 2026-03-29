# AI App Assembly Contract

### Requirement: Derived Applications Use A Machine-Readable Application Manifest
The system MUST define a machine-readable application manifest contract for derived applications.
The manifest MUST let a contributor identify the target application and the selected platform modules for assembly.

#### Scenario: An AI agent describes a new application to be derived
- GIVEN an AI agent needs to derive an independent application from the platform
- WHEN it prepares the application-assembly input
- THEN it can express the target application identity and selected modules through the repository's machine-readable manifest contract

### Requirement: Platform Provides A Machine-Readable Module Registry
The system MUST provide a machine-readable module registry that exposes the available platform core and optional modules, their assembly roles, their dependency expectations, and the finer-grained optional business capability units that may be selected during application assembly.

#### Scenario: An AI agent decides which modules it can include
- GIVEN an AI agent wants to choose modules without inferring from source directories
- WHEN it reads the repository's module registry
- THEN it can identify available modules, their classification, and any declared dependency expectations needed for assembly

#### Scenario: An AI agent chooses among decomposed optional business units
- GIVEN an AI agent wants to derive an application with only part of the delivery-management capability area
- WHEN it reads the repository's module registry
- THEN it can identify smaller optional project, ticket, or kanban capability units
- AND it can determine which of those units may be selected independently and which require declared dependencies

### Requirement: Repository Generates An Independent Application Skeleton
The system MUST provide a repository-owned scaffolding and assembly path that generates an independent monolithic application skeleton from the application manifest and selected modules, and that generated output MUST expose the machine-readable lifecycle metadata needed for later upgrade evaluation, with Java as the repository-owned tooling runtime for that platform workflow.

#### Scenario: A contributor scaffolds a new application through the repository-owned path
- GIVEN a contributor has provided a valid application-assembly input
- WHEN they run the repository-owned scaffolding and assembly path
- THEN the platform workflow is executed through the repository's Java-owned tooling runtime
- AND the generated output still reflects the selected modules instead of always copying the full default application

### Requirement: Scaffolded Output Includes Repository-Approved Validation Guidance
The system MUST define scaffolded-application validation and lifecycle guidance through language-neutral contracts that may be satisfied by multiple compatible implementations rather than only through a single verifier script.

#### Scenario: A contributor validates a generated application
- GIVEN a contributor has generated a new application from the platform
- WHEN they consult the generated output and repository documentation
- THEN they can identify the repository-approved validation commands or entrypoints for that derived application
- AND they can identify the machine-readable verification contract that those entrypoints are expected to satisfy

#### Scenario: A contributor validates a generated application with a non-Node verifier
- GIVEN a contributor has generated a new application from the platform
- WHEN they choose a compatible verifier implementation that targets the same verification contract
- THEN they can validate the generated application without depending on the internal structure of the Node verifier

#### Scenario: A contributor prepares to upgrade a generated application
- GIVEN a contributor has generated an application from the platform
- WHEN they consult the generated output and repository documentation
- THEN they can identify the repository-approved lifecycle or upgrade entrypoints for evaluating that derived application's compatibility with later platform releases

### Requirement: App Assembly Contract Is Implementation-Independent
The system MUST define its app assembly contract independently from any single implementation language or script structure.

#### Scenario: A contributor evaluates whether the contract depends on Node internals
- GIVEN a contributor reviews the app assembly definition
- WHEN they inspect what counts as the normative contract
- THEN they can identify the required machine-readable assets and observable behaviors without depending on Node-specific internal implementation details

### Requirement: Machine-Readable Contract Distinguishes Normative Inputs From Reference Implementations
The system MUST identify which machine-readable assets are normative assembly and verification inputs and which repository-owned Java tooling paths implement those workflows, without treating Node implementation scripts as the repository-owned platform tooling runtime.

#### Scenario: An AI or multi-language implementer reads the assembly assets
- GIVEN a contributor wants to build a compatible implementation in another language
- WHEN they inspect the repository-owned assembly and verification assets
- THEN they can distinguish the standard inputs and required outputs from the current reference implementation details

### Requirement: Compatible Implementations Must Satisfy Output Invariants
The system MUST define the observable output invariants that any compatible app assembly implementation must satisfy, including the requirement that selected decomposed optional business units are reflected consistently across generated routes, services, tables, and validation guidance.

#### Scenario: A contributor checks whether two implementations produce compatible outputs
- GIVEN two different implementations assemble an application from the same valid manifest
- WHEN the outputs are checked against the platform contract
- THEN both can be evaluated by the same output invariants rather than by matching one implementation's internal code paths

#### Scenario: A contributor checks a partially selected delivery-management assembly
- GIVEN a compatible implementation assembles an application with only a subset of the decomposed optional business units
- WHEN the output is checked against the platform contract
- THEN the generated application includes only the routes, services, tables, and guidance associated with the selected units and their declared dependencies

### Requirement: Repository Supports Multiple Compatible Assembly Implementations
The system MUST support multiple compatible app assembly implementations against the same standard contract and compatibility suite.

#### Scenario: A contributor checks whether the assembly standard is single-implementation only
- GIVEN a contributor reviews the repository's app assembly capability
- WHEN they inspect the supported implementations
- THEN they can see that more than one implementation may target the same standard contract without redefining the contract itself

### Requirement: Java CLI May Implement The Standard
The system MAY provide a Java CLI as a compatible app assembly implementation as long as it satisfies the same observable output invariants and compatibility checks as the existing reference implementation.

#### Scenario: A contributor uses the Java CLI to assemble an application
- GIVEN a contributor wants to assemble a derived application through the Java toolchain path
- WHEN they run the repository-owned Java CLI with a valid manifest
- THEN the Java CLI produces a derived application that can be checked by the same compatibility rules used for other implementations
