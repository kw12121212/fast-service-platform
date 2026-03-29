# Design: expand-app-assembly-compatibility-fixtures

## Approach

通过“新增 machine-readable fixtures + 扩展 compatibility suite 元数据 + 补测试断言”的方式，提高标准边界覆盖率。

设计上分三层：

1. 修改 compatibility-suite 主 spec，要求 fixture coverage 不只包含最小 valid / invalid 输入，还要覆盖更多代表性的模块组合和相邻 contract 输入边界。
2. 扩展 `docs/ai/compatibility/app-assembly-suite.json` 与 fixture 文件集，把新增正反例纳入 repository-owned compatibility target。
3. 扩展 `scripts/app-assembly.test.mjs` 和相关验证逻辑，确保 `Node` / `Java` 两条兼容实现都要通过这些新增样例。

第一版仍然围绕 observable behavior，不会把 suite 扩成实现细节断言。

## Key Decisions

- fixture 扩容优先覆盖“代表性边界”，而不是穷举所有组合。
  理由：当前目标是提高标准可信度，而不是制造难以维护的组合爆炸。
- 新增样例继续以 machine-readable manifest / contract input 为中心。
  理由：要保证 AI 和不同语言实现仍能消费同一套事实来源。
- compatibility suite 只断言 observable contract。
  理由：避免把 Node/Java 某个实现内部结构误当成标准。

## Alternatives Considered

- 先做统一 tooling entrypoints。
  放弃原因：使用体验提升有价值，但当前更缺的是标准边界覆盖。
- 直接做 AI direct assembly path。
  放弃原因：在 fixture coverage 还偏薄时，先上 AI direct path 会放大不稳定性。
- 通过随机组合自动生成大量 fixtures。
  放弃原因：短期内维护成本高，也不利于清晰表达“为什么这个样例重要”。
