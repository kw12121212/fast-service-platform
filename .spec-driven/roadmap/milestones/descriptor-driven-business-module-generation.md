# Descriptor-Driven Business Module Generation

## Goal

Lift the platform from reusable interaction components plus assembly contracts to a stronger business-module generation story, where AI contributors can derive bounded management modules from repository-owned descriptors instead of stitching pages and workflows together manually.

## Done Criteria

- The repository defines a bounded descriptor-driven path for generating at least one management-module shape from platform-owned contracts.
- Generated module output reuses the existing dynamic form, dynamic report, workflow, and template-boundary capabilities instead of creating parallel one-off patterns.
- Contributors can verify that descriptor-driven generation stays within the current V1 enterprise-management monolith boundary and does not become a general low-code platform.
- All planned changes in this milestone are archived.

## Planned Changes

- descriptor-driven-management-module
- descriptor-driven-report-page-generation
- descriptor-driven-workflow-page-generation

## Dependencies / Risks

- This milestone should follow stronger solution-input and project-scoped lifecycle guidance so generated modules have a clear upstream planning path and downstream validation path.
- Scope can expand too quickly into arbitrary CRUD or full low-code generation unless the first descriptor surface stays narrow around current platform-owned interaction patterns.
- Generated output must continue to respect template ownership boundaries and module-selection constraints rather than bypassing the existing assembly model.

## Status

- Declared: proposed
