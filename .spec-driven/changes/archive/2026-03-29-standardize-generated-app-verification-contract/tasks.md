# Tasks: standardize-generated-app-verification-contract

## Implementation

- [x] Add a generated-app verification contract spec and machine-readable asset
- [x] Update assembly metadata to reference the generated-app verification contract and distinguish normative verifier inputs from reference verifier implementations
- [x] Update repository AI/readiness docs to describe the generated-app verification contract and the current reference verifier path
- [x] Align the repository-owned `Node` generated-app verifier entrypoint with the standardized contract shape and terminology

## Testing

- [x] Add or update automated tests for generated-app verification contract coverage and verifier result semantics
- [x] Run `bun run lint`
- [x] Run `node --test scripts/app-assembly.test.mjs`
- [x] Run `./scripts/verify-app-assembly.sh`

## Verification

- [x] Verify a generated application can still be validated from generated output assets without depending on repository-only hidden context
- [x] Verify the `Node` verifier is documented as a reference verifier rather than the implied standard itself
- [x] Verify unchanged product boundaries remain intact: monolith target, enterprise-management scope, and dependency boundary
