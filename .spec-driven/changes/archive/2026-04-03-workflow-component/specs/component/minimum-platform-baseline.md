# Minimum Platform Baseline

## MODIFIED Requirements

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

### Requirement: Platform Baseline Includes A Reusable Workflow Component
The system MUST include the workflow component as a reusable platform capability available to derived applications that need bounded task or approval flows, so AI-generated enterprise workflows can present consistent status, assignee, action, and history UI without re-implementing the same interaction pattern per module.

#### Scenario: A contributor checks the platform baseline for reusable workflow capabilities
- GIVEN a contributor reviews the repository's reusable component baseline
- WHEN they check whether workflow execution UI is a platform-owned capability
- THEN they can identify the workflow component as a reusable baseline capability
- AND they can confirm it is accessible from the platform component index rather than embedded in a single page
