# Solution Input To Assembly Planning

## Goal

Make the path from structured business intent to repository-owned assembly decisions more explicit and repeatable so AI contributors can move from `solution input` to a valid `app-manifest` with less guesswork and clearer platform-owned guidance.

## In Scope

- A machine-readable `solution input` contract and schema
- A repository-owned planning artifact between `solution input` and `app-manifest`
- A repository-owned recommendation layer for optional module and manifest-shaping guidance
- AI-facing guidance that makes the planning path discoverable and verifiable

## Out of Scope

- An in-repo AI chat product or unconstrained requirement-analysis feature
- Replacing `app-manifest` as the direct assembly runtime contract
- Automatic business-code generation beyond the repository-owned planning boundary

## Done Criteria

- Contributors can identify a repository-owned planning path that turns `solution input` into module decisions and a valid `app-manifest`.
- The repository exposes enough structured guidance or checks that AI contributors do not need to infer module selection and manifest shaping from prose alone.
- The planning path preserves the contract boundary that `solution input` is upstream planning input while `app-manifest` remains the direct assembly runtime contract.
- All planned changes in this milestone are archived.

## Planned Changes

- `standardize-ai-solution-input-model` - Declared: complete - Machine-readable solution input contract, schema, and mapping boundary to app manifest
- `solution-input-to-manifest-planning` - Declared: complete - Repository-owned planning artifact between solution input and app manifest
- `solution-input-module-recommendation` - Declared: complete - Repository-owned recommendation artifact for optional module and manifest-shaping guidance

## Dependencies

- Depends on: project-scoped-derived-app-lifecycle (complete — unblocked).
- This milestone provides the concrete planning interfaces that descriptor-driven-business-module-generation depends on.

## Risks

- Scope can drift into building an in-repo AI chat or unconstrained requirement-analysis feature unless it remains inside the current machine-readable planning boundary.
- The intermediate artifact between solution input and manifest (schema, DSL, or structured prompt guidance) must be defined explicitly at the start of this milestone so that descriptor-driven-business-module-generation can build on a concrete interface rather than prose assumptions.
- Recommendation logic must preserve explicit module-registry and manifest ownership facts instead of hiding them behind opaque heuristics.

## Status

- Declared: complete

## Notes

- The first planned change standardized the solution-input model before the planning and recommendation layers were added.
