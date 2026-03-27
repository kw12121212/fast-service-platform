# Runtime Bootstrap

### Requirement: Backend Workspace Provides Runnable Lealone Bootstrap
The system MUST provide a runnable backend application under the `backend/` workspace using Java 25 and Lealone-Platform as the runtime foundation.

#### Scenario: Backend bootstrap is started from a clean workspace
- GIVEN a contributor has the repository and required project-local dependencies
- WHEN they start the backend application
- THEN the backend launches from the `backend/` workspace as a runnable Lealone-based application

### Requirement: Backend Uses Local Schema And Service Definitions
The system MUST define its initial backend data structures and service bindings through project-local schema and service definition inputs compatible with the Lealone-Platform integration model.

#### Scenario: Backend foundation is inspected
- GIVEN a contributor reviews how the backend core is defined
- WHEN they inspect the initial runtime inputs
- THEN they can identify project-local schema definitions and service definitions used to bootstrap the backend

### Requirement: Backend Provides Optional Demo Initialization
The system MUST support optional demo data so the backend can start either in a clean state or in a small usable demo state.

#### Scenario: Backend starts for the first time
- GIVEN the backend is launched on a fresh state
- WHEN demo data loading is enabled and performed
- THEN the backend is populated with the baseline data needed to exercise the minimum V1 component set

#### Scenario: Backend starts without demo data
- GIVEN the backend is launched on a fresh state
- WHEN demo data loading is not enabled
- THEN the backend still starts successfully without requiring demo data to exist
