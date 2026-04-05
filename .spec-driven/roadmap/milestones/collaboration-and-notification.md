# Collaboration And Notification

## Goal

Add comment, mention, notification, and activity tracking so team members can discuss work items, stay informed of changes, and maintain a visible project activity history.

## In Scope

- Threaded comments on work items (create, reply, list)
- @mention team members in comments (triggers notification)
- Notification center: unread count, notification list, mark-as-read
- Notification types: assignment, mention, status change, comment reply, sprint state change
- Project activity feed: chronological list of notable events (item created, status changed, assigned, commented)
- Rich-text or markdown comment editing

## Out of Scope

- Real-time push notifications (WebSocket/SSE — defer to infrastructure milestone)
- Email or external channel notifications (Slack, Teams integration)
- File attachments on comments
- Notification preference rules engine (start with on/off per type)

## Done Criteria

- A user can add and reply to comments on any work item.
- @mentioning a team member creates a notification for that user.
- Users can view and manage their notifications from a notification center.
- The project activity feed shows recent significant events.
- All planned changes in this milestone are archived.

## Planned Changes

- `comment-and-mention-system` - Comment entity, threaded replies, @mention parsing, and comment UI
    @mention resolution must query the project's team member list (human and AI) from team-and-people-management.
- `notification-service-and-center` - Notification generation, storage, delivery, and frontend notification center
    Notification types are produced by work-item and sprint state changes, not manually created.
- `project-activity-feed` - Chronological event log and feed UI for project-level activity

## Dependencies

- Depends on: work-item-and-backlog-management (comments attach to work items).
- Depends on: team-and-people-management (mentions reference team members, notifications target users).
- Sprint-related notifications depend on: sprint-and-iteration-management.

## Risks

- Notification volume can become noisy; start with essential types and allow opt-out per type.
- @mention parsing must handle edge cases (partial names, multiple matches) without false positives.

## Status

- Declared: proposed

## Notes

- This milestone can be developed in parallel with sprint-and-iteration-management and agile-board-and-visualization.
- Real-time delivery (WebSocket) is out of scope for now; notifications appear on page load or manual refresh.
