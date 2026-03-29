# decompose-optional-business-modules

## What

This change decomposes the current optional delivery-management business modules into smaller, more explicit optional module units with clearer dependency and assembly boundaries.

The repository will define a finer-grained module catalog for the existing optional business capability area so contributors can distinguish which project, ticket, and kanban capabilities may be selected independently and which must remain grouped by declared dependency.

The change will update the assembly-facing contracts, module registry, and compatibility expectations so derived applications can target more precise optional capability combinations without inferring hidden couplings from source code.

## Why

The current optional business modules are still relatively coarse. `software-project-management`, `ticket-management`, and `kanban-management` each bundle multiple behaviors and hide some practical coupling across routes, SQL assets, and workflows.

That is good enough for a first optional-module model, but it is not yet a strong long-term foundation for a reusable application platform. AI contributors still have to reason about large business packages instead of stable capability units.

This change moves the platform closer to a real composable module system: smaller optional business units, clearer dependency semantics, clearer assembly profiles, and fewer implicit assumptions during derivation, verification, and lifecycle work.

## Scope

In scope:

- Decompose the current optional delivery-management business area into smaller repository-defined optional module units.
- Define the new module boundaries, dependency declarations, and expected assembly behavior through the machine-readable module registry and the related platform specs.
- Update default and representative assembly profiles so contributors can see which optional capability combinations remain repository-supported.
- Update compatibility expectations so the supported observable combinations match the refined module boundaries.

Out of scope:

- Adding new product domains beyond the current project / ticket / kanban capability area.
- Replacing the required platform core or weakening the core admin baseline.
- Redesigning the AI tool orchestration model or the unified tooling façade.
- Introducing a new template system or a new implementation language.

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- The repository continues to support a default assembly profile that reproduces the current runnable baseline behavior.
- `admin-shell`, `user-management`, and `role-permission-management` remain the required platform core.
- The platform continues to treat delivery-management capabilities as optional built-in business modules rather than mandatory requirements for every derived application.
