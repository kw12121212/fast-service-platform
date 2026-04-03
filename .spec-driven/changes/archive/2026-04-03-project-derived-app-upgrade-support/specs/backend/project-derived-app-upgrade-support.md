# Project Derived App Upgrade Support

## ADDED Requirements

### Requirement: Bound Projects Expose Derived-App Upgrade Support Context
The system MUST expose project-scoped derived-app upgrade support context for a software project that is bound to a local Git repository.

#### Scenario: A contributor inspects derived-app upgrade support context for a bound project
- GIVEN a software project is bound to a local Git repository
- WHEN the contributor reads the project's derived-app upgrade support context
- THEN they can identify that derived-app upgrade support is available for that project
- AND they can identify whether the project is currently in a normal or restricted upgrade-support state

### Requirement: Project-Scoped Upgrade Support Delegates To Repository-Owned Upgrade Tooling
The system MUST allow a bound software project to request project-scoped derived-app upgrade support through the project scope.
The first project-scoped upgrade support path MUST use the current project-derived lifecycle target instead of requiring an arbitrary derived-app directory.
The first project-scoped upgrade support path MUST delegate supported target lookup, advisory review, upgrade compatibility evaluation, and `upgrade-execute --dry-run` to repository-owned upgrade entrypoints instead of defining separate project-local upgrade logic.

#### Scenario: A contributor requests project-scoped upgrade support in a supported state
- GIVEN a software project is bound to a local Git repository
- AND the project satisfies the preconditions for the first project-scoped upgrade support path
- WHEN the contributor requests supported target lookup, advisory review, upgrade compatibility evaluation, or `upgrade-execute --dry-run`
- THEN the repository-owned upgrade workflow is executed for that project
- AND the current project-derived lifecycle target is used as the upgrade subject
- AND the project-scoped request does not replace the repository-owned lifecycle, advisory, or upgrade contracts with a different runtime contract

#### Scenario: A contributor requests project-scoped upgrade support in an unsupported state
- GIVEN a software project is bound to a local Git repository
- AND the project does not satisfy the preconditions for the first project-scoped upgrade support path
- WHEN the contributor submits a project-scoped upgrade support request
- THEN the request is rejected
- AND the project's upgrade-support context remains readable afterward

### Requirement: Project-Scoped Upgrade Support Reports Actionable Outcomes
The system MUST surface project-scoped derived-app upgrade support outcomes in a way that distinguishes restricted or invalid request conditions from repository-owned upgrade execution failure.
The first project-scoped upgrade support path MUST expose the latest visible outcome without requiring persistent run-history browsing.
The latest visible outcome MUST identify which project-scoped upgrade-support request produced it.

#### Scenario: A contributor inspects a successful project-scoped upgrade-support outcome
- GIVEN a software project is bound to a local Git repository
- AND the contributor has run a successful project-scoped upgrade support request
- WHEN the contributor reads the project's upgrade-support context
- THEN they can identify that the request succeeded
- AND they can identify the evaluated target release or advisory subject associated with that successful outcome

#### Scenario: A contributor inspects the latest upgrade-support outcome
- GIVEN a software project is bound to a local Git repository
- AND the contributor has already run one or more project-scoped upgrade support requests
- WHEN the contributor reads the project's upgrade-support context
- THEN they can identify the latest visible upgrade-support outcome
- AND they do not need a persistent history log to interpret the current project-scoped upgrade-support state in the first release

#### Scenario: A contributor encounters an upgrade execution failure
- GIVEN a software project is bound to a local Git repository
- AND the contributor submits a syntactically valid project-scoped upgrade support request
- WHEN the repository-owned upgrade workflow fails during execution
- THEN the reported outcome identifies that the failure happened during upgrade execution rather than request validation alone

#### Scenario: A contributor requests a dry-run upgrade plan in a supported state
- GIVEN a software project is bound to a local Git repository
- AND the project satisfies the preconditions for the first project-scoped upgrade support path
- AND the contributor has selected a repository-declared supported target release
- WHEN the contributor requests `upgrade-execute --dry-run`
- THEN the repository-owned dry-run execution workflow is executed for that project
- AND the outcome includes the dry-run execution plan for the selected target release
- AND the request does not mutate the project-derived lifecycle target as if a real upgrade had already been applied

### Requirement: Unbound Projects Do Not Pretend To Have Normal Derived-App Upgrade Support State
The system MUST keep software projects usable when no repository binding exists, without exposing fake normal project-scoped derived-app upgrade support behavior.

#### Scenario: A contributor inspects derived-app upgrade support for an unbound project
- GIVEN a software project has no repository binding
- WHEN the contributor reads the project's derived-app upgrade support context
- THEN they receive a clear unbound or restricted state instead of normal upgrade-support execution behavior
- AND the project remains usable for the rest of the current platform workflows
