# Delta: descriptor-driven-management-module-generation

## ADDED Requirements

### Requirement: Repository Defines A Machine-Readable Management-Module Descriptor
The repository MUST define a machine-readable management-module descriptor that expresses a bounded business-module shape for descriptor-driven generation without becoming a general-purpose low-code contract.

#### Scenario: A contributor inspects the first descriptor-driven module asset
- GIVEN a contributor wants to understand what the repository-owned descriptor-driven module path supports
- WHEN they inspect the management-module descriptor asset
- THEN they can identify the supported module shape, required fields, and declared boundaries for that descriptor
- AND they do not interpret it as an unconstrained CRUD or low-code definition language

### Requirement: Descriptor-Driven Module Generation Produces A Standalone Manifest Preparation Path
The repository MUST define the descriptor-driven management-module path so it can turn bounded descriptor facts into standalone `app-manifest` preparation and controlled module-generation inputs without replacing the manifest as the direct assembly runtime contract.

#### Scenario: A contributor prepares assembly after descriptor-driven module generation
- GIVEN a contributor has prepared valid planning or recommendation output for a supported management-module shape
- WHEN they run the repository-owned descriptor-driven module preparation path
- THEN they can identify the resulting module-generation facts and standalone `app-manifest` preparation output
- AND they can identify that repository-owned assembly tooling still expects a valid `app-manifest`

### Requirement: First Descriptor-Driven Module Shape Reuses Platform-Owned Form And Report Components
The first descriptor-driven management-module path MUST reuse the existing dynamic form and dynamic report platform capabilities for generated management-module interactions instead of introducing parallel one-off page implementations.

#### Scenario: A contributor inspects generated output for the first descriptor-driven module shape
- GIVEN a contributor has generated a supported management-module shape through the repository-owned descriptor path
- WHEN they inspect the generated module output
- THEN they can identify form and report interactions that reuse the repository-owned dynamic form and dynamic report capabilities
- AND they do not find a separate one-off form or report pattern created only for that generated module

### Requirement: First Descriptor-Driven Module Shape Excludes Workflow-Specific Generation
The first descriptor-driven management-module path MUST stay narrower than the dedicated workflow-generation roadmap item and MUST NOT require workflow-specific page generation in order to be considered complete.

#### Scenario: A contributor checks whether the first descriptor-driven path includes workflow generation
- GIVEN a contributor reviews the first descriptor-driven management-module capability
- WHEN they inspect its supported output shape
- THEN they can identify that workflow-specific generation remains outside the first descriptor-driven module scope
- AND they do not need repository-owned workflow page generation for the first descriptor-driven path to be valid

### Requirement: Descriptor-Driven Module Generation Provides Repository-Owned Example And Validation Coverage
The repository MUST provide at least one repository-owned descriptor-driven management-module example and validation coverage that checks the path as a first-class repository asset.

#### Scenario: A contributor validates the descriptor-driven module path
- GIVEN a contributor wants to verify the repository-owned descriptor-driven module generation capability
- WHEN they run the documented validation entrypoints
- THEN they can validate at least one repository-owned descriptor example through repository-owned checks
- AND they do not have to rely on ad hoc manual interpretation to determine whether the path still works
