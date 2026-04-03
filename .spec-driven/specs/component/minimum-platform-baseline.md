# Minimum Platform Baseline

### Requirement: Minimum Reusable Enterprise Component Baseline
The system MUST provide a reusable enterprise application platform core that includes tests, an administrative home page, role and permission management, and user management.
The role and permission management portion of that core baseline MUST support minimum manageable RBAC workflows rather than a read-only diagnostic view.
The system MAY provide decomposed software project, ticket, and kanban capability units as optional built-in modules during application assembly instead of requiring the entire delivery-management area in every derived application.
The system MAY include optional demo data for demonstration or first-run setup.

#### Scenario: The platform baseline is checked for completeness
- GIVEN a contributor reviews the repository's current component baseline
- WHEN they verify what AI can reliably reuse from the platform
- THEN they can confirm the presence of tests, an administrative home page, role and permission management, and user management in the required platform core
- AND they can identify decomposed project, ticket, and kanban capability units as optional built-in modules rather than mandatory requirements for every derived application
- AND they can determine that the RBAC portion of the baseline is manageable rather than limited to manual inspection

#### Scenario: The platform baseline is checked after delivery-management decomposition
- GIVEN a contributor reviews the repository's current component baseline
- WHEN they verify what remains required core versus optional business capability
- THEN they can still identify the required admin, user, and RBAC platform core
- AND they can identify the decomposed delivery-management units as optional built-in modules instead of mandatory baseline requirements

### Requirement: Minimum Platform Baseline Must Be Functionally Composable
The system MUST define the minimum reusable platform baseline so the required core and selected optional modules form a usable enterprise-management capability rather than a disconnected feature list.

#### Scenario: A contributor evaluates platform usefulness
- GIVEN a contributor inspects the minimum baseline together
- WHEN they check whether the baseline can support a usable internal management application
- THEN they can see that the required platform core combines into a coherent initial application capability
- AND they can see that selected optional modules extend that capability without changing the core dependency boundary

### Requirement: Minimum Platform Baseline Includes Management Workflows
The system MUST require every derived application to support the core user and RBAC management workflows rather than limiting those components to read-only display.
When software project management, kanban management, and ticket management modules are selected, the system MUST support their baseline creation and ticket-state progression workflows.

#### Scenario: The platform baseline is checked for operable behavior
- GIVEN a derived application baseline is running
- WHEN a contributor performs the baseline enterprise-management workflows
- THEN they can create users
- AND they can manage roles and permissions without falling back to a read-only diagnostic path

#### Scenario: Delivery modules are selected in a derived application
- GIVEN a derived application includes software project, kanban, and ticket modules
- WHEN a contributor performs the delivery-management workflows
- THEN they can create software projects, kanban boards, and tickets
- AND they can advance tickets through the minimal delivery-state flow

### Requirement: Platform Baseline Includes A Reusable Dynamic Form Component
The system MUST include the dynamic form component as a reusable platform capability available to any derived application, so that AI-generated enterprise management workflows can produce consistent, backend-connected forms from declarative descriptors without re-implementing widget selection, validation, or mutation feedback per entity.

#### Scenario: A contributor checks the platform baseline for reusable form capabilities
- GIVEN a contributor reviews the repository's reusable component baseline
- WHEN they check whether form generation is a platform-owned capability
- THEN they can identify the dynamic form component as a reusable baseline capability
- AND they can confirm it is accessible from the platform component index rather than embedded in a single page

### Requirement: Platform Baseline Includes A Reusable Dynamic Report Component
The system MUST include the dynamic report component as a reusable platform capability available to any derived application, so that AI-generated enterprise management workflows can produce consistent summary cards, tables, and basic charts from declarative descriptors and caller-provided aggregated results.

#### Scenario: A contributor checks the platform baseline for reusable report capabilities
- GIVEN a contributor reviews the repository's reusable component baseline
- WHEN they check whether report generation is a platform-owned capability
- THEN they can identify the dynamic report component as a reusable baseline capability
- AND they can confirm it is accessible from the platform component index rather than embedded in a single page

### Requirement: Platform Baseline Includes A Reusable Workflow Component
The system MUST include the workflow component as a reusable platform capability available to derived applications that need bounded task or approval flows, so AI-generated enterprise workflows can present consistent status, assignee, action, and history UI without re-implementing the same interaction pattern per module.

#### Scenario: A contributor checks the platform baseline for reusable workflow capabilities
- GIVEN a contributor reviews the repository's reusable component baseline
- WHEN they check whether workflow execution UI is a platform-owned capability
- THEN they can identify the workflow component as a reusable baseline capability
- AND they can confirm it is accessible from the platform component index rather than embedded in a single page

### Requirement: Current Runnable Baseline Remains Reproducible As A Default Assembly
The system MUST preserve a default assembly profile that reproduces the repository's current runnable baseline application behavior even when the optional delivery-management area is decomposed into smaller module units.
The system MUST provide a repository-owned baseline demo path that allows contributors to demonstrate that default assembly behavior to human reviewers without reconstructing the baseline from scattered setup notes.

#### Scenario: A contributor checks for regression after optional modules are introduced
- GIVEN the platform introduces optional module assembly
- WHEN a contributor generates or runs the default assembly profile
- THEN they can still obtain the current baseline application behavior without manually reconstructing module selections

#### Scenario: A contributor prepares a baseline product demonstration
- GIVEN a contributor wants to show the current platform baseline to a human reviewer
- WHEN they use the repository-owned baseline demo path
- THEN they can launch and demonstrate the default baseline assembly behavior as one coherent enterprise-management application
- AND they can show the required core and current baseline delivery-management capabilities together without inventing an ad hoc sample
