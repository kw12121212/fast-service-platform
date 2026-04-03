# project-derived-app-upgrade-support

## What

Define the first project-scoped derived-app upgrade support path so a bound software project can evaluate upgrade readiness and inspect repository-owned upgrade guidance from the platform instead of relying only on scattered repository commands.

This change adds proposal scope for:

- exposing project-scoped derived-app upgrade support from the current Projects experience
- defining backend request and status behavior for project-scoped upgrade target lookup, advisory review, compatibility evaluation, and `upgrade-execute --dry-run` against the current project-derived lifecycle target
- preserving the current repository-owned lifecycle, advisory, and upgrade tooling as the only implementation boundary for upgrade behavior

## Why

`project-derived-app-assembly` and `project-derived-app-verification` have already established the first project-scoped lifecycle path for deriving and validating an application from a bound software project. The roadmap milestone still needs the upgrade step so contributors can inspect whether a project-derived application is upgradeable without leaving the project workflow.

Without a project-scoped upgrade support anchor:

- the `project-scoped-derived-app-lifecycle` milestone remains incomplete after assembly and verification
- contributors cannot review supported upgrade targets, advisory guidance, compatibility readiness, or dry-run execution plans from the same project context that produced and verified the derived application
- later planning work would still land on a fragmented lifecycle surface instead of a coherent project-scoped path

## Scope

In scope:

- define project-scoped upgrade support availability for a bound software project
- define how a contributor inspects supported target releases and repository-owned advisory guidance from the project scope
- define how a contributor requests repository-owned upgrade compatibility evaluation and `upgrade-execute --dry-run` from the project scope against the current project-derived lifecycle target
- define visible success, failure, and restricted states for the first project-scoped upgrade support path
- define the first upgrade support surface as current project-scoped requests plus the latest visible outcome rather than a persistent history model
- keep the first project-scoped upgrade support path inside the existing Projects experience and backend-backed project workflows
- keep repository-owned release lookup, advisory, upgrade evaluation, and dry-run execution tooling as the only execution boundary

Out of scope:

- redefining lifecycle metadata, release-history semantics, advisory semantics, or upgrade compatibility rules
- replacing repository-owned upgrade tooling with project-local or frontend-local logic
- adding a separate top-level lifecycle console outside the current Projects experience
- persistent project-scoped upgrade run history
- broad derived-app directory selection outside the project-scoped lifecycle target
- automatically executing a non-dry-run upgrade as part of the first project-scoped upgrade support release
- solution-input planning, module recommendation, or descriptor-driven generation behavior

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- Existing direct repository-owned upgrade commands remain valid.
- Project-scoped derived-app assembly and verification behavior remain unchanged.
- Repository binding, Git management, worktree management, merge support, and sandbox behavior remain unchanged.
- The repository-owned lifecycle, advisory, release-history, and upgrade contracts remain the normative boundaries for upgrade behavior.
- The first project-scoped upgrade support release does not expose non-dry-run upgrade execution.
