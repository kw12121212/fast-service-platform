## MODIFIED Requirements

### Requirement: External AI Input Context Is Out Of Repository Runtime Scope
Previously: The system MUST treat natural-language requirements, prototype images, and UI reference websites as external AI workflow context rather than repository-native interaction surfaces.

The system MUST treat natural-language requirements, prototype images, and UI reference websites as external AI workflow context rather than repository-native interaction surfaces, and it MUST allow the repository to standardize a structured AI solution input model that captures those external inputs in machine-readable form without turning them into in-repository runtime interaction surfaces.

#### Scenario: A contributor checks whether structured solution input changes repository runtime scope
- GIVEN a contributor inspects the repository's AI consumption boundary
- WHEN they see a machine-readable solution input model
- THEN they can confirm that it standardizes external AI workflow inputs
- AND they can confirm that the repository still does not expose in-repository chat or prompt-intake runtime features

### Requirement: AI Receives Layered Machine-Readable Indexes
Previously: The system MUST provide machine-readable indexes that distinguish repository entry guidance, platform module facts, and application assembly facts.

The system MUST provide machine-readable indexes that distinguish repository entry guidance, higher-level AI solution input facts, platform module facts, and application assembly facts.

#### Scenario: An AI agent loads layered facts before assembly
- GIVEN an AI agent can consume structured assets
- WHEN it inspects the repository-owned AI indexes
- THEN it can distinguish solution-definition inputs from module facts and assembly facts
- AND it can identify which layer is meant for business-intent capture versus direct assembly execution
