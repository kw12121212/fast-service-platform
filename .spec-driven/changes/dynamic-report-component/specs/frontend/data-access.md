# Frontend Data Access

## MODIFIED Requirements

### Requirement: Dynamic Reports Follow The Existing Data-Access Convention
The system MUST keep dynamic report integrations inside the existing frontend data-access convention, so report data is obtained through the same page-owned or shared API access patterns rather than ad hoc backend requests from the report component itself.

#### Scenario: A contributor reviews report data ownership
- GIVEN a frontend page renders a dynamic report
- WHEN the contributor inspects how the report receives backend-backed data
- THEN they can identify an existing frontend data-access path as the source of that data
- AND they do not find dynamic report-specific backend request logic embedded inside the reusable report component
