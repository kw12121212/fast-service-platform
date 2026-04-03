# Built-In Components

## MODIFIED Requirements

### Requirement: Built-In Components Are Classified By Assembly Role
The system MUST classify its built-in capabilities by assembly role so contributors can distinguish required platform core, optional built-in business modules, and engineering-support components.

#### Scenario: A contributor reviews the platform component catalog
- GIVEN a contributor reviews the platform component contract
- WHEN they inspect the component definition
- THEN they can determine which capabilities are mandatory core, which are optional business modules, and which exist as engineering-support components

#### Scenario: A contributor reviews reusable interaction components
- GIVEN a contributor reviews the platform-owned reusable UI capabilities
- WHEN they inspect the component definition
- THEN they can identify dynamic form, dynamic report, and workflow capabilities as reusable platform components
- AND they can distinguish those reusable interaction components from optional business modules and engineering-support components
