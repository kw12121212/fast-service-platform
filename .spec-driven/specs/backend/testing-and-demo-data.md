---
mapping:
  implementation:
    - docs/ai/tests/e2e-fixture.solution-input.json
    - docs/ai/tests/e2e-fixture.manifest.json
    - docs/ai/tests/e2e-fixture.solution-to-manifest-plan.json
    - docs/ai/tests/e2e-fixture.solution-to-manifest-recommendation.json
  tests:
    - scripts/verify-e2e-solution-pipeline.sh
---

# Testing And Demo Data

### Requirement: Backend Bootstrap Includes Automated Test Coverage
The system MUST include unit tests, service tests, and integration tests for backend bootstrap behavior and core enterprise component behavior in the first backend implementation.

#### Scenario: Backend core is validated
- GIVEN a contributor runs the backend test suite
- WHEN validation completes
- THEN the test results cover backend startup and the minimum enterprise-management component behavior through unit, service, and integration tests

### Requirement: Default Backend Validation Excludes Heavyweight Sandbox Runtime Execution
The system MUST keep the default backend validation path focused on unit, service, and lightweight integration coverage, while real sandbox runtime execution remains available through a separate repository-owned validation path.

#### Scenario: A contributor runs the default backend validation entrypoint
- GIVEN the repository provides a documented default backend validation command
- WHEN a contributor runs that default backend validation path
- THEN the backend validation covers required backend behavior without requiring heavyweight real sandbox image or container execution

#### Scenario: A contributor runs heavyweight sandbox runtime validation
- GIVEN the repository provides sandbox environments as platform behavior
- WHEN a contributor runs the dedicated heavyweight sandbox runtime validation path
- THEN the repository still exercises real sandbox runtime behavior through a repository-owned validation entrypoint

### Requirement: Backend Demo Data Supports Baseline Functional Validation
The system MUST provide optional demo data that allows the minimum enterprise-management baseline to be exercised after startup when enabled.

#### Scenario: A contributor validates baseline functionality
- GIVEN the backend has loaded optional demo data
- WHEN the contributor verifies baseline enterprise-management behavior
- THEN the initialized data is sufficient to exercise the required V1 backend components

### Requirement: Repository Provides A Repository-Owned E2E Fixture Solution Input
The repository MUST provide at least one repository-owned solution input fixture under `docs/ai/tests/` that is valid against the solution-input schema and references at least one descriptor-driven management module shape.

#### Scenario: A contributor inspects the e2e fixture solution input
- GIVEN a contributor wants to understand what the e2e fixture exercises
- WHEN they inspect the repository-owned e2e fixture solution input
- THEN they can identify a valid structured solution input that references at least one descriptor-driven management module
- AND the fixture does not depend on external AI contributor output
