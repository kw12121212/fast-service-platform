# Questions: consolidate-platform-tooling-on-java

## Open

<!-- No open questions -->

## Resolved

- [x] Q: Does the consolidation target remove Node from the entire repository or only from the platform tooling role?
  Context: This determines whether the change affects the frontend technical baseline or only the repository-owned assembly / verification / lifecycle toolchain.
  A: Remove Node only from the platform tooling implementation role. Keep Node/bun for the frontend stack.
