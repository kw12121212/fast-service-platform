# define-derived-app-lifecycle-and-upgrade-contract

## What

定义派生应用的生命周期与升级契约，让仓库不只负责“生成一个新应用”，还负责描述该应用如何声明自己的平台来源、版本兼容状态和升级输入。

本变更会新增一套机器可读的 derived-app lifecycle contract，并要求仓库提供对应的 AI 文档、结构化元数据和仓库拥有的升级评估入口。

## Why

当前仓库已经具备：

- `Node` 与 `Java` 两条 app assembly 兼容实现
- `Node` 与 `Java` 两条 generated-app verifier 路径
- 语言无关的 `contract + schemas + compatibility suite`

但平台仍然主要覆盖“如何生成一次”，还没有正式定义：

- 派生应用如何声明自己来自哪个平台版本
- 平台如何表达哪些升级是兼容的
- 贡献者如何判断现有派生应用能否升级
- AI 如何读取生命周期和升级事实来源

如果没有这层 contract，平台更像一次性生成器，而不是可长期演进的基础库。

## Scope

In scope:

- 定义派生应用生命周期的机器可读 contract
- 定义平台版本、派生应用来源、兼容状态和升级输入的最小语义
- 定义仓库拥有的升级评估或升级准备入口
- 更新 AI quickstart、context 和 playbook，让 AI 能找到 lifecycle / upgrade 资产
- 为 lifecycle / upgrade contract 增加对应的 spec delta 和任务清单

Out of scope:

- 全自动代码合并、自动 rebase 或自动解决冲突
- 支持多仓平台发布管理
- 引入新的外部依赖或新的平台运行形态
- 重做现有 app assembly contract、compatibility suite 或 verifier contract 的整体结构

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- 现有 `Node` 和 `Java` app assembly 实现仍然可以继续生成独立应用骨架
- 现有 generated-app verification contract 与 verifier 路径继续有效
- 当前 baseline 应用的 backend / frontend 运行方式和验证命令不应被破坏
