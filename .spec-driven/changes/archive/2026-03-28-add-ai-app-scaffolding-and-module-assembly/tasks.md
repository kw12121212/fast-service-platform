# Tasks: add-ai-app-scaffolding-and-module-assembly

## Implementation

- [x] Add the AI-facing app assembly contract, including a machine-readable application manifest and a machine-readable module registry
- [x] Update repository AI readiness assets so contributors and AI agents can discover the derivation flow, layered indexes, and validation path
- [x] Refactor the platform contract so `software project`, `ticket`, and `kanban` are optional built-in modules instead of mandatory baseline capabilities
- [x] Implement a repository-owned scaffolding and module-assembly flow that generates an independent monolithic application skeleton from selected modules
- [x] Preserve a default assembly profile that reproduces the current runnable baseline application behavior
- [x] Add or update repository validation entrypoints so scaffolded outputs and module selections can be checked through repository-owned commands

## Testing

- [x] Add automated tests for the scaffold/assembly contract and machine-readable registry/manifest validation
- [x] Run `./scripts/verify-backend.sh`
- [x] Run `./scripts/verify-frontend.sh`
- [x] Run any new repository-owned scaffold/assembly validation command introduced by this change
- [x] Run `./scripts/verify-fullstack.sh` for the default assembled application profile if integration behavior is affected

## Verification

- [x] Verify AI can discover the derivation workflow without reading source files first
- [x] Verify AI can select modules using machine-readable indexes instead of hard-coded source knowledge
- [x] Verify a scaffolded application is independent from the current repository runtime workspace
- [x] Verify unchanged product boundaries remain intact: monolith target, enterprise-management scope, and dependency boundary
