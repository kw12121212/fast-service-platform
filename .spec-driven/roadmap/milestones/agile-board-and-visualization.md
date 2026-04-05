# Agile Board And Visualization

## Goal

Provide Kanban and Scrum board views so teams can visually manage work item state through drag-and-drop interaction instead of list-only workflows.

## In Scope

- Kanban board: columns mapped to work-item statuses, cards representing work items
- Scrum board: Kanban board filtered to a specific sprint's work items
- Drag-and-drop card movement to trigger status transitions
- Swimlane grouping by assignee, priority, or work-item type
- Card content: title, type icon, assignee avatar, priority indicator, story points
- Quick-edit on card (assignee change, priority change) without opening full form

## Out of Scope

- Custom column configuration by end users (columns are derived from work-item status flow)
- Card color-coding rules engine
- Multi-board or cross-project board views
- Export board as image or PDF

## Done Criteria

- A project's work items render on a Kanban board with columns matching the configured statuses.
- A sprint's work items render on a Scrum board scoped to that sprint.
- Dragging a card to a different column triggers the corresponding status transition.
- Swimlane grouping toggles work for assignee, priority, and type.
- All planned changes in this milestone are archived.

## Planned Changes

- `kanban-board-component` - Reusable board component with columns, cards, drag-and-drop, and swimlanes
    Board columns are derived from the work-item status flow defined in work-item-entity-model.
    Card interaction triggers the same state transition that the form-based workflow uses.
- `scrum-board-view` - Sprint-scoped board view that filters to a specific sprint's items
    Depends on sprint backlog queries from sprint-backlog-management.

## Dependencies

- Depends on: work-item-and-backlog-management (for work items and status flow).
- Scrum board view also depends on: sprint-and-iteration-management (for sprint scope).

## Risks

- Drag-and-drop performance and mobile responsiveness need attention for usability.
- Board must stay a visualization of existing work-item state — it must not become a separate state management system.

## Status

- Declared: proposed

## Notes

- The Kanban board component should be a standalone reusable component, not tied to a specific page.
- This milestone can be developed in parallel with sprint-and-iteration-management (Kanban board only depends on work items; Scrum board depends on sprints).
