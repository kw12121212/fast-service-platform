## MODIFIED Requirements

### Requirement: Compatibility Suite Can Be Targeted By Multiple Implementations
Previously: The repository MUST define the compatibility suite so multiple implementations can target the same fixtures and validation expectations.

The repository MUST define the compatibility suite so multiple implementations can target the same fixtures and validation expectations, even when the repository itself only ships Java as the repository-owned platform tooling implementation runtime.

#### Scenario: A contributor validates the repository-owned implementation after tooling consolidation
- GIVEN the repository has consolidated platform tooling on Java
- WHEN a contributor runs the repository-owned compatibility validation path
- THEN the compatibility suite still validates the same observable contract behavior
- AND the repository-owned conformance target is the Java implementation path
