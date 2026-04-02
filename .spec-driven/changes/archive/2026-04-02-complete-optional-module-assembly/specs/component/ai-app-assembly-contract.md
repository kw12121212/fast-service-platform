# Delta: ai-app-assembly-contract

## ADDED Requirements

### Requirement: Partial-Module Assembly Output Excludes Omitted Module Artifacts
The system MUST ensure that when optional delivery-management modules are omitted from the selected profile, the generated output contains no frontend routes, frontend navigation items, database table definitions, or backend service registrations associated with those omitted modules.

#### Scenario: A contributor assembles an application without delivery-management modules
- GIVEN a contributor provides an assembly manifest that selects a profile without project, ticket, or kanban modules
- WHEN the repository-owned assembly tooling generates the derived application
- THEN the generated frontend source contains no route registrations for /projects, /tickets, or /kanban
- AND the generated frontend source contains no nav items for project, ticket, or kanban
- AND the generated SQL init contains no software_project, kanban_board, or ticket table definitions
- AND the generated SQL init contains no project_service, kanban_service, or ticket_service registrations

### Requirement: Assembly Emits A Module-Selection Config For The Frontend
The system MUST emit a TypeScript module-selection config file as part of the generated frontend source, encoding which optional modules are active for the assembled application, so that the frontend router and navigation can be driven by that config rather than hardcoded assumptions.

#### Scenario: A contributor inspects the generated module-selection config
- GIVEN a contributor assembles an application from any valid profile
- WHEN they inspect the generated frontend source
- THEN they can find a TypeScript module-selection config that explicitly names the active state of each optional module
- AND the config values match the selected profile from the assembly manifest
