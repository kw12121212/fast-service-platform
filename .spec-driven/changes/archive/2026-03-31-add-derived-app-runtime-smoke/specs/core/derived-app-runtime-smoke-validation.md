## ADDED Requirements

### Requirement: Repository Provides Runtime Smoke Validation For Derived Applications
The repository MUST provide a repository-owned runtime smoke validation path for a derived application directory so contributors can verify that a generated application is not only structurally valid but also minimally runnable.

#### Scenario: A contributor validates a derived application's runtime path
- GIVEN a contributor has a derived application generated from the repository-owned assembly path
- WHEN they run the repository-owned derived-app runtime smoke entrypoint against that derived application directory
- THEN the repository validates that the derived application's backend and frontend can be started for smoke purposes
- AND the validation does not stop at generated-file presence or build-only checks

### Requirement: Derived-App Runtime Smoke Exercises The Frontend Proxy Boundary
The repository MUST define derived-app runtime smoke so success is based on requests that pass through the derived application's active frontend `/service/*` proxy boundary instead of calling backend endpoints directly as the only proof.

#### Scenario: A contributor checks whether the generated frontend and backend are integrated
- GIVEN a contributor wants to know whether a derived application's frontend is correctly wired to its backend
- WHEN they run the repository-owned derived-app runtime smoke entrypoint
- THEN the validation reaches backend service responses through the derived frontend's `/service/*` proxy path
- AND the contributor does not have to infer integration success from backend-only direct calls

### Requirement: Derived-App Runtime Smoke Uses Narrow Observable Success Checks
The repository MUST keep the first derived-app runtime smoke path narrow by requiring only a minimal set of observable backend-backed responses that prove the generated application is running and returning valid JSON through the frontend proxy.

#### Scenario: A contributor uses the first runtime smoke path
- GIVEN a contributor runs the first repository-owned derived-app runtime smoke validation
- WHEN the smoke path completes successfully
- THEN they can identify that at least the required minimal proxied read endpoints returned valid JSON responses
- AND they do not interpret that success as full UI regression coverage

### Requirement: Derived-App Runtime Smoke Reports Actionable Failure Stages
The repository MUST surface derived-app runtime smoke failures in a way that distinguishes startup failures from proxy or response-shape failures.

#### Scenario: A contributor encounters a runtime smoke failure
- GIVEN a contributor runs the repository-owned derived-app runtime smoke entrypoint
- WHEN the smoke validation fails
- THEN the reported failure makes it possible to distinguish whether the failure happened during backend startup, frontend startup, proxy reachability, or response validation
