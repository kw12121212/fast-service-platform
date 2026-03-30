# Tasks: standardize-ai-solution-input-model

## Implementation

- [x] Define a machine-readable AI solution input contract, including required fields, optional fields, and input-layer scope boundaries.
- [x] Define and document the mapping boundary between `solution input` and `app-manifest`, including what remains the responsibility of repository-owned assembly tooling.
- [x] Add repository-owned AI context, quickstart, and playbook guidance that teaches contributors and AI agents how to use the structured input model before assembly.
- [x] Add machine-readable schema assets and at least one repository-owned example for the structured solution input model.
- [x] Extend validation guidance and repository contracts so the new input model is treated as a first-class AI-readable asset without breaking current manifest-driven workflows.

## Testing

- [x] `bun run lint` passes in `frontend/`
- [x] `node --test scripts/app-assembly.test.mjs` passes
- [x] `mvn -q -f tools/java-assembly-cli/pom.xml test` passes
- [x] `mvn -q -f tools/java-generated-app-verifier/pom.xml test` passes

## Verification

- [x] `node /home/code/.agents/skills/spec-driven-propose/scripts/spec-driven.js verify standardize-ai-solution-input-model` passes
- [x] Confirm the repository still treats `app-manifest` as the direct assembly input while exposing `solution input` as the higher-level AI input layer
