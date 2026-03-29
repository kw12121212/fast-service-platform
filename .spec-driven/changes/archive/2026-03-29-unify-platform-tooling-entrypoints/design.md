# Design: unify-platform-tooling-entrypoints

## Approach

通过“统一 façade 脚本 + 兼容 wrapper 保留 + AI 文档统一收口”的方式，整理现有平台工具入口。

设计上分三层：

1. 新增一个 repository-owned 主入口，按子命令暴露 assembly、verification、lifecycle、advisory 和 upgrade。
2. 保留现有分散脚本，但让它们在文档和 contract 中退居兼容层或具体实现层。
3. 更新 quickstart、context、playbooks 和 machine-readable contracts，使新入口成为默认路径。

第一版只统一入口，不重写底层逻辑和实现分层。

## Key Decisions

- 统一入口应作为 façade，而不是新的一层业务逻辑。
  理由：当前底层逻辑已经分布在 `app-assembly-lib.mjs` 和现有 wrappers 中，这次重点是收敛入口，不是重构核心能力。
- 现有脚本继续保留。
  理由：仓库和生成应用已有大量文档、测试和 contract 指向这些脚本，直接删除会带来不必要破坏。
- machine-readable contracts 要优先暴露统一入口。
  理由：AI 和其他实现首先消费 contract，因此入口统一必须体现在 contract 层，而不只是 prose 文档。

## Alternatives Considered

- 继续保持现状，只在文档里整理。
  放弃原因：没有 repository-owned façade，入口分散问题不会真正消失。
- 直接删除旧脚本，只保留一个新工具。
  放弃原因：迁移成本过高，也会破坏当前 generated app 和文档兼容性。
- 顺手重构底层脚本库。
  放弃原因：会把这次 change 扩成实现重构，超出当前 scope。
