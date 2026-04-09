# AI App Assembly Contract

### Requirement: Derived Applications Use A Machine-Readable Application Manifest
The system MUST define a machine-readable application manifest contract for derived applications.
The manifest MUST let a contributor identify the target application and the selected platform modules for assembly, and it MUST remain the direct input consumed by repository-owned assembly tooling even when higher-level AI solution input assets, solution-to-manifest planning assets, solution-to-manifest recommendation assets, and descriptor-driven management-module generation assets are present.

#### Scenario: An AI agent describes a new application to be derived
- GIVEN an AI agent needs to derive an independent application from the platform
- WHEN it prepares the application-assembly input
- THEN it can express the target application identity and selected modules through the repository's machine-readable manifest contract

#### Scenario: An AI contributor maps structured solution input into assembly input
- GIVEN an AI contributor starts from a repository-defined structured solution input
- WHEN it prepares the actual assembly request
- THEN it produces an `app-manifest` that remains the direct input to repository-owned assembly tooling
- AND it does not bypass the manifest layer by treating the higher-level solution input as the assembly runtime contract

#### Scenario: An AI contributor prepares assembly after reviewing planning output
- GIVEN an AI contributor has prepared a repository-owned planning artifact from a valid structured solution input
- WHEN it prepares the actual assembly request
- THEN it produces a standalone `app-manifest` for repository-owned assembly tooling
- AND it does not submit the planning artifact itself as the assembly runtime input

#### Scenario: An AI contributor prepares assembly after reviewing recommendation output
- GIVEN an AI contributor has prepared a repository-owned recommendation artifact from valid planning output
- WHEN it prepares the actual assembly request
- THEN it produces a standalone `app-manifest` for repository-owned assembly tooling
- AND it does not submit the recommendation artifact itself as the assembly runtime input

#### Scenario: An AI contributor prepares assembly after reviewing descriptor-driven module output
- GIVEN an AI contributor has prepared repository-owned descriptor-driven management-module output from valid planning or recommendation facts
- WHEN it prepares the actual assembly request
- THEN it produces a standalone `app-manifest` for repository-owned assembly tooling
- AND it does not submit the descriptor artifact itself as the assembly runtime input

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
The system MUST require any repository-owned committed derived-application example to preserve explicit assembly provenance so contributors can identify which assembly input and repository-owned entrypoint produced that example.
The system MUST allow a bound software project to invoke that same repository-owned scaffolding and assembly path through a project-scoped lifecycle workflow, while keeping `app-manifest` as the direct assembly runtime input.

#### Scenario: A contributor scaffolds a new application through the repository-owned path
- GIVEN a contributor has provided a valid application-assembly input
- WHEN they run the repository-owned scaffolding and assembly path
- THEN the platform workflow is executed through the repository's Java-owned tooling runtime
- AND the generated output still reflects the selected modules instead of always copying the full default application

#### Scenario: A contributor audits a repository-owned demo derived application
- GIVEN the repository keeps a committed derived-application example for demonstration
- WHEN a contributor inspects that example and its guide
- THEN they can identify the manifest or equivalent assembly input used to produce it
- AND they can identify the repository-owned assembly entrypoint used to generate it
- AND they do not need to infer whether the example was created through a special undocumented path

#### Scenario: A contributor invokes assembly from a bound project context
- GIVEN a contributor has provided a valid application-assembly input
- AND a software project is bound to a local Git repository
- WHEN they run the project-scoped assembly workflow
- THEN it uses the same repository-owned scaffolding and assembly path
- AND the project-scoped workflow does not replace `app-manifest` with a different runtime contract

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
The system MUST identify which machine-readable assets are normative assembly and verification inputs, which higher-level AI solution input assets, solution-to-manifest planning assets, solution-to-manifest recommendation assets, and descriptor-driven management-module generation assets may precede assembly, and which repository-owned Java tooling paths implement those workflows, without treating those upstream planning, recommendation, or descriptor-generation assets as the assembly runtime contract itself.

