# Minimum Project Deliverables

### Requirement: Required Minimum Generated Artifacts
The system MUST require every V1 generated project to include tests, an administrative home page, software project management functionality, ticket management functionality, kanban management functionality, role and permission management, and user management.
The system MAY include optional demo data for demonstration or first-run setup.

#### Scenario: A generated project is checked for completeness
- GIVEN a generated V1 project is reviewed
- WHEN a contributor verifies the minimum output contract
- THEN they can confirm the presence of tests, an administrative home page, software project management functionality, ticket management functionality, kanban management functionality, role and permission management, and user management
- AND they can determine whether optional demo data is included for demonstration or setup purposes

### Requirement: Minimum Deliverables Must Be Functionally Composable
The system MUST define the minimum deliverables so they form a usable enterprise-management baseline together rather than a disconnected feature list.

#### Scenario: A contributor evaluates baseline usefulness
- GIVEN a contributor inspects the minimum deliverables together
- WHEN they check whether the baseline can support a usable internal management application
- THEN they can see that the required components combine into a coherent initial application capability

### Requirement: Minimum Deliverables Include Baseline Management Workflows
The system MUST require the V1 baseline to support baseline management workflows for the current core entities rather than limiting those deliverables to read-only display.

#### Scenario: A generated project is checked for operable baseline behavior
- GIVEN a generated V1 project is running
- WHEN a contributor performs the baseline enterprise-management workflows
- THEN they can create users, software projects, kanban boards, and tickets
- AND they can advance tickets through the minimal delivery-state flow
