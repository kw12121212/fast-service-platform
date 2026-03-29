# AI App Assembly Contract

## MODIFIED Requirements

### Requirement: Scaffolded Output Includes Repository-Approved Validation Guidance
Previously:
The system MUST define scaffolded-application validation through a language-neutral verification contract rather than only through a single verifier script.

The system MUST define scaffolded-application validation through a language-neutral verification contract that may be satisfied by multiple compatible verifier implementations rather than only through a single verifier script.

#### Scenario: A contributor validates a generated application with a non-Node verifier
- GIVEN a contributor has generated a new application from the platform
- WHEN they choose a compatible verifier implementation that targets the same verification contract
- THEN they can validate the generated application without depending on the internal structure of the Node verifier
