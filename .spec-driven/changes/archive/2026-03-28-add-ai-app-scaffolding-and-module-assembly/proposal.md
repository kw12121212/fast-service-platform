# add-ai-app-scaffolding-and-module-assembly

## What

Define Fast Service Platform as a repository-owned base library that AI can use to derive an independent enterprise-management application skeleton.

Add a machine-readable app assembly contract, layered AI indexes, and a repository-owned scaffolding flow that can generate a standalone monolithic application from selected platform modules.

Reclassify `software project management`, `ticket management`, and `kanban management` from mandatory baseline features into optional built-in modules that AI may choose during application assembly.

## Why

The repository currently proves that a runnable baseline application exists and that AI can contribute safely inside the repository, but it does not yet define how AI should consume the platform as a reusable base for creating a new application.

Without a machine-readable module registry, assembly manifest, and derivation workflow, AI cannot reliably distinguish platform core from example business modules, cannot select modules intentionally, and cannot generate a new application skeleton without editing the current mother app directly.

This change turns the project from a runnable baseline application into a reusable platform plus assembly path, which is closer to the stated product goal of AI-driven component reuse.

## Scope

In scope:

- Define the observable contract for AI-driven application derivation from this repository
- Add layered AI-facing indexes for repository entry, module registry, and application assembly
- Define the boundary between platform core and optional built-in modules
- Make `software project`, `ticket`, and `kanban` modules optional during assembly
- Define and implement a repository-owned scaffolding and module-assembly flow that generates an independent application skeleton
- Define validation expectations for scaffolded applications and assembled module selections
- Update AI documentation and readiness assets so AI can find and use the new derivation path

Out of scope:

- Adding in-repository AI chat, prompt intake, prototype upload, or reference-site crawling features
- Expanding V1 beyond enterprise internal management applications
- Expanding V1 beyond monolithic applications
- Introducing new external software libraries outside the existing dependency boundary without a separate approved change
- Designing arbitrary new business modules beyond the module-boundary and assembly work needed for this change

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- The repository remains an AI-oriented enterprise component platform rather than a built-in AI chat product
- The dependency boundary remains `Lealone-Platform + repository-internal dependencies`
- The current backend/frontend toolchain baseline remains Java 25, Maven 3.9.x, Node 24, bun, Vite 8, React 19, shadcn/ui, and Tailwind CSS 4
- The current repository can still provide a runnable default baseline application after the new assembly contract is introduced
