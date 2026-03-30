# Runtime Bootstrap Delta

## MODIFIED Requirements

### Requirement: Backend Workspace Provides Runnable Lealone Bootstrap
Previously: The system MUST provide a runnable backend application under the `backend/` workspace using Java 25 and Lealone-Platform as the runtime foundation.

The system MUST provide a runnable backend application under the `backend/` workspace using Java 25 and `Lealone` as the runtime foundation.

#### Scenario: Backend bootstrap is started from a clean workspace
- GIVEN a contributor has the repository and the required project-local `Lealone` source dependency installed
- WHEN they start the backend application
- THEN the backend launches from the `backend/` workspace as a runnable Lealone-based application

### Requirement: Backend Uses Local Schema And Service Definitions
Previously: The system MUST define its initial backend data structures and service bindings through project-local schema and service definition inputs compatible with the Lealone-Platform integration model.

The system MUST define its initial backend data structures and service bindings through project-local schema and service definition inputs compatible with the current `Lealone` SQL-defined table and service integration model.

#### Scenario: Backend foundation is inspected
- GIVEN a contributor reviews how the backend core is defined
- WHEN they inspect the initial runtime inputs
- THEN they can identify project-local schema definitions and service definitions used to bootstrap the backend
