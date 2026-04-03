# project-derived-app-verification

## What

Define the first project-scoped derived-app verification path so a bound software project can trigger and inspect repository-owned generated-app validation from the platform instead of relying only on scattered repository commands.

This change adds proposal scope for:

- exposing project-scoped derived-app verification from the current Projects experience
- defining backend request and status behavior for running repository-owned validation against a project-scoped derived-app target
- preserving the current repository-owned verification contract and validation tooling as the only implementation boundary for verification behavior

## Why

`project-derived-app-assembly` has already established the first project-scoped lifecycle entrypoint, but contributors still need to leave the project workflow to verify whether a generated application is actually valid. The roadmap places `project-derived-app-verification` immediately after assembly inside the same lifecycle milestone so the platform can offer a coherent derive-then-validate path before taking on broader upgrade support or earlier planning guidance.

Without a project-scoped verification anchor:

- the project-scoped lifecycle remains incomplete after assembly succeeds
- contributors cannot evaluate generated output from the same project context that initiated derivation
- `project-derived-app-upgrade-support` would need to build on a weaker project-scoped lifecycle surface

## Scope

In scope:

- define project-scoped verification availability for a bound software project
- define how a contributor requests generated-app verification and runtime smoke validation from the project scope against the latest visible project-scoped assembly output
- define visible success, failure, and restricted states for the first project-scoped verification path
- define the first verification surface as the current request plus the latest visible outcome rather than a persistent history model
- keep the first project-scoped verification path inside the existing Projects experience and backend-backed project workflows
- keep repository-owned verification tooling and contracts as the verification execution boundary

Out of scope:

- redefining generated-app verification semantics or replacing the repository-owned verification contract
- adding project-scoped upgrade target selection, advisory review, upgrade evaluation, or upgrade execution
- adding solution-input planning, module recommendation, or descriptor-driven generation behavior
- introducing a separate top-level lifecycle console outside the current Projects experience
- persistent project-scoped verification run history
- selecting arbitrary verification target directories outside the latest visible project-scoped assembly output
- reimplementing repository-owned verification or runtime smoke logic inside frontend or project-local code

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- Existing direct repository-owned verification commands remain valid.
- Project-scoped derived-app assembly behavior remains unchanged.
- Repository binding, Git management, worktree management, merge support, and sandbox behavior remain unchanged.
- The repository-owned generated-app verification contract and runtime smoke contract remain the normative validation boundaries.
