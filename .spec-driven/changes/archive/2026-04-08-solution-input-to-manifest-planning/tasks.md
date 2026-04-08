# Tasks: solution-input-to-manifest-planning

## Implementation

- [x] Define the machine-readable `solution input -> manifest` planning artifact, its schema, and at least one repository-owned example.
- [x] Update the relevant specs so contributors can distinguish `solution input`, planning output, and `app-manifest` responsibilities without ambiguity.
- [x] Update AI-facing guidance, including AI context, orchestration guidance, quickstart, and the solution-input / derivation playbook, to expose the planning step.
- [x] Add or extend repository validation coverage so the planning asset is checked as a first-class AI-readable repository asset.

## Testing

- [x] `./scripts/platform-tool.sh assembly verify` passes with the planning asset wired into the repository-owned assembly validation path.
- [x] Unit test task: `node --test scripts/app-assembly.test.mjs` passes with coverage for the new planning asset and its repository-owned examples.

## Verification

- [x] `node /home/code/.agents/skills/roadmap-recommend/scripts/spec-driven.js verify solution-input-to-manifest-planning`
- [x] Confirm the repository still requires a standalone `app-manifest` before assembly and does not treat the planning artifact as a direct runtime input
