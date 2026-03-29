# AI Repository Readiness

## MODIFIED Requirements

### Requirement: Repository Documents Compatible Assembly Implementations
The repository MUST identify the available compatible assembly implementations and how contributors can invoke them through repository-owned tooling.

#### Scenario: A contributor chooses an assembly implementation
- GIVEN a contributor wants to derive an application from the platform
- WHEN they read the repository's AI-ready assembly guidance
- THEN they can identify the available compatible implementations and the repository-owned way to invoke each one

### Requirement: Repository Provides Validation Paths For Each Compatible Implementation
The repository MUST provide repository-owned validation paths that let contributors verify each compatible app assembly implementation against the standard contract.

#### Scenario: A contributor validates the Java implementation
- GIVEN a contributor has used the Java CLI assembly path
- WHEN they run the documented validation flow
- THEN they can verify the Java implementation against the standard compatibility expectations
