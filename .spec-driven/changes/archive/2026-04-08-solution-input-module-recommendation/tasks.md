# Tasks: solution-input-module-recommendation

## Implementation

- [x] Define the machine-readable `solution-to-manifest recommendation` contract, its schema, and at least one repository-owned example recommendation artifact.
- [x] Update the relevant specs so contributors can distinguish deterministic planning facts, optional recommendation guidance, and standalone `app-manifest` responsibilities without ambiguity.
- [x] Update AI-facing guidance, including AI context, orchestration guidance, quickstart, and the relevant solution-input / derivation playbooks, to expose the recommendation step after planning.
- [x] Add or extend repository validation coverage so the recommendation asset is checked as a first-class AI-readable repository asset.

## Testing

- [x] `./scripts/platform-tool.sh assembly verify` passes with the recommendation asset wired into the repository-owned assembly validation path.
- [x] Unit test task: `node --test scripts/app-assembly.test.mjs` passes with coverage for the new recommendation asset and its repository-owned examples.

## Verification

- [x] `node /home/code/.agents/skills/roadmap-recommend/scripts/spec-driven.js verify solution-input-module-recommendation`
- [x] Confirm the repository still requires a standalone `app-manifest` before assembly and keeps recommendation guidance optional relative to deterministic planning
