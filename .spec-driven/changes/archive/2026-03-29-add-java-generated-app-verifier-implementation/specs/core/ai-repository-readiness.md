# AI Repository Readiness

## MODIFIED Requirements

### Requirement: Repository Documents Compatible Assembly Implementations
Previously:
The repository MUST identify the available compatible assembly implementations and how contributors can invoke them through repository-owned tooling.

The repository MUST identify the available compatible assembly and generated-app verifier implementations and how contributors can invoke them through repository-owned tooling.

#### Scenario: A contributor chooses a generated-app verifier implementation
- GIVEN a contributor wants to validate a derived application
- WHEN they read the repository's AI-ready assembly and verification guidance
- THEN they can identify the available generated-app verifier implementations and the repository-owned way to invoke each one

### Requirement: Repository Provides Validation Paths For Each Compatible Implementation
Previously:
The repository MUST provide repository-owned validation paths that let contributors verify each compatible app assembly implementation and generated-app verifier path against the standard contract.

The repository MUST provide repository-owned validation paths that let contributors verify each compatible app assembly implementation and generated-app verifier implementation against the standard contract.

#### Scenario: A contributor validates the Java generated-app verifier
- GIVEN a contributor wants to validate a derived application through the Java verifier path
- WHEN they run the documented repository-owned validation flow
- THEN they can verify the generated application through the Java verifier against the same generated-app verification contract
