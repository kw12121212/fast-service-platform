# Tasks: standardize-app-assembly-spec-and-compatibility-suite

## Implementation

- [x] Define the normative app assembly standard, including which machine-readable assets are part of the contract and which fields or behaviors are mandatory
- [x] Define the generated-output invariants that compatible implementations must satisfy
- [x] Add a repository-owned compatibility suite spec, including fixtures, expected outcomes, and validation expectations
- [x] Update AI readiness and derivation guidance so contributors treat the contract and compatibility suite as the primary facts
- [x] Reclassify the current Node-based scaffolding path as a reference implementation rather than the only implementation model

## Testing

- [x] Add or update automated checks that validate compatibility fixtures and output invariants independently of one implementation's internal structure
- [x] Run the repository-owned validation entrypoints affected by the standardization change
- [x] Verify the current Node reference implementation still conforms to the standardized compatibility rules

## Verification

- [x] Verify another language or AI implementation could target the contract without depending on Node-specific internal details
- [x] Verify the compatibility suite checks observable assembly behavior rather than script-specific implementation details
- [x] Verify unchanged product boundaries remain intact: monolith target, enterprise-management scope, and dependency boundary
