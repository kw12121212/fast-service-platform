# Built-In Components

## MODIFIED Requirements

### Requirement: Built-In Components Are Classified By Assembly Role
The system MUST classify its built-in capabilities by assembly role so contributors can distinguish required platform core, optional built-in business modules, and engineering-support components.

#### Scenario: A contributor reviews the platform component catalog
- GIVEN a contributor checks what capabilities the platform provides
- WHEN they inspect the component definition
- THEN they can determine which capabilities are mandatory core, which are optional business modules, and which exist as engineering-support components

### Requirement: Delivery-Management Modules Are Optional Built-In Modules
The system MUST treat software project management, ticket management, and kanban management as optional built-in modules that may be selected or omitted during application assembly.

#### Scenario: An AI contributor decides whether to include delivery-management capabilities
- GIVEN an AI contributor assembles a new application from platform modules
- WHEN it evaluates the delivery-management capabilities
- THEN it can choose whether to include software project management, ticket management, and kanban management without violating the platform contract

### Requirement: App Scaffolding And Assembly Are Platform Components
The system MUST treat independent application scaffolding and module assembly as repository-owned platform components rather than ad hoc contributor workflows.

#### Scenario: A contributor checks whether app generation belongs inside the platform
- GIVEN a contributor reviews the platform's built-in component definition
- WHEN they inspect how new applications are created
- THEN they see repository-owned scaffolding and assembly capabilities defined as platform behavior
