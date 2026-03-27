# Project Repository Management

### Requirement: Projects Experience Supports Repository Binding
The system MUST allow contributors to bind a software project to a local repository from the current software project management experience.

#### Scenario: A contributor binds a repository from the projects experience
- GIVEN the admin frontend is running against the current backend core
- AND a software project is visible in the projects experience
- WHEN the contributor submits a valid repository binding for that project
- THEN the binding is sent through the backend-backed project workflow
- AND the projects experience reflects the new bound repository state

### Requirement: Projects Experience Shows Repository Context Clearly
The system MUST show the current repository context for a project in the software project management experience.

#### Scenario: A contributor opens a project with a repository binding
- GIVEN a software project is bound to a local Git repository
- WHEN the contributor views that project in the admin frontend
- THEN they can see the repository root path
- AND they can see the current branch
- AND they can see whether the working tree is clean or dirty
- AND they can see the latest commit summary

#### Scenario: A contributor opens a project without a repository binding
- GIVEN a software project has no repository binding
- WHEN the contributor views that project in the admin frontend
- THEN they see a clear empty state explaining that no repository is currently bound

### Requirement: Projects Experience Reflects Repository Mutation Outcomes
The system MUST make repository binding outcomes visible in the software project management experience.

#### Scenario: A contributor submits a repository binding
- GIVEN a contributor uses the repository binding workflow for a project
- WHEN the backend accepts or rejects the request
- THEN the admin frontend shows a visible success or failure outcome
- AND the project view reflects the current backend repository state after a successful update
