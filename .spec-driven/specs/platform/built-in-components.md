# Built-In Components

### Requirement: Built-In Components Are Classified By Assembly Role
The system MUST classify its built-in capabilities by assembly role so contributors can distinguish required platform core, optional built-in business modules, and engineering-support components.

#### Scenario: A contributor reviews the platform component catalog
- GIVEN a contributor reviews the platform component contract
- WHEN they inspect the component definition
- THEN they can determine which capabilities are mandatory core, which are optional business modules, and which exist as engineering-support components

### Requirement: Delivery-Management Modules Are Optional Built-In Modules
The system MUST treat delivery-management capabilities as optional built-in modules that may be selected or omitted during application assembly, and the repository MAY decompose those capabilities into smaller optional project, ticket, and kanban units as long as their dependency expectations remain explicit.

#### Scenario: An AI contributor decides whether to include delivery-management capabilities
- GIVEN an AI contributor assembles a new application from platform modules
- WHEN it evaluates the delivery-management capabilities
- THEN it can choose whether to include the available optional project, ticket, and kanban capability units without violating the platform contract

#### Scenario: A contributor reviews decomposed delivery-management capabilities
- GIVEN a contributor inspects the platform's optional business components
- WHEN they review the delivery-management area after decomposition
- THEN they can identify smaller optional capability units for project, ticket, and kanban behavior
- AND they can identify the explicit dependency expectations that govern how those units may be assembled together

### Requirement: Engineering Support Components
The system MUST treat Git repository management, worktree management, code merge support, and sandbox environments as engineering-support components provided by the platform.

#### Scenario: A contributor checks engineering-support capabilities
- GIVEN a contributor reviews whether engineering-support abilities belong inside the platform
- WHEN they inspect the component definition
- THEN they see Git repository management, worktree management, code merge support, and sandbox environments defined as platform components

### Requirement: App Scaffolding And Assembly Are Platform Components
The system MUST treat independent application scaffolding and module assembly as repository-owned platform components rather than ad hoc contributor workflows.

#### Scenario: A contributor checks whether app generation belongs inside the platform
- GIVEN a contributor reviews the platform's built-in component definition
- WHEN they inspect how new applications are created
- THEN they see repository-owned scaffolding and assembly capabilities defined as platform behavior
