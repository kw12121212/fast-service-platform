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

## ADDED Requirements

### Requirement: Repository Provides A Repository-Owned E2E Fixture Solution Input
The repository MUST provide at least one repository-owned solution input fixture under `docs/ai/tests/` that is valid against the solution-input schema and references at least one descriptor-driven management module shape.

#### Scenario: A contributor inspects the e2e fixture solution input
- GIVEN a contributor wants to understand what the e2e fixture exercises
- WHEN they inspect the repository-owned e2e fixture solution input
- THEN they can identify a valid structured solution input that references at least one descriptor-driven management module
- AND the fixture does not depend on external AI contributor output
