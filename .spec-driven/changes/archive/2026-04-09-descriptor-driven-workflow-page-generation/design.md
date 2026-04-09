# Design: descriptor-driven-workflow-page-generation

## Approach

Extend the existing management-module descriptor schema with an optional `workflow` section that maps 1:1 to the `WorkflowPanel` component's `WorkflowDescriptor` type. This follows the same pattern used for report (columns/sections) and form: the descriptor expresses what the component expects, and no new runtime code is needed because `WorkflowPanel` already handles all rendering.

### Schema evolution

1. Add a `workflowActionDescriptor` definition:
   ```
   workflowActionDescriptor:
     type: object
     required: [action, label]
     properties:
       action: enum [submit, approve, reject, reassign]
       label: string
       requiresAssignee: boolean (default false)
   ```

2. Add a `workflowDescriptor` definition:
   ```
   workflowDescriptor:
     type: object
     required: [componentId, entityName, stateLabel, assigneeLabel, commentLabel, commentPlaceholder, historyTitle, actions]
     properties:
       componentId: const "workflow-panel"
       entityName: string
       stateLabel: string
       assigneeLabel: string
       commentLabel: string
       commentPlaceholder: string
       historyTitle: string
       actions: array of workflowActionDescriptor
       backendWorkflowService:
         type: object
         required: [workflowServiceName, ddlReference]
         properties:
           workflowServiceName: string (e.g. "ticket_workflow_service")
           ddlReference: string (path to CREATE WORKFLOW DDL)
   ```

3. Make `workflow` optional on `managementModule`:
   ```
   managementModule.properties.workflow: $ref workflowDescriptor
   ```
   Do NOT add `workflow` to `managementModule.required` — it remains optional.

4. Evolve `boundaries.usesWorkflowGeneration` from `const: false` to `type: boolean`, with a conditional constraint:
   - When `managementModule.workflow` is present: `usesWorkflowGeneration` MUST be `true`
   - When `managementModule.workflow` is absent: `usesWorkflowGeneration` MUST be `false`

5. Add optional `workflowComponentId` to `generatedModuleInputs.frontend`:
   ```
   workflowComponentId: const "workflow-panel" (optional, required when workflow present)
   ```

### Contract and playbook updates

- Remove `"workflow-specific-page-generation"` from `excludedCapabilities` in `descriptor-driven-management-module-contract.json`
- Add `workflow-panel` to `platformOwnedInteractions` with its component path
- Add `workflowDescriptorCapabilities` section to the contract documenting the supported workflow surface
- Extend the playbook to explain when to include a workflow section and how its properties map to `WorkflowPanel` props

### New example descriptor

Add a third example (e.g. `leave-request.management-module.json`) that demonstrates a workflow-enabled management module: a leave request with submit/approve/reject actions, a form for request details, and a report listing submitted requests.

### Spec update

Update `core/descriptor-driven-management-module-generation.md` to replace the "excludes workflow generation" requirement with a "supports optional workflow generation" requirement that reuses the existing `WorkflowPanel` component.

## Key Decisions

1. **`workflow` is optional, not required** — Management modules without workflow remain valid. The workflow section is opt-in, matching the pattern where not every management module needs approval flows.

2. **Workflow descriptor maps 1:1 to WorkflowPanel's WorkflowDescriptor type** — No abstraction layer. The descriptor expresses exactly what `WorkflowPanel` expects: entity name, labels, actions. This keeps the generation path transparent and avoids introducing a separate workflow descriptor DSL.

3. **Action types stay bounded to the existing four** — submit, approve, reject, reassign. These are what `WorkflowActionType` supports. Custom action types are explicitly out of scope.

4. **Backend workflow service binding is declarative** — The descriptor names the `CREATE WORKFLOW` service and DDL reference but does not replace the DDL. This stays consistent with the descriptor's role as an upstream asset, not a runtime input.

5. **`usesWorkflowGeneration` becomes conditional, not removed** — The boundary constraint remains but now reflects whether workflow is present. This preserves the boundary-awareness design of the descriptor system.

6. **Schema-only extension at the descriptor level, no new frontend runtime code** — The `WorkflowPanel` component already handles all rendering. This change only unlocks the descriptor path to express workflow pages.

## Alternatives Considered

1. **Remove `usesWorkflowGeneration` boundary entirely** — Would lose the explicit boundary signalling that the descriptor system provides. Rejected; the conditional approach preserves boundary-awareness while enabling the capability.

2. **Create a separate workflow-page descriptor schema** — Would create a parallel descriptor type for workflow-only pages, separate from management-module descriptors. Rejected because the milestone scope is specifically about extending the management-module descriptor's capabilities, not about a standalone workflow-page path.

3. **Expand `WorkflowActionType` to support custom actions** — Would open the descriptor to arbitrary workflow actions, risking scope creep beyond V1 boundaries. Rejected; the existing four action types are sufficient for V1 enterprise management scenarios.

4. **Defer workflow generation to V2** — Would leave the descriptor-driven milestone incomplete and continue blocking V1 closure. Rejected; this is a bounded extension that reuses existing components, not a new feature.
