# add-derived-app-upgrade-execution-path

## What

定义派生应用升级执行路径，让仓库从“能评估、能给 advisory”继续走到“能生成升级计划、支持 dry-run、输出需要人工处理的升级项，并提供仓库拥有的升级执行入口”。

本变更会增加 machine-readable 的 upgrade plan / execution contract，并要求仓库提供对应的 AI 文档、playbook 和 repository-owned execution path。

## Why

当前仓库已经具备：

- lifecycle contract
- upgrade evaluation
- platform release advisory

这些能力已经能回答：

- 派生应用来自哪个平台版本
- 是否满足升级前提
- 当前 release 改了什么、影响哪些模块

但仍然没有正式回答：

- 具体哪些文件或资产需要升级
- 哪些升级动作可以自动应用
- 哪些项必须人工介入
- 升级后要按什么路径复验

如果没有 execution path，平台仍然停留在“能判断、能解释”，还不能真正支撑受控升级。

## Scope

In scope:

- 定义 machine-readable 的 upgrade plan / execution contract
- 定义 repository-owned 升级执行入口，至少支持 `dry-run`
- 定义自动可应用项和人工介入项的最小语义
- 定义升级后必须复用的验证路径
- 更新 AI quickstart、context 和 upgrade playbook，让 AI 能找到 execution 资产和入口
- 增加对应的 spec delta 和任务清单

Out of scope:

- 自动 merge Git 历史
- 自动解决所有代码冲突
- 完整的多版本升级矩阵和长期分支策略
- 重做现有 advisory / lifecycle contract 的基础语义
- 引入新的外部依赖或新的平台运行形态

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- 现有 lifecycle metadata、upgrade evaluation 和 release advisory 入口继续有效
- 现有 `Node` 和 `Java` assembly / verifier 路径不应回归
- 当前 baseline 应用的 backend / frontend 运行和验证路径不应被破坏
