# Descriptor-Driven Business Module Generation

## Goal

Lift the platform from reusable interaction components plus assembly contracts to a stronger business-module generation story, where AI contributors can derive bounded management modules from repository-owned descriptors instead of stitching pages and workflows together manually.

## In Scope

- A bounded descriptor-driven path for generating management-module shapes from repository-owned descriptors
- Descriptor-driven report page generation that reuses existing dynamic report capabilities
- Descriptor-driven workflow page generation that reuses existing workflow capabilities
- AI-readable guidance that makes descriptor-driven generation discoverable and verifiable

## Out of Scope

- A general low-code or arbitrary CRUD generation system
- New runtime AI chat, prompt intake, or unconstrained requirement-analysis features
- Product-boundary expansion beyond enterprise internal management monolith applications

## Done Criteria

- The repository defines a bounded descriptor-driven path for generating at least one management-module shape from platform-owned contracts.
- Generated module output reuses the existing dynamic form, dynamic report, workflow, and template-boundary capabilities instead of creating parallel one-off patterns.
- Contributors can verify that descriptor-driven generation stays within the current V1 enterprise-management monolith boundary and does not become a general low-code platform.
- All planned changes in this milestone are archived.

## Planned Changes
- `descriptor-driven-management-module` - Declared: complete - Bounded descriptor-driven generation for at least one management-module shape
- `descriptor-driven-report-page-generation` - Declared: planned - Descriptor-driven generation path for report pages that reuse platform-owned report patterns
- `descriptor-driven-workflow-page-generation` - Declared: planned - Descriptor-driven generation path for workflow pages that reuse platform-owned workflow patterns

## Dependencies

- Depends on: project-scoped-derived-app-lifecycle (complete) and solution-input-to-assembly-planning (complete).
- This milestone must consume the concrete intermediate artifact defined by solution-input-to-assembly-planning rather than re-deriving mapping logic from prose.

## Risks

- Scope can expand too quickly into arbitrary CRUD or full low-code generation unless the first descriptor surface stays narrow around current platform-owned interaction patterns. V1 scope is explicitly limited to enterprise internal management monolith applications — this milestone does not expand that boundary.
- Generated output must continue to respect template ownership boundaries and module-selection constraints rather than bypassing the existing assembly model.

## Status

- Declared: proposed

## Notes

- This milestone is the remaining critical path between the completed solution-input planning work and V1 closure.

