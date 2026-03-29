# App Assembly Compatibility Suite

## MODIFIED Requirements

### Requirement: Repository Defines Compatibility Fixtures For App Assembly
Previously:
The repository MUST define compatibility fixtures for app assembly that cover at least valid manifests, invalid manifests, and representative module selections.

The repository MUST define compatibility fixtures for app assembly that cover at least valid manifests, invalid manifests, representative module selections, and adjacent contract-input boundaries that affect upgrade-related generated output.

#### Scenario: A contributor inspects expanded fixture coverage
- GIVEN a contributor wants to know whether the compatibility suite exercises more than the minimal baseline fixtures
- WHEN they inspect the repository-owned fixture set
- THEN they can find valid and invalid inputs that cover multiple representative module combinations and contract-input edge cases

### Requirement: Compatibility Suite Verifies Observable Output Behavior
Previously:
The repository MUST define a compatibility suite that checks observable assembly and generated-app verification behavior rather than the internal structure of a particular implementation.

The repository MUST define a compatibility suite that checks observable assembly and generated-app verification behavior rather than the internal structure of a particular implementation, including generated output invariants that remain relevant when lifecycle, advisory, and release-lineage assets are present.

#### Scenario: A contributor validates expanded generated output invariants
- GIVEN a contributor runs the repository-owned compatibility validation path against the expanded fixture set
- WHEN the suite validates generated output
- THEN it checks the observable presence and coherence of the contract-governed generated assets without depending on one implementation's internal file-generation flow
