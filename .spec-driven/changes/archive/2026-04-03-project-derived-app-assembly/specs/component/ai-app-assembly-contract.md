# AI App Assembly Contract

## MODIFIED Requirements

### Requirement: Repository Generates An Independent Application Skeleton
Previously: The system MUST provide a repository-owned scaffolding and assembly path that generates an independent monolithic application skeleton from the application manifest and selected modules, and that generated output MUST expose the machine-readable lifecycle metadata needed for later upgrade evaluation, with Java as the repository-owned tooling runtime for that platform workflow.
The system MUST require any repository-owned committed derived-application example to preserve explicit assembly provenance so contributors can identify which assembly input and repository-owned entrypoint produced that example.

The system MUST provide a repository-owned scaffolding and assembly path that generates an independent monolithic application skeleton from the application manifest and selected modules, and that generated output MUST expose the machine-readable lifecycle metadata needed for later upgrade evaluation, with Java as the repository-owned tooling runtime for that platform workflow.
The system MUST require any repository-owned committed derived-application example to preserve explicit assembly provenance so contributors can identify which assembly input and repository-owned entrypoint produced that example.
The system MUST allow a bound software project to invoke that same repository-owned scaffolding and assembly path through a project-scoped lifecycle workflow, while keeping `app-manifest` as the direct assembly runtime input.

#### Scenario: A contributor invokes assembly from a bound project context
- GIVEN a software project is bound to a local Git repository
- AND the contributor provides a valid `app-manifest` input and output directory through the project-scoped lifecycle path
- WHEN the project-scoped assembly workflow runs
- THEN it uses the same repository-owned scaffolding and assembly path
- AND the project-scoped workflow does not replace `app-manifest` with a different runtime contract
