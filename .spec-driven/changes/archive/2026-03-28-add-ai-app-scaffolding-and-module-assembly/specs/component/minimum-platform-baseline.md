# Minimum Platform Baseline

## MODIFIED Requirements

### Requirement: Minimum Platform Baseline Separates Required Core From Optional Business Modules
The system MUST define a minimum required platform core that is distinct from optional built-in business modules used during application assembly.

#### Scenario: A contributor checks what every derived application must include
- GIVEN a contributor reviews the platform baseline for derived applications
- WHEN they inspect the minimum baseline contract
- THEN they can distinguish required platform core capabilities from optional business modules
- AND they do not need to assume that every current domain in the repository is mandatory for every derived application

### Requirement: Delivery-Management Domains May Be Selected During Assembly
The system MAY provide software project management, ticket management, and kanban management as optional built-in modules selectable during application assembly instead of requiring them in every derived application.

#### Scenario: An AI agent assembles an application without delivery-management modules
- GIVEN an AI agent derives a new application with a subset of available built-in modules
- WHEN it omits software project management, ticket management, and kanban management
- THEN the platform still provides a valid derived-application baseline if all required core modules are present

### Requirement: Current Runnable Baseline Remains Reproducible As A Default Assembly
The system MUST preserve a default assembly profile that reproduces the repository's current runnable baseline application behavior.

#### Scenario: A contributor checks for regression after optional modules are introduced
- GIVEN the platform introduces optional module assembly
- WHEN a contributor generates or runs the default assembly profile
- THEN they can still obtain the current baseline application behavior without manually reconstructing module selections
