# Questions: project-derived-app-verification

## Open

<!-- No open questions -->

## Resolved

- [x] Q: Should the first project-scoped verification path cover only generated-app contract verification, or should it also expose project-scoped runtime smoke validation in the same initial change?
  Context: This determines whether the first lifecycle validation step stays narrowly centered on contract verification or includes the adjacent runtime proof surface described by the repository-owned smoke contract.
  A: The first project-scoped verification path should expose both generated-app contract verification and runtime smoke validation.

- [x] Q: Should the first project-scoped verification request be limited to a project's latest visible assembly output, or should contributors be allowed to specify another explicit derived-app directory?
  Context: This changes how tightly project-scoped verification is coupled to project-scoped assembly outcomes and how much target-selection behavior the backend and frontend must expose.
  A: Limit the first project-scoped verification request to the latest visible project-scoped assembly output.
