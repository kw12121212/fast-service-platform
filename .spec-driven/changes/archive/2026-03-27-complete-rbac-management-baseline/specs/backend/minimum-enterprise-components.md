# Minimum Enterprise Components Delta

## ADDED Requirements

### Requirement: Backend Provides Minimum Manageable RBAC Surface
The system MUST provide the minimum backend surface needed to manage roles, permissions, and user-role relationships as part of the first reusable RBAC baseline.

#### Scenario: A contributor reviews RBAC management capabilities
- GIVEN a contributor inspects the backend access-control component
- WHEN they check whether the RBAC baseline can be managed
- THEN they find backend-supported role management, permission management, and role-assignment behavior beyond a single hard-coded role lookup
- AND they can inspect the roles currently assigned to a user through the backend-supported RBAC baseline

## MODIFIED Requirements

### Requirement: RBAC Uses Menu And Function Granularity
Previously: The system MUST support role-based permissions using both menu-level and function-level authorization in the first backend core.

The system MUST support role-based permissions using both menu-level and function-level authorization in the first backend core, and it MUST expose enough RBAC query behavior to manage that baseline from the platform.

#### Scenario: Access-control behavior is reviewed
- GIVEN a contributor inspects the RBAC baseline
- WHEN they check the supported authorization granularity and manageable query surface
- THEN they find both menu-level and function-level permissions in the backend core
- AND they can inspect the current RBAC baseline through backend-supported management queries
