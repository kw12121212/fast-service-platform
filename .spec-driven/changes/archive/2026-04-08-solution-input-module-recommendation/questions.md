# Questions: solution-input-module-recommendation

## Open

<!-- No open questions -->

## Resolved

- [x] Q: `solution-input-module-recommendation` 应该产出独立的 machine-readable recommendation artifact，还是作为现有 `solution-to-manifest plan` 的可选区块？
  Context: 这决定 recommendation 的承载形式，以及 planning 与 recommendation 的边界是否清晰。
  A: 采用独立 artifact；不把 recommendation 塞回已有 planning artifact。

- [x] Q: 这个 change 是否只定义 contract/schema/example/playbook，还是同时要求 repository-owned CLI/tooling entrypoint？
  Context: 这决定 proposal 是收敛在 planning-facing AI assets，还是扩展到执行型 tooling。
  A: 本次只定义 contract/schema/example/playbook，不把 CLI 作为必选范围。
