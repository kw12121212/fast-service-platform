# Tasks: decompose-optional-business-modules

## Implementation

- [x] Refine the machine-readable module registry to define smaller optional delivery-management module units with explicit dependency declarations and supported profiles.
- [x] Update the assembly-facing and baseline specs so the new optional module boundaries are reflected in the platform's normative contracts.
- [x] Update compatibility fixtures and repository validation assets so the supported observable module combinations match the decomposed optional-module model.

## Testing

- [x] `bun run lint` passes in `frontend/`
- [x] `node --test scripts/app-assembly.test.mjs` passes
- [x] `./scripts/platform-tool.sh assembly verify` passes with the refined optional-module combinations

## Verification

- [x] `node /home/code/.agents/skills/spec-driven-propose/scripts/spec-driven.js verify decompose-optional-business-modules` passes
- [x] Confirm the refined module model preserves the current default runnable baseline while exposing smaller optional business capability units
