# Design: bootstrap-backend-enterprise-component-core

## Approach

Create a backend bootstrap that is small enough to ship as the first real implementation change but strong enough to carry the V1 minimum deliverables.
The backend foundation will follow Lealone-Platform's documented model of SQL-defined tables and services with a Java application entrypoint, then layer domain-oriented enterprise components on top.
The initial implementation will focus on the minimum enterprise-management domains and leave software-development management extensions for follow-up backend changes.

## Key Decisions

- Start with a single backend application in the reserved `backend/` workspace.
  Rationale: V1 is explicitly monolithic and should avoid premature distribution concerns.
- Use a single-module Maven project for the first backend core.
  Rationale: the initial backend needs to become runnable quickly, and a single module keeps structure and AI editing cost lower than a parent-child multi-module layout.
- Organize backend packages by domain instead of by technical layer only.
  Rationale: the platform is component- and domain-oriented, and domain packaging keeps AI changes closer to business boundaries.
- Align the runtime bootstrap with Lealone-Platform's official usage pattern of dependency declaration, SQL table/service definitions, and `LealoneApplication.start(...)`.
  Rationale: this keeps the first backend core close to the upstream platform's intended integration model.
- Implement only the minimum V1 component set in the first backend core.
  Rationale: user management, RBAC, software project management, ticket management, and kanban management are the mandatory baseline needed to satisfy V1.
- Use combined menu-level and function-level authorization as the first RBAC granularity model.
  Rationale: menu-only control is too coarse, while function-only control would not cover the administrative navigation model expected in enterprise back-office systems.
- Model project-to-ticket and kanban-to-ticket as one-to-many relationships, and keep kanban behavior limited to a minimal state-flow baseline.
  Rationale: this gives V1 enough workflow structure to be useful without turning the first backend core into a full workflow engine.
- Treat Git repository management, worktree management, merge support, and sandbox environments as later backend extensions rather than part of the first core bootstrap.
  Rationale: they are required platform components overall, but they are not part of the minimum V1 deliverable floor and would make the first backend change too large.
- Include optional demo data and tests in the first backend change.
  Rationale: the backend core should be easy to demonstrate while still being able to start without demo content.
- Combine SQL-based demo-data inputs with Java initialization logic for first-run setup.
  Rationale: SQL is a natural fit for Lealone schema and baseline demo data, while Java initialization logic can handle setup steps that are awkward to express as static SQL alone.
- Make demo data optional and scoped to a small dataset.
  Rationale: the platform should be able to start from a clean state or a demo state depending on the use case, while still supporting quick demonstrations.
- Cover unit tests, service tests, and integration tests in the initial backend validation scope.
  Rationale: the first backend core needs layered validation instead of a single narrow test type.

## Alternatives Considered

- Build the backend around a generic Java template unrelated to Lealone-Platform conventions.
  Rejected because the platform's backend value is explicitly built on Lealone-Platform.
- Start with a parent-child multi-module Maven structure.
  Rejected because the first backend core benefits more from a fast, low-complexity runnable baseline than from early module partitioning.
- Use only menu-level authorization or only function-level authorization.
  Rejected because either choice alone would underspecify the access-control behavior needed by an enterprise management backend.
- Try to implement every declared platform component in the first backend change.
  Rejected because it would mix the minimum enterprise-management core with larger software-development-management features and delay a runnable baseline.
- Defer tests and demo data to later changes.
  Rejected because demonstration and validation support are needed in the first runnable backend core.