#### Scenario: An AI or multi-language implementer reads the assembly assets
- GIVEN a contributor wants to build a compatible implementation in another language
- WHEN they inspect the repository-owned assembly and verification assets
- THEN they can distinguish the standard inputs and required outputs from the current reference implementation details

#### Scenario: An AI or multi-language implementer reads the planning and assembly assets
- GIVEN a contributor wants to build or inspect a compatible planning and assembly workflow
- WHEN they inspect the repository-owned planning, assembly, and verification assets
- THEN they can distinguish the upstream planning assets from the direct assembly runtime contract
- AND they can identify that repository-owned assembly tooling still expects a valid `app-manifest`

#### Scenario: An AI or multi-language implementer reads the recommendation and assembly assets
- GIVEN a contributor wants to build or inspect a compatible planning, recommendation, and assembly workflow
- WHEN they inspect the repository-owned recommendation, planning, assembly, and verification assets
- THEN they can distinguish the upstream recommendation guidance from the direct assembly runtime contract
- AND they can identify that repository-owned assembly tooling still expects a valid `app-manifest`

#### Scenario: An AI or multi-language implementer reads the descriptor and assembly assets
- GIVEN a contributor wants to build or inspect a compatible descriptor-driven planning and assembly workflow
- WHEN they inspect the repository-owned descriptor-generation, planning, assembly, and verification assets
- THEN they can distinguish the upstream descriptor-generation assets from the direct assembly runtime contract
- AND they can identify that repository-owned assembly tooling still expects a valid `app-manifest`

### Requirement: Compatible Implementations Must Satisfy Output Invariants
The system MUST define the observable output invariants that any compatible app assembly implementation must satisfy, including the requirement that selected decomposed optional business units are reflected consistently across generated routes, services, tables, validation guidance, and the structured template-layer ownership model exposed for generated output.

#### Scenario: A contributor checks whether two implementations produce compatible outputs
- GIVEN two different implementations assemble an application from the same valid manifest
- WHEN the outputs are checked against the platform contract
- THEN both can be evaluated by the same output invariants rather than by matching one implementation's internal code paths

#### Scenario: A contributor checks a partially selected delivery-management assembly
- GIVEN a compatible implementation assembles an application with only a subset of the decomposed optional business units
- WHEN the output is checked against the platform contract
- THEN the generated application includes only the routes, services, tables, and guidance associated with the selected units and their declared dependencies

#### Scenario: A contributor checks output ownership boundaries after assembly
- GIVEN a compatible implementation assembles a derived application
- WHEN the output is checked against the platform contract
- THEN the contributor can identify which generated areas are platform-managed template output, which are module-contributed fragments, and which are intended customization boundaries

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

### Requirement: Partial-Module Assembly Output Excludes Omitted Module Artifacts
The system MUST ensure that when optional delivery-management modules are omitted from the selected profile, the generated output contains no frontend routes, frontend navigation items, database table definitions, or backend service registrations associated with those omitted modules.

#### Scenario: A contributor assembles an application without delivery-management modules
- GIVEN a contributor provides an assembly manifest that selects a profile without project, ticket, or kanban modules
- WHEN the repository-owned assembly tooling generates the derived application
- THEN the generated frontend source contains no route registrations for /projects, /tickets, or /kanban
- AND the generated frontend source contains no nav items for project, ticket, or kanban
- AND the generated SQL init contains no software_project, kanban_board, or ticket table definitions
- AND the generated SQL init contains no project_service, kanban_service, or ticket_service registrations

### Requirement: Assembly Emits A Module-Selection Config For The Frontend
The system MUST emit a TypeScript module-selection config file as part of the generated frontend source, encoding which optional modules are active for the assembled application, so that the frontend router and navigation can be driven by that config rather than hardcoded assumptions.

#### Scenario: A contributor inspects the generated module-selection config
- GIVEN a contributor assembles an application from any valid profile
- WHEN they inspect the generated frontend source
- THEN they can find a TypeScript module-selection config that explicitly names the active state of each optional module
- AND the config values match the selected profile from the assembly manifest
