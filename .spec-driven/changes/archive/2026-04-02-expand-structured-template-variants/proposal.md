# expand-structured-template-variants

## What

Expand the default derived-app template classification map to cover all module-contributed files, add the `module-selection` customization zone, and introduce two missing module-fragment classification entries for `admin-shell` — so that every module declared in the module registry has at least one classified output in the template map.

## Why

The structured app template system contract and schema exist, but the default classification map (`docs/ai/template-classifications/default-derived-app-template-map.json`) covers only 2 of the 7 module-registry modules (`project-management` only, with 2 entries). The remaining five modules (`admin-shell`, `user-management`, `role-permission-management`, `kanban-management`, `ticket-management`) contribute real frontend pages and backend service implementations that are currently unclassified.

An AI agent assembling, upgrading, or customizing a derived application cannot determine upgrade-safe ownership for these files without classified entries. The template system's value — safe upgrade boundaries, customization guidance, ownership reasoning — depends on complete coverage.

## Scope

**In scope:**
- Add module-fragment entries to `default-derived-app-template-map.json` for all unclassified module-contributed frontend pages and backend service implementations:
  - `admin-shell`: `admin-shell.tsx`, `dashboard-page.tsx`
  - `user-management`: `users-page.tsx`, `UserServiceImpl.java`
  - `role-permission-management`: `role-permissions-page.tsx`, `AccessControlServiceImpl.java`
  - `kanban-management`: `kanban-page.tsx`, `KanbanServiceImpl.java`
  - `ticket-management`: `tickets-page.tsx`, `TicketServiceImpl.java`
- Add a `customization-zone` entry for `frontend/src/app/module-selection.ts` (derived apps set their active module profile here)
- Update the spec `core/structured-app-template-system.md` to require complete module fragment coverage
- Validate the expanded map against the existing JSON schema

**Out of scope:**
- Adding new modules or new module registry entries
- Changing the template contract schema version or ownership-mode semantics
- Classifying internal platform infrastructure files (bootstrap, common utilities, engineering-support ports)
- Adding variant classification maps per named assembly profile
- Modifying actual module implementations

## Unchanged Behavior

- The template contract schema (`structured-app-template-contract.schema.json`, `derived-app-template-map.schema.json`) must not change
- Existing 15 classification map entries must not change their `unitType`, `ownership`, or `upgradeBehavior` values
- The slot definitions in `structured-app-template-contract.json` must not change
- All existing module registry entries, assembly profiles, and module-selection behavior must be unaffected
