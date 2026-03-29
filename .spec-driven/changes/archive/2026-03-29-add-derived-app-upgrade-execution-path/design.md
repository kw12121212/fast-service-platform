# Design: add-derived-app-upgrade-execution-path

## Approach

通过“upgrade plan contract + repository-owned execution path + post-upgrade verification guidance”的方式，把派生应用升级从说明层推进到受控执行层。

设计上分三层：

1. 修改 derived-app lifecycle / upgrade 主 spec，定义 upgrade plan、dry-run、auto-apply items 和 manual-intervention items 的可观察语义。
2. 修改 AI repository readiness，使 AI 可以发现 execution contract、playbook、entrypoint 和升级后验证路径。
3. 第一版实现一个 repository-owned execution path：
   - 支持 `dry-run`
   - 输出 machine-readable plan
   - 对有限范围的 repository-owned assets 执行自动更新
   - 显式报告需要人工介入的项

第一版仍然不做全自动 merge / rebase。

## Key Decisions

- 把 execution path 先定义成“受控计划 + 有限自动应用”，而不是全自动升级器。
  理由：这样可以把风险压在仓库拥有的可预测资产上，避免一开始就进入不可控冲突处理。
- `dry-run` 必须是第一类能力。
  理由：升级执行前，AI 和人工贡献者都需要先看到计划、风险和人工介入项。
- 升级后验证继续复用既有 verifier / evaluator 路径。
  理由：避免新 execution path 自己定义一套不一致的成功标准。

## Alternatives Considered

- 直接做完全自动升级。
  放弃原因：当前 contract 还不足以支撑对所有派生应用做黑盒自动修改，风险过高。
- 只输出 advisory，不提供 execution。
  放弃原因：这会继续把升级停留在“说明层”，不能真正缩短派生应用的维护路径。
- 只做一个人工文档模板，不做仓库入口。
  放弃原因：缺少 machine-readable plan 和统一执行入口，不利于 AI 消费。
