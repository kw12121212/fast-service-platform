---
mapping:
  implementation:
    - frontend/src/features/teams/teams-page.tsx
    - docs/ai/management-modules/team-directory.management-module.json
    - docs/ai/module-registry.json
    - frontend/src/app/module-selection.ts
    - frontend/src/app/router.tsx
    - frontend/src/app/navigation.ts
    - frontend/src/lib/api/hooks.ts
    - frontend/src/lib/api/types.ts
  tests: []
---

### Requirement: Team Management Descriptor-Driven Module
The system MUST provide a team management module generated from a descriptor-driven management-module descriptor that reuses the existing dynamic form and dynamic report platform components.

#### Scenario: A contributor inspects the team management module
- GIVEN the team management module is registered in the module registry
- WHEN a contributor navigates to the team management route
- THEN the team list is rendered using the dynamic report component
- AND team create/edit interactions use the dynamic form component

#### Scenario: A contributor manages team members through the UI
- GIVEN a team exists
- WHEN a contributor views the team detail
- THEN team members are listed with their display names and team-scoped roles

### Requirement: Team Management Module Registration
The team management module MUST be registered in the module registry as an optional business module depending on user-management and role-permission-management.

#### Scenario: A contributor checks the module registry
- GIVEN the team management module is installed
- WHEN a contributor inspects the module registry
- THEN a `team-management` entry exists with role `optional-business-module`
- AND it depends on `user-management` and `role-permission-management`

### Requirement: Team Management Module In Assembly Profiles
The baseline-v1 assembly profile and any profiles that include project management SHOULD include the team-management module.

#### Scenario: The baseline profile includes team management
- GIVEN the baseline-v1 assembly profile is used
- WHEN a contributor inspects the profile modules
- THEN `team-management` appears in the module list
