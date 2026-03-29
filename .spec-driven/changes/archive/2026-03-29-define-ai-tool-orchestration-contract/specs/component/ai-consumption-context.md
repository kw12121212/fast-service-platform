## MODIFIED Requirements

### Requirement: AI Can Target The Standard Without Depending On A Single Runtime Implementation
Previously: The system MUST let AI contributors consume the assembly standard through normative machine-readable assets and compatibility expectations rather than depending on the internal structure of a single implementation runtime.

The system MUST let AI contributors consume the platform standard through normative machine-readable assets, orchestration guidance, and compatibility expectations rather than depending on the internal structure of a single implementation runtime.

#### Scenario: An AI contributor plans a tool-driven platform workflow
- GIVEN an AI contributor wants to perform assembly, verification, advisory, lifecycle, or upgrade work against the platform
- WHEN it reads the repository-owned AI consumption assets
- THEN it can identify the normative contracts and the repository-owned tooling sequence it is expected to use
- AND it does not need to reverse-engineer a direct replacement implementation from one runtime
