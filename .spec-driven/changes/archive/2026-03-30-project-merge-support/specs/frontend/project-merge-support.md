# Project Merge Support

## ADDED Requirements

### Requirement: Projects Experience Shows Merge Support Context For Linked Worktrees
The system MUST show project-scoped merge support context for a managed linked worktree from the current Projects experience.

#### Scenario: A contributor opens merge support for a linked worktree
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local Git repository
- AND the project includes a managed linked worktree checked out to local branch `feature/api-preview`
- WHEN the contributor views merge support for that linked worktree in the Projects experience
- THEN they can see the source worktree path
- AND they can see the source branch currently checked out in that worktree
- AND they can inspect the available target branches exposed by the backend-backed merge workflow
- AND they can see whether the linked worktree is merge-ready or restricted

### Requirement: Projects Experience Supports Merge Requests From Linked Worktrees
The system MUST allow contributors to request a project-scoped merge from a managed linked worktree into another existing local branch from the current Projects experience.

#### Scenario: A contributor merges a linked worktree branch into another local branch
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local Git repository
- AND the project includes a managed linked worktree checked out to local branch `feature/api-preview`
- AND the linked worktree is merge-ready
- WHEN the contributor requests a merge from that linked worktree into local branch `main`
- THEN the request is sent through the backend-backed project merge workflow
- AND the Projects experience reflects the refreshed Git and worktree context after a successful merge

### Requirement: Projects Experience Shows Restricted Merge States Clearly
The system MUST show clear restricted or unavailable merge states from the current Projects experience.

#### Scenario: A contributor inspects a linked worktree that cannot be merged
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local Git repository
- AND the selected merge source is either the main repository worktree, a dirty linked worktree, or a linked worktree without any valid target branch
- WHEN the contributor views the merge controls in the Projects experience
- THEN the Projects experience shows a clear restricted or unavailable state
- AND it does not present that state as a normal successful merge path

#### Scenario: A contributor inspects merge support for an unbound project
- GIVEN the admin frontend is running against the current backend core
- AND a software project has no repository binding
- WHEN the contributor views merge support in the Projects experience
- THEN the Projects experience shows a clear unbound state instead of normal merge controls

### Requirement: Projects Experience Reflects Merge Outcomes Clearly
The system MUST make project-scoped merge outcomes visible in the current Projects experience.

#### Scenario: A contributor submits a project merge
- GIVEN a contributor uses the project merge workflow from the Projects experience
- WHEN the backend accepts or rejects the merge request
- THEN the Projects experience shows a visible success or failure outcome
- AND the visible project Git and worktree context reflects the current backend repository state after a successful merge

#### Scenario: A contributor submits a conflicting merge
- GIVEN a contributor uses the project merge workflow from the Projects experience
- AND the requested merge would produce conflicts
- WHEN the backend rejects the merge
- THEN the Projects experience shows a clear merge-failed outcome
- AND it indicates that the conflict was not auto-resolved by the platform
