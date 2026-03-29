# Design: standardize-generated-app-verification-contract

## Approach

Add a dedicated generated-app verification contract that sits beside the app assembly contract instead of being implied by `verify-derived-app.mjs`.

The contract should define:

1. The verifier's normative inputs
   - generated manifest
   - copied module registry
   - copied assembly contract
   - generated verification contract
   - generated app files whose observable contents are checked

2. The verifier's observable checks
   - required generated files exist
   - generated context matches the manifest
   - route wiring matches selected modules
   - backend service and table contracts match selected modules

3. The verifier's result semantics
   - stable success/failure shape
   - issue reporting expectations
   - repository-owned CLI entrypoint expectations

4. The distinction between normative verifier contract and reference verifier implementation
   - `Node` verifier remains repository-owned
   - future `Java` or AI-native verifiers can target the same contract
   - compatibility assets can refer to verifier contract behavior without assuming one script

The implementation work that would follow this proposal should then update docs and machine-readable metadata so generated apps carry the verifier contract along with the other normative assets.

## Key Decisions

- Add a dedicated verifier contract instead of hiding verifier behavior inside the assembly contract.
  Rationale: verification has its own inputs, outputs, and compatibility semantics that should be independently understandable.

- Keep the contract machine-readable.
  Rationale: AI agents and non-Node implementations need a stable fact source rather than prose-only guidance.

- Keep `Node` as the first reference verifier.
  Rationale: the repository already owns that path, and this change is about decoupling the standard from it, not replacing it.

- Do not require a second verifier implementation in the same change.
  Rationale: standardizing the contract first keeps the scope manageable and avoids mixing standard definition with multi-implementation rollout.

## Alternatives Considered

- Leave verifier behavior embedded inside `verify-derived-app.mjs`.
  Rejected because it keeps generated-app verification implementation-shaped rather than standard-shaped.

- Treat the assembly contract as sufficient and avoid a separate verifier contract.
  Rejected because generated-app verification has distinct semantics that become hard to evolve or reason about when buried under assembly metadata.

- Add a `Java` verifier immediately without defining a verifier contract first.
  Rejected because it would repeat the same problem in another language and would not clearly state what both implementations are meant to conform to.
