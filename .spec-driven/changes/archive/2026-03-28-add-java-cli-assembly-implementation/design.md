# Design: add-java-cli-assembly-implementation

## Approach

Add a standalone Maven workspace, expected under a tool-oriented directory such as `tools/java-assembly-cli/`, with its own `pom.xml`, Java sources, and tests.

The Java CLI should:

1. Read the same machine-readable assets used by the Node implementation
   - app manifest
   - module registry
   - app assembly contract
   - compatibility fixtures when needed for test coverage

2. Apply the same observable contract rules
   - required core modules
   - dependency validation
   - output directory constraints
   - required generated files
   - selected-module output invariants

3. Generate a compatible derived application skeleton
   - it does not need to share Node internals
   - it does need to satisfy the same compatibility checks

4. Integrate into the repository-owned validation path
   - the compatibility suite should be able to target both implementations
   - the Java CLI should be explicitly discoverable in docs and contract metadata

## Key Decisions

- Put the Java CLI in a separate Maven workspace.
  Rationale: the CLI is a repository tool, not part of the running backend service. Keeping it separate avoids mixing runtime code with generation tooling.

- Keep the compatibility suite as the primary conformance target.
  Rationale: the Java implementation should prove the standard is implementation-independent rather than forcing the standard to adapt to one tool.

- Do not require byte-for-byte identical output with Node.
  Rationale: conformance should be based on observable contract behavior, not identical formatting or file-generation internals.

- Keep Node as the reference implementation while Java becomes an additional compatible implementation.
  Rationale: this reduces migration risk and preserves the existing path while expanding compatibility proof.

## Alternatives Considered

- Add the Java CLI directly inside `backend/`.
  Rejected because it would blur the line between backend runtime code and repository tooling.

- Replace the Node implementation immediately with Java.
  Rejected because it would turn a compatibility expansion into a migration, increasing risk and scope.

- Delay Java until after more standardization changes.
  Rejected because a second implementation is the fastest way to prove the current standard and compatibility suite are meaningful.
