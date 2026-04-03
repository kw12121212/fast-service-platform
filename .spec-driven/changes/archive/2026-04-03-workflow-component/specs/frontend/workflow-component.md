# Workflow Component

## ADDED Requirements

### Requirement: Platform Provides A Reusable Workflow Component
The system MUST provide a reusable frontend workflow component that accepts a declarative workflow descriptor and renders a complete single-record workflow panel without requiring the caller to hand-build status presentation, action controls, assignee metadata, required comment entry, and history display for each workflow-enabled page.

#### Scenario: A contributor renders a workflow panel from a descriptor
- GIVEN a contributor supplies a workflow descriptor and current workflow-instance data
- WHEN they render the workflow component
- THEN the component displays the described workflow status and metadata areas
- AND the contributor does not need to hand-build the workflow panel structure for that page

### Requirement: Workflow Descriptor Stays Within A Narrow V1 Boundary
The system MUST constrain the first workflow descriptor to single-record or single-process-instance workflows, with bounded action definitions including `submit`, `approve`, `reject`, and `reassign`, plus observable status presentation, rather than expanding into a general workflow engine or process designer.

#### Scenario: A contributor reviews the first workflow descriptor
- GIVEN a contributor inspects the workflow component contract
- WHEN they review the supported descriptor surface
- THEN they can identify support for a single-record workflow view with bounded actions including `submit`, `approve`, `reject`, and `reassign`
- AND they cannot infer support for drag-and-drop process design, arbitrary branching-rule authoring, or multi-record orchestration

### Requirement: Workflow Component Hands Off Action Execution To The Caller
The system MUST deliver the selected workflow action, required comment input, and any declared action payload to the caller through a callback rather than fetching backend data or executing the workflow transition itself.

#### Scenario: A contributor executes an allowed workflow action
- GIVEN a workflow component displays an allowed action for the current workflow instance
- WHEN the contributor triggers that action
- THEN the component invokes the caller-provided action callback with the selected action, entered comment, and any declared payload
- AND the component does not perform the backend transition by itself

### Requirement: Workflow Component Uses Existing Mutation Feedback Convention
The system MUST surface workflow action success or failure outcomes through the same frontend mutation feedback convention used by the existing admin write workflows.

#### Scenario: A workflow action is rejected by the backend
- GIVEN a workflow action is submitted through the caller-owned execution path
- WHEN the backend rejects the action and the caller passes the error result back to the component
- THEN the contributor sees a visible error outcome through the existing mutation feedback convention

### Requirement: Workflow Component Supports Assignee And History Visibility
The system MUST allow the workflow panel to display current responsibility metadata and a visible history list for the current workflow instance when that data is supplied by the caller.

#### Scenario: A contributor reviews workflow ownership and prior actions
- GIVEN the caller provides current assignee metadata and workflow history entries
- WHEN the contributor opens the workflow panel
- THEN they can identify who currently owns or reviews the workflow step
- AND they can review the visible sequence of prior workflow actions from the supplied history data

### Requirement: Current Runnable Frontend Includes A Real Workflow Example
The system MUST provide at least one real workflow example in the current runnable admin frontend so contributors can verify how the reusable workflow component is integrated with backend-backed workflow data and action execution.

#### Scenario: A contributor opens the current workflow example
- GIVEN the current frontend is running against the current backend core
- WHEN the contributor opens the workflow example page or example area
- THEN they can identify at least one workflow panel rendered through the reusable workflow component
- AND that panel reflects current backend-backed workflow data and action execution rather than static mock content
