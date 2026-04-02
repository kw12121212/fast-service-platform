# Delta: app-assembly-compatibility-suite

## ADDED Requirements

### Requirement: Compatibility Suite Covers Partial-Module Profile Fixtures
The repository MUST include at least one partial-module profile fixture (such as a core-admin profile that omits all delivery-management modules) in the compatibility suite, and the suite MUST verify that the generated output for that fixture contains no routes, nav items, tables, or services for the omitted modules.

#### Scenario: A contributor validates a core-admin profile assembly
- GIVEN a contributor runs the compatibility suite against a core-admin profile fixture
- WHEN the suite validates the generated output
- THEN it confirms no project, ticket, or kanban routes are present in the generated frontend
- AND it confirms no project, ticket, or kanban nav items are present
- AND it confirms no software_project, kanban_board, or ticket table definitions are present in the generated SQL
- AND it confirms no project_service, kanban_service, or ticket_service registrations are present in the generated SQL

#### Scenario: Full-profile output is not regressed by partial-module fixture addition
- GIVEN a contributor runs the compatibility suite against the baseline-v1 profile fixture
- WHEN the suite validates the generated output
- THEN all delivery-management routes, nav items, tables, and services are present in the output
- AND the partial-module fixture addition has not changed any baseline-v1 output invariant
