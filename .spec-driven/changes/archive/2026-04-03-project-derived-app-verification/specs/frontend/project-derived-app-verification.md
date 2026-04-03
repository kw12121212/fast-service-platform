# Project Derived App Verification

## ADDED Requirements

### Requirement: Projects Experience Shows Project-Scoped Derived-App Verification Availability
The system MUST show whether project-scoped derived-app verification is available for a software project from the current Projects experience.

#### Scenario: A contributor opens derived-app verification from the Projects experience
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local Git repository
- WHEN the contributor views the project's derived-app verification area in the Projects experience
- THEN they can identify the project-scoped verification entrypoint
- AND they can identify whether the current project state supports a normal verification request

### Requirement: Projects Experience Supports Project-Scoped Verification Requests
The system MUST allow contributors to request project-scoped derived-app verification from the current Projects experience when the current project state satisfies the first verification path's preconditions.
The first project-scoped verification path MUST use the latest visible successful project-scoped assembly output as its target instead of asking the contributor to choose an arbitrary directory.

#### Scenario: A contributor requests project-scoped verification from the Projects experience
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local Git repository
- AND the project satisfies the preconditions for the first project-scoped verification path
- WHEN the contributor submits a valid project-scoped verification request from the Projects experience
- THEN the request is sent through the backend-backed project verification workflow
- AND the visible project state reflects the successful verification outcome after the request completes

### Requirement: Projects Experience Shows Restricted Verification States Clearly
The system MUST show clear restricted or unavailable project-scoped verification states from the current Projects experience.

#### Scenario: A contributor inspects project-scoped verification for an unbound project
- GIVEN the admin frontend is running against the current backend core
- AND a software project has no repository binding
- WHEN the contributor views the project's derived-app verification area in the Projects experience
- THEN the Projects experience shows a clear unbound or restricted state instead of normal verification controls
- AND it does not present that state as a normal successful verification path

#### Scenario: A contributor inspects project-scoped verification for a bound project in a restricted state
- GIVEN the admin frontend is running against the current backend core
- AND a software project is bound to a local Git repository
- AND the project does not satisfy the preconditions for the first project-scoped verification path
- WHEN the contributor views the project's derived-app verification area in the Projects experience
- THEN the Projects experience shows a clear restricted or unavailable state
- AND it does not present that state as a normal verification-ready path

### Requirement: Projects Experience Reflects Verification Outcomes Clearly
The system MUST make project-scoped derived-app verification outcomes visible in the current Projects experience.
The first verification surface MUST let contributors identify the latest visible outcome without requiring persistent run-history browsing.

#### Scenario: A contributor submits a project-scoped verification request
- GIVEN a contributor uses project-scoped derived-app verification from the Projects experience
- WHEN the backend accepts or rejects the request
- THEN the Projects experience shows a visible success or failure outcome
- AND the visible project verification context reflects the current backend state after a successful request

#### Scenario: A contributor sees a validation execution failure in the Projects experience
- GIVEN a contributor uses project-scoped derived-app verification from the Projects experience
- AND the request passes project-scoped request validation
- WHEN the repository-owned validation workflow fails during execution
- THEN the Projects experience shows a clear failed outcome
- AND it does not present the failure as an invalid-request-only response
