# Design: decompose-optional-business-modules

## Approach

The change will refine the platform's optional module model by breaking the current delivery-management business area into smaller observable capability units and then propagating those units through the repository's assembly assets.

The implementation will start from the machine-readable module registry, because that is the repository's current fact source for assembly behavior. The new module units will be defined there first, with explicit dependencies and observable wiring expectations. Then the related contracts, supported assembly profiles, and compatibility fixtures will be aligned with the refined boundaries.

The default runnable baseline will remain reproducible as a supported profile. The decomposition will focus on improving optional-module precision rather than changing the required core or removing the current baseline behavior.

## Key Decisions

- Decomposition should be centered on observable capability boundaries, not internal code organization.
  Rationale: the specs and contracts must describe what contributors can select and what behavior they should expect, not how files are internally arranged.

- The repository should preserve at least one profile that reproduces today's full runnable baseline.
  Rationale: the platform still needs a stable default assembly target while optional boundaries become more precise.

- The new module graph should only split where the repository can define clear dependency and validation rules.
  Rationale: over-splitting modules without stable assembly semantics would increase confusion rather than improve composability.

## Alternatives Considered

- Keep the current three optional delivery-management modules as-is.
  Rejected because the current granularity is still too coarse for a reusable long-term base library.

- Decompose modules directly from source-package structure.
  Rejected because code layout alone does not define stable assembly semantics.

- Wait until a future template-system change before refining module boundaries.
  Rejected because the module contract itself should be made clearer before further templating or higher-level input work builds on top of it.
