# complete-optional-module-assembly

## What

Make the three optional delivery-management modules (project, ticket, kanban) genuinely selectable at assembly time. A derived app generated from a partial-module profile must contain no routes, nav links, tables, services, or demo data for omitted modules.

## Why

The platform's module registry defines multiple assembly profiles (core-admin, project-admin, project-kanban, baseline-v1), but the running baseline ignores them entirely. Every route, nav item, SQL table, and backend service is unconditionally hardcoded. The spec says optional modules may be omitted during assembly, but the code makes that impossible without manual editing. Partial-module assembly is not verifiably trustworthy until the output invariant is proven by a fixture.

## Scope

**In scope:**
- Frontend router: replace hardcoded delivery-management route registrations with entries driven by a generated TypeScript module-selection config
- Frontend navigation: replace hardcoded delivery-management nav items with entries conditionally included from the same generated config
- Backend SQL init: split or conditionally gate tables.sql and services.sql so omitted-module tables and services are not created when a partial profile is selected
- Demo data: make DemoDataSupport skip module-specific initializers for omitted modules
- Assembly tooling: emit the TypeScript module-selection config during app generation, based on the selected profile from the app manifest
- Compatibility fixtures: add a core-admin profile assembly fixture proving that omitted-module routes, nav items, tables, and services are absent from the generated output
- Verification: extend the compatibility suite to cover partial-module assembly output invariants

**Out of scope:**
- Runtime module toggling (modules remain fixed at assembly time)
- New module types beyond project, ticket, and kanban
- Changing the module registry format or profile definitions
- Adding new assembly profiles beyond what is already defined in the registry

## Unchanged Behavior

- The default baseline profile (baseline-v1) continues to include all three delivery-management modules
- All current routes, nav items, tables, and services remain present when the full profile is selected
- The module registry format and profile definitions are not changed
- Existing full-module assembly output is unaffected
- No runtime module toggling is introduced
