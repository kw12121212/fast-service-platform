# Frontend Module Selection

## ADDED Requirements

### Requirement: Frontend Routes Are Driven By Module-Selection Config
The system MUST register frontend routes for optional delivery-management modules only when those modules are active in the module-selection config, and MUST NOT unconditionally register delivery-management routes regardless of the config values.

#### Scenario: A contributor inspects a full-module frontend
- GIVEN a frontend assembled with all delivery-management modules active
- WHEN the contributor navigates to /projects, /tickets, or /kanban
- THEN those routes resolve correctly

#### Scenario: A contributor inspects a core-only frontend
- GIVEN a frontend assembled with all delivery-management modules inactive
- WHEN the contributor navigates to /projects, /tickets, or /kanban
- THEN those routes are not registered and do not resolve

### Requirement: Frontend Navigation Is Driven By Module-Selection Config
The system MUST include delivery-management nav items in the admin shell only when the corresponding module is active in the module-selection config, and MUST NOT render nav items for inactive modules.

#### Scenario: A contributor inspects navigation in a core-only frontend
- GIVEN a frontend assembled with all delivery-management modules inactive
- WHEN the contributor opens the admin shell
- THEN no project, ticket, or kanban nav items are visible

#### Scenario: A contributor inspects navigation in a full-module frontend
- GIVEN a frontend assembled with all delivery-management modules active
- WHEN the contributor opens the admin shell
- THEN project, ticket, and kanban nav items are all present

### Requirement: Platform Source Ships With A Default Module-Selection Config That Activates All Modules
The system MUST provide a default module-selection config in the platform frontend source that activates all three delivery-management modules, matching the baseline-v1 profile, so the running platform baseline is unaffected.

#### Scenario: A contributor runs the platform baseline without assembly
- GIVEN the platform frontend source is run directly without modification
- WHEN the contributor checks the module-selection config
- THEN project, ticket, and kanban are all set to active
- AND the admin shell displays all delivery-management nav items and routes
