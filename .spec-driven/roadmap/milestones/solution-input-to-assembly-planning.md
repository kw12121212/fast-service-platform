# Solution Input To Assembly Planning

## Goal

Make the path from structured business intent to repository-owned assembly decisions more explicit and repeatable so AI contributors can move from `solution input` to a valid `app-manifest` with less guesswork and clearer platform-owned guidance.

## Done Criteria

- Contributors can identify a repository-owned planning path that turns `solution input` into module decisions and a valid `app-manifest`.
- The repository exposes enough structured guidance or checks that AI contributors do not need to infer module selection and manifest shaping from prose alone.
- The planning path preserves the contract boundary that `solution input` is upstream planning input while `app-manifest` remains the direct assembly runtime contract.
- All planned changes in this milestone are archived.

## Planned Changes

- solution-input-gap-analysis
- solution-input-to-manifest-planning
- solution-input-module-recommendation

## Dependencies / Risks

- This milestone should follow project-scoped derived-app lifecycle work so planning guidance lands against a clearer execution path.
- Scope can drift into building an in-repo AI chat or unconstrained requirement-analysis feature unless it remains inside the current machine-readable planning boundary.
- Recommendation logic must preserve explicit module-registry and manifest ownership facts instead of hiding them behind opaque heuristics.

## Status

- Declared: proposed
