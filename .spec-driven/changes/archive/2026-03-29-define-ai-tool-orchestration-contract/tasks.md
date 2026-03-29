# Tasks: define-ai-tool-orchestration-contract

## Implementation

- [x] Add a machine-readable AI tool-orchestration contract that defines the default repository-owned entrypoints, workflow ordering, and fallback semantics for supported platform workflows.
- [x] Update AI readiness assets and playbooks to teach AI contributors how to use the unified tooling facade and related repository-owned wrappers for assembly, verification, advisory, lifecycle, and upgrade tasks.
- [x] Align generated-app and assembly-facing contracts with the new orchestration guidance without changing the existing compatible implementation matrix.

## Testing

- [x] `bun run lint` passes in `frontend/`
- [x] `node --test scripts/app-assembly.test.mjs` passes
- [x] `./scripts/platform-tool.sh assembly verify` and `./scripts/platform-tool.sh upgrade advisory` pass with the updated orchestration guidance

## Verification

- [x] `node /home/code/.agents/skills/spec-driven-propose/scripts/spec-driven.js verify define-ai-tool-orchestration-contract` passes
- [x] Confirm the new orchestration contract and playbooks keep AI in the tool-orchestration role instead of introducing AI-direct workflow implementation
