# Tasks: add-java-generated-app-verifier-implementation

## Implementation

- [x] Add a separate Maven workspace for the Java generated-app verifier
- [x] Implement generated-app verification contract loading and normative-input discovery in the Java verifier
- [x] Implement Java-side generated-app validation that satisfies the current generated-app verification contract
- [x] Update repository metadata and AI/readiness docs to list the Java verifier as a compatible generated-app verifier
- [x] Update repository-owned verifier entrypoints so contributors can identify and run both Node and Java verifier paths

## Testing

- [x] Add automated tests for Java verifier contract loading and generated-app verification behavior
- [x] Run `bun run lint`
- [x] Run `mvn -q -f tools/java-generated-app-verifier/pom.xml test`
- [x] Run `node --test scripts/app-assembly.test.mjs`
- [x] Run `./scripts/verify-app-assembly.sh`

## Verification

- [x] Verify the Java verifier can validate a generated application from generated output assets alone
- [x] Verify the Java verifier lives outside the `backend/` runtime workspace
- [x] Verify the Node verifier remains documented as a reference verifier rather than the only verifier path
- [x] Verify unchanged product boundaries remain intact: monolith target, enterprise-management scope, and dependency boundary
