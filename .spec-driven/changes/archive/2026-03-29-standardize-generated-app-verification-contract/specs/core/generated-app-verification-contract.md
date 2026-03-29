# Generated App Verification Contract

## ADDED Requirements

### Requirement: Repository Defines A Machine-Readable Generated-App Verification Contract
The repository MUST define a machine-readable contract for verifying generated applications after assembly.

#### Scenario: A contributor inspects generated-app verification semantics
- GIVEN a contributor wants to understand how a derived application is validated
- WHEN they inspect the repository-owned verification assets
- THEN they can find a machine-readable contract that defines the verifier inputs, expected checks, and result semantics

### Requirement: Generated-App Verification Depends On Observable Output Assets
The generated-app verification contract MUST be based on observable assets present in the generated application rather than on hidden repository-only state.

#### Scenario: A verifier validates a generated application outside the source repository
- GIVEN a generated application has been assembled with the repository-owned normative assets
- WHEN a compatible verifier evaluates that generated application
- THEN the verifier can determine pass or fail from the generated output assets and documented contract inputs

### Requirement: Repository May Provide Reference Generated-App Verifiers
The repository MAY provide one or more reference generated-app verifier implementations as long as they satisfy the same generated-app verification contract.

#### Scenario: A contributor evaluates the current Node verifier
- GIVEN the repository currently ships a `Node` generated-app verifier
- WHEN a contributor inspects its role in the platform
- THEN they can identify it as a reference verifier for the generated-app verification contract rather than as the contract itself
