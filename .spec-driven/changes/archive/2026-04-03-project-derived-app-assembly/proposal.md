# project-derived-app-assembly

## What

Define the first project-scoped derived-app assembly path so a bound software project can trigger repository-owned application scaffolding from the platform instead of relying only on manual repository commands.

This change adds proposal scope for:

- exposing project-scoped derived-app assembly from the current Projects experience
- defining backend request and status behavior for running manifest-driven assembly against an explicit output directory
- preserving the current repository-owned assembly tooling as the only assembly implementation path

## Why

Repository-owned assembly already exists, but it still lives as a tooling surface that contributors must discover and run outside the project workflow. The roadmap places project-scoped derived-app lifecycle work ahead of solution-input planning and descriptor-driven module generation, which makes assembly the next dependency-ordered entrypoint to formalize.

Without a project-scoped assembly anchor:

- `project-derived-app-verification` and `project-derived-app-upgrade-support` have no coherent project entrypoint to build on
- contributors still need scattered commands and manual context switching to derive applications
- later planning work lands against a weaker downstream execution path

## Scope

In scope:

- define project-scoped assembly availability for a bound software project
- define how a contributor submits a valid `app-manifest` input and explicit output directory through the project scope
- define visible success, failure, and restricted states for the first assembly path
- define the first assembly path against the bound project's main repository context only
- define the first assembly status surface as the current request plus the latest visible outcome
- keep the first path inside the existing Projects experience and backend-backed project workflows
- keep repository-owned assembly tooling and the `app-manifest` contract as the assembly execution boundary

Out of scope:

- project-scoped generated-app verification, runtime smoke, or upgrade execution
- solution-input to manifest planning or module recommendation
- linked-worktree-scoped assembly initiation
- persistent project-scoped assembly run history
- multi-step release advisory or upgrade target-selection UX
- multi-repository orchestration, CI/CD, deployment, or remote-hosting workflows
- replacing `./scripts/platform-tool.sh` or Java-owned assembly tooling with a new frontend or Node-specific implementation

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- Existing direct repository-owned assembly commands remain valid.
- Repository binding, Git management, worktree management, merge support, and sandbox behavior remain unchanged.
- The current baseline application and minimum admin page set remain unchanged.
- `solution input` remains upstream planning input rather than a direct assembly runtime contract.
