## MODIFIED Requirements

### Requirement: Minimum Reusable Enterprise Component Baseline
Previously: The system MUST provide a reusable enterprise application platform core that includes tests, an administrative home page, role and permission management, and user management.
The role and permission management portion of that core baseline MUST support minimum manageable RBAC workflows rather than a read-only diagnostic view.
The system MAY provide software project management, ticket management, and kanban management as optional built-in modules during application assembly instead of requiring them in every derived application.
The system MAY include optional demo data for demonstration or first-run setup.

The system MUST provide a reusable enterprise application platform core that includes tests, an administrative home page, role and permission management, and user management.
The role and permission management portion of that core baseline MUST support minimum manageable RBAC workflows rather than a read-only diagnostic view.
The system MAY provide decomposed software project, ticket, and kanban capability units as optional built-in modules during application assembly instead of requiring the entire delivery-management area in every derived application.
The system MAY include optional demo data for demonstration or first-run setup.

#### Scenario: The platform baseline is checked after delivery-management decomposition
- GIVEN a contributor reviews the repository's current component baseline
- WHEN they verify what remains required core versus optional business capability
- THEN they can still identify the required admin, user, and RBAC platform core
- AND they can identify the decomposed delivery-management units as optional built-in modules instead of mandatory baseline requirements

### Requirement: Current Runnable Baseline Remains Reproducible As A Default Assembly
Previously: The system MUST preserve a default assembly profile that reproduces the repository's current runnable baseline application behavior.

The system MUST preserve a default assembly profile that reproduces the repository's current runnable baseline application behavior even when the optional delivery-management area is decomposed into smaller module units.
