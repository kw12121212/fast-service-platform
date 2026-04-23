# e2e-solution-to-module-validation

## What

Create one repository-owned end-to-end fixture that exercises the complete V1 path: structured solution input → planning → recommendation → manifest preparation → descriptor-driven module generation → project-scoped assembly → generated-app verification → runtime smoke validation. The fixture must pass through every platform entrypoint without depending on external AI contributor improvisation at any step.

## Why

All individual V1 capabilities have been built and archived (solution input, planning, recommendation, descriptor-driven modules, assembly, verification, smoke), but no repository-owned fixture proves the full path works as a coherent pipeline. Closing V1 requires evidence that the path from business intent to a running derived application is repeatable using only repository-owned assets. This is the gate that unblocks V2 work.

## Scope

In scope:
- One repository-owned solution input fixture (a concrete JSON document under `docs/ai/tests/`)
- A deterministic e2e test script that chains every V1 workflow entrypoint in sequence
- The fixture uses the existing `core-admin-console` solution input shape or a narrower fixture-specific input
- The fixture exercises descriptor-driven module generation for at least one management-module shape (e.g. `leave-request` or `department-directory`)
- The fixture produces a valid manifest, runs assembly, verification, and smoke against the output
- The e2e script reports pass/fail at each stage with actionable failure messages

Out of scope:
- A broad fixture matrix covering every module combination or deployment scenario
- New V2 features (multi-tenant, distributed output, registry expansion)
- Performance or load testing of the pipeline
- CI integration (the fixture runs locally via a repository-owned command)

## Unchanged Behavior

- All existing repository-owned entrypoints (assembly, verification, smoke, planning, recommendation, descriptor-driven generation) remain unchanged — the fixture invokes them, it does not modify their contracts
- Existing unit and integration tests continue to pass
- The baseline demo fixture and its manifest remain as-is
- Platform tooling façade and wrapper scripts are not altered
