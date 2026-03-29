# AI Repository Readiness

## MODIFIED Requirements

### Requirement: Repository Exposes Compatibility Assets As First-Class AI Inputs
The repository MUST expose the app assembly standard, schema assets, compatibility fixtures, and validation entrypoints as first-class AI-readable inputs.

#### Scenario: An AI contributor prepares to implement against the assembly standard
- GIVEN an AI contributor needs to understand how conformance is checked
- WHEN it reads the repository's AI readiness path
- THEN it can identify the normative contract assets, the compatibility suite, and the validation commands before reading a specific implementation

### Requirement: Validation Entrypoints Cover Contract Compatibility
The repository MUST provide validation entrypoints that check contract compatibility for app assembly implementations in addition to validating the current reference implementation.

#### Scenario: A contributor validates an implementation against the standard
- GIVEN a contributor has an app assembly implementation to validate
- WHEN they run the repository-owned compatibility validation path
- THEN they can determine whether the implementation satisfies the standard's observable contract rather than only whether one repository script still works
