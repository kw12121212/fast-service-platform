# Roadmap

This roadmap records the most valuable next directions for the platform.

It is not permission to bypass `.spec-driven/`. Any non-trivial change still needs to go through proposal, implementation, verification, and archive.

## Current Reading

The repository is no longer blocked on basic platform definition. It already has:

- a runnable backend and frontend baseline
- repository-owned AI contracts, schemas, manifests, and compatibility fixtures
- repository-owned tooling for assembly, verification, release lookup, and upgrades
- a committed baseline demo
- software-project repository binding
- bound-project Git branch inspection and safe branch switching
- project-scoped worktree management
- project-scoped merge support from managed linked worktrees
- project-scoped sandbox environments for managed linked worktrees

That changes the next priority.

The platform does not most urgently need another language implementation or more contract surface area. The next bottlenecks are:

- completing the remaining engineering-support workflow gaps around real project repositories
- increasing trust that derived applications are runnable, verifiable, and maintainable
- making optional platform modules more operationally composable, not just conceptually separated

## Recently Completed Foundations

- [x] `add-ai-app-scaffolding-and-module-assembly`
- [x] `standardize-app-assembly-spec-and-compatibility-suite`
- [x] `add-java-cli-assembly-implementation`
- [x] `standardize-generated-app-verification-contract`
- [x] `add-java-generated-app-verifier-implementation`
- [x] `define-derived-app-lifecycle-and-upgrade-contract`
- [x] `add-platform-release-delta-and-upgrade-advisory`
- [x] `add-derived-app-upgrade-execution-path`
- [x] `standardize-platform-release-history-and-version-lineage`
- [x] `define-ai-tool-orchestration-contract`
- [x] `decompose-optional-business-modules`
- [x] `consolidate-platform-tooling-on-java`
- [x] `standardize-ai-solution-input-model`
- [x] `introduce-structured-app-template-system`
- [x] `add-repository-owned-baseline-demo`
- [x] `project-worktree-management`
- [x] `project-merge-support`
- [x] `project-sandbox-environment`
- [x] `dynamic-form-component`
- [x] `migrate-admin-forms-to-dynamic-form`
- [x] `dynamic-report-component`
- [x] `add-derived-app-runtime-smoke`

## Roadmap Principles

- Prefer closing adjacent platform gaps over opening new product directions.
- Prefer repository-owned trust mechanisms over more descriptive documentation alone.
- Prefer project-scoped engineering workflows over generic standalone Git tooling.
- Prefer composable built-in modules over hidden baseline coupling.
- Prefer end-to-end runnable proof over purely structural validation.

## Priority Roadmap

### P0

#### 1. Dynamic Form Component

- Why next:
  The platform already has runnable admin pages and backend-backed management workflows, but it still lacks a reusable component for turning structured business descriptions plus table definitions into editable, savable forms.
- Scope direction:
  - generate frontend form structure from business description, table definition, and field metadata
  - support common input widgets, validation, default values, and edit or create modes
  - connect generated forms to backend-backed save flows instead of stopping at static UI generation
  - keep V1 focused on single-entity or single-table form workflows
- Main risk:
  Dynamic forms can expand too quickly into a full low-code designer unless field semantics, layout rules, and save boundaries stay narrow.
- Suggested change name:
  `dynamic-form-component`

### P1

#### 2. Dynamic Report Component

- Why next:
  Once the platform can generate structured data-entry experiences, the next adjacent reusable capability is reporting. AI-generated enterprise systems need a controlled way to turn business descriptions plus live data into platform-owned report views.
- Scope direction:
  - generate report views from report description and data definitions
  - support a narrow first version with summary cards, tables, and simple charts
  - define filtering, grouping, and aggregation boundaries explicitly
  - keep the first version inside platform-owned report patterns rather than turning it into a full BI product
- Main risk:
  Reporting scope can balloon into ad hoc analytics, formula builders, and multi-source modeling if the first version is not constrained.
- Suggested change name:
  `dynamic-report-component`

#### 3. Derived-App Runtime Smoke Validation

- Why next:
  The repository already validates contracts and generated-app structure well. The next trust gap is runtime proof: a derived app should be demonstrably buildable and smokable, not just structurally compatible.
- Scope direction:
  - add runtime-oriented fixtures for generated or demo-derived apps
  - verify backend, frontend, and `/service/*` behavior for derived applications
  - make failures actionable for both human contributors and AI agents
- Main risk:
  Runtime fixtures can become expensive or brittle if they are too broad.
- Suggested change name:
  `add-derived-app-runtime-smoke`

### P2

#### 5. Operational Module Assembly Completion

- Why later:
  The platform already describes optional modules well, but optionality should become more operationally real. A module should be removable or selectable without hidden baseline assumptions leaking through routes, navigation, dependencies, or verification flow.
- Scope direction:
  - harden module dependency declarations
  - verify route, page, and backend behavior when optional modules are omitted
  - reduce accidental coupling between project, ticket, and kanban areas
- Main risk:
  This touches both backend and frontend assumptions at once and can expose latent coupling.
- Suggested change name:
  `complete-optional-module-assembly`

#### 6. Platform Release And Upgrade Smoke Hardening

- Why later:
  Release history, upgrade targets, and advisory flows are already defined. The next maturity step is proving those flows against real repository-owned fixtures rather than mostly contract-level guarantees.
- Scope direction:
  - add runnable release-to-release upgrade fixtures
  - verify advisory and execution behavior against concrete versioned examples
  - strengthen confidence in repository-owned upgrade semantics
- Main risk:
  Release fixtures can become maintenance-heavy unless version scope stays small.
- Suggested change name:
  `harden-release-upgrade-smoke`

### P3

#### 7. Template Variant Expansion

- Why last:
  The structured template system now exists. It should expand only after merge, sandbox, derived-app runtime proof, and operational module assembly are stronger, otherwise new template variants will amplify shaky foundations.
- Scope direction:
  - add more explicit slot and template variants
  - improve controlled customization points for derived apps
  - keep ownership boundaries explicit between platform-managed zones and customization zones
- Main risk:
  Template systems can become magic-heavy if they evolve faster than runtime guarantees.
- Suggested change name:
  `expand-structured-template-variants`

## Recommended Order

1. [x] `dynamic-form-component`
2. [x] `dynamic-report-component`
3. [x] `add-derived-app-runtime-smoke`
4. [ ] `complete-optional-module-assembly`
5. [ ] `harden-release-upgrade-smoke`
6. [ ] `expand-structured-template-variants`

## Explicitly Not A Near-Term Priority

These directions are intentionally not near-term roadmap priorities:

- adding another implementation language for platform tooling
- building a direct end-user AI chat interface inside this repository
- expanding beyond internal enterprise management applications
- changing the V1 target shape away from monolithic applications
- introducing broad new external dependency stacks without a platform-boundary reason

## How To Use This Roadmap

- Treat this file as a candidate-priority list, not an automatic schedule.
- Re-check the repository reality before starting any item.
- Convert any selected direction into a spec-driven change before implementation.
- Update this file when repository reality changes enough that the ordering is no longer defensible.
