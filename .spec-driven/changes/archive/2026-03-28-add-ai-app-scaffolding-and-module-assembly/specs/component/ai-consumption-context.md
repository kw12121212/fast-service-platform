# AI Consumption Context

## MODIFIED Requirements

### Requirement: External AI Can Derive An Independent Application From Repository-Owned Assets
The system MUST treat independent application derivation as an external AI workflow that consumes repository-owned assembly assets rather than in-repository AI runtime features.
The derivation workflow MUST stay within the platform dependency boundary of Lealone-Platform and repository-internal dependencies.

#### Scenario: An AI agent determines whether it can create a new app from the platform
- GIVEN an AI agent needs to create a new enterprise-management application from this repository
- WHEN it checks the repository's AI consumption contract
- THEN it can identify a repository-owned derivation path for generating an independent application skeleton
- AND it can confirm that no in-repository AI chat or prompt-intake runtime feature is required
- AND it can confirm that the derivation path does not require additional external software libraries beyond the platform boundary

### Requirement: AI Receives Layered Machine-Readable Indexes
The system MUST provide machine-readable indexes that distinguish repository entry guidance, platform module facts, and application assembly facts.

#### Scenario: An AI agent loads repository facts before deriving an application
- GIVEN an AI agent can consume structured assets
- WHEN it inspects the repository-owned AI indexes
- THEN it can distinguish:
- repository contribution and environment-entry facts
- platform core and optional module facts
- application assembly inputs, outputs, and validation expectations
