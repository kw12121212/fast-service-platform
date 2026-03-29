## MODIFIED Requirements

### Requirement: Delivery-Management Modules Are Optional Built-In Modules
Previously: The system MUST treat software project management, ticket management, and kanban management as optional built-in modules that may be selected or omitted during application assembly.

The system MUST treat delivery-management capabilities as optional built-in modules that may be selected or omitted during application assembly, and the repository MAY decompose those capabilities into smaller optional project, ticket, and kanban units as long as their dependency expectations remain explicit.

#### Scenario: A contributor reviews decomposed delivery-management capabilities
- GIVEN a contributor inspects the platform's optional business components
- WHEN they review the delivery-management area after decomposition
- THEN they can identify smaller optional capability units for project, ticket, and kanban behavior
- AND they can identify the explicit dependency expectations that govern how those units may be assembled together
