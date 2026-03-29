# Design: define-derived-app-lifecycle-and-upgrade-contract

## Approach

通过“新 contract + 补充现有 assembly/readiness 规范”的方式，把 derived app 的生命周期定义为仓库拥有的标准能力。

设计上分三层：

1. 新增一个独立的 lifecycle / upgrade 主 spec，定义可观察行为：
   - 派生应用如何声明平台来源
   - 平台如何声明兼容版本和升级输入
   - 贡献者如何通过仓库入口判断升级可行性
2. 修改现有 app assembly contract，使生成后的应用必须暴露生命周期相关元数据和升级入口指引。
3. 修改 AI repository readiness，使 AI 可以直接发现 lifecycle contract、schema、playbook 和验证入口。

第一版实现只要求“可描述、可检查、可准备升级”，不要求直接做全自动升级执行。

## Key Decisions

- 把 lifecycle / upgrade 提升成独立主 spec，而不是塞进现有 assembly contract。
  理由：这已经是“生成之后”的长期行为，不只是装配时输入输出。
- 第一版先做机器可读 contract 和仓库拥有的升级评估入口，不做自动 merge / rebase。
  理由：这样能先把平台版本、兼容矩阵和升级语义定稳，避免实现范围失控。
- 继续坚持语言无关标准。
  理由：生命周期和升级契约后续也应能被 `Node`、`Java` 或 AI 直接消费，而不是绑定某个脚本。

## Alternatives Considered

- 直接在现有 `app-assembly-contract` 里追加 upgrade 字段。
  放弃原因：会把“生成时契约”和“生成后生命周期”混在一起，边界不清。
- 直接做一个自动 upgrade CLI，再倒推规范。
  放弃原因：会让实现先于标准，重复之前希望避免的路径依赖。
- 暂时只写 `ROADMAP` 不开 spec。
  放弃原因：这会继续推迟平台最核心的长期演进能力，不利于后续收敛。
