---
mapping:
  implementation:
    - backend/src/main/java/com/fastservice/platform/backend/team/TeamServiceImpl.java
    - backend/src/main/resources/sql/tables.sql
    - backend/src/main/resources/sql/services.sql
    - backend/src/main/resources/sql/demo.sql
  tests: []
---

### Requirement: Team Entity And CRUD
The system MUST provide a team entity with name, description, and status fields, and MUST support create, read, update, and delete operations for teams.

#### Scenario: A contributor creates a team
- GIVEN a bound project exists
- WHEN a contributor creates a team with a name and optional description
- THEN the team is persisted and retrievable with the given name, description, and an initial active status

#### Scenario: A contributor lists teams
- GIVEN one or more teams exist
- WHEN a contributor queries the team list
- THEN all teams are returned with their name, description, status, and member count

#### Scenario: A contributor updates a team
- GIVEN a team exists
- WHEN a contributor updates the team name or description
- THEN the team reflects the updated values

#### Scenario: A contributor deletes a team
- GIVEN a team exists with no bound projects
- WHEN a contributor deletes the team
- THEN the team and all its memberships are removed

### Requirement: Team Membership Management
The system MUST support adding and removing users as team members, and MUST enumerate members for a given team.

#### Scenario: A contributor adds a user to a team
- GIVEN a team exists and a user exists
- WHEN a contributor adds the user as a team member
- THEN the user appears in the team member list

#### Scenario: A contributor removes a user from a team
- GIVEN a user is a member of a team
- WHEN a contributor removes the user from the team
- THEN the user no longer appears in the team member list
- AND any team-scoped role assignments for that membership are removed

#### Scenario: A contributor lists team members
- GIVEN a team has multiple members
- WHEN a contributor queries the team member list
- THEN all members are returned with their user details and assigned team roles

### Requirement: Team-Project Binding
The system MUST support binding teams to one or more projects and listing which teams are bound to a given project.

#### Scenario: A contributor binds a team to a project
- GIVEN a team and a project exist
- WHEN a contributor binds the team to the project
- THEN the team appears in the project's bound-team list

#### Scenario: A contributor unbinds a team from a project
- GIVEN a team is bound to a project
- WHEN a contributor unbinds the team from the project
- THEN the team no longer appears in the project's bound-team list

#### Scenario: A contributor lists teams for a project
- GIVEN multiple teams are bound to a project
- WHEN a contributor queries teams for that project
- THEN all bound teams are returned

### Requirement: Team-Scoped Roles
The system MUST support team-scoped roles (Scrum Master, Product Owner, Developer, Observer) assignable to team members within the team-project context, separate from platform RBAC.

#### Scenario: A contributor assigns a team role to a member
- GIVEN a user is a member of a team
- WHEN a contributor assigns the Scrum Master role to that member
- THEN the member's team role list includes Scrum Master

#### Scenario: A contributor removes a team role from a member
- GIVEN a member has the Developer team role
- WHEN a contributor removes that role assignment
- THEN the member no longer has the Developer team role

#### Scenario: Team roles do not affect platform RBAC
- GIVEN a member has the Scrum Master team-scoped role
- WHEN a contributor queries the platform RBAC roles for that user
- THEN the Scrum Master team role does not appear in the platform RBAC role list
