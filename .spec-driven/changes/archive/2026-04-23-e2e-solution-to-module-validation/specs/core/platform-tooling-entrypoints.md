---
mapping:
  implementation:
    - scripts/verify-e2e-solution-pipeline.sh
    - scripts/platform-tool.sh
    - docs/ai/tests/e2e-fixture.solution-input.json
    - docs/ai/tests/e2e-fixture.solution-to-manifest-plan.json
    - docs/ai/tests/e2e-fixture.solution-to-manifest-recommendation.json
  tests:
    - scripts/verify-e2e-solution-pipeline.sh
---

## ADDED Requirements

### Requirement: Repository Provides An End-To-End Solution Pipeline Validation Fixture
The repository MUST provide a repository-owned end-to-end fixture that exercises the full V1 solution-input-to-derived-app path through every platform workflow stage without depending on external AI contributor improvisation.

#### Scenario: A contributor runs the end-to-end validation fixture
- GIVEN a contributor has a clean repository checkout
- WHEN they run the repository-owned e2e solution pipeline validation entrypoint
- THEN the fixture executes the full path: solution input validation, planning, recommendation, manifest preparation, descriptor-driven module generation, assembly, verification, and runtime smoke
- AND each stage reports pass or fail independently

#### Scenario: The e2e fixture completes successfully
- GIVEN the repository-owned e2e solution pipeline fixture runs without encountering a failure
- WHEN the fixture finishes
- THEN the contributor can confirm that the full V1 path produced a derived application that passed both structural verification and runtime smoke validation
- AND the fixture cleans up temporary output artifacts

### Requirement: E2E Fixture Reports Actionable Stage-Level Failure
The repository MUST report e2e pipeline failures at the stage level so contributors can identify which workflow stage failed without re-running the full pipeline.

#### Scenario: A contributor encounters an e2e pipeline failure
- GIVEN a contributor runs the repository-owned e2e solution pipeline validation
- WHEN a stage fails
- THEN the reported failure identifies which pipeline stage failed
- AND the output includes enough context to diagnose the failure without re-running the entire pipeline
