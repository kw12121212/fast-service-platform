## MODIFIED Requirements

### Requirement: Repository Generates An Independent Application Skeleton
Previously: The system MUST provide a repository-owned scaffolding and assembly path that generates an independent monolithic application skeleton from the application manifest and selected modules, and that generated output MUST expose the machine-readable lifecycle metadata needed for later upgrade evaluation, with Java as the repository-owned tooling runtime for that platform workflow.

The system MUST provide a repository-owned scaffolding and assembly path that generates an independent monolithic application skeleton from the application manifest and selected modules, and that generated output MUST expose the machine-readable lifecycle metadata needed for later upgrade evaluation, with Java as the repository-owned tooling runtime for that platform workflow.
The system MUST require any repository-owned committed derived-application example to preserve explicit assembly provenance so contributors can identify which assembly input and repository-owned entrypoint produced that example.

#### Scenario: A contributor audits a repository-owned demo derived application
- GIVEN the repository keeps a committed derived-application example for demonstration
- WHEN a contributor inspects that example and its guide
- THEN they can identify the manifest or equivalent assembly input used to produce it
- AND they can identify the repository-owned assembly entrypoint used to generate it
- AND they do not need to infer whether the example was created through a special undocumented path
