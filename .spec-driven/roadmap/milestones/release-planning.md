# Release Planning

## Goal

Add release-level planning so a project can group sprints into releases, manage a release backlog, and produce release notes from completed work items.

## In Scope

- Release CRUD: name, description, target date, status (Planning / In Progress / Released)
- Release-sprint binding: a release contains one or more sprints
- Release backlog: work items targeted for a release (aggregated from bound sprints + direct assignment)
- Release notes generation: auto-collect completed work items from bound sprints, grouped by type
- Release status dashboard: completion percentage, items remaining, days to target

## Out of Scope

- Deployment pipeline integration (CI/CD triggering)
- Release branching strategy automation
- Version numbering enforcement
- Release approval workflows beyond basic status transitions
- Customer-facing or external release notes publishing

## Done Criteria

- A project can create a release and bind one or more sprints to it.
- Work items completed in bound sprints are automatically collected into release notes.
- Release notes can be viewed and exported as structured text.
- Release dashboard shows progress toward the release target.
- All planned changes in this milestone are archived.

## Planned Changes

- `release-entity-and-lifecycle` - Declared: planned - Release CRUD, status flow, sprint binding, and release planning service
- `release-backlog-and-notes` - Declared: planned - Release backlog aggregation, structured release notes generation, and release dashboard

## Dependencies

- Depends on: sprint-and-iteration-management (for sprint binding and completed-item queries).
- Depends on: work-item-and-backlog-management (for work item data).

## Risks

- Release scope can drift into deployment automation — stay focused on planning and documentation.
- Release notes quality depends on work-item description quality — the generated output is only as good as the input.

## Status

- Declared: proposed

## Notes

- This milestone can be developed in parallel with agile-dashboard-and-metrics since both consume sprint data but produce different outputs.
- Release notes generation is a lightweight text assembly, not a document publishing system.
