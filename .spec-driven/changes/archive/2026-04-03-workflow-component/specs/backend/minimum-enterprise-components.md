# Minimum Enterprise Components

## MODIFIED Requirements

### Requirement: Kanban Starts With Minimal State Flow
The system MUST limit initial kanban behavior to a minimal state-flow baseline in the first backend core.
The system MUST expose a narrow ticket workflow detail and action surface that supports the reusable frontend workflow component with bounded actions, required comments, assignee visibility, reassignment, and visible history for a single ticket instance, without turning the backend core into a general workflow engine.

#### Scenario: Kanban workflow scope is inspected
- GIVEN a contributor reviews the first backend implementation scope
- WHEN they inspect kanban behavior
- THEN they find a minimal state-flow baseline rather than a full workflow engine

#### Scenario: A contributor loads workflow detail for a single ticket
- GIVEN a contributor inspects a ticket through the backend workflow path
- WHEN they request the current workflow detail for that ticket
- THEN they can observe the current ticket state, current assignee, bounded allowed actions, and visible workflow history for that single ticket instance

#### Scenario: A contributor executes a bounded workflow action with a required comment
- GIVEN a contributor executes a supported ticket workflow action through the backend workflow path
- WHEN they submit the action with the required comment and any required reassignment target
- THEN the backend applies only the bounded action behavior for that single ticket instance
- AND the updated workflow state or assignee becomes observable through the same workflow detail path
