# AI App Assembly Contract

## MODIFIED Requirements

### Requirement: Repository Generates An Independent Application Skeleton
Previously:
The system MUST provide a repository-owned scaffolding and assembly path that generates an independent monolithic application skeleton from the application manifest and selected modules.

The system MUST provide a repository-owned scaffolding and assembly path that generates an independent monolithic application skeleton from the application manifest and selected modules, and that generated output MUST expose the machine-readable lifecycle metadata needed for later upgrade evaluation.

#### Scenario: A contributor inspects the generated application's lifecycle metadata
- GIVEN a contributor has generated an independent application from the platform
- WHEN they inspect the generated output assets
- THEN they can identify the machine-readable metadata that declares which platform release or lifecycle contract the derived application was produced from

### Requirement: Scaffolded Output Includes Repository-Approved Validation Guidance
Previously:
The system MUST define scaffolded-application validation through a language-neutral verification contract that may be satisfied by multiple compatible verifier implementations rather than only through a single verifier script.

The system MUST define scaffolded-application validation and lifecycle guidance through language-neutral contracts that may be satisfied by multiple compatible implementations rather than only through a single verifier script.

#### Scenario: A contributor prepares to upgrade a generated application
- GIVEN a contributor has generated an application from the platform
- WHEN they consult the generated output and repository documentation
- THEN they can identify the repository-approved lifecycle or upgrade entrypoints for evaluating that derived application's compatibility with later platform releases
