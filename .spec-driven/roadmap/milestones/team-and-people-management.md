# Team And People Management

## Goal

Extend the existing RBAC baseline into a team-aware people model that treats AI employees as first-class team members — so human and AI contributors share the same collaboration surface (assignments, comments, notifications, sprint participation) and downstream agile workflows never need to distinguish between them at the interaction layer.

## In Scope

- Team CRUD with team membership management
- Team-project binding (a team can be bound to one or more projects)
- Human user profile basics (display name, email, avatar)
- AI employee profile: a team member backed by an external AI API (e.g. Claude API) with a configurable persona, capabilities declaration, and API binding
- AI employee as first-class member: appears in assignee dropdowns, receives @mentions, participates in comment threads, shows up on boards and dashboards identically to human members
- Team-level roles within a project context (Scrum Master, Product Owner, Developer, Observer) — applicable to both human and AI members
- AI API integration surface: configurable endpoint, model selection, system prompt / persona template per AI employee
- Reuse existing RBAC infrastructure rather than replacing it

## Out of Scope

- Cross-organization or multi-tenant team sharing
- External identity provider integration (SSO, LDAP, OAuth)
- Capacity planning, availability calendars, or leave tracking
- Hierarchical team structures (team-of-teams, department trees)
- AI agent autonomy or tool-use beyond the interaction surface (AI employees respond to assigned work through the collaboration layer, they do not execute arbitrary actions)
- Multi-model orchestration or agent chaining within the team model

## Done Criteria

- A bound project can define teams, add human members, and assign team-level roles.
- A bound project can define AI employees with an API binding and persona, and add them to teams.
- AI employees appear identically to human members in assignee lists, @mention resolution, board cards, and notification recipients.
- Team membership (human + AI) is visible in the project context and queryable by downstream modules.
- AI employees can receive a message (e.g. via @mention or assignment notification) and produce a response through the configured AI API.
- Existing RBAC permissions continue to work unchanged alongside the new team model.
- All planned changes in this milestone are archived.

## Planned Changes
- `team-management-module` - Declared: complete - Team CRUD, membership, team-project binding, and team roles for human and AI members
- `user-profile-baseline` - Declared: planned - Basic human user profile fields such as display name, email, and avatar
- `ai-employee-model` - Declared: planned - AI employee member type with persona, capabilities, and provider binding configuration
- `ai-api-integration-surface` - Declared: planned - Backend API integration surface for sending collaboration messages to configured AI providers

## Dependencies

- Depends on: platform-v1-integration-and-closure (V2 entry point — V1 must be closed first).
- Reuses the existing RBAC management baseline and dynamic form/report components from V1.
- AI API integration requires an active API key / credential at runtime — credential management must stay outside the team model (use existing platform config).

## Risks

- Team model can sprawl into a full HR or org-chart system unless boundaries stay narrow.
- Team-level roles must not conflict with or duplicate the existing platform-level RBAC model.
- AI employee interaction latency can vary significantly — the collaboration surface must handle async responses (AI does not block the UI).
- AI API cost and rate limits must be surfaced to the project admin so teams can control AI employee usage.

## Status

- Declared: proposed

## Notes

- This is the V2 foundation milestone — all downstream agile milestones depend on teams and people (human + AI) existing.
- The AI employee design principle is: "AI is a team member, not a tool." Every downstream module treats AI employees the same as human members. If a module needs to distinguish, it queries capabilities, not member type.
- Time tracking and capacity features are explicitly deferred beyond this milestone.

