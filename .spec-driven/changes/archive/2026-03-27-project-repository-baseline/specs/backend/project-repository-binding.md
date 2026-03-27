## ADDED Requirements

### Requirement: Software Projects May Bind One Local Repository
The system MUST allow a software project to keep one optional binding to a local Git repository so the project can act as both a delivery scope and an engineering scope.

#### Scenario: A contributor binds a project to a local repository
- GIVEN a software project exists without a repository binding
- WHEN the contributor binds that project to a valid local Git repository
- THEN the project keeps that repository binding
- AND the bound repository becomes available through the project's backend-supported engineering context

### Requirement: Repository Binding Uses Explicit Local Paths
The system MUST require project repository binding to use an absolute local path that resolves to a valid Git repository.

#### Scenario: A contributor binds a project using a valid repository path
- GIVEN a software project exists
- WHEN the contributor submits an absolute local path for a valid Git repository
- THEN the repository binding is accepted

#### Scenario: A contributor binds a project using an invalid repository path
- GIVEN a software project exists
- WHEN the contributor submits a path that is not absolute or does not resolve to a valid Git repository
- THEN the repository binding is rejected
- AND the project's previous repository state remains unchanged

### Requirement: Backend Exposes Minimal Repository Summary For A Bound Project
The system MUST expose a minimal repository summary for a project with a repository binding.

#### Scenario: A contributor inspects a bound project's repository summary
- GIVEN a software project is bound to a local Git repository
- WHEN the contributor reads the project's repository summary
- THEN they can identify the repository root path
- AND they can identify the current branch
- AND they can tell whether the working tree is clean or dirty
- AND they can inspect the latest commit summary

### Requirement: Unbound Projects Remain Valid
The system MUST keep software projects usable even when no repository binding has been configured.

#### Scenario: A contributor inspects a project without a repository binding
- GIVEN a software project has no repository binding
- WHEN the contributor reads the project's repository context
- THEN they receive a clear unbound state instead of repository details
- AND the project remains usable for the rest of the current platform workflows
