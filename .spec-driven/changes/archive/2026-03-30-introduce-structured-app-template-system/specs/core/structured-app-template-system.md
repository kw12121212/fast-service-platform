## ADDED Requirements

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
