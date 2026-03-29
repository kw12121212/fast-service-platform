# Design: add-ai-app-scaffolding-and-module-assembly

## Approach

Introduce a repository-owned application assembly contract that AI can consume through machine-readable assets instead of inferring platform shape from source files alone.

The implementation is expected to add three layers:

1. Repository entry layer:
   Extend the current AI quickstart/context path so AI can discover not only how to modify the repository, but also how to derive a new application from it.

2. Module-description layer:
   Add a machine-readable module registry that classifies platform capabilities into:
   - required platform core
   - optional built-in business modules
   - engineering-support components

   The registry should describe module purpose, dependency relationships, default inclusion behavior, output impact, and validation expectations.

3. Application-assembly layer:
   Add an application manifest contract plus a repository-owned scaffolding/assembly command that can produce an independent application skeleton from selected modules while preserving the platform dependency boundary and monolithic target shape.

The current repository should remain able to produce its existing baseline app as a default assembly profile so that existing behavior is preserved while the new derivation workflow is added.

## Key Decisions

- Treat derived-application creation as a first-class platform behavior rather than an informal doc workflow.
  Rationale: the product is positioned around AI-driven reuse and assembly, so the derivation path needs its own contract and tooling.

- Keep application derivation machine-readable.
  Rationale: AI needs stable facts more than prose. Repository entry docs remain useful, but module and assembly decisions should come from structured assets.

- Separate platform core from optional business modules.
  Rationale: AI cannot make intentional application choices if all current domains look equally mandatory. `project`, `ticket`, and `kanban` are useful modules, but they should not define the mandatory baseline for every derived app.

- Preserve a default assembled application that matches the current runnable repository.
  Rationale: introducing assembly should not regress the existing baseline or remove the current demonstration path.

- Keep the current dependency boundary and monolithic V1 target.
  Rationale: this change is about reuse, packaging, and selection, not about widening product scope or changing architecture.

## Alternatives Considered

- Keep the current repository as the only application target and rely on docs for AI reuse.
  Rejected because AI would still need to infer module boundaries manually and would have no reliable way to derive an independent application skeleton.

- Add only documentation and spec changes, without scaffolding or assembly implementation.
  Rejected because the user explicitly wants the operational path included, and docs-only changes would leave the core product gap unresolved.

- Keep `project`, `ticket`, and `kanban` in the mandatory minimum baseline.
  Rejected because these domains reflect one useful default application, not the universally required core of every enterprise internal-management app.

- Add a generic code generator with unconstrained output freedom.
  Rejected because it would weaken the platform boundary and make AI output less predictable. The assembly path should be repository-owned and constrained by module contracts.
