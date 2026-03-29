## MODIFIED Requirements

### Requirement: Platform Provides A Machine-Readable Module Registry
Previously: The system MUST provide a machine-readable module registry that exposes the available platform core and optional modules, their assembly roles, and their dependency expectations.

The system MUST provide a machine-readable module registry that exposes the available platform core and optional modules, their assembly roles, their dependency expectations, and the finer-grained optional business capability units that may be selected during application assembly.

#### Scenario: An AI agent chooses among decomposed optional business units
- GIVEN an AI agent wants to derive an application with only part of the delivery-management capability area
- WHEN it reads the repository's module registry
- THEN it can identify smaller optional project, ticket, or kanban capability units
- AND it can determine which of those units may be selected independently and which require declared dependencies

### Requirement: Compatible Implementations Must Satisfy Output Invariants
Previously: The system MUST define the observable output invariants that any compatible app assembly implementation must satisfy.

The system MUST define the observable output invariants that any compatible app assembly implementation must satisfy, including the requirement that selected decomposed optional business units are reflected consistently across generated routes, services, tables, and validation guidance.

#### Scenario: A contributor checks a partially selected delivery-management assembly
- GIVEN a compatible implementation assembles an application with only a subset of the decomposed optional business units
- WHEN the output is checked against the platform contract
- THEN the generated application includes only the routes, services, tables, and guidance associated with the selected units and their declared dependencies
