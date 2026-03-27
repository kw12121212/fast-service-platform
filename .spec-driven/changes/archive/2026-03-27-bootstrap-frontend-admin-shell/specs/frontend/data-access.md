# Frontend Data Access Delta

## ADDED Requirements

### Requirement: Frontend Uses A Consistent Data-Access Convention
The system MUST provide a consistent frontend data-access convention for the first admin frontend instead of scattering ad hoc backend requests across pages.

#### Scenario: A contributor reviews frontend backend integration
- GIVEN the first admin frontend has been implemented
- WHEN the contributor inspects how pages reach backend data
- THEN they can identify a consistent data-access pattern used across the minimum V1 pages
