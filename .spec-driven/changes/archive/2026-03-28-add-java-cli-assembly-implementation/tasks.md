# Tasks: add-java-cli-assembly-implementation

## Implementation

- [x] Add a separate Maven workspace for the Java assembly CLI
- [x] Implement manifest, registry, and contract loading in the Java CLI
- [x] Implement Java-side assembly validation and generation that satisfy the existing app assembly standard
- [x] Update repository contract metadata and documentation to list the Java CLI as a compatible implementation
- [x] Update compatibility validation so the Java CLI can be checked alongside the Node reference implementation

## Testing

- [x] Add automated tests for Java CLI manifest validation and derived-output generation behavior
- [x] Run the Java workspace test command
- [x] Run the repository-owned app assembly compatibility validation
- [x] Re-run the existing Node reference implementation validation to confirm unchanged behavior

## Verification

- [x] Verify the Java CLI can generate a compatible derived application without depending on Node implementation internals
- [x] Verify the Java CLI lives outside the `backend/` runtime workspace
- [x] Verify unchanged product boundaries remain intact: monolith target, enterprise-management scope, and dependency boundary
