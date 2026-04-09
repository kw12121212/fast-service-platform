# Sprint And Iteration Management

## Goal

Add sprint/iteration lifecycle management so a team can plan time-boxed iterations, pull work items into a sprint backlog, and track sprint progress toward a sprint goal.

## In Scope

- Sprint CRUD: name, goal, start date, end date, associated team
- Sprint planning: pull work items from product backlog into sprint backlog
- Sprint backlog: sprint-scoped work item list with status
- Sprint state machine: Planning → Active → Review → Closed
- Sprint goal as a visible, editable text field
- Sprint member list derived from the associated team

## Out of Scope

- Sprint burndown/burnup charts (covered by agile-dashboard-and-metrics milestone)
- Sprint retrospective structured output (can be added later as a workflow)
- Multi-team sprint (a sprint belongs to one team)
- Automated sprint rollover of incomplete items

## Done Criteria

- A team can create a sprint with a goal and time window.
- Work items can be moved from the product backlog to a sprint backlog during planning.
- Sprint backlog shows current status of each sprint work item.
- Sprint state transitions are visible and controlled.
- All planned changes in this milestone are archived.

## Planned Changes

- `sprint-entity-and-lifecycle` - Declared: planned - Sprint entity, state machine, team binding, and CRUD service
- `sprint-backlog-management` - Declared: planned - Sprint-scoped backlog view, planning flow, and downstream sprint metrics queries

## Dependencies

- Depends on: work-item-and-backlog-management (for work items and product backlog).
- Depends on: team-and-people-management (for team-sprint binding).

## Risks

- Sprint lifecycle can expand into a full project management product if scope creeps beyond single-team time-boxed iterations.
- Date handling must stay simple — no timezone complexity in V2.

## Status

- Declared: proposed

## Notes

- Sprint and agile-board milestones can be developed in parallel once work items exist.
- Sprint rollover rules (what happens to incomplete items at sprint end) should be configurable but start with a simple default (return to product backlog).
