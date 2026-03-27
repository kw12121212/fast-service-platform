# Questions: define-ai-enterprise-component-platform-v1

## Open

<!-- No open questions -->

## Resolved

- [x] Q: What is the V1 target application category?
  Context: The platform needs a concrete first target before component and scaffold work can be planned.
  A: V1 is limited to enterprise internal management applications.
- [x] Q: What human input channels are allowed?
  Context: Input constraints define how AI receives product intent and visual guidance.
  A: Input is limited to natural language, optional prototype images, and UI-only reference websites.
- [x] Q: What dependency boundary should V1 enforce?
  Context: The platform needs a clear rule for what generated projects may rely on.
  A: Generated projects should rely on Lealone-Platform and dependencies already present in this project, without requiring additional external software libraries.
- [x] Q: What is the minimum built-in component set for V1?
  Context: V1 must have a concrete baseline of capabilities that every generated project can depend on.
  A: The minimum component set is user management, role-based permission management, software project management, ticket management, and kanban management.
- [x] Q: How should Git, worktree, merge support, and sandbox environments be treated?
  Context: These capabilities could otherwise be interpreted as external tools or optional integrations.
  A: They are platform-provided components of this project.
- [x] Q: What minimum deliverables must every generated project include?
  Context: "Whole project" needs a concrete output contract for later verification.
  A: Each generated project must include tests, seed or initialization data, an admin dashboard home page, software project management, role and permission management, and user management.
