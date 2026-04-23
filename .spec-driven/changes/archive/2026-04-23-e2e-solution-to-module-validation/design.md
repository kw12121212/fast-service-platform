# Design: e2e-solution-to-module-validation

## Approach

Add a single repository-owned e2e test script (`scripts/verify-e2e-solution-pipeline.sh`) that orchestrates the full V1 pipeline in sequence:

1. **Validate solution input** — check the fixture solution-input JSON against the repository-owned schema
2. **Run planning** — feed the solution input through the planning contract, producing a planning artifact
3. **Run recommendation** — feed the planning output through the recommendation contract, producing a recommendation artifact
4. **Prepare manifest** — derive a valid `app-manifest` from the planning + recommendation output
5. **Run descriptor-driven module generation** — for each management module referenced by the manifest, run the descriptor-driven generation path
6. **Run project-scoped assembly** — assemble the derived application from the manifest into a temporary output directory
7. **Run generated-app verification** — verify the assembled application structure
8. **Run runtime smoke** — start the derived application and verify proxied endpoints return valid JSON

The script reports stage-by-stage pass/fail and exits non-zero on any failure. A fixture-specific solution input JSON is stored under `docs/ai/tests/` alongside a corresponding expected manifest or manifest-check fields.

## Key Decisions

- **One representative fixture, not a matrix**: a single solution input that includes at least one descriptor-driven management module is sufficient to prove the path works. Expanding coverage before the path is stable adds fragility.
- **Shell script orchestration**: the e2e script delegates to existing repository-owned entrypoints (Java tooling, assembly CLI, verifier, smoke runner) rather than reimplementing pipeline logic. This keeps the fixture a consumer, not a replacement.
- **Temporary output directory**: assembly output goes to a temp directory that is cleaned up after the run. The fixture does not leave generated artifacts in the repository tree.
- **Stage-level reporting**: each pipeline stage reports its own status. On failure, the script reports which stage failed and preserves enough context to diagnose without re-running.

## Alternatives Considered

- **CI-integrated e2e pipeline**: deferred. The fixture must be runnable locally first; CI wiring can follow once the path is proven stable.
- **Multiple fixture variants (admin-only, with-workflow, multi-module)**: too early. One fixture that includes a descriptor-driven management module proves the end-to-end contract. More variants can be added later as the path stabilizes.
- **Node-based orchestration**: unnecessary. The existing entrypoints are all invokable from shell. A shell script is simpler and has no additional dependency.
