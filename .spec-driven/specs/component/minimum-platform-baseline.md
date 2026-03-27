# Minimum Platform Baseline

### Requirement: Minimum Reusable Enterprise Component Baseline
The system MUST provide a reusable enterprise application component baseline that includes tests, an administrative home page, software project management functionality, ticket management functionality, kanban management functionality, role and permission management, and user management.
The role and permission management portion of that baseline MUST support minimum manageable RBAC workflows rather than a read-only diagnostic view.
The system MAY include optional demo data for demonstration or first-run setup.

#### Scenario: The platform baseline is checked for completeness
- GIVEN a contributor reviews the repository's current component baseline
- WHEN they verify what AI can reliably reuse from the platform
- THEN they can confirm the presence of tests, an administrative home page, software project management functionality, ticket management functionality, kanban management functionality, role and permission management, and user management
- AND they can determine that the RBAC portion of the baseline is manageable rather than limited to manual inspection

### Requirement: Minimum Platform Baseline Must Be Functionally Composable
The system MUST define the minimum reusable platform baseline so it forms a usable enterprise-management capability rather than a disconnected feature list.

#### Scenario: A contributor evaluates platform usefulness
- GIVEN a contributor inspects the minimum baseline together
- WHEN they check whether the baseline can support a usable internal management application
- THEN they can see that the required components combine into a coherent initial application capability

### Requirement: Minimum Platform Baseline Includes Management Workflows
The system MUST require the minimum platform baseline to support baseline management workflows for the current core entities rather than limiting those components to read-only display.

#### Scenario: The platform baseline is checked for operable behavior
- GIVEN the repository baseline is running
- WHEN a contributor performs the baseline enterprise-management workflows
- THEN they can create users, software projects, kanban boards, and tickets
- AND they can advance tickets through the minimal delivery-state flow
