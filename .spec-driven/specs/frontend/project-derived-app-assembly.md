# Project Derived App Assembly

### Requirement: Projects Experience Shows Project-Scoped Derived-App Assembly Availability
The system MUST show whether project-scoped derived-app assembly is available for a software project from the current Projects experience.
The first project-scoped assembly path MUST be presented against the bound project's main repository context rather than as a linked-worktree selection flow.

#### Scenario: A contributor opens derived-app assembly from the Projects experience
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local Git repository
- WHEN the contributor views the project's derived-app assembly area in the Projects experience
- THEN they can identify the project-scoped assembly entrypoint
- AND they can identify whether the current project state supports a normal assembly request

#### Scenario: A contributor views assembly for a project that also has linked worktrees
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local Git repository
- AND the project also has managed linked worktrees
- WHEN the contributor views the project's derived-app assembly area in the Projects experience
- THEN the Projects experience presents the first assembly path against the main bound repository context
- AND it does not require the contributor to choose a linked worktree as part of the initial assembly flow

### Requirement: Projects Experience Supports Manifest-Driven Derived-App Assembly Requests
The system MUST allow contributors to request project-scoped derived-app assembly from the current Projects experience by submitting a valid `app-manifest` input and an explicit output directory.
The first Projects experience for project-scoped derived-app assembly MUST support direct editing or pasting of raw `app-manifest` content.

#### Scenario: A contributor requests project-scoped assembly from the Projects experience
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local Git repository
- WHEN the contributor submits a valid project-scoped assembly request from the Projects experience
- THEN the request is sent through the backend-backed project assembly workflow
- AND the visible project state reflects the successful assembly outcome after the request completes

#### Scenario: A contributor prepares manifest input in the Projects experience
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local Git repository
- WHEN the contributor uses the project's derived-app assembly area
- THEN they can directly paste or edit raw `app-manifest` content for the assembly request

### Requirement: Projects Experience Shows Restricted Assembly States Clearly
The system MUST show clear restricted or unavailable project-scoped assembly states from the current Projects experience.

#### Scenario: A contributor inspects project-scoped assembly for an unbound project
- GIVEN the admin frontend is running against the current backend core
- AND a software project has no repository binding
- WHEN the contributor views the project's derived-app assembly area in the Projects experience
- THEN the Projects experience shows a clear unbound or restricted state instead of normal assembly controls
- AND it does not present that state as a normal successful assembly path

### Requirement: Projects Experience Reflects Assembly Outcomes Clearly
The system MUST make project-scoped derived-app assembly outcomes visible in the current Projects experience.
The first assembly surface MUST let contributors identify the latest visible outcome without requiring persistent run-history browsing.

#### Scenario: A contributor submits a project-scoped assembly request
- GIVEN a contributor uses project-scoped derived-app assembly from the Projects experience
- WHEN the backend accepts or rejects the request
- THEN the Projects experience shows a visible success or failure outcome
- AND the visible project assembly context reflects the current backend state after a successful request

#### Scenario: A contributor sees an execution failure in the Projects experience
- GIVEN a contributor uses project-scoped derived-app assembly from the Projects experience
- AND the request passes input validation
- WHEN the repository-owned assembly workflow fails during execution
- THEN the Projects experience shows a clear failed outcome
- AND it does not present the failure as an invalid-input-only response

#### Scenario: A contributor sees the latest assembly outcome after service restart
- GIVEN the admin frontend is running against the current backend core
- AND a project-scoped assembly outcome has already been recorded for a bound project
- AND the backend service has restarted since that outcome was recorded
- WHEN the contributor opens the project's derived-app assembly area in the Projects experience
- THEN the Projects experience still reflects the latest visible assembly outcome
