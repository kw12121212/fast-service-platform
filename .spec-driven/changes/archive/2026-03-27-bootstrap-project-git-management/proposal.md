# bootstrap-project-git-management

## What

Define the first project-attached Git management baseline for software projects
that are already bound to a local Git repository.

This change adds proposal scope for:

- exposing richer Git context for a bound project beyond a single repository
  summary line
- listing the existing local branches available in the bound repository
- showing a small recent-commit list for the currently bound repository state
- allowing contributors to switch a bound project to an existing local branch
  when the repository working tree is clean
- making detached HEAD visible as a restricted state instead of treating it as a
  normal branch-switching flow

## Why

The current platform baseline already lets a software project bind to one local
Git repository and inspect a minimal repository summary. That proves the
project-to-repository bridge, but it still stops short of real Git management.

Without a first Git management baseline:

- a project cannot safely move between existing local branches
- contributors cannot inspect enough repository context to understand the
  current engineering state from the platform
- the next engineering-support steps such as worktree management, merge support,
  and sandbox workflows have no stable Git-management layer to build on

This change turns project-bound repositories into the first operable engineering
capability while keeping the scope narrow enough to stay safe and verifiable.

## Scope

In scope:

- define backend behavior for reading Git context from an already bound local
  repository
- define backend behavior for listing existing local branches for a bound
  project
- define backend behavior for listing a small recent-commit history for a bound
  project
- define backend behavior for switching a bound project to an existing local
  branch only when the working tree is clean
- define frontend behavior in the existing Projects experience for showing Git
  context, branch options, detached HEAD restrictions, and switch success or
  failure feedback

Out of scope:

- creating new branches
- fetch, pull, push, remote tracking, or credentials
- merge, rebase, cherry-pick, or conflict resolution
- worktree creation, deletion, or switching
- sandbox execution
- introducing a new top-level engineering module or replacing the current
  project management experience

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- User, RBAC, ticket, and kanban behavior remains unchanged.
- Projects without a repository binding remain valid platform data.
- Existing repository binding behavior remains local-path based and limited to
  one optional repository per project.
- The platform does not expand into remote-hosting integrations or branch
  creation behavior in this change.
