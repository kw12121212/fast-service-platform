# dynamic-form-component

## What

Introduce a platform-level reusable frontend `DynamicForm` component that accepts a declarative `FormDescriptor` and renders a complete, backend-connected form for single-entity create and edit workflows.

## Why

Every admin entity (user, project, ticket, kanban, role) currently has a hand-written form. When AI generates a new business entity, it must write the full form UI from scratch — duplicating widget selection, validation wiring, and mutation feedback handling every time. A platform-owned `DynamicForm` component reduces that to providing a descriptor, making AI-generated enterprise management forms faster to produce, visually consistent, and maintainable from a single location.

## Scope

**In scope:**
- Frontend `DynamicForm` React component accepting a `FormDescriptor` prop
- `FormDescriptor` carries: entity name, ordered field list; each field specifies key, label, widget type, required flag, default value, validation rules, and (for select fields) static enum options
- V1 widget set: `text`, `number`, `date`, `select` (static enum options), `textarea`, `boolean` (switch)
- Create mode: fields initialize from descriptor defaults
- Edit mode: caller provides `initialValues`; component does not fetch data itself
- `onSubmit` callback hands field values to the caller's data-access save flow
- Backend validation errors displayed via the existing mutation feedback convention
- `DynamicForm` registered as a reusable platform component

**Out of scope (V1):**
- Nested or relational fields (e.g. selecting a related entity from another table)
- Dynamic layout, drag-and-drop arrangement, multi-step or grouped forms
- Backend schema auto-inference (descriptor is purely frontend-side)
- Low-code designer UI

## Unchanged Behavior

- Existing hand-written admin forms (user, project, ticket, kanban, role) continue to work without modification
- Existing frontend data-access conventions are unchanged
- Existing mutation feedback behavior is unchanged
- No backend API changes are introduced
