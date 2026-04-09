# Structured App Template System

### Requirement: Repository Defines A Structured App Template System
The repository MUST define a machine-readable structured app template system for generated applications.

#### Scenario: A contributor inspects generated-output structure
- GIVEN a contributor wants to understand how generated output is organized
- WHEN they inspect the repository-owned template-system assets
- THEN they can identify a structured model for stable templates, module-contributed fragments, slot boundaries, and customization zones

### Requirement: Template System Distinguishes Platform-Owned And Derived-Owned Areas
The repository MUST distinguish which generated-output areas remain platform-owned and which areas are intended for derived-application customization.

#### Scenario: A contributor evaluates whether a generated file may be safely customized
- GIVEN a contributor inspects a generated application path or section
- WHEN they consult the repository-owned template-system assets
- THEN they can determine whether that area is platform-managed, customization-managed, or a structured slot boundary

### Requirement: Template System Exposes Slot Boundaries As Observable Contract Facts
The repository MUST expose slot boundaries and override points as observable contract facts rather than as implementation-only conventions.

#### Scenario: An AI contributor chooses where to apply a customization
- GIVEN an AI contributor needs to customize a generated application
- WHEN it reads the template-system contract
- THEN it can identify repository-approved override points or slot hosts
- AND it does not need to guess based on one implementation's internal file-copy strategy

### Requirement: Template System Supports Upgrade-Aware Output Ownership
The repository MUST define the structured template system so lifecycle and upgrade workflows can use it to reason about safe overwrite boundaries and manual-intervention zones.

#### Scenario: A contributor evaluates an upgrade plan
- GIVEN a contributor reviews a derived-app upgrade plan
- WHEN the plan references generated-output ownership
- THEN it can rely on the structured template-system contract to distinguish safe platform updates from customization-sensitive areas

### Requirement: Default Template Classification Map Covers All Module Registry Modules
The repository MUST ensure the default derived-app template classification map includes at least one classified entry for every module declared in the module registry.

#### Scenario: A contributor checks template coverage for a module
- GIVEN a module is declared in the module registry
- WHEN a contributor inspects the default classification map
- THEN they can find at least one classified entry with a `moduleId` matching that module
- AND the entry declares an observable `unitType`, `ownership`, and `upgradeBehavior`

### Requirement: Module-Selection Configuration Is Classified As A Derived-App Customization Zone
The repository MUST classify the module-selection configuration file as a derived-app-owned customization zone in the default classification map.

#### Scenario: A contributor evaluates whether module-selection may be safely refreshed during upgrade
- GIVEN a contributor reviews a derived-app upgrade plan
- WHEN they consult the classification map entry for the module-selection configuration
- THEN they can determine it is `derived-managed` with `preserve-by-default` upgrade behavior
- AND they do not need to inspect the file content to make that determination

### Requirement: Descriptor-Driven Module Output Uses Structured Template Ownership Boundaries
The repository MUST ensure descriptor-driven management-module generation emits output through the structured app template system's platform-managed templates, module fragments, slot boundaries, and customization zones rather than through unclassified arbitrary file generation.

#### Scenario: A contributor inspects template ownership for descriptor-driven module output
- GIVEN a contributor has generated a supported management module through the repository-owned descriptor path
- WHEN they inspect the generated output against the template-system assets
- THEN they can identify which generated areas are platform-managed templates, which are module-contributed fragments, and which remain approved customization boundaries
- AND they do not need to infer ownership from undocumented file-copy behavior
