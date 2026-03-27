# Project Git Management Delta

## ADDED Requirements

### Requirement: Bound Projects Expose Minimal Git Management Context
The system MUST expose minimal Git management context for a software project
that is bound to a local Git repository.

#### Scenario: A contributor inspects Git context for a bound project
- GIVEN a software project is bound to a local Git repository
- WHEN the contributor reads the project's Git management context
- THEN they can identify the repository root path
- AND they can identify whether the repository is currently on a branch or in
  detached HEAD state
- AND they can identify whether the working tree is clean or dirty
- AND they can inspect a small recent-commit list for the current repository
  state

### Requirement: Bound Projects Expose Existing Local Branches
The system MUST expose the existing local branch list for a software project
that is bound to a local Git repository.

#### Scenario: A contributor inspects branch options for a bound project
- GIVEN a software project is bound to a local Git repository
- WHEN the contributor reads the project's available branch list
- THEN they can inspect the existing local branches for that repository
- AND they can identify which branch is currently checked out when the
  repository is not in detached HEAD state

### Requirement: Branch Switching Requires A Clean Working Tree
The system MUST allow a software project bound to a local Git repository to
switch to an existing local branch only when the repository working tree is
clean.

#### Scenario: A contributor switches a bound project to another local branch
- GIVEN a software project is bound to a local Git repository
- AND the repository working tree is clean
- AND the target branch already exists locally
- WHEN the contributor requests a branch switch
- THEN the repository switches to the requested local branch
- AND the updated branch becomes visible through the project's Git management
  context

#### Scenario: A contributor tries to switch branches with a dirty working tree
- GIVEN a software project is bound to a local Git repository
- AND the repository working tree is dirty
- WHEN the contributor requests a branch switch
- THEN the branch switch is rejected
- AND the current repository state remains unchanged

### Requirement: Detached HEAD Is A Restricted Git Management State
The system MUST expose detached HEAD as a restricted Git management state for a
bound software project.

#### Scenario: A contributor inspects a bound project in detached HEAD state
- GIVEN a software project is bound to a local Git repository
- AND the repository is in detached HEAD state
- WHEN the contributor reads the project's Git management context
- THEN they can see that detached HEAD is the current repository state
- AND they can still inspect the recent-commit list
- AND the project does not expose detached HEAD as a normal branch-switching
  target

### Requirement: Unbound Projects Do Not Pretend To Have Git Management State
The system MUST keep software projects usable when no repository binding exists,
without exposing fake Git management data.

#### Scenario: A contributor inspects Git management state for an unbound project
- GIVEN a software project has no repository binding
- WHEN the contributor reads the project's Git management context
- THEN they receive a clear unbound state instead of branch or commit data
- AND the project remains usable for the rest of the current platform workflows
