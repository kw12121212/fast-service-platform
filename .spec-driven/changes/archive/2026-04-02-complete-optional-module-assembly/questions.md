# Questions: complete-optional-module-assembly

## Open

<!-- No open questions -->

## Resolved

- [x] Q: Should the generated frontend module-selection file be TypeScript or JSON?
  Context: Determines how the router and nav consume the config, and whether TypeScript type checking applies to the conditional logic.
  A: TypeScript. Keeps the config in the same type system as the rest of the frontend and enables exhaustiveness checking.
