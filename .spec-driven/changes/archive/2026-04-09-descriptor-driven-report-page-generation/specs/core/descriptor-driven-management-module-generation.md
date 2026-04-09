# Delta: core/descriptor-driven-management-module-generation

## ADDED Requirements

### Requirement: Descriptor Report Section Supports Multi-Section Report Pages
The management-module descriptor MUST support a `sections` array in the `report` block that declares an ordered list of report sections with types matching the dynamic-report component's section types: summary-cards, table, bar-chart, line-chart, and pie-chart.

#### Scenario: A contributor prepares a descriptor with multi-section report
- GIVEN a contributor wants to generate a management module with a rich report page
- WHEN they prepare a descriptor with a `sections` array containing summary-cards, table, and chart sections
- THEN the descriptor passes schema validation
- AND each section's type maps directly to a supported dynamic-report section type

### Requirement: Sections-Based Report Descriptor Is Backward Compatible With Columns-Based Report Descriptor
The management-module descriptor MUST accept either `columns` (existing) or `sections` (new) in the report block. When `sections` is present, it takes precedence. When `sections` is absent, `columns` continues to work unchanged.

#### Scenario: An existing columns-based descriptor still validates after the schema change
- GIVEN the department-directory example descriptor uses the `columns` array
- WHEN the contributor validates it against the updated schema
- THEN it passes validation without modification

### Requirement: Each Report Section Declares A Section Key And Type
Each entry in the `sections` array MUST declare a `sectionKey` (camelCase identifier) and a `type` (one of: summary-cards, table, bar-chart, line-chart, pie-chart).

#### Scenario: A contributor inspects a section-based report descriptor
- GIVEN a descriptor contains a sections array with multiple entries
- WHEN the contributor inspects each entry
- THEN each entry has a unique sectionKey and a valid type value
- AND table sections include column definitions
- AND chart sections include a chartType (bar, line, or pie)

### Requirement: Repository Provides A Multi-Section Report Descriptor Example
The repository MUST provide at least one management-module example descriptor that uses the `sections` array to demonstrate multi-section report page generation.

#### Scenario: A contributor reviews the multi-section report example
- GIVEN a contributor wants to understand the sections-based report descriptor
- WHEN they inspect the repository-owned example descriptor
- THEN they find a descriptor with at least two different section types (e.g. summary-cards and table)
- AND the example does not use the legacy `columns` array
