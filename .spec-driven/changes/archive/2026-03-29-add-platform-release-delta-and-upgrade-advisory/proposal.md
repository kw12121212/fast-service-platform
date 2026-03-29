# add-platform-release-delta-and-upgrade-advisory

## What

定义平台 release delta 和 upgrade advisory 的标准资产，让仓库不仅能判断派生应用是否兼容当前平台发布，还能说明“当前平台版本相对上一个版本改了什么、影响哪些模块、建议先做哪些检查和验证”。

本变更会补充机器可读的 release delta / advisory 资产，并要求仓库提供对应的 AI 文档、playbook 和 repository-owned advisory 入口。

## Why

当前仓库已经具备：

- 派生应用 lifecycle contract
- 平台 release metadata
- repository-owned upgrade evaluation path

但当前能力主要回答“兼容不兼容”，还没有正式回答：

- 当前平台发布相对前一个发布改了什么
- 哪些模块或 contract 被影响
- 哪些变化是兼容升级，哪些需要人工介入
- 派生应用在升级前应优先做哪些检查

如果没有这层 advisory，upgrade evaluation 的结果仍然偏黑盒，不足以支撑 AI 或人类贡献者做下一步升级决策。

## Scope

In scope:

- 定义机器可读的 platform release delta / upgrade advisory 资产
- 定义 release delta 至少应描述的变化类别、影响范围和建议动作
- 定义 repository-owned advisory 入口
- 更新 AI quickstart、context 和 lifecycle playbook，让 AI 能找到 advisory 资产
- 增加对应的 spec delta 和任务清单

Out of scope:

- 自动执行升级、自动 merge、自动 rebase 或自动冲突解决
- 完整的多版本发布历史系统
- 重做现有 lifecycle contract 或 app assembly contract 的基础语义
- 引入新的外部依赖或新的平台运行形态

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- 现有 derived-app lifecycle metadata 与 upgrade evaluation path 继续有效
- 现有 `Node` 和 `Java` app assembly / verifier 路径不应回归
- 当前 baseline 应用的 backend / frontend 运行和验证路径不应被破坏
