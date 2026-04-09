# descriptor-driven-report-page-generation

## What

Expand the descriptor-driven management-module path so that the `report` section of a management-module descriptor can express the full range of dynamic-report section types — summary cards, tables, bar charts, line charts, and pie charts — instead of only a flat column list. Add a second repository-owned example descriptor that demonstrates multi-section report page generation, and update the contract, schema, and playbook accordingly.

## Why

The first descriptor-driven management module (`descriptor-driven-management-module`, archived) established a bounded path for generating management modules with one report view and one form view. However, the report descriptor schema (`reportDescriptor` in `descriptor-driven-management-module.schema.json`) only supports a `columns` array, which maps to a single table section in the dynamic-report component. The dynamic-report component already supports summary-cards, tables, bar/line/pie charts — but the descriptor schema cannot express these richer configurations.

This gap means descriptor-driven generation can only produce flat table views, even though the platform's dynamic-report component is capable of multi-section dashboards with mixed visualization types. Closing this gap is the natural next step in the descriptor-driven-business-module-generation milestone before tackling workflow page generation.

This change is on the critical path to V1 closure: the descriptor-driven-business-module-generation milestone blocks platform-v1-integration-and-closure, which in turn blocks all V2 work.

## Scope

**In scope:**

- Evolve the `reportDescriptor` definition in `descriptor-driven-management-module.schema.json` to support an ordered `sections` array with section types matching dynamic-report capabilities: summary-cards, table, bar chart, line chart, pie chart
- Keep backward compatibility: the existing `columns`-based report descriptors remain valid; `sections` is an alternative structure that supersedes `columns` when present
- Add a second repository-owned example descriptor that demonstrates a multi-section report page (e.g. a department-overview report with summary cards + table + chart)
- Update `descriptor-driven-management-module-contract.json` to reflect the expanded report descriptor surface
- Update the AI playbook (`prepare-descriptor-driven-management-module.md`) to cover the sections-based report descriptor
- Update validation coverage to verify that section-based report descriptors pass schema validation

**Out of scope:**

- Workflow-specific page generation (separate planned change: `descriptor-driven-workflow-page-generation`)
- Changes to the dynamic-report component itself (already supports all section types)
- Changes to the dynamic-form component or form descriptor
- New frontend rendering code (the dynamic-report component already renders all section types)
- Arbitrary low-code or unconstrained report-builder DSL
- Backend query or aggregation changes (report data aggregation remains caller-owned)

## Unchanged Behavior

- Existing `columns`-based descriptors (like `department-directory.management-module.json`) must continue to validate and generate correctly
- The dynamic-report component's rendering behavior does not change
- The management-module descriptor's form, templateBindings, manifestPreparation, and boundaries sections are not affected
- The `boundaries.usesWorkflowGeneration` constraint remains `false`
- The AI playbook's overall sequence (plan → optionally review recommendation → prepare descriptor → produce manifest → assembly) does not change
