## ADDED Requirements

### Requirement: Descriptor-Driven Module Output Uses Structured Template Ownership Boundaries
The repository MUST ensure descriptor-driven management-module generation emits output through the structured app template system's platform-managed templates, module fragments, slot boundaries, and customization zones rather than through unclassified arbitrary file generation.

#### Scenario: A contributor inspects template ownership for descriptor-driven module output
- GIVEN a contributor has generated a supported management module through the repository-owned descriptor path
- WHEN they inspect the generated output against the template-system assets
- THEN they can identify which generated areas are platform-managed templates, which are module-contributed fragments, and which remain approved customization boundaries
- AND they do not need to infer ownership from undocumented file-copy behavior
