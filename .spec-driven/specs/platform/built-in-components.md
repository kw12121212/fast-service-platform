# Built-In Components

### Requirement: Built-In Components Are Classified By Assembly Role
The system MUST classify its built-in capabilities by assembly role so contributors can distinguish required platform core, optional built-in business modules, and engineering-support components.

#### Scenario: A contributor reviews the platform component catalog
- GIVEN a contributor reviews the platform component contract
- WHEN they inspect the component definition
- THEN they can determine which capabilities are mandatory core, which are optional business modules, and which exist as engineering-support components

#### Scenario: A contributor reviews reusable interaction components
- GIVEN a contributor reviews the platform-owned reusable UI capabilities
- WHEN they inspect the component definition
- THEN they can identify dynamic form, dynamic report, and workflow capabilities as reusable platform components
- AND they can distinguish those reusable interaction components from optional business modules and engineering-support components

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

### Requirement: Dynamic Report Supports Multiple Section Types
The system MUST support dynamic report pages with multiple section types including summary cards, tables, bar charts, line charts, and pie charts, so descriptor-driven generation can express full dashboard capabilities.

#### Scenario: A contributor reviews dynamic report section capabilities
- GIVEN a contributor reviews the platform's dynamic report component
- WHEN they inspect the supported section types
- THEN they see support for: summary-cards, table, bar-chart, line-chart, pie-chart
- AND each section type has clearly defined required properties

#### Scenario: An AI generates a descriptor with multi-section report
- GIVEN an AI prepares a management-module descriptor
- WHEN it defines a report with multiple sections
- THEN it can use section types: summary-cards (with cardKeys), table (with columns and title), bar-chart (with title), line-chart (with title), pie-chart (with title)
- AND the descriptor validates against the management-module schema

### Requirement: Report Descriptor Supports Both Columns And Sections Paths
The system MUST support both legacy columns-based report descriptors and new sections-based report descriptors, maintaining backward compatibility while enabling richer dashboard expressions.

#### Scenario: A contributor uses the legacy columns path
- GIVEN a contributor prepares a simple table-only report descriptor
- WHEN they use the columns array format
- THEN the descriptor validates successfully
- AND the generated report renders as a single table

#### Scenario: A contributor uses the sections path
- GIVEN a contributor prepares a multi-section dashboard descriptor
- WHEN they use the sections array format
- THEN the descriptor validates successfully
- AND the generated report renders all specified sections in order

### Requirement: Optional Module Dependency Declarations Are Operationally Enforced
The system MUST ensure that omitting an optional delivery-management module from an assembly profile removes that module's routes, navigation items, database schema, and backend services from the generated output, so that module dependency declarations are operationally real and not only descriptive.

#### Scenario: A contributor assembles an application and omits the kanban module
- GIVEN a contributor selects a profile that omits the kanban module but includes project and ticket
- WHEN the assembly tooling generates the derived application
- THEN the generated output contains no kanban routes, no kanban nav item, no kanban_board table, and no kanban_service registration
- AND the project and ticket module artifacts are unaffected
