# consolidate-platform-tooling-on-java

## What

This change consolidates the repository-owned platform tooling on the Java toolchain and removes `Node` from the platform tooling implementation role.

The repository will keep `Node/bun` as the frontend runtime and build baseline, but repository-owned assembly, generated-app verification, release advisory, upgrade target selection, upgrade evaluation, and upgrade execution will all be implemented and exposed through Java-based tooling paths.

The unified tooling facade, AI context, generated-app guidance, and validation assets will be updated so contributors and AI agents treat Java as the repository-owned tooling runtime instead of depending on `Node` scripts for platform workflows.

## Why

The platform currently has a split toolchain identity. The frontend legitimately depends on `Node/bun`, but the repository-owned platform workflows are still spread across `Node`, `Java`, and shell wrappers. That weakens the platform boundary and makes AI usage harder to teach consistently.

The project direction is now clear: `Node` should remain only where the frontend stack requires it. Platform assembly, verifier, lifecycle, advisory, and upgrade workflows should converge on one repository-owned implementation runtime. Java is the better fit because the backend baseline is already Java-centric and the repository now has multiple Java-compatible tooling paths.

This change reduces toolchain sprawl, clarifies the platform boundary, and makes the AI tool-orchestration contract simpler and more stable.

## Scope

In scope:

- Consolidate repository-owned platform tooling workflows on Java, including assembly, generated-app verification, release advisory, upgrade target selection, upgrade evaluation, and upgrade execution.
- Update the unified tooling facade and compatible wrappers so they invoke Java-owned implementations for platform workflows.
- Update machine-readable contracts, AI context, generated-app guidance, and validation assets so they no longer describe `Node` as a repository-owned platform tooling implementation.
- Preserve compatibility of the observable workflow surfaces exposed through `scripts/platform-tool.sh` and the generated application guidance where feasible.

Out of scope:

- Replacing the frontend `Node/bun` baseline or changing the React/Vite frontend stack.
- Removing `Node` from frontend build, lint, or runtime workflows.
- Introducing a new implementation language beyond the current Java toolchain.
- Redesigning the module model, lifecycle contracts, or release-lineage semantics beyond what is required to move the implementation runtime from `Node` to Java.

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- The repository continues to use `Node/bun` for the frontend workspace and frontend validation workflows.
- The unified tooling facade remains the default repository-owned invocation surface for platform workflows.
- App assembly, generated-app verification, lifecycle, advisory, and upgrade contracts remain language-neutral observable contracts rather than Java-internal implementation descriptions.
