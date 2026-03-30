# Project Worktree Management

## ADDED Requirements

### Requirement: Projects Experience Shows Worktree Inventory
The system MUST show project-scoped worktree inventory for a software project with a bound local Git repository from the current Projects experience.

#### Scenario: A contributor opens worktree management for a bound project
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local Git repository
- WHEN the contributor views the project's worktree management area
- THEN they can inspect the visible worktree list for that repository
- AND they can identify the main repository worktree and any linked worktrees
- AND they can identify the path and checked-out branch for each visible worktree entry

### Requirement: Projects Experience Supports Worktree Creation
The system MUST allow contributors to request creation of a project worktree for an existing local branch from the current Projects experience when the repository is in a supported state.

#### Scenario: A contributor creates a worktree from the Projects experience
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local Git repository
- AND the repository is not in detached HEAD state
- AND the target branch is not already represented by another worktree
- WHEN the contributor requests a worktree for an existing local branch from the Projects experience
- THEN the request is sent through the backend-backed project worktree workflow
- AND the resulting worktree inventory reflects the new linked worktree after a successful change

#### Scenario: A contributor sees a restricted worktree creation state
- GIVEN the admin frontend is running against the current backend core
- AND a software project is either unbound, detached, or targeting a branch that already has a worktree
- WHEN the contributor views the project's worktree creation controls
- THEN the Projects experience shows a clear restricted or unavailable state
- AND it does not present that state as a normal successful creation path

### Requirement: Projects Experience Explains Worktree Deletion Restrictions
The system MUST make worktree deletion restrictions visible from the current Projects experience.

#### Scenario: A contributor inspects a deletable worktree
- GIVEN the admin frontend is running against the current backend core
- AND a software project shows a linked worktree that is clean and fully pushed
- WHEN the contributor views the worktree actions in the Projects experience
- THEN the deletion action is presented as available

#### Scenario: A contributor inspects a restricted worktree deletion state
- GIVEN the admin frontend is running against the current backend core
- AND a software project shows a linked worktree that is dirty, ahead of upstream, or has no upstream branch
- WHEN the contributor views the worktree actions in the Projects experience
- THEN the Projects experience shows that deletion is restricted
- AND it explains that the worktree must be handled manually before deletion

### Requirement: Projects Experience Supports Worktree Repair And Prune
The system MUST allow contributors to request project-scoped `repair` and `prune` operations from the current Projects experience.

#### Scenario: A contributor runs repair or prune from the Projects experience
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local Git repository
- WHEN the contributor requests a worktree repair or prune operation from the Projects experience
- THEN the request is sent through the backend-backed worktree management workflow
- AND the Projects experience reflects the refreshed worktree state after a successful operation

### Requirement: Projects Experience Reflects Worktree Mutation Outcomes Clearly
The system MUST make worktree management outcomes visible in the current Projects experience.

#### Scenario: A contributor submits a worktree management mutation
- GIVEN a contributor uses project-scoped worktree creation, deletion, repair, or prune from the Projects experience
- WHEN the backend accepts or rejects the request
- THEN the Projects experience shows a visible success or failure outcome
- AND the visible worktree context reflects the current backend repository state after a successful change
