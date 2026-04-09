# Delta: core/descriptor-driven-management-module-generation

## MODIFIED Requirements

### Requirement: Descriptor-Driven Module Generation Supports Optional Workflow Page Generation
The descriptor-driven management-module path MUST support an optional workflow section that reuses the existing `WorkflowPanel` component for single-record workflow interactions, including status presentation, bounded action controls (submit, approve, reject, reassign), comment entry, assignee metadata, and workflow history display.

#### Scenario: A contributor includes a workflow section in a management-module descriptor
- GIVEN a contributor prepares a management-module descriptor that includes a `workflow` section
- WHEN they validate and process the descriptor
- THEN the generated module includes a workflow panel that reuses the repository-owned `WorkflowPanel` component
- AND the workflow descriptor's properties map directly to the `WorkflowPanel` component's `WorkflowDescriptor` type

#### Scenario: A contributor omits the workflow section
- GIVEN a contributor prepares a management-module descriptor without a `workflow` section
- WHEN they validate and process the descriptor
- THEN the generated module does not include a workflow panel
- AND the descriptor validates successfully with `boundaries.usesWorkflowGeneration` set to `false`

## REMOVED Requirements

### Requirement: First Descriptor-Driven Module Shape Excludes Workflow-Specific Generation
(REPLACED by "Descriptor-Driven Module Generation Supports Optional Workflow Page Generation" above — the first descriptor-driven path now supports optional workflow generation that reuses the existing WorkflowPanel component.)
