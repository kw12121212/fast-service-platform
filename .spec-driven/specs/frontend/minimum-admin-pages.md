# Minimum Admin Pages

### Requirement: Frontend Makes Minimum V1 Business Pages Visible
The system MUST make the dashboard, user management, role-permission management, software project management, ticket management, and kanban management pages visible in the first admin frontend.

#### Scenario: A contributor reviews the first frontend baseline
- GIVEN the admin frontend is available
- WHEN the contributor checks the visible page set
- THEN they can access the dashboard, user management, role-permission management, software project management, ticket management, and kanban management pages

### Requirement: Frontend Pages Are Backed By Current Backend Data
The system MUST connect the first admin pages to the current backend core rather than limiting them to static placeholders.

#### Scenario: A contributor opens a minimum admin page
- GIVEN the backend core is running with accessible data
- WHEN the contributor visits one of the minimum admin pages
- THEN the page uses the backend data path needed to display its baseline content
