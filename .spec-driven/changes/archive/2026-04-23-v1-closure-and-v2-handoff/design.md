# Design: v1-closure-and-v2-handoff

## Approach

Produce a single closure document (`docs/ai/V1-CLOSURE.md`) that serves as the authoritative V1 record. The document is structured as a factual inventory — no aspirational language, no V2 commitments beyond naming the open edges. Then make minimal spec and roadmap updates to mark V1 as closed.

The closure document is derived entirely from existing repository state: the archived changes list, the milestone files, the v1-scope-boundaries spec, and the e2e fixture. No new information is invented.

## Key Decisions

- **Closure document location**: `docs/ai/V1-CLOSURE.md` — colocated with other AI-facing documentation so AI contributors encounter it naturally when reading the repository.
- **Spec update is additive only**: The v1-scope-boundaries spec gets a closure annotation section, but no requirements are removed or relaxed. The boundaries remain in force for any work that touches V1 output.
- **Milestone status update**: The platform-v1-integration-and-closure milestone moves from `proposed` to `complete` after both its planned changes are archived.

## Alternatives Considered

- **Closure as a changelog entry only**: Rejected — a changelog entry is too narrow to serve as a V1/V2 handoff reference. The closure document needs to be self-contained.
- **No spec changes**: Rejected — without marking the v1-scope-boundaries spec as closed, future contributors have no signal that the boundary constraints are finalized. A closure annotation prevents ambiguity.
- **Moving v1-scope-boundaries out of specs**: Rejected — the boundaries remain true for V1 output. They should stay in the spec index but be annotated as closed.
