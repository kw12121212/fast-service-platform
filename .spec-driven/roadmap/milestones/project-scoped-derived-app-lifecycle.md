# Project-Scoped Derived App Lifecycle

## Goal

Turn the repository's existing derived-app assembly, verification, smoke, and upgrade tooling into a coherent project-scoped lifecycle experience so a bound software project can drive those workflows through the platform instead of depending on scattered manual commands.

## In Scope

- Project-scoped entrypoints for derived-app assembly
- Project-scoped entrypoints for derived-app verification and upgrade evaluation
- Project-scoped visibility for runtime-smoke and environment-dependent lifecycle steps

## Out of Scope

- A generic CI/CD or deployment product
- Reimplementation of repository-owned assembly or verification logic inside the UI
- Host assumptions that every environment can run every heavyweight validation path

## Done Criteria

- A bound software project can expose project-scoped entrypoints for deriving an application, validating the generated output, running runtime smoke, and evaluating upgrade readiness.
- Contributors can see that derived-app lifecycle operations belong to the current project scope rather than to an ad hoc script collection.
- The first project-scoped lifecycle path preserves repository-owned tooling boundaries instead of re-implementing assembly, verification, smoke, or upgrade logic in the UI.
- All planned changes in this milestone are archived.

## Planned Changes

- `project-derived-app-assembly` - Declared: complete - Project-scoped orchestration of repository-owned derived-app assembly
- `project-derived-app-verification` - Declared: complete - Project-scoped orchestration of repository-owned derived-app verification
- `project-derived-app-upgrade-support` - Declared: complete - Project-scoped upgrade readiness and execution support for derived applications

## Dependencies

- Depends on repository binding, worktree, sandbox, assembly, verification, runtime-smoke, and upgrade contracts remaining repository-owned.
- Reuses existing repository tooling entrypoints instead of creating project-local lifecycle implementations.

## Risks

- This milestone depends on the existing repository-binding, worktree, sandbox, assembly, verification, runtime-smoke, and upgrade contracts staying repository-owned rather than being reimplemented as project-local logic.
- Scope can sprawl into a full CI/CD or deployment product unless it stays focused on project-scoped orchestration of existing derived-app workflows.
- The project UX must make long-running or environment-dependent lifecycle steps visible without pretending that every host can run every heavy validation path.

## Status

- Declared: complete

## Notes

- This milestone turned previously separate derived-app operations into a coherent project-scoped lifecycle surface.
