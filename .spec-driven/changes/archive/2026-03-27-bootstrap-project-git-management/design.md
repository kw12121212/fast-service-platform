# Design: bootstrap-project-git-management

## Approach

Treat Git management as the next layer on top of the existing
project-to-repository binding baseline, not as a separate engineering console.

The proposed implementation path is:

1. keep the current software-project repository binding as the anchor for all
   Git operations
2. add backend-facing Git inspection operations that read bound-project branch
   context, recent commits, and detached HEAD state from the local Git CLI
3. add a backend-facing branch-switch operation that only targets existing local
   branches and rejects unsafe switching conditions such as a dirty working tree
4. extend the existing Projects experience so contributors can inspect Git
   context and switch branches without leaving the current project-management
   flow

This keeps the new behavior narrow: the platform does not try to become a full
Git client, it only adds the minimum project-attached Git operations needed
before worktree management.

## Key Decisions

- Git management remains attached to an already bound software project.
  Rationale: the repository-binding baseline already established the software
  project as both delivery scope and engineering scope. Reusing that anchor
  avoids inventing another technical identity.

- The first Git write operation is limited to switching to an existing local
  branch.
  Rationale: this is the smallest useful write capability that produces clear
  user value without expanding into branch-creation or merge workflows.

- Branch switching is allowed only when the working tree is clean.
  Rationale: this creates an externally understandable safety rule and avoids
  silent behavior around uncommitted changes in the first Git-management
  baseline.

- Recent commit history is intentionally a small list rather than a full log.
  Rationale: the platform needs enough context to make the current repository
  state legible, not a complete history browser.

- Detached HEAD is shown as a restricted state.
  Rationale: detached HEAD is important repository context, but supporting full
  mutation behavior there would complicate the first Git baseline beyond the
  agreed scope.

- The backend may continue using the local Git CLI as the execution boundary.
  Rationale: the repository already uses the Git CLI for repository inspection,
  and continuing that pattern respects the no-extra-library product principle.

## Alternatives Considered

- Include branch creation in the first Git-management baseline.
  Rejected because branch creation is intentionally being reserved for the
  later worktree-focused change where branch lifecycle and workspace lifecycle
  can be designed together.

- Start with worktree management before Git branch management.
  Rejected because worktree workflows depend on a clearer Git-management layer
  for reading branch state and safely changing repository context.

- Introduce remote Git operations now.
  Rejected because remote behavior adds credentials, network variability, and
  provider-specific workflows before the local Git baseline is proven.

- Create a separate Engineering navigation area now.
  Rejected because the current Git scope is still project-attached and does not
  justify a broader information-architecture change.
