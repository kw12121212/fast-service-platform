# Questions: project-repository-baseline

## Open

<!-- No open questions -->

## Resolved

- [x] Q: Should the first repository binding accept only local absolute paths?
  Context: The proposal needs a clear, externally visible repository-path contract.
  A: Yes. The first repository baseline uses explicit local absolute paths only.

- [x] Q: Should the first UI live in the existing Projects area or in a new engineering module?
  Context: The proposal needs a bounded frontend surface.
  A: It stays in the existing Projects experience for this baseline.

- [x] Q: Can the backend rely on the local Git CLI instead of introducing a new library dependency?
  Context: The project has a no-extra-library product principle and needs a practical repository inspection boundary.
  A: Yes. Using the local Git CLI is acceptable for this baseline.
