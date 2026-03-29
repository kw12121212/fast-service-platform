# Generated App Verification Contract

## MODIFIED Requirements

### Requirement: Repository May Provide Reference Generated-App Verifiers
Previously:
The repository MAY provide one or more reference generated-app verifier implementations as long as they satisfy the same generated-app verification contract.

The repository MAY provide one or more reference or compatible generated-app verifier implementations as long as they satisfy the same generated-app verification contract.

#### Scenario: A contributor evaluates the Java verifier
- GIVEN the repository ships a Java generated-app verifier
- WHEN a contributor inspects its role in the platform
- THEN they can identify it as a compatible verifier implementation that targets the same generated-app verification contract used by the Node reference verifier
