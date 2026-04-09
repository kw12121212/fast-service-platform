# Tasks: descriptor-driven-workflow-page-generation

## Implementation

- [x] Add `workflowActionDescriptor` and `workflowDescriptor` definitions to `docs/ai/schemas/descriptor-driven-management-module.schema.json`
- [x] Add optional `workflow` property (ref `workflowDescriptor`) to `managementModule` in the schema
- [x] Evolve `boundaries.usesWorkflowGeneration` from `const: false` to conditional: `true` when `managementModule.workflow` present, `false` when absent
- [x] Add optional `workflowComponentId` to `generatedModuleInputs.frontend` in the schema (required when workflow present)
- [x] Validate the existing `department-directory.management-module.json` and `department-overview.management-module.json` still pass the updated schema unchanged
- [x] Create a third example descriptor `docs/ai/management-modules/leave-request.management-module.json` with workflow, report (columns), and form sections
- [x] Validate the new example descriptor against the updated schema
- [x] Update `docs/ai/descriptor-driven-management-module-contract.json`: remove `workflow-specific-page-generation` from `excludedCapabilities`, add `workflow-panel` to `platformOwnedInteractions`, add `workflowDescriptorCapabilities` section
- [x] Update `docs/ai/playbooks/prepare-descriptor-driven-management-module.md` to cover workflow descriptor usage including when to include workflow and how properties map to WorkflowPanel
- [x] Update `.spec-driven/specs/core/descriptor-driven-management-module-generation.md` to replace the "excludes workflow generation" requirement with a "supports optional workflow generation" requirement
- [x] Update `.spec-driven/specs/platform/built-in-components.md` to document that workflow-panel is available for descriptor-driven generation alongside dynamic-form and dynamic-report

## Testing

- [x] Run schema validation against all three example descriptors: `ajv validate -s docs/ai/schemas/descriptor-driven-management-module.schema.json -d "docs/ai/management-modules/*.management-module.json"`
- [x] Write and run a unit test that validates: (a) a workflow-enabled descriptor passes, (b) a descriptor with workflow but `usesWorkflowGeneration: false` fails, (c) a descriptor without workflow but `usesWorkflowGeneration: true` fails, (d) existing descriptors without workflow still pass

## Verification

- [x] Verify all three example descriptors pass schema validation without errors
- [x] Verify the existing department-directory and department-overview descriptors are unchanged and still validate
- [x] Verify the new leave-request descriptor includes workflow, report, and form sections
- [x] Verify the contract no longer lists workflow-specific-page-generation as excluded
- [x] Verify the playbook references the workflow descriptor path
- [x] Run `/spec-driven-verify descriptor-driven-workflow-page-generation` to confirm artifact completeness
