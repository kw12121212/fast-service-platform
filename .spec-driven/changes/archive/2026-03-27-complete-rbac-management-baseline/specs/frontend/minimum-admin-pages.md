# Minimum Admin Pages Delta

## MODIFIED Requirements

### Requirement: Frontend Makes Minimum V1 Business Pages Visible
Previously: The system MUST make the dashboard, user management, role-permission management, software project management, ticket management, and kanban management pages visible in the first admin frontend.

The system MUST make the dashboard, user management, role-permission management, software project management, ticket management, and kanban management pages visible in the first admin frontend, and the role-permission page MUST operate as a minimum manageable RBAC page rather than a manual lookup placeholder.

#### Scenario: A contributor reviews the first frontend baseline
- GIVEN the admin frontend is available
- WHEN the contributor checks the visible page set
- THEN they can access the dashboard, user management, role-permission management, software project management, ticket management, and kanban management pages
- AND the role-permission page exposes the minimum manageable RBAC baseline
