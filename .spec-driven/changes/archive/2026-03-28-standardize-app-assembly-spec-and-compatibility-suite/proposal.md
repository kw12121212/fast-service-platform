# standardize-app-assembly-spec-and-compatibility-suite

## What

Standardize the app assembly flow as a language-neutral contract instead of a Node-script-specific behavior.

Define machine-readable schema boundaries, output invariants, and a repository-owned compatibility suite so multiple implementations, including AI-generated ones, can be validated against the same contract.

Clarify that the current Node-based scaffolding path is the first reference implementation rather than the only valid implementation.

## Why

The repository now has a working app assembly contract and a Node-based scaffolding flow, but the operational truth still sits too close to the current JavaScript implementation.

If a Java, Python, Go, or AI-native implementation is added later, there is not yet a sufficiently formal compatibility target that defines what counts as a conforming implementation beyond matching the current script behavior.

This change reduces lock-in to one implementation language and makes the platform more durable as an AI-oriented base library.

## Scope

In scope:

- Define the assembly contract in a way that is language-neutral and implementation-independent
- Add or formalize machine-readable schema assets for manifest, module registry, and any other required assembly inputs
- Define output invariants for generated applications
- Define a repository-owned compatibility suite with fixtures, expected outcomes, and validation rules
- Clarify the role of the current Node implementation as a reference implementation
- Update AI readiness and derivation guidance so contributors know to rely on the contract and compatibility suite first

Out of scope:

- Building a second production implementation in Java, Python, or another language
- Replacing the current Node implementation in this change
- Expanding the product boundary beyond enterprise internal management or monolithic applications
- Introducing new external software libraries outside the current dependency boundary
- Redesigning the current business-module catalog beyond what is needed to standardize the contract

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- The repository remains an AI-oriented enterprise component platform rather than an in-repo AI chat product
- The dependency boundary remains `Lealone-Platform + repository-internal dependencies`
- The repository continues to provide a working Node-based assembly path during and after the standardization
- The current default assembly profile continues to represent the runnable baseline application
