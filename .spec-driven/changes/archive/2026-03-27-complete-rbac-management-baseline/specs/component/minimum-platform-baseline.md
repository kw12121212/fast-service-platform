# Minimum Platform Baseline Delta

## MODIFIED Requirements

### Requirement: Minimum Reusable Enterprise Component Baseline
Previously: The system MUST provide a reusable enterprise application component baseline that includes tests, an administrative home page, software project management functionality, ticket management functionality, kanban management functionality, role and permission management, and user management.
The system MAY include optional demo data for demonstration or first-run setup.

The system MUST provide a reusable enterprise application component baseline that includes tests, an administrative home page, software project management functionality, ticket management functionality, kanban management functionality, role and permission management, and user management.
The role and permission management portion of that baseline MUST support minimum manageable RBAC workflows rather than a read-only diagnostic view.
The system MAY include optional demo data for demonstration or first-run setup.

#### Scenario: The platform baseline is checked for completeness
- GIVEN a contributor reviews the repository's current component baseline
- WHEN they verify what AI can reliably reuse from the platform
- THEN they can confirm the presence of tests, an administrative home page, software project management functionality, ticket management functionality, kanban management functionality, role and permission management, and user management
- AND they can determine that the RBAC portion of the baseline is manageable rather than limited to manual inspection
