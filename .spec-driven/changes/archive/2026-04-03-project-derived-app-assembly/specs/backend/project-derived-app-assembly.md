# Project Derived App Assembly

## ADDED Requirements

### Requirement: Bound Projects Expose Derived-App Assembly Context
The system MUST expose project-scoped derived-app assembly context for a software project that is bound to a local Git repository.
The first project-scoped assembly path MUST use the bound project's main repository context as its only normal assembly source.

#### Scenario: A contributor inspects derived-app assembly context for a bound project
- GIVEN a software project is bound to a local Git repository
- WHEN the contributor reads the project's derived-app assembly context
- THEN they can identify that derived-app assembly is available for that project
- AND they can identify whether the project is currently in a normal or restricted assembly state

#### Scenario: A contributor inspects assembly context for a project with managed linked worktrees
- GIVEN a software project is bound to a local Git repository
- AND that project also has managed linked worktrees
- WHEN the contributor reads the project's derived-app assembly context
- THEN they can identify the project-scoped assembly path against the main bound repository context
- AND they do not need to choose a linked worktree as a normal assembly source in the first release

### Requirement: Project-Scoped Assembly Accepts Manifest Input And Explicit Output Directory
The system MUST allow a bound software project to request derived-app assembly by providing a valid `app-manifest` input and an explicit absolute output directory.

#### Scenario: A contributor requests project-scoped assembly with valid input
- GIVEN a software project is bound to a local Git repository
- WHEN the contributor submits a valid `app-manifest` input and an explicit absolute output directory through the project's assembly workflow
- THEN the repository-owned assembly workflow is executed for that project
- AND the generated application is written to the requested output directory

#### Scenario: A contributor requests project-scoped assembly with invalid input
- GIVEN a software project is bound to a local Git repository
- WHEN the contributor submits an invalid manifest input or a non-absolute output directory through the project's assembly workflow
- THEN the request is rejected
- AND the project's assembly context remains readable afterward

### Requirement: Project-Scoped Assembly Reports Actionable Outcomes
The system MUST surface project-scoped derived-app assembly outcomes in a way that distinguishes invalid request input from repository-owned assembly execution failure.
The first project-scoped assembly path MUST expose the latest visible outcome without requiring persistent run-history browsing.

#### Scenario: A contributor inspects a successful project-scoped assembly outcome
- GIVEN a software project is bound to a local Git repository
- AND the contributor has run a successful project-scoped assembly request
- WHEN the contributor reads the project's assembly context
- THEN they can identify that the request succeeded
- AND they can identify the output directory used for that successful assembly

#### Scenario: A contributor inspects the latest assembly outcome
- GIVEN a software project is bound to a local Git repository
- AND the contributor has already run one or more project-scoped assembly requests
- WHEN the contributor reads the project's assembly context
- THEN they can identify the latest visible assembly outcome
- AND they do not need a persistent history log to interpret the current project-scoped assembly state in the first release

#### Scenario: A contributor encounters an assembly execution failure
- GIVEN a software project is bound to a local Git repository
- AND the contributor submits a syntactically valid project-scoped assembly request
- WHEN the repository-owned assembly workflow fails during execution
- THEN the reported outcome identifies that the failure happened during assembly execution rather than request validation

### Requirement: Unbound Projects Do Not Pretend To Have Normal Derived-App Assembly State
The system MUST keep software projects usable when no repository binding exists, without exposing fake normal project-scoped derived-app assembly behavior.

#### Scenario: A contributor inspects derived-app assembly for an unbound project
- GIVEN a software project has no repository binding
- WHEN the contributor reads the project's derived-app assembly context
- THEN they receive a clear unbound or restricted state instead of normal assembly execution behavior
- AND the project remains usable for the rest of the current platform workflows
