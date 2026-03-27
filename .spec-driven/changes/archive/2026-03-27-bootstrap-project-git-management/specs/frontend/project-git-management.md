# Project Git Management Delta

## ADDED Requirements

### Requirement: Projects Experience Shows Git Management Context
The system MUST show Git management context for a software project with a bound
local repository from the current Projects experience.

#### Scenario: A contributor opens a bound project in the Projects experience
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local Git repository
- WHEN the contributor views that project in the Projects experience
- THEN they can see the repository root path
- AND they can see whether the repository is on a branch or in detached HEAD
  state
- AND they can see whether the working tree is clean or dirty
- AND they can inspect a small recent-commit list

### Requirement: Projects Experience Shows Available Local Branches
The system MUST show the existing local branch options for a bound project in
the current Projects experience.

#### Scenario: A contributor views branch options for a bound project
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local Git repository
- WHEN the contributor opens the project's Git management area
- THEN they can inspect the existing local branches for that repository
- AND they can identify the current checked-out branch when the repository is
  not in detached HEAD state

### Requirement: Projects Experience Supports Safe Branch Switching
The system MUST allow contributors to request a switch to an existing local
branch for a bound project from the current Projects experience.

#### Scenario: A contributor switches to another local branch
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local Git repository
- AND the repository working tree is clean
- WHEN the contributor requests a switch to an existing local branch from the
  Projects experience
- THEN the branch-switch request is sent through the backend-backed Git
  management workflow
- AND the Projects experience reflects the updated repository state after a
  successful switch

#### Scenario: A contributor cannot switch branches from a restricted state
- GIVEN the admin frontend is running against the current backend core
- AND a software project is either unbound, dirty, or in detached HEAD state
- WHEN the contributor views the project's Git management controls
- THEN the Projects experience shows a clear restricted or unavailable state
- AND it does not present that state as a normal successful branch-switch flow

### Requirement: Projects Experience Reflects Git Mutation Outcomes Clearly
The system MUST make Git management outcomes visible in the current Projects
experience.

#### Scenario: A contributor submits a branch-switch request
- GIVEN a contributor uses the branch-switch workflow for a bound project
- WHEN the backend accepts or rejects the request
- THEN the Projects experience shows a visible success or failure outcome
- AND the visible Git context reflects the current backend repository state
  after a successful change
