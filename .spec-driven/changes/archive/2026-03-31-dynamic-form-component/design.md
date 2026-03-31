# Design: dynamic-form-component

## Approach

Define `FormDescriptor` and `FieldDescriptor` as TypeScript interfaces that fully describe a single-entity form. `DynamicForm` maps each `FieldDescriptor` to the corresponding shadcn/ui + Tailwind widget. Frontend validation runs from descriptor-declared rules before `onSubmit` is invoked. Backend errors are surfaced through the same toast or inline feedback pattern used by existing write workflows. Edit mode initializes form state from the caller-supplied `initialValues` prop; create mode uses `defaultValue` from each field descriptor.

## Key Decisions

- **FormDescriptor is a frontend-side prop, not fetched from backend** — keeps V1 simple, avoids backend API changes, and lets the descriptor live next to the page that owns the save flow.
- **Edit mode initial data is the caller's responsibility (Method A)** — clear separation between data-fetching context and form rendering; callers already hold the fetched entity when opening an edit dialog.
- **V1 widget set is narrow (6 types)** — prevents scope creep toward a low-code designer; additional widgets can be added in a later change when a concrete need arises.
- **onSubmit follows existing data-access convention** — consistent with all other write workflows; no new save abstraction needed.

## Alternatives Considered

- **Backend schema introspection for descriptor generation**: would allow fully automatic form generation but requires a new backend API surface and couples the component to a specific schema format. Deferred past V1.
- **Component self-fetches initial data in edit mode**: conflates data-fetching with form rendering; callers already have the entity in context when triggering an edit flow, so this would duplicate a fetch unnecessarily.
