## MODIFIED Requirements

### Requirement: Compatible Implementations Must Satisfy Output Invariants
Previously: The system MUST define the observable output invariants that any compatible app assembly implementation must satisfy, including the requirement that selected decomposed optional business units are reflected consistently across generated routes, services, tables, and validation guidance.

The system MUST define the observable output invariants that any compatible app assembly implementation must satisfy, including the requirement that selected decomposed optional business units are reflected consistently across generated routes, services, tables, validation guidance, and the structured template-layer ownership model exposed for generated output.

#### Scenario: A contributor checks output ownership boundaries after assembly
- GIVEN a compatible implementation assembles a derived application
- WHEN the output is checked against the platform contract
- THEN the contributor can identify which generated areas are platform-managed template output, which are module-contributed fragments, and which are intended customization boundaries
