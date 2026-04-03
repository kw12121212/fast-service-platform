# Project Derived App Verification

### Requirement: Bound Projects Expose Derived-App Verification Context
The system MUST expose project-scoped derived-app verification context for a software project that is bound to a local Git repository.

#### Scenario: A contributor inspects derived-app verification context for a bound project
- GIVEN a software project is bound to a local Git repository
- WHEN the contributor reads the project's derived-app verification context
- THEN they can identify that derived-app verification is available for that project
- AND they can identify whether the project is currently in a normal or restricted verification state

### Requirement: Project-Scoped Verification Delegates To Repository-Owned Validation
The system MUST allow a bound software project to request derived-app verification through the project scope.
The first project-scoped verification path MUST use the latest visible successful project-scoped assembly output as its verification target.
The first project-scoped verification path MUST delegate generated-app verification and runtime smoke execution to repository-owned validation entrypoints instead of defining separate project-local validators.

#### Scenario: A contributor requests project-scoped verification in a supported state
- GIVEN a software project is bound to a local Git repository
- AND the project satisfies the preconditions for the first project-scoped verification path
- WHEN the contributor submits a valid project-scoped verification request
- THEN the repository-owned validation workflow is executed for that project
- AND the latest visible successful project-scoped assembly output is used as the verification target
- AND the project-scoped request does not replace the repository-owned verification contract or runtime smoke contract with a different runtime contract

#### Scenario: A contributor requests project-scoped verification in an unsupported state
- GIVEN a software project is bound to a local Git repository
- AND the project does not satisfy the preconditions for the first project-scoped verification path
- WHEN the contributor submits a project-scoped verification request
- THEN the request is rejected
- AND the project's verification context remains readable afterward

### Requirement: Project-Scoped Verification Reports Actionable Outcomes
The system MUST surface project-scoped derived-app verification outcomes in a way that distinguishes restricted or invalid request conditions from repository-owned validation execution failure.
The first project-scoped verification path MUST expose the latest visible outcome without requiring persistent run-history browsing.

#### Scenario: A contributor inspects a successful project-scoped verification outcome
- GIVEN a software project is bound to a local Git repository
- AND the contributor has run a successful project-scoped verification request
- WHEN the contributor reads the project's verification context
- THEN they can identify that the request succeeded
- AND they can identify the verified target associated with that successful outcome

#### Scenario: A contributor inspects the latest verification outcome
- GIVEN a software project is bound to a local Git repository
- AND the contributor has already run one or more project-scoped verification requests
- WHEN the contributor reads the project's verification context
- THEN they can identify the latest visible verification outcome
- AND they do not need a persistent history log to interpret the current project-scoped verification state in the first release

#### Scenario: A contributor encounters a verification execution failure
- GIVEN a software project is bound to a local Git repository
- AND the contributor submits a syntactically valid project-scoped verification request
- WHEN the repository-owned validation workflow fails during execution
- THEN the reported outcome identifies that the failure happened during validation execution rather than request validation alone

### Requirement: Unbound Projects Do Not Pretend To Have Normal Derived-App Verification State
The system MUST keep software projects usable when no repository binding exists, without exposing fake normal project-scoped derived-app verification behavior.

#### Scenario: A contributor inspects derived-app verification for an unbound project
- GIVEN a software project has no repository binding
- WHEN the contributor reads the project's derived-app verification context
- THEN they receive a clear unbound or restricted state instead of normal verification execution behavior
- AND the project remains usable for the rest of the current platform workflows
