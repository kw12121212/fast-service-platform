# Design: project-derived-app-upgrade-support

## Approach

Attach the next derived-app lifecycle step to the existing software project scope instead of creating a separate upgrade console.

The proposed implementation path is:

1. expose project-scoped derived-app upgrade support availability and restricted states for a bound software project
2. add a backend-backed project upgrade support workflow that uses the current project-derived lifecycle target and delegates release lookup, advisory review, upgrade compatibility evaluation, and `upgrade-execute --dry-run` to repository-owned upgrade entrypoints
3. render the request path and latest visible upgrade-support outcome from the current Projects experience
4. keep the first upgrade-support step focused on orchestration and observable outcome reporting rather than redefining upgrade semantics or expanding into full project-local non-dry-run upgrade automation

## Key Decisions

- Start with project-scoped target lookup, advisory review, compatibility evaluation, and `upgrade-execute --dry-run` before non-dry-run upgrade execution.
  Rationale: the milestone goal explicitly mentions evaluating upgrade readiness, and exposing the dry-run plan gives contributors a stronger project-scoped decision surface while still staying inside repository-owned upgrade contracts.

- Keep the first upgrade support surface inside the current Projects experience.
  Rationale: that is the smallest visible extension of the existing project-centered engineering workflow.

- Reuse repository-owned release lookup, advisory, and upgrade evaluation entrypoints instead of introducing project-local upgrade logic.
  Rationale: the roadmap milestone is about project-scoped orchestration of existing lifecycle tooling, not replacing repository-owned contracts.

- Include `upgrade-execute --dry-run` in the first project-scoped upgrade support release.
  Rationale: contributors should be able to inspect the repository-owned upgrade plan from the same project workflow where they choose the target and review compatibility, without crossing into a real upgrade mutation path.

- Require a bound software project and an eligible project-derived lifecycle target as the first upgrade gate.
  Rationale: the lifecycle milestone is scoped to bound projects, and upgrade support only makes sense when the project already exposes a derived-app target with readable lifecycle metadata.

- Limit the first state surface to the current request and latest visible outcome.
  Rationale: contributors need actionable project-scoped readiness information before they need a broader run-history model.

- Keep the first project-scoped upgrade target anchored to repository-declared supported releases rather than arbitrary version input.
  Rationale: the repository already owns release history, lineage, and advisory assets; the project-scoped path should surface those facts instead of inventing a looser target model.

## Alternatives Considered

- Jump directly to full project-scoped non-dry-run upgrade execution.
  Rejected because it would add a larger and riskier lifecycle surface before the project scope exposes a narrower readiness path.

- Keep upgrade work as repository-only commands with no project-scoped visibility.
  Rejected because that would leave the lifecycle milestone incomplete after assembly and verification.

- Fold project-scoped upgrade support into the existing verification change.
  Rejected because verification has already been scoped and archived, and silently expanding that step would weaken milestone discipline.

- Build a new standalone lifecycle or upgrade area.
  Rejected because the current requirement is to make lifecycle steps discoverable from project scope, and the first upgrade-support step is too narrow to justify a new information architecture surface.

- Let contributors choose arbitrary derived-app directories as the first project-scoped upgrade target.
  Rejected because that weakens the lifecycle chain already established by project-scoped assembly and verification.
