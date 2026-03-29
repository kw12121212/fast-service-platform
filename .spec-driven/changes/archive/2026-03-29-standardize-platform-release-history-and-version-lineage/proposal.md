# standardize-platform-release-history-and-version-lineage

## What

定义平台 release history、version lineage 和 supported upgrade path 的标准资产，让平台的 lifecycle、advisory、evaluation 和 execution 能从“当前 release 视角”升级为“多 release 路径视角”。

本变更会补充机器可读的 release history / lineage 资产，定义 release 之间的来源关系、支持的 upgrade from/to 组合、compatibility window 和 advisory 归档方式，并要求仓库提供 repository-owned 的 release lookup / upgrade target selection 入口。

## Why

当前仓库已经具备：

- derived-app lifecycle contract
- platform release metadata
- platform release advisory
- repository-owned upgrade evaluation and execution path

但这些能力仍然主要围绕“当前平台版本”和“上一个版本”工作，还缺少正式回答以下问题的标准资产：

- 平台完整的 release history 在哪里
- 某个 release 是从哪个 lineage 演进而来
- 派生应用从当前来源版本允许升到哪些目标版本
- 哪些 advisory 属于哪个 release
- 哪条 upgrade path 是仓库正式支持的，哪些跨版本跳跃不被支持

如果没有这层 history / lineage 标准，后续升级路径会越来越像“只对当前 head 有效”，不利于平台长期演进，也不利于 AI 或多语言工具做稳定的 upgrade target 选择。

## Scope

In scope:

- 定义机器可读的 platform release history / version lineage 资产
- 定义 release history 至少应描述的 release identity、parent lineage、advisory references、support status 和 upgrade path matrix
- 定义 repository-owned release lookup / upgrade target selection 入口
- 修改 lifecycle / upgrade 主 spec，要求 upgrade evaluation 和 execution 依赖标准化 lineage 输入，而不是只依赖 current/previous release
- 更新 AI quickstart、context 和 lifecycle / upgrade playbook，让 AI 能发现 history / lineage 资产和入口
- 增加对应的 spec delta 和任务清单

Out of scope:

- 自动执行跨多个 release 的业务代码迁移
- 重做现有 advisory、evaluation 或 execution 的基础语义
- 引入完整的 release artifact registry 服务或外部数据库
- 新增第二套 lineage/evaluator 语言实现

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- 现有 derived-app lifecycle metadata、advisory、upgrade evaluation 和 execution path 继续有效
- 现有 `Node` 和 `Java` assembly / verifier 路径不应回归
- 当前 baseline 应用的 backend / frontend 运行和验证路径不应被破坏
