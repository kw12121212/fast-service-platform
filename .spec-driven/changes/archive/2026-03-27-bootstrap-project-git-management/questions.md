# Questions: bootstrap-project-git-management

## Open

<!-- No open questions -->

## Resolved

- [x] Q: Should the first Git-management baseline support creating branches?
  Context: The proposal needs a clear separation between Git-management scope
  and the later worktree change.
  A: No. Branch creation is reserved for the later worktree-focused change.

- [x] Q: Should recent commits be shown as a single summary or a small list?
  Context: The proposal needs a bounded repository-context contract for the UI
  and backend surface.
  A: Show a small recent-commit list.

- [x] Q: How should detached HEAD be handled in the first Git-management
  baseline?
  Context: The proposal needs a clear rule for a common but non-standard branch
  state.
  A: Treat detached HEAD as a restricted state for this baseline.
