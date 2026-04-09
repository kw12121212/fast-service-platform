## MODIFIED Requirements

### Requirement: Derived Applications Use A Machine-Readable Application Manifest
Previously: The system MUST define a machine-readable application manifest contract for derived applications.
The manifest MUST let a contributor identify the target application and the selected platform modules for assembly, and it MUST remain the direct input consumed by repository-owned assembly tooling even when higher-level AI solution input assets, solution-to-manifest planning assets, and solution-to-manifest recommendation assets are present.

The system MUST define a machine-readable application manifest contract for derived applications.
The manifest MUST let a contributor identify the target application and the selected platform modules for assembly, and it MUST remain the direct input consumed by repository-owned assembly tooling even when higher-level AI solution input assets, solution-to-manifest planning assets, solution-to-manifest recommendation assets, and descriptor-driven management-module generation assets are present.

#### Scenario: An AI contributor prepares assembly after reviewing descriptor-driven module output
- GIVEN an AI contributor has prepared repository-owned descriptor-driven management-module output from valid planning or recommendation facts
- WHEN it prepares the actual assembly request
- THEN it produces a standalone `app-manifest` for repository-owned assembly tooling
- AND it does not submit the descriptor artifact itself as the assembly runtime input

### Requirement: Machine-Readable Contract Distinguishes Normative Inputs From Reference Implementations
Previously: The system MUST identify which machine-readable assets are normative assembly and verification inputs, which higher-level AI solution input assets, solution-to-manifest planning assets, and solution-to-manifest recommendation assets may precede assembly, and which repository-owned Java tooling paths implement those workflows, without treating those upstream planning or recommendation assets as the assembly runtime contract itself.

The system MUST identify which machine-readable assets are normative assembly and verification inputs, which higher-level AI solution input assets, solution-to-manifest planning assets, solution-to-manifest recommendation assets, and descriptor-driven management-module generation assets may precede assembly, and which repository-owned Java tooling paths implement those workflows, without treating those upstream planning, recommendation, or descriptor-generation assets as the assembly runtime contract itself.

#### Scenario: An AI or multi-language implementer reads the descriptor and assembly assets
- GIVEN a contributor wants to build or inspect a compatible descriptor-driven planning and assembly workflow
- WHEN they inspect the repository-owned descriptor-generation, planning, assembly, and verification assets
- THEN they can distinguish the upstream descriptor-generation assets from the direct assembly runtime contract
- AND they can identify that repository-owned assembly tooling still expects a valid `app-manifest`
