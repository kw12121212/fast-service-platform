# RTK

RTK means `Repository Technical Knowledge`.

This document gives both humans and AI agents a shared technical view of the repository: what the platform is, what boundaries it follows, what technology choices are intentional, and what implementation reality already exists.

## Positioning

This project is an AI-oriented enterprise application component platform built on top of `Lealone`.

The main reuser is `AI`. In external workflows, AI may combine requirements and visual references, but the job of this repository is narrower: provide reliable, AI-friendly backend and frontend components that can be reused to assemble internal enterprise management systems.

## Product Boundary

- V1 targets internal enterprise management applications only.
- V1 targets a monolithic application shape.
- This repository does not directly implement AI chat, prompt intake, prototype upload, or reference-site ingestion.
- Avoiding new external software libraries is a product principle.

## Backend Baseline

- `Java 25 LTS` is the unified backend language and runtime baseline.
- [Lealone](https://github.com/lealone/Lealone) is the backend foundation.
- The goal is not to expose raw Lealone primitives directly, but to build higher-level enterprise components on top of Lealone in a form AI agents can reuse predictably.

## Lealone Assessment

The current implementation still follows the current Lealone path:

- Maven-managed dependencies
- SQL-defined tables and services
- Java service implementations
- `LealoneApplication`-based startup

At the same time, real integration work exposed practical friction around source builds, Tomcat runtime dependencies, smoke validation, and startup predictability. The working conclusion is:

- `Lealone` is structurally friendly to AI because the service and schema model is explicit.
- It still needs repository-owned engineering support around build, verification, and operational predictability to serve as a solid platform base for AI-generated enterprise systems.

## Frontend Baseline

- `Vite 8` is the frontend build and dev entrypoint.
- `React 19` is the frontend UI runtime.
- `shadcn/ui` is the source-controlled component system.
- `Tailwind CSS 4` is the styling and design-token baseline.
- `bun` is the frontend package manager and script runner.
- `Node 24` is the frontend runtime baseline.

## V1 Component Model

The runnable baseline application currently includes these business domains:

- user management
- role and permission management
- software project management
- ticket management
- kanban management

From the platform perspective for derived applications, the more useful split is:

- Mandatory core:
  - admin shell
  - user management
  - role and permission management
- Optional built-in business modules:
  - software project management
  - ticket management
  - kanban management

## Engineering-Support Components

These are also platform components, not incidental local tooling:

- Git repository management
- project-scoped worktree management
- code merge support
- sandbox environments

Implementation status today:

- Git repository binding and branch switching are implemented.
- Project-scoped worktree management is implemented.
- Project-scoped merge support from managed linked worktrees into existing local branches is implemented.
- Project-scoped sandbox environments for managed linked worktrees are implemented through `podman`, with persistent images and temporary containers.

## Minimum Platform Baseline

The V1 platform baseline must include at least:

- tests
- optional demo data
- admin dashboard shell
- user management
- role and permission management
- software project management
- ticket management
- kanban management

## AI Collaboration Constraints

- Route every non-trivial change through `.spec-driven/` first.
- Treat repository documents and directory structure as AI navigation surfaces: entrypoints, rules, specs, and workspace boundaries should be easy to locate.
- Prefer AI-friendly structure in both backend and frontend:
  - fewer hidden conventions
  - less magic generation
  - less cross-directory coupling
- When AI creates or changes applications, it should reuse built-in platform components before inventing new dependency stacks or external libraries.
- The repository should provide stable AI-facing entrypoints: quickstart docs, machine-readable context, playbooks, module metadata, assembly contracts, and verification commands.
- For app assembly, the source of truth is `contract + schemas + compatibility suite`. Language-specific implementations are compatible implementations or references, not the definition of the standard.
- The upstream AI path may now include `solution input -> planning -> recommendation -> descriptor-driven management module -> app-manifest -> assembly`, but `app-manifest` remains the direct assembly runtime input.

## Implementation Reality

This repository is already in a real implementation phase, not just product planning.

- `backend/` is a runnable single-module Maven backend.
- `frontend/` is a runnable PC admin frontend.
- The backend implements the minimum enterprise domains already listed in this document.
- The frontend implements the corresponding dashboard and management pages.
- The frontend talks directly to the current backend through `/service/*`.
- The backend supports optional demo data.
- The default backend validation path is a fast baseline, while real sandbox runtime validation runs through a separate repository-owned entrypoint.
- The software-project area already includes repository binding, Git branch switching, project-scoped worktree management, linked-worktree merge support, and linked-worktree sandbox support.

## Working Assumptions For Contributors

- Continue evolving the repository as a V1 enterprise component platform, not as an unrelated standalone business product.
- Prefer extending existing repository patterns over introducing new framework layers without need.
- If a change affects boundaries, collaboration contracts, module structure, or AI workflows, update specs before implementation.
