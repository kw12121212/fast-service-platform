# Minimum Project Deliverables Delta

## ADDED Requirements

### Requirement: Minimum Deliverables Include Baseline Management Workflows
The system MUST require the V1 baseline to support baseline management workflows for the current core entities rather than limiting those deliverables to read-only display.

#### Scenario: A generated project is checked for operable baseline behavior
- GIVEN a generated V1 project is running
- WHEN a contributor performs the baseline enterprise-management workflows
- THEN they can create users, software projects, kanban boards, and tickets
- AND they can advance tickets through the minimal delivery-state flow
