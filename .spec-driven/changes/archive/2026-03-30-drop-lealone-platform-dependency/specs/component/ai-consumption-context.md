# AI Consumption Context

## MODIFIED Requirements

### Requirement: Component Platform Preserves Dependency Boundary
Previously: The system MUST define its reusable component baseline so that AI consuming the platform does not require additional external software libraries beyond Lealone-Platform and dependencies already present in this project.
The repository's AI-readiness documentation, manifests, playbooks, and verification entrypoints MUST preserve that same dependency boundary.

The system MUST define its reusable component baseline so that AI consuming the platform does not require additional external software libraries beyond `Lealone` and dependencies already present in this project.
The repository's AI-readiness documentation, manifests, playbooks, and verification entrypoints MUST preserve that same dependency boundary.

#### Scenario: A contributor reviews the dependency boundary
- GIVEN a contributor checks what AI is allowed to rely on when reusing the platform
- WHEN they inspect the component-platform contract
- THEN they see that the expected capability base comes from `Lealone` and project-internal dependencies instead of new external software libraries
- AND they can confirm that the AI-readiness path is built on repository-native assets rather than additional external tooling

### Requirement: External AI Can Derive An Independent Application From Repository-Owned Assets
Previously: The system MUST treat independent application derivation as an external AI workflow that consumes repository-owned assembly assets rather than in-repository AI runtime features.
The derivation workflow MUST stay within the platform dependency boundary of Lealone-Platform and repository-internal dependencies.

The system MUST treat independent application derivation as an external AI workflow that consumes repository-owned assembly assets rather than in-repository AI runtime features.
The derivation workflow MUST stay within the platform dependency boundary of `Lealone` and repository-internal dependencies.

#### Scenario: An AI agent determines whether it can create a new app from the platform
- GIVEN an AI agent needs to create a new enterprise-management application from this repository
- WHEN it checks the repository's AI consumption contract
- THEN it can identify a repository-owned derivation path for generating an independent application skeleton
- AND it can confirm that no in-repository AI chat or prompt-intake runtime feature is required
- AND it can confirm that the derivation path does not require additional external software libraries beyond the `Lealone`-based platform boundary
