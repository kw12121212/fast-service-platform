## ADDED Requirements

### Requirement: Baseline Demo Reuses The Derived-App Runtime Smoke Path
The repository MUST validate the committed baseline demo through the same repository-owned derived-app runtime smoke path used for other derived applications, so the demo is proven through a real generated frontend-backend integration path before presentation.

#### Scenario: A contributor validates the baseline demo before presenting it
- GIVEN a contributor wants confidence that the committed baseline demo still works as a derived application
- WHEN they run the documented baseline demo validation path
- THEN that validation includes the repository-owned derived-app runtime smoke entrypoint
- AND the smoke proof exercises the demo frontend's `/service/*` proxy boundary instead of stopping at contract or build-only checks
