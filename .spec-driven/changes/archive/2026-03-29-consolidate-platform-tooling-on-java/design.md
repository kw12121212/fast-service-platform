# Design: consolidate-platform-tooling-on-java

## Approach

The change will migrate repository-owned platform workflows from `Node` implementation paths to Java implementation paths while preserving the current observable command surfaces as much as possible.

The work will start from the unified facade and contract assets, because those files define what contributors and AI agents are supposed to invoke. From there, each platform workflow category will be traced to its current implementation path and moved behind a Java-owned implementation. Shell wrappers may remain, but they should become thin forwarding layers to Java rather than owners of workflow logic.

The repository will keep `Node/bun` only where it is part of the frontend baseline. Platform-tooling contracts, generated-app guidance, and AI orchestration assets will be updated so they clearly distinguish frontend runtime needs from platform tooling implementation needs.

## Key Decisions

- Keep the frontend `Node/bun` baseline and only remove `Node` from the platform tooling role.
  Rationale: the user wants tooling consolidation, not a frontend stack rewrite.

- Preserve the unified facade and wrapper entrypoints while changing their underlying runtime.
  Rationale: the observable tooling surface is already part of the repository contract and should not be broken unnecessarily.

- Treat Java as the sole repository-owned implementation runtime for platform workflows after the change.
  Rationale: a half-migrated dual-runtime toolchain would preserve the same ambiguity this change is trying to remove.

- Keep contracts language-neutral even while the repository standardizes on Java.
  Rationale: the normative behavior should remain independent from one runtime's internal structure even if the repository only ships one implementation path.

## Alternatives Considered

- Leave the current mixed `Node + Java + shell` platform tooling model in place.
  Rejected because it keeps the AI orchestration story fragmented and leaves the platform boundary unclear.

- Remove `Node` from the entire repository, including the frontend.
  Rejected because that would require replacing the frontend technical baseline and is far outside the intended scope.

- Keep `Node` as a fallback implementation for platform workflows after Java becomes primary.
  Rejected because it would preserve a split ownership model and weaken the consolidation objective.
