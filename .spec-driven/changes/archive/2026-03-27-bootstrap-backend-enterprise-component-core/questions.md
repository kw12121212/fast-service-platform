# Questions: bootstrap-backend-enterprise-component-core

## Open

<!-- No open questions -->

## Resolved

- [x] Q: Should the first backend core use a single-module Maven project or a parent-child multi-module structure?
  Context: The project layout affects implementation complexity and how quickly the first runnable backend can be delivered.
  A: Use a single-module Maven project first.
- [x] Q: Should demo data rely on SQL only or Java only?
  Context: First-run backend usability depends on how demo data and setup logic are produced.
  A: Use a combined approach with both SQL and Java initialization to produce optional demo data.
- [x] Q: What permission granularity should the first RBAC baseline use?
  Context: Access-control behavior affects both data model shape and backend service boundaries.
  A: Combine menu-level and function-level permissions.
- [x] Q: How should the minimum project, ticket, and kanban relationships be modeled?
  Context: The first backend core needs a clear workflow baseline for enterprise project management.
  A: Project to ticket is one-to-many, and kanban to ticket is one-to-many.
- [x] Q: How much kanban behavior should V1 support?
  Context: Kanban scope directly affects domain complexity in the first backend core.
  A: Support only the minimum state-flow baseline first.
- [x] Q: Is demo data required for every generated backend?
  Context: This affects both the generated project contract and first-run backend behavior.
  A: Demo data is optional and should be limited to a small dataset useful for demonstration.
- [x] Q: What test layers should the first backend core include?
  Context: The test baseline must be explicit before implementation begins.
  A: Include unit tests, service tests, and integration tests.
- [x] Q: How should backend packages be organized?
  Context: Package structure affects how the backend scales as more components are added.
  A: Organize packages by domain.
