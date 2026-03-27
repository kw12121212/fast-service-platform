# Design: project-repository-baseline

## Approach

Treat repository management as a project-attached capability, not as a standalone technical module.

The proposed implementation path is:

1. extend the software-project model with optional repository-binding data
2. add backend-facing operations that bind a project to a local Git repository and return a minimal repository summary
3. render the repository summary from the existing Projects page so contributors can connect a project to a real workspace without leaving the current project-management flow

The repository summary stays intentionally small in this first step. It only needs enough observable state to prove that the platform can attach a project to a real codebase and inspect its engineering context safely.

## Key Decisions

- One software project maps to at most one bound repository in this baseline.
  Rationale: this is the smallest useful bridge between business scope and engineering scope, and it avoids inventing multi-repository orchestration before the platform has even shipped the first repository contract.

- Repository binding is local-path based and accepts only absolute paths.
  Rationale: absolute local paths are explicit, easy for humans and AI to reason about, and avoid hidden resolution rules.

- The first repository capability is read-mostly.
  Rationale: the platform needs an observable repository anchor before it needs dangerous or workflow-heavy write operations.

- Repository management appears inside the existing Projects experience.
  Rationale: this keeps the change narrow, preserves the current admin information architecture, and reinforces that the repository belongs to a software project instead of becoming a disconnected engineering tool.

- The backend may use the local Git CLI as an execution boundary.
  Rationale: the product principle is to avoid adding new external libraries, while Git is already the natural system boundary for repository inspection.

## Alternatives Considered

- Start with worktree management first.
  Rejected because worktrees only make sense after a project has a concrete repository anchor.

- Introduce a separate Engineering top-level module now.
  Rejected because it expands the visible surface before the platform has enough engineering behavior to justify another navigation area.

- Model remote repository hosting first.
  Rejected because it adds credentials, provider differences, and network workflows before the local repository baseline is proven.
