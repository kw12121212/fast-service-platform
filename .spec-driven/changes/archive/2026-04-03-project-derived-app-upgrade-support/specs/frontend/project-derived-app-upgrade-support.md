# Project Derived App Upgrade Support

## ADDED Requirements

### Requirement: Projects Experience Shows Project-Scoped Derived-App Upgrade Support Availability
The system MUST show whether project-scoped derived-app upgrade support is available for a software project from the current Projects experience.

#### Scenario: A contributor opens derived-app upgrade support from the Projects experience
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local Git repository
- WHEN the contributor views the project's derived-app upgrade support area in the Projects experience
- THEN they can identify the project-scoped upgrade support entrypoint
- AND they can identify whether the current project state supports a normal upgrade-support request

### Requirement: Projects Experience Supports Project-Scoped Upgrade Support Requests
The system MUST allow contributors to request project-scoped derived-app upgrade support from the current Projects experience when the current project state satisfies the first upgrade-support path's preconditions.
The first project-scoped upgrade support path MUST use the current project-derived lifecycle target instead of asking the contributor to choose an arbitrary directory.

#### Scenario: A contributor requests project-scoped upgrade support from the Projects experience
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local Git repository
- AND the project satisfies the preconditions for the first project-scoped upgrade support path
- WHEN the contributor submits a valid project-scoped upgrade support request from the Projects experience
- THEN the request is sent through the backend-backed project upgrade-support workflow
- AND the visible project state reflects the successful upgrade-support outcome after the request completes

#### Scenario: A contributor requests a dry-run upgrade plan from the Projects experience
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local Git repository
- AND the project satisfies the preconditions for the first project-scoped upgrade support path
- WHEN the contributor requests `upgrade-execute --dry-run` for a supported target release from the project's upgrade support area
- THEN the request is sent through the backend-backed project upgrade-support workflow
- AND the Projects experience shows the resulting dry-run execution plan without presenting it as a completed real upgrade

#### Scenario: A contributor reviews supported target releases in the Projects experience
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local Git repository
- AND the project satisfies the preconditions for the first project-scoped upgrade support path
- WHEN the contributor uses the project's derived-app upgrade support area
- THEN they can identify the repository-declared supported target releases and related advisory guidance for the project-derived lifecycle target

### Requirement: Projects Experience Shows Restricted Upgrade Support States Clearly
The system MUST show clear restricted or unavailable project-scoped derived-app upgrade support states from the current Projects experience.

#### Scenario: A contributor inspects project-scoped upgrade support for an unbound project
- GIVEN the admin frontend is running against the current backend core
- AND a software project has no repository binding
- WHEN the contributor views the project's derived-app upgrade support area in the Projects experience
- THEN the Projects experience shows a clear unbound or restricted state instead of normal upgrade-support controls
- AND it does not present that state as a normal successful upgrade-support path

#### Scenario: A contributor inspects project-scoped upgrade support for a bound project in a restricted state
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local Git repository
- AND the project does not satisfy the preconditions for the first project-scoped upgrade support path
- WHEN the contributor views the project's derived-app upgrade support area in the Projects experience
- THEN the Projects experience shows a clear restricted or unavailable state
- AND it does not present that state as a normal upgrade-support-ready path

### Requirement: Projects Experience Reflects Upgrade Support Outcomes Clearly
The system MUST make project-scoped derived-app upgrade support outcomes visible in the current Projects experience.
The first upgrade-support surface MUST let contributors identify the latest visible outcome without requiring persistent run-history browsing.
The latest visible outcome shown from the Projects experience MUST identify which project-scoped upgrade-support request produced it.

#### Scenario: A contributor submits a project-scoped upgrade support request
- GIVEN a contributor uses project-scoped derived-app upgrade support from the Projects experience
- WHEN the backend accepts or rejects the request
- THEN the Projects experience shows a visible success or failure outcome
- AND the visible project upgrade-support context reflects the current backend state after a successful request

#### Scenario: A contributor sees an upgrade execution failure in the Projects experience
- GIVEN a contributor uses project-scoped derived-app upgrade support from the Projects experience
- AND the request passes project-scoped request validation
- WHEN the repository-owned upgrade workflow fails during execution
- THEN the Projects experience shows a clear failed outcome
- AND it does not present the failure as an invalid-request-only response
