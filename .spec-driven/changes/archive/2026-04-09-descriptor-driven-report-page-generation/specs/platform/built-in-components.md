# Delta: platform/built-in-components

## ADDED Requirements

### Requirement: Component Registry Documents Report Descriptor Section Types
The platform built-in component registry MUST document that the dynamic-report component accepts section types summary-cards, table, bar-chart, line-chart, and pie-chart in the descriptor-driven report section, in addition to the flat columns table view.

#### Scenario: A contributor checks what report section types are available for descriptor-driven generation
- GIVEN a contributor reviews the built-in component registry
- WHEN they look at the dynamic-report entry
- THEN they can identify the supported section types for descriptor-driven report page generation
- AND the registry lists summary-cards, table, bar-chart, line-chart, and pie-chart
