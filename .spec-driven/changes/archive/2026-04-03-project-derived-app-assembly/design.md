# Design: project-derived-app-assembly

## Approach

Attach the first derived-app lifecycle entrypoint to the existing software project scope instead of creating a separate lifecycle console.

The proposed implementation path is:

1. expose project-scoped derived-app assembly availability and restricted states for a bound software project
2. add a backend-backed project assembly workflow that accepts a valid `app-manifest` input and an explicit absolute output directory
3. delegate the actual scaffolding work to the existing repository-owned assembly tooling so the project experience orchestrates the workflow without redefining it
4. render the request path and visible outcome from the current Projects experience

This keeps the first lifecycle step narrow. The project becomes the place where contributors discover and trigger assembly, while the repository-owned manifest-driven tooling remains the only assembly implementation boundary.

## Key Decisions

- Start with project-scoped assembly before project-scoped verification or upgrade support.
  Rationale: assembly is the dependency-ordered foundation for later lifecycle steps in the milestone.

- Keep `app-manifest` as the direct runtime input for project-scoped assembly.
  Rationale: the repository already defines `app-manifest` as the assembly contract, and this change should not collapse planning input into runtime input.

- Reuse the existing repository-owned assembly tooling path instead of adding project-local assembly logic.
  Rationale: the milestone explicitly requires project-scoped lifecycle orchestration to preserve repository-owned tooling boundaries.

- Keep the first lifecycle surface inside the current Projects experience.
  Rationale: that is the smallest visible extension of the existing project-centered engineering workflow.

- Require a bound software project as the first assembly gate.
  Rationale: the roadmap goal is a bound project driving lifecycle work, not a detached global assembly console.

- Allow any contributor-provided absolute output directory in the first project-scoped assembly path.
  Rationale: this preserves the current repository-owned assembly contract instead of adding implicit repository-derived path rules in the first lifecycle step.

- Limit the first project-scoped assembly path to the bound project's main repository context.
  Rationale: bringing managed linked worktrees into the first release would couple this change too early to worktree-specific source selection and restrictions.

- Expose only the current request and latest outcome in the first lifecycle state surface.
  Rationale: the first step needs actionable status, not a larger persistent run-history model.

## Alternatives Considered

- Start with project-scoped generated-app verification or upgrade support first.
  Rejected because both depend on a clearer project-scoped assembly entrypoint and generated output anchor.

- Accept `solution input` directly from the project workflow.
  Rejected because it bypasses the current manifest-driven assembly contract and overlaps with a later roadmap milestone.

- Introduce a separate top-level derived-app lifecycle area now.
  Rejected because the first step is too narrow to justify a new information architecture surface.

- Expand the first change to include multi-output management, advisory review, or release-target selection.
  Rejected because those additions would sprawl beyond the first assembly-focused lifecycle step.
