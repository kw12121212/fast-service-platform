# Tasks: descriptor-driven-management-module

## Implementation

- [x] Define the machine-readable `management-module descriptor`, its bounded shape, and at least one repository-owned example aligned to current V1 platform boundaries.
- [x] Update the relevant specs so contributors can distinguish planning, recommendation, descriptor-driven module generation, `app-manifest`, and assembly responsibilities without ambiguity.
- [x] Wire descriptor-driven module generation into AI-facing guidance, including AI context, orchestration guidance, and the derivation playbook.
- [x] Add or extend repository-owned validation coverage so the descriptor-driven module path is checked as a first-class AI-readable repository asset.

## Testing

- [x] `./scripts/platform-tool.sh assembly verify` passes with descriptor-driven module assets wired into the repository-owned assembly validation path.
- [x] Unit test task: `node --test scripts/app-assembly.test.mjs` passes with coverage for the new descriptor-driven module artifact and its repository-owned example.

## Verification

- [x] `node /home/code/.agents/skills/roadmap-recommend/scripts/spec-driven.js verify descriptor-driven-management-module`
- [x] Confirm the repository still requires a standalone `app-manifest` before assembly and does not treat the descriptor artifact as a direct runtime input.
- [x] Confirm the first descriptor-driven path remains bounded to a narrow management-module shape and does not subsume workflow-specific generation.
