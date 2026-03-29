# AI App Assembly Contract

## MODIFIED Requirements

### Requirement: App Assembly Contract Is Implementation-Independent
The system MUST define its app assembly contract independently from any single implementation language or script structure.

#### Scenario: A contributor evaluates whether the contract depends on Node internals
- GIVEN a contributor reviews the app assembly definition
- WHEN they inspect what counts as the normative contract
- THEN they can identify the required machine-readable assets and observable behaviors without depending on Node-specific internal implementation details

### Requirement: Machine-Readable Contract Distinguishes Normative Inputs From Reference Implementations
The system MUST identify which machine-readable assets are normative assembly inputs and which repository scripts are reference implementations or verification tools.

#### Scenario: An AI or multi-language implementer reads the assembly assets
- GIVEN a contributor wants to build a compatible implementation in another language
- WHEN they inspect the repository-owned assembly assets
- THEN they can distinguish the standard inputs and required outputs from the current reference implementation details

### Requirement: Compatible Implementations Must Satisfy Output Invariants
The system MUST define the observable output invariants that any compatible app assembly implementation must satisfy.

#### Scenario: A contributor checks whether two implementations produce compatible outputs
- GIVEN two different implementations assemble an application from the same valid manifest
- WHEN the outputs are checked against the platform contract
- THEN both can be evaluated by the same output invariants rather than by matching one implementation's internal code paths
