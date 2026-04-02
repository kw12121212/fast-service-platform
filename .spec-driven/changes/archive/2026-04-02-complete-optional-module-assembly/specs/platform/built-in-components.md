# Delta: built-in-components

## ADDED Requirements

### Requirement: Optional Module Dependency Declarations Are Operationally Enforced
The system MUST ensure that omitting an optional delivery-management module from an assembly profile removes that module's routes, navigation items, database schema, and backend services from the generated output, so that module dependency declarations are operationally real and not only descriptive.

#### Scenario: A contributor assembles an application and omits the kanban module
- GIVEN a contributor selects a profile that omits the kanban module but includes project and ticket
- WHEN the assembly tooling generates the derived application
- THEN the generated output contains no kanban routes, no kanban nav item, no kanban_board table, and no kanban_service registration
- AND the project and ticket module artifacts are unaffected
