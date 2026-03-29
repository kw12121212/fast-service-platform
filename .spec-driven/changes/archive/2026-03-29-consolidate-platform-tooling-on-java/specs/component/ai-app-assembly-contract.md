## MODIFIED Requirements

### Requirement: Repository Generates An Independent Application Skeleton
Previously: The system MUST provide a repository-owned scaffolding and assembly path that generates an independent monolithic application skeleton from the application manifest and selected modules, and that generated output MUST expose the machine-readable lifecycle metadata needed for later upgrade evaluation.

The system MUST provide a repository-owned scaffolding and assembly path that generates an independent monolithic application skeleton from the application manifest and selected modules, and that generated output MUST expose the machine-readable lifecycle metadata needed for later upgrade evaluation, with Java as the repository-owned tooling runtime for that platform workflow.

#### Scenario: A contributor scaffolds a new application through the repository-owned path
- GIVEN a contributor has provided a valid application-assembly input
- WHEN they run the repository-owned scaffolding and assembly path
- THEN the platform workflow is executed through the repository's Java-owned tooling runtime
- AND the generated output still reflects the selected modules instead of always copying the full default application

### Requirement: Machine-Readable Contract Distinguishes Normative Inputs From Reference Implementations
Previously: The system MUST identify which machine-readable assets are normative assembly and verification inputs and which repository scripts are reference implementations or verification tools.

The system MUST identify which machine-readable assets are normative assembly and verification inputs and which repository-owned Java tooling paths implement those workflows, without treating Node implementation scripts as the repository-owned platform tooling runtime.
