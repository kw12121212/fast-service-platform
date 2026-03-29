# standardize-generated-app-verification-contract

## What

Define a language-neutral contract for verifying generated applications after app assembly.

The change will formalize the generated-app verifier inputs, observable checks, result shape, and failure semantics so contributors can distinguish the verification standard from the current `Node` verifier implementation.

The repository will continue to ship the existing `Node` verifier path, but it will be documented and modeled as a reference verifier rather than the only implied verifier.

## Why

The repository now has multiple compatible assembly implementations, but generated-app verification is still effectively `Node-shaped`.

Today the generated application copies `scripts/app-assembly-lib.mjs` and `scripts/verify-derived-app.mjs`, which means the verification path is still coupled to one implementation family. That weakens the claim that the generated output is governed by a language-neutral contract.

Standardizing generated-app verification is the next step in making the repository's assembly standard genuinely portable across `Node`, `Java`, and AI-native implementations.

## Scope

In scope:

- Define a generated-app verification contract with machine-readable assets
- Specify verifier inputs, observable checks, result semantics, and failure behavior
- Clarify which generated-app files are normative verification inputs
- Reposition the current `Node` verifier as a reference verifier
- Update AI/readiness docs and assembly metadata to expose the verifier contract clearly

Out of scope:

- Adding a second verifier implementation in this change
- Reworking the full app assembly contract beyond verifier-related clarifications
- Replacing the current `Node` verifier as the default repository-owned verifier path
- Changing product boundaries, generated app shape, or module semantics

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- The platform remains scoped to `enterprise internal management` and `monolithic` generated applications
- The assembly standard remains driven by `contract + schemas + compatibility suite`
- The existing `Node` verifier path remains available during and after this change
- Compatible assembly implementations continue to be validated by observable behavior rather than internal structure
