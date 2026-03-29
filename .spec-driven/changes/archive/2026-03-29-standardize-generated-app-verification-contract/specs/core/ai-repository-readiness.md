# AI Repository Readiness

## MODIFIED Requirements

### Requirement: Repository Exposes Compatibility Assets As First-Class AI Inputs
Previously:
The repository MUST expose the app assembly standard, schema assets, compatibility fixtures, and validation entrypoints as first-class AI-readable inputs.

The repository MUST expose the app assembly standard, schema assets, compatibility fixtures, validation entrypoints, and generated-app verification contract as first-class AI-readable inputs.

#### Scenario: An AI contributor prepares to implement against the assembly standard
- GIVEN an AI contributor needs to understand how conformance is checked
- WHEN it reads the repository's AI readiness path
- THEN it can identify the normative assembly contract assets, the generated-app verification contract, the compatibility suite, and the validation commands before reading a specific implementation

### Requirement: Repository Provides Validation Paths For Each Compatible Implementation
Previously:
The repository MUST provide repository-owned validation paths that let contributors verify each compatible app assembly implementation against the standard contract.

The repository MUST provide repository-owned validation paths that let contributors verify each compatible app assembly implementation and generated-app verifier path against the standard contract.

#### Scenario: A contributor validates the generated-app verifier path
- GIVEN a contributor wants to understand whether a generated application passes repository-owned validation
- WHEN they inspect the repository's validation guidance
- THEN they can identify the current reference verifier path and the contract it is expected to satisfy
