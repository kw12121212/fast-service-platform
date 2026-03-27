# Input And Dependency Contract Delta

## ADDED Requirements

### Requirement: Natural Language As Primary Human Input
The system MUST accept natural language as the primary human input for AI-driven project creation.

#### Scenario: A human describes a target application
- GIVEN a human wants AI to create an internal management application
- WHEN they provide a natural-language request
- THEN the platform treats that request as the primary project-definition input

### Requirement: Prototype Images As Supplemental Input
The system MAY accept prototype images as supplemental input for the generation process.

#### Scenario: Prototype images are supplied
- GIVEN a human provides prototype images together with a textual description
- WHEN the platform evaluates the request
- THEN it can use the images as supplemental guidance without replacing the textual request as the primary input

### Requirement: Reference Websites Are Limited To UI Guidance
The system MUST treat reference websites as UI guidance only.

#### Scenario: A reference website is provided
- GIVEN a human includes a reference website in the request
- WHEN the platform interprets that reference
- THEN it uses the reference for UI direction only and does not treat it as an external functional dependency

### Requirement: No Additional External Software Libraries Required
The system MUST define the V1 capability baseline so that generated projects do not require additional external software libraries beyond Lealone-Platform and dependencies already present in this project.

#### Scenario: A contributor reviews the dependency boundary
- GIVEN a contributor checks what generated projects are allowed to rely on
- WHEN they inspect the V1 generation contract
- THEN they see that the expected capability base comes from Lealone-Platform and project-internal dependencies instead of new external software libraries
