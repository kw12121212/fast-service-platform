# Design: standardize-app-assembly-spec-and-compatibility-suite

## Approach

Separate the app assembly capability into three layers:

1. Normative contract layer:
   Define which machine-readable assets are part of the standard, which fields are normative, and which output behaviors implementations must satisfy.

2. Compatibility layer:
   Define fixtures, golden expectations, and repository-owned validation rules that determine whether an implementation conforms to the contract.

3. Reference implementation layer:
   Keep the current Node-based scaffolding flow, but explicitly classify it as one compatible implementation that follows the normative contract and passes the compatibility suite.

The core goal is to ensure that future implementations do not need to mimic the current script structure; they only need to satisfy the same contract and pass the same compatibility verification.

## Key Decisions

- Make the contract, not the Node script, the source of truth.
  Rationale: the platform is meant to be consumed by AI and potentially by multiple implementation languages, so one runtime-specific script should not define compatibility alone.

- Use machine-readable schema plus behavioral specs together.
  Rationale: schema is good for structure, but it does not express all observable behavior. Main specs remain necessary for semantic rules and output expectations.

- Define a compatibility suite as a first-class repository asset.
  Rationale: without executable compatibility checks, “language-neutral” remains aspirational and implementations will drift.

- Keep the current Node scaffolding path in place as a reference implementation.
  Rationale: standardization should strengthen what exists, not create a gap where the contract exists but no maintained implementation remains.

## Alternatives Considered

- Treat the current Node implementation as the de facto standard and document it better.
  Rejected because it still makes other implementations depend on reverse-engineering one script instead of following a stable contract.

- Define only prose guidance without machine-readable schema or compatibility fixtures.
  Rejected because AI and tool-driven implementations need structured constraints and executable validation, not narrative guidance alone.

- Require a second implementation in this same change.
  Rejected because that would expand scope from standardization into multi-implementation delivery.
