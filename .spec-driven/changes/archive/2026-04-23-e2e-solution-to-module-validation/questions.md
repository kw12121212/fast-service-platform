# Questions: e2e-solution-to-module-validation

## Open

## Resolved

- [x] Q: Which management module should the e2e fixture use — `leave-request` (includes workflow), `department-directory` (form + report only), or a new narrow fixture-specific module?
  Context: The fixture needs at least one descriptor-driven module to prove the full path. Choosing the right one affects whether the fixture also proves the workflow page generation path.
  A: Use `leave-request` — it exercises the most complete descriptor shape (form + report + workflow), giving the fixture the broadest V1 coverage in a single pass.

- [x] Q: Should the e2e pipeline script run the runtime smoke stage by default, or should smoke be a separate opt-in step given it requires starting a full JVM process?
  Context: Runtime smoke is the heaviest stage and may not be desirable in quick-check scenarios. The milestone scope includes it, but the default execution mode is a design choice.
  A: Include smoke by default (milestone scope requires it), support `--skip-smoke` for quick iteration.
