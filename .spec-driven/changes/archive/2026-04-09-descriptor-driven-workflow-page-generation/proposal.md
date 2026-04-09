# descriptor-driven-workflow-page-generation

## What

Extend the descriptor-driven management-module path to support optional workflow page generation alongside the existing report and form views. When a management-module descriptor includes a `workflow` section, the generated module will reuse the existing `WorkflowPanel` component for single-record workflow interactions — including status presentation, bounded action controls (submit, approve, reject, reassign), comment entry, assignee metadata, and workflow history display.

## Why

The descriptor-driven-business-module-generation milestone has already completed two planned changes:

- `descriptor-driven-management-module` — established the bounded descriptor path with report (columns) and form
- `descriptor-driven-report-page-generation` — expanded report to multi-section dashboards (sections)

Both explicitly excluded workflow-specific page generation. The milestone's remaining planned change is exactly this: adding descriptor-driven workflow page generation that reuses the platform's existing `WorkflowPanel` component.

The `WorkflowPanel` component already implements the full V1 workflow interaction surface — it accepts a `WorkflowDescriptor` (labels, actions) and a `WorkflowInstance` (state, assignee, history), and it delegates action execution to the caller via an `onAction` callback. The backend already uses `CREATE WORKFLOW` DDL for workflow service definitions. What is missing is the descriptor bridge: expressing a workflow page shape in the management-module descriptor so that descriptor-driven generation can produce workflow-enabled management modules without hand-building the workflow panel integration.

This is the last planned change in the descriptor-driven milestone, and completing it closes the milestone, unblocking `platform-v1-integration-and-closure`.

## Scope

**In scope:**

- Add a `workflowDescriptor` definition to `descriptor-driven-management-module.schema.json` that maps to the existing `WorkflowPanel` component's `WorkflowDescriptor` type
- Make `workflow` an optional property of `managementModule` — descriptors without workflow continue to work unchanged
- Evolve the `boundaries.usesWorkflowGeneration` constraint from `const: false` to a conditional: `true` when `workflow` is present, `false` when absent
- Add `workflowComponentId` as an optional property in `generatedModuleInputs.frontend`
- Add a backend workflow service binding in the workflow descriptor (service name, DDL reference)
- Remove `workflow-specific-page-generation` from the contract's `excludedCapabilities`
- Add `workflow-panel` to the contract's `platformOwnedInteractions`
- Update the AI playbook to cover workflow descriptor usage
- Add a third repository-owned example descriptor that demonstrates a workflow-enabled management module
- Add validation coverage for workflow-enabled descriptors
- Update the main spec `descriptor-driven-management-module-generation.md` to relax the "excludes workflow generation" requirement

**Out of scope:**

- Changes to the `WorkflowPanel` component itself (already implements the full V1 workflow surface)
- Changes to the dynamic-form or dynamic-report components
- Multi-record workflow orchestration or arbitrary branching
- Backend `CREATE WORKFLOW` DDL changes (the existing DDL syntax is unchanged)
- A general workflow engine or process designer
- Expanding the descriptor to support custom workflow action types beyond submit, approve, reject, reassign

## Unchanged Behavior

- Existing descriptors without `workflow` continue to validate and generate correctly
- The `WorkflowPanel` component's rendering behavior does not change
- The management-module descriptor's report, form, manifestPreparation, and templateBindings sections remain valid as-is for non-workflow descriptors
- The AI playbook's overall sequence (plan → optionally review recommendation → prepare descriptor → produce manifest → assembly) does not change
- `standaloneManifestRequired`, `extendsClosedModuleRegistry`, and `descriptorIsAssemblyRuntimeInput` boundary constraints remain enforced
