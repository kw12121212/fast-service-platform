# Design: rename-devops-to-engineering

## Approach

Apply a terminology-only rename in three places: product/spec wording, repository technical knowledge, and backend placeholder package names.
The rename will use `engineering` as the stable English identifier because it is concise in code, broad enough for Git/worktree/merge/sandbox support, and less misleading than `devops`.
No functional behavior, service contract, or runtime structure should change.

## Key Decisions

- Use `engineering` as the code-level namespace.
  Rationale: it is short, readable, and does not imply deployment or infrastructure ownership.
- Describe the component area in docs/specs as engineering-support capabilities rather than software-development-management or devops.
  Rationale: that better matches the actual component list without importing overloaded industry terminology.
- Keep the existing component set unchanged.
  Rationale: the problem is terminology clarity, not platform scope.

## Alternatives Considered

- Keep `devops` and only explain it better in prose.
  Rejected because the misleading name would remain in code and continue to shape future changes.
- Rename the area to `workspace`.
  Rejected because Git repository management and merge support go beyond workspace handling alone.
- Rename the area to `developer-platform`.
  Rejected because it is clearer in prose than in code, but too long and awkward as a package namespace.
