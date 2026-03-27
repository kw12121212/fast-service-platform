# Questions: complete-rbac-management-baseline

## Open

<!-- No open questions -->

## Resolved

- [x] Q: Should the next RBAC change aim for the minimum management baseline or a richer componentized relationship view?
  Context: This determines whether the proposal should stay on the smallest reusable RBAC floor or widen into more advanced UI and query behavior.
  A: Limit the next change to the minimum management baseline.
- [x] Q: For the first relationship-query baseline, should the backend expose `listRolesForUser`, `listUsersForRole`, or both?
  Context: The minimum management page needs one clear relationship-view direction to stay inside the agreed minimal scope.
  A: Use `listRolesForUser` for the first baseline.
