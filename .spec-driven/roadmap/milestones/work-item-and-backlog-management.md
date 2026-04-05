# Work Item And Backlog Management

## Goal

Introduce the core agile entity model — work items and product backlog — so a bound project can capture, prioritize, and track work at Epic, Feature, Story, Bug, and Task granularity.

## In Scope

- Work item type hierarchy: Epic, Feature, Story, Bug, Task
- Work item fields: title, description, type, priority, status, assignee, labels/tags, story points
- Work item state machine: configurable status flow per work-item type (e.g. New → Active → Resolved → Closed)
- Product backlog: flat list of work items with drag-to-reorder prioritization
- Work item search and filtering by type, status, assignee, label, keyword
- Parent-child linking between work items (Epic → Feature → Story)
- Reuse dynamic form for work-item create/edit, dynamic report for backlog list views

## Out of Scope

- Kanban or board visualization (covered by agile-board-and-visualization milestone)
- Sprint-specific backlog management (covered by sprint-and-iteration-management milestone)
- Custom work-item type creation by end users
- Time logging or effort tracking
- Import/export of work items from external tools

## Done Criteria

- A bound project can create, edit, and delete work items of each supported type.
- The product backlog displays work items in priority order and supports reorder.
- Work items can be filtered and searched across the project.
- Work items support assignee selection from the project's team members.
- Parent-child linking allows navigating between Epic, Feature, and Story.
- All planned changes in this milestone are archived.

## Planned Changes

- `work-item-entity-model` - Backend work item entity, state machine, and CRUD service
    Define a work-item-type-aware status flow that is declarative enough for the board milestone to consume.
    Use the existing workflow component pattern for state transitions where applicable.
- `product-backlog-management` - Backlog list, prioritization, filtering, and search
    Use dynamic report component for backlog list rendering.
    Use dynamic form component for work-item create/edit.
- `work-item-linking` - Parent-child linking and traceability between work-item levels

## Dependencies

- Depends on: team-and-people-management (for assignee selection and team-member queries).
- Reuses dynamic form, dynamic report, and workflow components from V1.

## Risks

- Work item state machine can become too complex if every type has a different flow — start with a shared baseline and allow narrow customization.
- Backlog reorder performance degrades at scale; initial implementation should target small-team volumes (hundreds, not tens of thousands).

## Status

- Declared: proposed

## Notes

- This is the core data model milestone — board, sprint, dashboard, and release milestones all depend on work items existing.
- Story points are included as a simple numeric field; no forced estimation framework.
