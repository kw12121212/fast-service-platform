# Project Sandbox Environment

### Requirement: Projects Experience Shows Sandbox Context For Linked Worktrees
The system MUST show project-scoped sandbox context for a managed linked worktree from the current Projects experience.

#### Scenario: A contributor opens sandbox context for a linked worktree
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local repository
- AND the project includes a managed linked worktree checked out to branch `feature/api-preview`
- WHEN the contributor views sandbox support for that linked worktree in the Projects experience
- THEN they can see the linked worktree path
- AND they can see whether a persistent image exists for that linked worktree
- AND they can see whether that linked worktree currently has an active temporary container
- AND they can see whether the linked worktree is sandbox-ready or restricted

### Requirement: Projects Experience Supports Sandbox Image Creation For Linked Worktrees
The system MUST allow contributors to request sandbox image creation for a managed linked worktree from the current Projects experience.

#### Scenario: A contributor creates a sandbox image from the Projects experience
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local repository
- AND the project includes a managed linked worktree checked out to branch `feature/api-preview`
- WHEN the contributor requests sandbox image creation from the Projects experience
- THEN the request is sent through the backend-backed project sandbox workflow
- AND the Projects experience reflects the refreshed sandbox context after the image creation attempt completes

### Requirement: Projects Experience Supports Temporary Container Lifecycle For Linked Worktrees
The system MUST allow contributors to create and destroy a temporary sandbox container for a managed linked worktree from the current Projects experience.

#### Scenario: A contributor creates a temporary sandbox container from the Projects experience
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local repository
- AND the project includes a managed linked worktree checked out to branch `feature/api-preview`
- AND a persistent sandbox image already exists for that linked worktree
- WHEN the contributor requests sandbox container creation from the Projects experience
- THEN the request is sent through the backend-backed sandbox workflow
- AND the Projects experience reflects the refreshed sandbox context after the container creation attempt completes

#### Scenario: A contributor destroys a temporary sandbox container from the Projects experience
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local repository
- AND the project includes a managed linked worktree checked out to branch `feature/api-preview`
- AND that linked worktree currently has an active temporary sandbox container
- WHEN the contributor requests sandbox container destruction from the Projects experience
- THEN the request is sent through the backend-backed sandbox workflow
- AND the Projects experience reflects the refreshed sandbox context after the container destruction completes

### Requirement: Projects Experience Shows Restricted Sandbox States Clearly
The system MUST show clear restricted or unavailable sandbox states from the current Projects experience.

#### Scenario: A contributor inspects a worktree that cannot use normal sandbox behavior
- GIVEN the admin frontend is running against the current backend core
- AND the selected sandbox source is either the main repository worktree, a linked worktree without an image, or a linked worktree that already has an active container when another create is requested
- WHEN the contributor views sandbox controls in the Projects experience
- THEN the Projects experience shows a clear restricted or unavailable state
- AND it does not present that state as a normal successful sandbox path

#### Scenario: A contributor inspects sandbox support for an unbound project
- GIVEN the admin frontend is running against the current backend core
- AND a software project has no repository binding
- WHEN the contributor views sandbox support in the Projects experience
- THEN the Projects experience shows a clear unbound state instead of normal sandbox controls

### Requirement: Projects Experience Reflects Sandbox Outcomes Clearly
The system MUST make sandbox lifecycle outcomes visible in the current Projects experience.

#### Scenario: A contributor submits a sandbox action
- GIVEN a contributor uses the sandbox workflow from the Projects experience
- WHEN the backend accepts or rejects the image creation, container creation, or container destruction request
- THEN the Projects experience shows a visible success or failure outcome
- AND the visible sandbox context reflects the current backend state after a successful request

#### Scenario: A contributor triggers an initialization failure
- GIVEN a contributor uses the sandbox workflow from the Projects experience
- AND the selected `init-image.sh` or `init-project.sh` exits with a non-zero status
- WHEN the backend rejects the request
- THEN the Projects experience shows a clear sandbox-failed outcome
- AND it indicates that sandbox initialization did not complete successfully
