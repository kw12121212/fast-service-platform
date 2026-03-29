# unify-platform-tooling-entrypoints

## What

为平台现有的 assembly、verification、lifecycle、advisory 和 upgrade 相关脚本增加统一的 repository-owned tooling façade，让贡献者和 AI 可以通过一致的命令入口访问这些能力，而不必分别记忆大量分散的脚本名称。

本变更会定义统一入口的命令分组、保留现有脚本作为兼容 wrapper，并把 AI quickstart、context、playbook 和 machine-readable contracts 统一更新到新的入口表达。

## Why

当前仓库已经具备：

- app assembly
- generated-app verification
- upgrade target lookup
- release advisory
- upgrade evaluation
- upgrade execution

但这些能力目前通过大量分散脚本暴露，例如：

- `scaffold-derived-app*.sh`
- `verify-derived-app*.sh`
- `evaluate-derived-app-upgrade.sh`
- `show-platform-release-advisory.sh`
- `list-platform-upgrade-targets.sh`
- `execute-derived-app-upgrade.sh`

这会带来三个问题：

- AI 和人工贡献者要记忆的入口越来越多
- quickstart、context、playbook 和 contracts 中的命令路径重复且分散
- 后续继续扩展能力时，入口层会越来越碎

如果不先统一入口，标准能力越多，实际使用和自动化调用成本反而越高。

## Scope

In scope:

- 定义统一的 repository-owned tooling façade
- 为 assembly、verification、lifecycle、advisory 和 upgrade 能力提供统一子命令入口
- 保留现有脚本作为兼容 wrapper，避免立即破坏当前调用路径
- 更新 AI quickstart、context、playbook 和相关 machine-readable contracts，使它们优先暴露统一入口
- 增加对应测试和验证，证明新入口与兼容 wrapper 都可用

Out of scope:

- 重写 app assembly 或 verifier 的底层逻辑
- 删除现有脚本兼容层
- 引入新的实现语言
- 增加新的业务能力或新的 platform contract 领域
- 实现 AI direct assembly / verification path

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- 当前 `Node` / `Java` assembly 与 verifier 能力继续可用
- 当前 lifecycle、advisory、lineage 和 upgrade execution 语义不应改变
- 现有 repository-owned 脚本路径在过渡期继续可调用
