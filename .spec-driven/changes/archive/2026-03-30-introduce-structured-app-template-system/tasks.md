# Tasks: introduce-structured-app-template-system

## Implementation

- [x] Define a machine-readable structured app template contract that classifies stable templates, slots, module fragments, and derived-app customization zones.
- [x] Extend assembly and lifecycle / upgrade contracts so generated output ownership and override boundaries are expressed through the new template system.
- [x] Add repository-owned AI context, quickstart, and playbook guidance that teaches contributors how template slots and customization zones relate to assembly and upgrade workflows.
- [x] Add machine-readable template metadata assets and at least one repository-owned example or classification map for current generated output.
- [x] Extend validation guidance so template-boundary semantics become part of the repository-owned AI-readable output model without weakening existing generated-output invariants.

## Testing

- [x] `bun run lint` passes in `frontend/`
- [x] `node --test scripts/app-assembly.test.mjs` passes
- [x] `mvn -q -f tools/java-assembly-cli/pom.xml test` passes
- [x] `mvn -q -f tools/java-generated-app-verifier/pom.xml test` passes

## Verification

- [x] `node /home/code/.agents/skills/spec-driven-propose/scripts/spec-driven.js verify introduce-structured-app-template-system` passes
- [x] Confirm the repository still uses `app-manifest` as the direct assembly input while the new template layer only standardizes generated-output structure and ownership boundaries
