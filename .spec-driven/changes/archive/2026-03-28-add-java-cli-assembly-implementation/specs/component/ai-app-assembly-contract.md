# AI App Assembly Contract

## MODIFIED Requirements

### Requirement: Repository Supports Multiple Compatible Assembly Implementations
The system MUST support multiple compatible app assembly implementations against the same standard contract and compatibility suite.

#### Scenario: A contributor checks whether the assembly standard is single-implementation only
- GIVEN a contributor reviews the repository's app assembly capability
- WHEN they inspect the supported implementations
- THEN they can see that more than one implementation may target the same standard contract without redefining the contract itself

### Requirement: Java CLI May Implement The Standard
The system MAY provide a Java CLI as a compatible app assembly implementation as long as it satisfies the same observable output invariants and compatibility checks as the existing reference implementation.

#### Scenario: A contributor uses the Java CLI to assemble an application
- GIVEN a contributor wants to assemble a derived application through the Java toolchain path
- WHEN they run the repository-owned Java CLI with a valid manifest
- THEN the Java CLI produces a derived application that can be checked by the same compatibility rules used for other implementations
