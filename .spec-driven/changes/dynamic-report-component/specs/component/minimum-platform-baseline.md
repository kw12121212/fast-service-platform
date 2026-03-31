# Minimum Platform Baseline

## MODIFIED Requirements

### Requirement: Platform Baseline Includes A Reusable Dynamic Report Component
The system MUST include the dynamic report component as a reusable platform capability available to any derived application, so that AI-generated enterprise management workflows can produce consistent summary cards, tables, and basic charts from declarative descriptors and caller-provided aggregated results.

#### Scenario: A contributor checks the platform baseline for reusable report capabilities
- GIVEN a contributor reviews the repository's reusable component baseline
- WHEN they check whether report generation is a platform-owned capability
- THEN they can identify the dynamic report component as a reusable baseline capability
- AND they can confirm it is accessible from the platform component index rather than embedded in a single page
