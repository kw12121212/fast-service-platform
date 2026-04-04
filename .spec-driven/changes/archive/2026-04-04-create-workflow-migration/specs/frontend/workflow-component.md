# Delta: Workflow Component — Backend Convention

## ADDED Requirements

### Requirement: Backend Workflow Services MUST Be Defined Via CREATE WORKFLOW DDL
The system MUST declare all backend workflow services using the Lealone `CREATE WORKFLOW` DDL syntax rather than hand-written service implementations.
The system MUST expose workflow services under a dedicated workflow service name (e.g. `ticket_workflow_service`) rather than embedding workflow methods inside a general-purpose entity service.

#### Scenario: A contributor adds a new workflow-enabled entity
- GIVEN a contributor needs to add a workflow to a new entity
- WHEN they define the backend service
- THEN they MUST use `CREATE WORKFLOW <entity>_workflow_service (...)` DDL rather than writing a hand-coded service class
- AND the workflow service MUST appear in `services.sql` as a separate entry from the entity's CRUD service

### Requirement: Generated Workflow Implementation MUST Conform to WorkflowInstance Contract
The system MUST ensure that the JSON returned by a generated workflow service's `getWorkflow` method conforms to the frontend `WorkflowInstance` contract.
If the generated implementation does not produce the required JSON shape, the system MUST introduce a server-side adapter — it MUST NOT change the frontend contract to match the generated output.

#### Scenario: A contributor verifies a generated workflow implementation
- GIVEN a `CREATE WORKFLOW` DDL entry has generated a new implementation class
- WHEN the contributor runs the workflow integration tests
- THEN the tests confirm the JSON response matches the expected `WorkflowInstance` shape
- AND any discrepancy is resolved via a server-side adapter rather than a frontend type change

### Requirement: Workflow Integration Tests Are The Generation Verification Gate
The system MUST include integration tests for each `CREATE WORKFLOW` service that verify: the JSON response shape, valid state transition acceptance, invalid state transition rejection, required comment enforcement, and reassign payload validation.
These tests serve as the re-verification mechanism when the generated class file is deleted and regenerated.

#### Scenario: A contributor regenerates a workflow service implementation
- GIVEN schema or service context has changed and the generated workflow class file is deleted
- WHEN the system restarts and the LLM regenerates the implementation
- THEN the contributor re-runs the workflow integration tests to confirm the new generation is correct
- AND the tests are the authoritative check — manual inspection of generated code is not required
