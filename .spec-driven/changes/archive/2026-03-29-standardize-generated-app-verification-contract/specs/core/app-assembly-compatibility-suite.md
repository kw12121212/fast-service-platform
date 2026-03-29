# App Assembly Compatibility Suite

## MODIFIED Requirements

### Requirement: Compatibility Suite Verifies Observable Output Behavior
Previously:
The repository MUST define a compatibility suite that checks observable assembly behavior rather than the internal structure of a particular implementation.

The repository MUST define a compatibility suite that checks observable assembly and generated-app verification behavior rather than the internal structure of a particular implementation.

#### Scenario: A contributor validates an implementation with the compatibility suite
- GIVEN a contributor runs the repository-owned compatibility validation path
- WHEN the suite checks the generated output
- THEN it validates observable contract behavior such as selected-module wiring, required files, and output invariants
- AND it treats generated-app verification behavior as a contract-governed observable surface rather than as one script's private implementation detail
