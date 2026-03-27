# Questions: bootstrap-admin-write-workflows

## Open

<!-- No open questions -->

## Resolved

- [x] Q: Should the next change focus on expanding into new platform component areas or make the current V1 admin baseline operable first?
  Context: This determines whether the repository should deepen the existing minimum enterprise-management baseline or branch into devops capabilities next.
  A: Make the current V1 admin baseline operable first.
- [x] Q: Should this change aim for full CRUD behavior?
  Context: Full CRUD would significantly widen page behavior, validation, and state-management scope.
  A: No. Limit this change to the first minimum write workflows.
- [x] Q: Should role and permission authoring be included in the first write-workflow milestone?
  Context: The current role-permission page and backend read model do not yet provide a clean end-to-end management flow for that area.
  A: No. Keep role-permission management read-focused in this change.
- [x] Q: Should this change also implement Git, worktree, merge, or sandbox platform components?
  Context: Those capabilities are defined as platform components, but they are a separate expansion path from the current admin baseline.
  A: No. Keep this change on the current minimum enterprise-management domains.
