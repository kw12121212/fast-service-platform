# Project Worktree Management

## ADDED Requirements

### Requirement: Bound Projects Expose Worktree Inventory
The system MUST expose worktree inventory for a software project that is bound to a local Git repository.

#### Scenario: A contributor inspects worktrees for a bound project
- GIVEN a software project is bound to a local Git repository
- WHEN the contributor reads the project's worktree management context
- THEN they can inspect the known worktree list for that repository
- AND they can identify the main repository worktree and any linked worktrees
- AND they can identify the path and checked-out branch for each visible worktree entry

### Requirement: Created Worktrees Use A Deterministic Sibling Directory Convention
The system MUST create project worktrees under a sibling directory root derived from the bound repository name, and the created worktree directory name MUST be derived from the target branch name using a filesystem-safe representation.

#### Scenario: A contributor creates a worktree for a branch
- GIVEN a software project is bound to a local Git repository in a directory named `proj`
- AND the repository is in a supported worktree-management state
- AND the target local branch is `feature/api-preview`
- WHEN the contributor requests creation of a worktree for that branch
- THEN the created worktree is placed under a sibling directory root named `proj-worktrees`
- AND the created worktree path uses a filesystem-safe directory name derived from `feature/api-preview`
- AND the resulting worktree location is visible through the project's worktree management context

### Requirement: Each Branch Maps To At Most One Worktree
The system MUST reject creation of a project worktree when the target branch is already represented by an existing worktree for the same repository context.

#### Scenario: A contributor tries to create a duplicate branch worktree
- GIVEN a software project is bound to a local Git repository
- AND the repository already has a worktree for local branch `feature-preview`
- WHEN the contributor requests another worktree for local branch `feature-preview`
- THEN the creation request is rejected
- AND the existing worktree inventory remains unchanged

### Requirement: Detached HEAD Is A Restricted Worktree Management State
The system MUST treat detached HEAD as a restricted worktree management state for a bound software project.

#### Scenario: A contributor inspects worktree management while the repository is detached
- GIVEN a software project is bound to a local Git repository
- AND the repository is currently in detached HEAD state
- WHEN the contributor reads the project's worktree management context
- THEN they can see that detached HEAD is a restricted state
- AND the project does not expose detached HEAD as a normal worktree creation or repair state

### Requirement: Worktree Deletion Requires Clean And Fully Pushed State
The system MUST allow a project worktree to be deleted only when it is clean, has an upstream branch, and has no unpushed commits relative to that upstream branch.

#### Scenario: A contributor deletes a safe worktree
- GIVEN a software project is bound to a local Git repository
- AND the selected linked worktree is clean
- AND the selected linked worktree has an upstream branch
- AND the selected linked worktree has no unpushed commits
- WHEN the contributor requests deletion of that linked worktree
- THEN the worktree is removed
- AND the updated worktree inventory no longer shows that linked worktree

#### Scenario: A contributor tries to delete a dirty worktree
- GIVEN a software project is bound to a local Git repository
- AND the selected linked worktree has uncommitted changes
- WHEN the contributor requests deletion of that linked worktree
- THEN the deletion request is rejected
- AND the worktree remains available in the project's worktree inventory

#### Scenario: A contributor tries to delete a worktree without an upstream
- GIVEN a software project is bound to a local Git repository
- AND the selected linked worktree has no upstream branch
- WHEN the contributor requests deletion of that linked worktree
- THEN the deletion request is rejected
- AND the platform indicates that the worktree must be reviewed manually before deletion

#### Scenario: A contributor tries to delete a worktree with unpushed commits
- GIVEN a software project is bound to a local Git repository
- AND the selected linked worktree has an upstream branch
- AND the selected linked worktree is ahead of that upstream branch
- WHEN the contributor requests deletion of that linked worktree
- THEN the deletion request is rejected
- AND the platform indicates that the worktree must be pushed or otherwise handled manually before deletion

### Requirement: Bound Projects Support Worktree Repair And Prune
The system MUST support repository-backed `repair` and `prune` maintenance operations for a bound software project's worktree context.

#### Scenario: A contributor repairs a stale worktree context
- GIVEN a software project is bound to a local Git repository
- AND the repository's worktree metadata includes stale or broken linked worktree state
- WHEN the contributor requests a repair operation from the project's worktree management path
- THEN the repository-backed repair operation is executed
- AND the contributor can inspect the refreshed worktree management context afterward

#### Scenario: A contributor prunes stale worktree records
- GIVEN a software project is bound to a local Git repository
- AND the repository contains stale worktree records that no longer point to valid worktree directories
- WHEN the contributor requests a prune operation from the project's worktree management path
- THEN the stale worktree records are removed from the repository's visible worktree context
- AND the contributor can inspect the updated worktree inventory afterward

### Requirement: Unbound Projects Do Not Pretend To Have Worktree State
The system MUST keep software projects usable when no repository binding exists, without exposing fake worktree data or normal worktree management operations.

#### Scenario: A contributor inspects worktree management for an unbound project
- GIVEN a software project has no repository binding
- WHEN the contributor reads the project's worktree management context
- THEN they receive a clear unbound state instead of worktree inventory or actions
- AND the project remains usable for the rest of the current platform workflows
