# Questions: dynamic-form-component

## Open

<!-- No open questions -->

## Resolved

- [x] Q: In edit mode, should the component fetch initial data itself or accept it from the caller?
  Context: Determines whether DynamicForm has a data-fetching dependency or stays a pure rendering component.
  A: Caller's responsibility (Method A) — component accepts `initialValues` prop; no internal data fetching.
