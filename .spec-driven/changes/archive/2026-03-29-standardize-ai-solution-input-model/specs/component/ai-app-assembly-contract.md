## MODIFIED Requirements

### Requirement: Derived Applications Use A Machine-Readable Application Manifest
Previously: The system MUST define a machine-readable application manifest contract for derived applications.
The manifest MUST let a contributor identify the target application and the selected platform modules for assembly.

The system MUST define a machine-readable application manifest contract for derived applications.
The manifest MUST let a contributor identify the target application and the selected platform modules for assembly, and it MUST remain the direct input consumed by repository-owned assembly tooling even when a higher-level AI solution input model is present.

#### Scenario: An AI contributor maps structured solution input into assembly input
- GIVEN an AI contributor starts from a repository-defined structured solution input
- WHEN it prepares the actual assembly request
- THEN it produces an `app-manifest` that remains the direct input to repository-owned assembly tooling
- AND it does not bypass the manifest layer by treating the higher-level solution input as the assembly runtime contract

### Requirement: Machine-Readable Contract Distinguishes Normative Inputs From Reference Implementations
Previously: The system MUST identify which machine-readable assets are normative assembly and verification inputs and which repository-owned Java tooling paths implement those workflows, without treating Node implementation scripts as the repository-owned platform tooling runtime.

The system MUST identify which machine-readable assets are normative assembly and verification inputs, which higher-level AI solution input assets may precede assembly, and which repository-owned Java tooling paths implement those workflows, without treating the higher-level solution input as the assembly runtime contract itself.
