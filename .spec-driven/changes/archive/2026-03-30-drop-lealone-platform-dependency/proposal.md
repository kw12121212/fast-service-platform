# drop-lealone-platform-dependency

## What

Realign the repository from the legacy `Lealone-Platform` dependency layout to the current upstream `Lealone` layout.
The change will remove `vendor/lealone-platform` from the local dependency contract, migrate the backend and generated-code references to the current `Lealone` modules and package names, and update repository docs, AI context assets, and demo assets so they consistently describe a Lealone-only backend foundation.

## Why

The upstream dependency split has changed materially.
`Lealone` now provides the ORM, service, HTTP, and application bootstrap pieces that this repository currently consumes, while `Lealone-Platform` no longer provides those modules in the form this repository expects.
If this repository keeps describing `Lealone-Platform` as the required foundation, its backend dependency declarations, generated imports, installation script, troubleshooting guidance, and baseline demo instructions will drift further from the real upstream model and become harder to validate.

## Scope

In scope:
- Update backend runtime dependencies and imports from legacy `Lealone-Platform` locations to the current `Lealone` module and package layout.
- Update generated-source references and repository-owned generation templates that still emit `com.lealone.plugins.*` imports for the current backend path.
- Remove `vendor/lealone-platform` from the required local dependency bootstrap, installation script, AI context manifest, troubleshooting guidance, and related repository docs.
- Update the repository's documented backend foundation and dependency boundary from `Lealone-Platform` to `Lealone`.
- Update the committed baseline demo and any repository-owned assets that mirror the same backend dependency assumptions.

Out of scope:
- Adding or exposing upstream `agent` or AI app platform capabilities as product features in this repository.
- Expanding the repository's business-domain scope, platform boundary, or application shape.
- Introducing new external software libraries or alternative backend foundations.
- Refactoring unrelated backend or frontend features beyond what is required to complete the dependency migration.

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):
- The repository remains an AI-oriented platform for internal enterprise management applications rather than a built-in AI chat or agent product.
- The current backend bootstrap model remains project-local SQL table definitions, project-local SQL service definitions, and a Java application entrypoint.
- The repository continues to rely on `Lealone` and repository-internal dependencies rather than adding new external software libraries.
- The frontend, validation entrypoints, and `/service/*` integration expectations remain the standard verification path for the runnable baseline.
