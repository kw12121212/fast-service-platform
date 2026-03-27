# Design: clarify-ai-component-platform-goal

## Approach

Update the repository's authoritative description in three layers: root docs, RTK, and active specs.
The spec update will move the active focus away from `generation/*` terminology and toward component-oriented contracts that better match the repository's actual purpose.
Historical archive changes remain untouched; only current source-of-truth docs and specs will be realigned.

## Key Decisions

- Keep AI as the primary consumer of the platform.
  Rationale: the repository still exists for AI reuse, just not as an in-repository AI interaction product.
- Remove active emphasis on built-in natural-language input handling and generated-project framing.
  Rationale: those ideas describe an external workflow context, not the repository's implemented product.
- Replace active `generation/*` main specs with `component/*` main specs.
  Rationale: the new names make the repository boundary legible and avoid implying a built-in generation engine.
- Preserve the existing enterprise-management and engineering-support component scope.
  Rationale: the issue is positioning clarity, not component contraction or expansion.

## Alternatives Considered

- Keep the existing `generation/*` specs and only soften wording in README.
  Rejected because the main specs are the authoritative source of truth and would remain misleading.
- Remove all references to AI from the project positioning.
  Rejected because AI reuse is still the primary usage model for the platform.
- Introduce a new runtime generation module to justify the old wording.
  Rejected because it would move the repository toward the wrong product shape.
