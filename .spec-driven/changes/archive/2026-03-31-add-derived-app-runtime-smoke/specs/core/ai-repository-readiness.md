# AI Repository Readiness Delta

## ADDED Requirements

### Requirement: Repository Exposes Derived-App Runtime Smoke Validation Guidance
The repository MUST expose the derived-app runtime smoke validation path as a first-class repository validation entrypoint in its AI-facing guidance so contributors can tell when runtime proof is required in addition to generated-app contract verification.

#### Scenario: A contributor chooses how to validate a derived application
- GIVEN a contributor has generated or regenerated a derived application
- WHEN they inspect the repository's quickstart, AI context, or related playbooks
- THEN they can identify the derived-app runtime smoke entrypoint
- AND they can distinguish it from generated-app contract verification and main-workspace full-stack smoke validation
