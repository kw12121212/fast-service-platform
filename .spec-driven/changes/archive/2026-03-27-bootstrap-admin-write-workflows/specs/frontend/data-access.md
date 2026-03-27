# Frontend Data Access Delta

## MODIFIED Requirements

### Requirement: Frontend Uses A Consistent Data-Access Convention
Previously: The system MUST provide a consistent frontend data-access convention for the first admin frontend instead of scattering ad hoc backend requests across pages.

The system MUST provide a consistent frontend data-access convention for the first admin frontend, covering both backend reads and supported admin write workflows instead of scattering ad hoc backend requests across pages.

#### Scenario: A contributor reviews frontend backend integration
- GIVEN the first admin frontend has been implemented
- WHEN the contributor inspects how pages read from and write to backend data
- THEN they can identify a consistent data-access pattern used across the supported minimum V1 pages
