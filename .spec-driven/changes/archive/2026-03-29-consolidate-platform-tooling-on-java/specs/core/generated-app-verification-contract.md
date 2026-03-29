## MODIFIED Requirements

### Requirement: Repository May Provide Reference Generated-App Verifiers
Previously: The repository MAY provide one or more reference or compatible generated-app verifier implementations as long as they satisfy the same generated-app verification contract.

The repository MAY provide one or more reference or compatible generated-app verifier implementations as long as they satisfy the same generated-app verification contract, and the repository-owned verifier path MUST be Java-owned after tooling consolidation.

#### Scenario: A contributor evaluates the repository-owned verifier path
- GIVEN a contributor inspects the repository-owned generated-app verifier
- WHEN they review the current implementation guidance
- THEN they can identify a Java-owned verifier path as the repository-owned implementation runtime for generated-app verification
- AND they do not need to depend on a Node-owned verifier implementation path
