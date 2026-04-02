# Design: complete-optional-module-assembly

## Approach

### Generated TypeScript Module-Selection Config

During app assembly, the tooling emits a `module-selection.ts` file into the generated frontend source tree. This file exports a typed constant that names which optional modules are active for the assembled app. The frontend router and navigation import this constant and conditionally include module-specific entries at build time.

Example shape:
```ts
export const moduleSelection = {
  project: true,
  ticket: true,
  kanban: false,
} as const;
```

The baseline platform source uses a default `module-selection.ts` that enables all three modules (matching `baseline-v1`). Assembly for any other profile overwrites this file with the appropriate boolean values.

### Frontend Router

`router.tsx` replaces its three unconditional module route imports with a conditional block:
- If `moduleSelection.project` is true, include the projects route
- If `moduleSelection.ticket` is true, include the tickets route
- If `moduleSelection.kanban` is true, include the kanban route

Since the check happens at module import time (static constant), dead-code elimination in the Vite build can strip the unused route components entirely from the output bundle.

### Frontend Navigation

`navigation.ts` replaces the three unconditional delivery-management nav items with a filter driven by `moduleSelection`. The nav array is constructed by starting from a base set and spreading module-gated items only when their flag is true.

### Backend SQL Init

`tables.sql` is split into a core section (always applied) and per-module sections. The assembly tooling emits only the relevant sections based on the selected profile. For the baseline platform, all sections remain in the default file.

`services.sql` follows the same pattern: a core section plus per-module service blocks.

### Demo Data

`DemoDataSupport.java` is updated to accept the active module set (or to read a properties/config constant) and skip the instantiation of module-specific service impls when those modules are not selected.

### Compatibility Fixture

A new `core-admin` fixture assembly input is added to the compatibility suite. Its expected output is verified to contain:
- No `/projects`, `/tickets`, or `/kanban` routes
- No project, ticket, or kanban nav items
- No `software_project`, `kanban_board`, or `ticket` table creation statements
- No `project_service`, `kanban_service`, or `ticket_service` service registrations

## Key Decisions

- **TypeScript config over JSON:** The module-selection file is `.ts` so it integrates cleanly with the existing Vite + React build and enables TypeScript-enforced exhaustiveness in conditionals. JSON would require an extra import/parse step.
- **Assembly-time, not runtime:** Module flags are constants, not environment variables or API calls. This keeps the build output clean and avoids runtime overhead or conditional hydration issues.
- **Baseline default config enables all modules:** The platform source ships with a `module-selection.ts` that mirrors `baseline-v1`. This means the running baseline is unaffected and no behavior changes until assembly overwrites the file.
- **SQL split by module, not by feature flag:** Conditional SQL at init time would require runtime config plumbing in Java. Splitting the SQL file and emitting only the relevant sections from the assembler is simpler and keeps the backend stateless about module state.

## Alternatives Considered

- **Template partials approach:** Each module contributes its own route fragment, nav fragment, and SQL fragment; assembly stitches them. More extensible long-term but more assembly tooling complexity for V1. Deferred to a later template variant expansion.
- **Runtime feature flags via env vars:** Would allow toggling modules without regenerating the app. Rejected because it adds runtime complexity, makes bundles larger, and is inconsistent with the assembly-time ownership model.
- **Separate SQL files per module always committed:** Simpler for contributors but requires the backend bootstrap logic to conditionally include files, which re-introduces runtime coupling. The emitted-at-assembly-time approach keeps the backend init dumb.
