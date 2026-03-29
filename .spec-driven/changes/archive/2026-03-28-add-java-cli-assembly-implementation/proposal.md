# add-java-cli-assembly-implementation

## What

Add a Java CLI as a second compatible app assembly implementation in a separate Maven workspace.

The Java CLI will consume the existing assembly contract, schemas, module registry, and compatibility suite to generate an independent application skeleton without depending on the Node implementation's internal structure.

The repository's validation path will be updated so the Java CLI can be checked against the same observable compatibility rules already used for the Node reference implementation.

## Why

The platform now defines a language-neutral assembly standard and compatibility suite, but it still has only one maintained implementation.

Adding a Java CLI validates that the standard is real rather than Node-shaped. It also gives the repository a compatible implementation built on the backend toolchain baseline, which is useful for contributors who want the assembly path in Java instead of Node.

This change proves that the compatibility suite can support multiple implementations without changing the contract itself.

## Scope

In scope:

- Add a separate Maven workspace for a Java CLI assembly implementation
- Implement Java CLI support for reading the current manifest, module registry, and assembly contract
- Generate a derived application skeleton through the Java CLI using the existing language-neutral contract
- Update compatibility validation so the Java CLI can be validated against the same observable rules
- Update AI/readiness docs to describe the Java CLI as a compatible implementation alongside the Node reference implementation

Out of scope:

- Replacing the Node implementation as the default implementation in this change
- Reworking the standardized contract unless a small correction is required for Java compatibility
- Adding more than one new implementation language
- Moving the Java CLI into the existing `backend/` runtime workspace

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- `contract + schemas + compatibility suite` remain the standard facts for app assembly
- The existing Node implementation remains available as a reference implementation
- The platform remains scoped to enterprise internal management and monolithic generated applications
- The dependency boundary remains `Lealone-Platform + repository-internal dependencies`
