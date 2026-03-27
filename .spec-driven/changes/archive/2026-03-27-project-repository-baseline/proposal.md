# project-repository-baseline

## What

Define the first project-to-repository baseline so a software project can be connected to one local Git repository and inspected through the platform.

This change adds proposal scope for:

- binding a software project to a local repository path
- reading the minimal repository summary for a bound project
- exposing that summary in the existing software project management experience

## Why

The current V1 baseline already covers the minimum enterprise-management domains, but the platform still stops at business records. A software project exists as delivery metadata only and is not connected to the real engineering workspace behind it.

That gap blocks the next stage of platform work:

- Git repository management has no project anchor
- worktree, merge, and sandbox capabilities have nowhere coherent to attach
- AI cannot treat a project as both a business scope and an engineering scope

The repository and RTK documents already define engineering-support components as part of the platform, and the backend already reserves extension space for them. This change turns that reserved space into the first concrete, project-centered engineering capability.

## Scope

In scope:

- extend the software-project baseline so a project may keep one bound local repository
- define backend behavior for binding, validating, and reading repository status
- define frontend behavior for showing empty, success, and error states around repository binding
- keep the repository interaction inside the current Projects experience instead of introducing a separate engineering console

Out of scope:

- cloning repositories
- pull, push, fetch, or credential management
- worktree creation and switching
- merge execution or conflict resolution
- sandbox execution
- remote hosting integrations such as GitHub or GitLab

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- User, RBAC, ticket, and kanban behavior remains unchanged.
- The minimum visible admin page set remains unchanged.
- Projects without a repository binding remain valid platform data.
