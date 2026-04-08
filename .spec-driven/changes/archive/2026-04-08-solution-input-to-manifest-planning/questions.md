# Questions: solution-input-to-manifest-planning

## Open

<!-- No open questions -->

## Resolved

- [x] Q: 这次 proposal 里的 planning 要落成什么形式？
  Context: 这决定 proposal 是只补文档/规则，还是要新增 machine-readable planning 接口。
  A: 采用 machine-readable planning artifact，并通过 playbook 暴露这条路径，但不替代 `app-manifest`。

- [x] Q: 这次 change 是否同时纳入模块推荐逻辑？
  Context: 这决定 proposal 是否把 deterministic planning 和 recommendation heuristics 一起实现。
  A: 不纳入。当前 change 只定义 planning 边界和输出；模块推荐留给后续 `solution-input-module-recommendation`。
