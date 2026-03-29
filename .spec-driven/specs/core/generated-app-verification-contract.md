# Generated App Verification Contract

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
The repository MAY provide one or more reference or compatible generated-app verifier implementations as long as they satisfy the same generated-app verification contract, and the repository-owned verifier path MUST be Java-owned after tooling consolidation.

#### Scenario: A contributor evaluates the repository-owned verifier path
- GIVEN a contributor inspects the repository-owned generated-app verifier
- WHEN they review the current implementation guidance
- THEN they can identify a Java-owned verifier path as the repository-owned implementation runtime for generated-app verification
- AND they do not need to depend on a Node-owned verifier implementation path
