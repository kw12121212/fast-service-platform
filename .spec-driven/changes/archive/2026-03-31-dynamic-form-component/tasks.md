# Tasks: dynamic-form-component

## Implementation

- [x] Define `FieldDescriptor` TypeScript interface (key, label, widget, required, defaultValue, validationRules, options)
- [x] Define `FormDescriptor` TypeScript interface (entityName, fields array)
- [x] Implement `DynamicForm` component — maps each `FieldDescriptor` to the appropriate shadcn/ui widget
- [x] Implement create mode — initialize field state from `FieldDescriptor.defaultValue`
- [x] Implement edit mode — accept `initialValues` prop and initialize field state from it
- [x] Implement frontend validation — enforce required fields and descriptor-declared rules before calling `onSubmit`
- [x] Implement `onSubmit` callback — pass collected field values to caller
- [x] Surface backend validation errors via the existing mutation feedback convention
- [x] Export `DynamicForm`, `FormDescriptor`, and `FieldDescriptor` from the platform component index

## Testing

- [x] Unit test: renders all 6 V1 widget types (text, number, date, select, textarea, boolean) from a descriptor
- [x] Unit test: create mode initializes each field to its `defaultValue`
- [x] Unit test: edit mode initializes each field from `initialValues`
- [x] Unit test: required field validation blocks `onSubmit` and shows an error
- [x] Unit test: `onSubmit` is called with the correct field values after valid submission
- [x] Unit test: backend error passed to the component is displayed via the mutation feedback convention
- [x] Frontend lint passes

## Verification

- [x] `DynamicForm` is accessible from the platform component index
- [x] Create form: renders from descriptor, validates, and calls `onSubmit` with correct values
- [x] Edit form: initializes from `initialValues` and calls `onSubmit` with updated values
- [x] Backend rejection: error message appears via existing feedback convention
- [x] Existing hand-written admin forms (user, project, ticket, kanban, role) are unaffected
