# Design: project-derived-app-verification

## Approach

Attach the next derived-app lifecycle step to the existing software project scope instead of creating a separate validation console.

The proposed implementation path is:

1. expose project-scoped derived-app verification availability and restricted states for a bound software project
2. add a backend-backed project verification workflow that uses the latest visible project-scoped assembly output and delegates generated-app verification plus runtime smoke to repository-owned validation entrypoints
3. render the request path and latest visible verification outcome from the current Projects experience
4. keep the first verification step focused on orchestration and observable outcome reporting rather than redefining verification semantics

This keeps the next lifecycle step narrow. The project becomes the place where contributors discover and trigger validation, while the repository-owned verification contract remains the only implementation boundary for how validation actually works.

## Key Decisions

- Start with project-scoped verification before project-scoped upgrade support.
  Rationale: verification is the smaller next lifecycle step and gives upgrade support a stronger project-scoped foundation.

- Keep the first verification surface inside the current Projects experience.
  Rationale: that is the smallest visible extension of the existing project-centered engineering workflow.

- Reuse repository-owned validation entrypoints instead of introducing project-local verification logic.
  Rationale: the roadmap milestone is about project-scoped orchestration of existing lifecycle tooling, not replacing repository-owned contracts.

- Require a bound software project as the first verification gate.
  Rationale: the milestone goal is a project-scoped lifecycle path for bound projects rather than a detached global validation surface.

- Limit the first state surface to the current request and latest visible outcome.
  Rationale: contributors need actionable project-scoped status before they need a broader run-history model.

- Include both generated-app contract verification and runtime smoke in the first project-scoped verification step.
  Rationale: the first project-scoped lifecycle validation path should expose both structural and runnable proof using existing repository-owned validation boundaries.

- Limit the first project-scoped verification target to the latest visible project-scoped assembly output.
  Rationale: this keeps the first verification path tightly coupled to the project lifecycle state already surfaced by project-scoped assembly and avoids introducing a broader target-selection model.

## Alternatives Considered

- Jump directly to project-scoped upgrade support.
  Rejected because it adds a larger lifecycle surface before the project scope exposes a clearer validation step.

- Fold project-scoped verification into the existing assembly change.
  Rejected because assembly has already been scoped and archived, and silently expanding that step would weaken milestone discipline.

- Build a new standalone lifecycle or validation area.
  Rejected because the current requirement is to make lifecycle steps discoverable from project scope, and the first verification step is too narrow to justify a new information architecture surface.

- Redefine repository-owned verification semantics as part of this change.
  Rejected because the current gap is project-scoped orchestration and visibility, not the normative verification contract itself.
