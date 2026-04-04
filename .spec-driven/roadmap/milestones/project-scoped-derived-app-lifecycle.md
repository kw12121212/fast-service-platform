# Project-Scoped Derived App Lifecycle

## Goal

Turn the repository's existing derived-app assembly, verification, smoke, and upgrade tooling into a coherent project-scoped lifecycle experience so a bound software project can drive those workflows through the platform instead of depending on scattered manual commands.

## Done Criteria

- A bound software project can expose project-scoped entrypoints for deriving an application, validating the generated output, running runtime smoke, and evaluating upgrade readiness.
- Contributors can see that derived-app lifecycle operations belong to the current project scope rather than to an ad hoc script collection.
- The first project-scoped lifecycle path preserves repository-owned tooling boundaries instead of re-implementing assembly, verification, smoke, or upgrade logic in the UI.
- All planned changes in this milestone are archived.

## Planned Changes

- project-derived-app-assembly
- project-derived-app-verification
- project-derived-app-upgrade-support

## Dependencies / Risks

- This milestone depends on the existing repository-binding, worktree, sandbox, assembly, verification, runtime-smoke, and upgrade contracts staying repository-owned rather than being reimplemented as project-local logic.
- Scope can sprawl into a full CI/CD or deployment product unless it stays focused on project-scoped orchestration of existing derived-app workflows.
- The project UX must make long-running or environment-dependent lifecycle steps visible without pretending that every host can run every heavy validation path.

## Status

- Declared: complete
