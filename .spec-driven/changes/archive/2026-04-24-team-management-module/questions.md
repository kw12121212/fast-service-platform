# Questions: team-management-module

## Open

- [x] Q: Should the team descriptor include a workflow section for team status transitions (e.g., Active → Archived)?
  Context: The workflow-panel component is available but adding workflow adds implementation scope. The milestone scope mentions team roles but not team status workflow.
  A: Skip workflow — team.status is a simple enum (Active/Archived) updated via standard edit form. Workflow can be added later if needed.

- [x] Q: What is the exact set of team-scoped roles to seed in the `team_role` table?
  Context: The milestone lists Scrum Master, Product Owner, Developer, Observer. Confirm these four are sufficient or if additional roles should be included.
  The team_member_role join table supports multiple roles per member, so adding roles later is non-breaking.
  A: Four roles: Scrum Master, Product Owner, Developer, Observer.

## Resolved

- [x] Q: Should team management use the descriptor-driven pipeline or be hand-coded?
  Context: Determines the implementation pattern and how much custom code is needed.
  A: Descriptor-driven — use existing JSON management-module descriptor with dynamic form/report components.

- [x] Q: Should user-profile-baseline be folded into this change?
  Context: Team member display would benefit from richer user profiles (e.g., avatar).
  A: Keep separate — use existing app_user fields as-is. Avatar and extended profiles deferred to user-profile-baseline.

- [x] Q: Should team roles extend the existing RBAC tables or use a parallel structure?
  Context: Affects whether platform RBAC is modified or kept clean.
  A: Parallel team_role table — separate from platform RBAC, scoped to the team-project context.
