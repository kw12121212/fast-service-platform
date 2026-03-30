# Design: drop-lealone-platform-dependency

## Approach

The migration will be treated as a repository-foundation realignment rather than a feature addition.
Implementation should update every repository-owned path that still assumes `Lealone-Platform` is the provider of runtime bootstrap, ORM JSON types, or source-install prerequisites.

The work falls into four coordinated slices:

1. Backend dependency and import migration
   Switch the backend from `com.lealone.plugins:lealone-boot` and legacy `com.lealone.plugins.*` imports to the current `Lealone` modules and package names used by upstream.

2. Generated-code and template migration
   Update committed generated executors and repository-owned generation templates so the repository no longer reintroduces stale `com.lealone.plugins.*` references after regeneration or app assembly.

3. Local dependency contract migration
   Remove `vendor/lealone-platform` from the required local source checkouts and make the install script and troubleshooting path work from `vendor/lealone` alone for the current baseline.

4. Documentation and AI-asset migration
   Update README, RTK, AI quickstart, machine-readable context, troubleshooting guidance, and baseline demo guidance so they all describe the same Lealone-only foundation and bootstrap expectations.

## Key Decisions

- Treat this as a compatibility migration, not a product-scope expansion.
  Upstream now includes additional AI-oriented capabilities, but the user explicitly chose not to add them to this repository's product boundary.

- Remove `vendor/lealone-platform` from the required local dependency contract instead of keeping a transitional dual-source requirement.
  The repository's current runnable path no longer needs `spring`, `python`, `javascript`, or `create-app`, and keeping the old requirement would preserve misleading setup guidance.

- Preserve the current backend programming model while changing the dependency source.
  The repository should keep its SQL-defined tables and services plus Java bootstrap structure because that model is already part of the current baseline and specs.

- Update repository-owned generated and demo assets in the same change.
  Leaving committed generated code or demo assets on old imports would make the repository internally inconsistent and would recreate the migration problem on the next regeneration cycle.

## Alternatives Considered

- Keep `Lealone-Platform` as an additional required vendor checkout during a transition period.
  Rejected because the current repository path does not materially depend on the remaining `Lealone-Platform` modules, and the extra required checkout would preserve unnecessary setup and documentation burden.

- Adopt upstream `agent` or AI app platform capabilities while updating dependencies.
  Rejected because it would expand scope beyond compatibility migration and would require new product-boundary decisions and specs.

- Leave the specs and documentation on `Lealone-Platform` while only patching Maven imports until the code compiles.
  Rejected because the local dependency contract, troubleshooting path, and AI-readable context are observable platform behavior and must remain aligned with the real runtime foundation.
