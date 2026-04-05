# Agile Dashboard And Metrics

## Goal

Provide sprint-level and project-level dashboards with key agile metrics so teams and stakeholders can assess progress, velocity, and health at a glance.

## In Scope

- Sprint burndown chart: remaining work (story points or item count) over the sprint timeline
- Velocity chart: completed story points per sprint over recent iterations
- Project progress dashboard: total / completed / in-progress work item counts by type
- Sprint summary card: sprint goal, days remaining, completion percentage
- Basic cumulative flow diagram: work items by status over time

## Out of Scope

- Custom report builder or ad-hoc query interface
- Cross-project portfolio dashboard
- Predictive analytics or AI-driven forecasting
- Export dashboard data as PDF or spreadsheet
- Real-time dashboard auto-refresh (manual refresh is sufficient)

## Done Criteria

- A sprint dashboard shows burndown data based on current sprint work items and their statuses.
- A velocity chart displays completed story points across recent sprints.
- A project-level dashboard shows aggregate work-item counts and completion rates.
- Charts render correctly for the current data volume (small-team scale).
- All planned changes in this milestone are archived.

## Planned Changes

- `sprint-metrics-dashboard` - Sprint burndown chart, sprint summary card, and velocity chart
    Burndown data source: sprint work items + status change history from activity feed.
    Velocity data source: completed story points per closed sprint.
- `project-progress-dashboard` - Project-level work item counts, completion rates, and cumulative flow
    Cumulative flow requires status-change event history — coordinate with activity-feed design.

## Dependencies

- Depends on: sprint-and-iteration-management (for sprint data and velocity).
- Depends on: work-item-and-backlog-management (for work-item counts and status).
- Depends on: agile-board-and-visualization (board interaction produces status-change events used by metrics).

## Risks

- Chart libraries add frontend bundle size; evaluate lightweight options.
- Metric accuracy depends on consistent status-change event history — this must be reliable before dashboards can be trusted.

## Status

- Declared: proposed

## Notes

- This milestone should be one of the last V2 agile milestones since it consumes data from all prior modules.
- Story-point-based metrics require that teams consistently estimate — the dashboard should degrade gracefully when story points are missing.
