# Delta: platform/built-in-components

## ADDED Requirements

### Requirement: Workflow Panel Is Available For Descriptor-Driven Generation
The system MUST make the reusable workflow panel component available for descriptor-driven management-module generation alongside dynamic form and dynamic report, so that generated management modules can include single-record workflow interactions without hand-building workflow panel integrations.

#### Scenario: A contributor reviews descriptor-driven interaction capabilities
- GIVEN a contributor reviews the platform-owned reusable UI capabilities
- WHEN they inspect the descriptor-driven generation surface
- THEN they can identify dynamic form, dynamic report, and workflow panel as reusable platform components available for descriptor-driven generation
- AND each component has a corresponding descriptor section (form, report, workflow) that maps to its component-specific descriptor type
