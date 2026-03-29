# Design: add-platform-release-delta-and-upgrade-advisory

## Approach

通过“release metadata 扩展 + advisory contract + repository-owned advisory 入口”的方式，把平台版本差异说明纳入仓库标准能力。

设计上分三层：

1. 修改 derived-app lifecycle / upgrade 主 spec，要求平台发布资产不仅定义兼容输入，还要定义 release delta 和 advisory 语义。
2. 修改 AI repository readiness，使 AI 可以发现 advisory contract、release delta 资产、playbook 和入口。
3. 第一版实现一个 repository-owned advisory 入口，读取当前平台 release metadata 和 advisory 资产，输出影响模块、风险类型和建议动作。

第一版只负责“说明和建议”，不负责真正执行升级。

## Key Decisions

- 把 advisory 定位成升级决策支持层，而不是升级执行层。
  理由：当前最缺的是“知道变了什么、该先检查什么”，而不是直接自动改代码。
- advisory 资产必须是机器可读的。
  理由：AI 和不同语言实现都需要消费同一套发布差异说明，而不是只读 prose。
- repository-owned advisory 入口继续保持语言无关 contract 优先。
  理由：避免再次把标准绑定到某个脚本内部结构。

## Alternatives Considered

- 直接跳到自动 upgrade execution path。
  放弃原因：没有 release delta / advisory 基础，升级执行会缺少稳定的决策输入。
- 只在 `platform-release.json` 里增加几行 prose 字段。
  放弃原因：不够结构化，不利于 AI 和多实现消费。
- 只写文档，不增加仓库入口。
  放弃原因：会让 advisory 停留在说明层，缺少可执行验证和统一入口。
