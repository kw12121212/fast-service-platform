# AI App Assembly Contract

## MODIFIED Requirements

### Requirement: Scaffolded Output Includes Repository-Approved Validation Guidance
Previously:
The system MUST define how a scaffolded application is validated after assembly.

The system MUST define scaffolded-application validation through a language-neutral verification contract rather than only through a single verifier script.

#### Scenario: A contributor validates a generated application
- GIVEN a contributor has generated a new application from the platform
- WHEN they consult the generated output and repository documentation
- THEN they can identify the repository-approved validation commands or entrypoints for that derived application
- AND they can identify the machine-readable verification contract that those entrypoints are expected to satisfy

### Requirement: Machine-Readable Contract Distinguishes Normative Inputs From Reference Implementations
Previously:
The system MUST identify which machine-readable assets are normative assembly inputs and which repository scripts are reference implementations or verification tools.

The system MUST identify which machine-readable assets are normative assembly and verification inputs and which repository scripts are reference implementations or verification tools.

#### Scenario: An AI or multi-language implementer reads the assembly assets
- GIVEN a contributor wants to build a compatible implementation in another language
- WHEN they inspect the repository-owned assembly and verification assets
- THEN they can distinguish the standard inputs and required outputs from the current reference implementation details
