# Tasks: add-repository-owned-baseline-demo

## Implementation

- [x] Define the repository-owned baseline demo behavior and dedicated `demo/` directory expectations in delta specs.
- [x] Add a committed baseline demo derived app under the repository-owned `demo/` area using the current platform assembly path instead of a hand-built parallel sample.
- [x] Add a human-readable `GUIDE` that explains how the demo is created from this repository, how to regenerate it, how to run it, and how to validate it before presentation.
- [x] Expose the demo entrypoint from repository documentation so a contributor can discover the demo and its guide without inspecting source code manually.
- [x] Add repository-owned validation guidance or commands that confirm the demo remains runnable, demonstrable, and aligned with the platform baseline.

## Testing

- [x] `./scripts/platform-tool.sh generated-app verify <demo-dir>` passes for the committed baseline demo
- [x] Demo-specific backend and frontend validation commands documented in the guide pass
- [x] Repository baseline validation still passes after introducing the committed demo artifact

## Verification

- [x] `node /home/code/.agents/skills/spec-driven-brainstorm/scripts/spec-driven.js verify add-repository-owned-baseline-demo` passes
- [x] Confirm the committed demo remains traceable to repository-owned manifest-driven assembly instead of an undocumented manual fork
