# Dynamic Report Component

## ADDED Requirements

### Requirement: Platform Provides A Reusable Dynamic Report Component
The system MUST provide a reusable frontend report component that accepts a declarative report descriptor and renders a complete single-page report without requiring the caller to hand-write each summary card, table block, or chart block.

#### Scenario: A contributor assembles a report from a descriptor
- GIVEN a contributor supplies a report descriptor with an ordered section list
- WHEN they render the dynamic report component
- THEN the component displays each described section in descriptor order
- AND the contributor does not need to hand-build each individual report widget

### Requirement: Dynamic Report Descriptor Covers V1 Section Types
The system MUST support the following V1 report section types in the first dynamic report descriptor: summary cards, table, bar chart, line chart, and pie chart.

#### Scenario: A contributor describes a report with varied section types
- GIVEN a report descriptor contains summary cards, a table, a bar chart, a line chart, and a pie chart
- WHEN the dynamic report component renders that descriptor
- THEN each section is rendered with the visualization that matches its declared type

### Requirement: Dynamic Report Consumes Caller-Provided Aggregated Results
The system MUST render from caller-provided aggregated report results rather than fetching backend data itself or requiring the component to derive aggregations from raw entity rows.

#### Scenario: A contributor renders a report from aggregated inputs
- GIVEN a contributor provides a report descriptor and aggregated results that match the descriptor sections
- WHEN the dynamic report component renders the report
- THEN the rendered summary values, table rows, and chart series reflect the caller-provided aggregated results
- AND the component does not fetch backend data by itself

### Requirement: Dynamic Report Is Compatible With Existing Data-Access Ownership
The system MUST keep report data ownership with the calling page or frontend data-access layer instead of moving backend request logic into the dynamic report component.

#### Scenario: A contributor reviews a dynamic report integration
- GIVEN a frontend page uses the dynamic report component
- WHEN the contributor inspects how report data reaches the component
- THEN they can identify the page or existing frontend data-access path as the owner of backend interaction
- AND the dynamic report component remains a rendering-only platform capability

### Requirement: The Current Runnable Frontend Includes A Real Dynamic Report Example
The system MUST provide at least one real dynamic report example in the current runnable admin frontend so contributors can verify how the reusable report component is applied to backend-backed data.

#### Scenario: A contributor opens the baseline dashboard
- GIVEN the current frontend is running against the current backend core
- WHEN the contributor opens the dashboard
- THEN they can identify at least one report section rendered through the reusable dynamic report component
- AND that report section reflects current backend-backed data rather than static mock content
