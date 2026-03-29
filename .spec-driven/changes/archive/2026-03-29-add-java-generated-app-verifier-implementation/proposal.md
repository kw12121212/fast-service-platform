# add-java-generated-app-verifier-implementation

## What

Add a Java generated-app verifier as a second compatible verifier implementation for derived applications.

The Java verifier will consume the generated application's own verification contract and generated assets to determine pass or fail without depending on the current Node verifier's internal structure.

The repository's generated-app validation guidance will be updated so contributors can identify both the existing Node reference verifier and the Java-compatible verifier path against the same generated-app verification contract.

## Why

The repository now defines a language-neutral generated-app verification contract, but only one repository-owned verifier exists in practice.

Adding a Java verifier proves that generated-app verification is actually contract-shaped rather than Node-shaped. It also gives contributors a verifier path aligned with the Java toolchain that already exists in the repository's assembly story.

This change extends the same pattern already established for app assembly itself: one contract, multiple compatible implementations.

## Scope

In scope:

- Add a separate Maven workspace for a Java generated-app verifier
- Implement Java loading of the generated app verification contract and its normative inputs
- Implement Java-side validation of generated apps against the observable checks defined by the contract
- Update repository metadata and AI/readiness docs to identify the Java verifier as a compatible verifier implementation
- Update repository-owned verification paths so contributors can invoke or inspect both Node and Java verifier paths

Out of scope:

- Replacing the current Node verifier as the default repository-owned verifier path
- Reworking the generated-app verification contract beyond small corrections needed for Java compatibility
- Adding more than one new verifier language
- Moving the Java verifier into the existing `backend/` runtime workspace

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- The generated-app verification contract remains the normative standard
- The existing Node verifier remains available as a reference verifier
- The platform remains scoped to enterprise internal management and monolithic generated applications
- Generated-app verification continues to depend on observable assets present in the generated application
