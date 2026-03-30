# standardize-ai-solution-input-model

## What

定义一个 machine-readable 的 AI solution input model，用来承接“应用想做什么”的结构化输入，并明确它与现有 `app-manifest`、`module-registry` 和 repository-owned assembly tooling 的关系。

这次 change 会把 AI 输入分成两层：

- `solution input`：描述业务目标、角色、核心对象、关键流程、UI 参考来源和约束
- `app manifest`：描述已经收敛后的装配结果、模块选择和应用标识

仓库将为 `solution input` 提供标准 contract、schema、AI context 入口和 playbook，并要求其输出可以被稳定映射为现有 assembly path 所消费的 `app-manifest`。

## Why

当前平台已经把 assembly、verification、lifecycle、upgrade 和 AI tool orchestration 标准化了，但 AI 输入层仍然偏“装配清单”。

这意味着：

- AI 往往需要过早理解模块细节，才能开始使用平台
- 用户需求、角色、对象和流程这类更高层输入，还没有稳定的 machine-readable 契约
- 现有 `app-manifest` 更适合作为装配输入，不适合作为需求输入

如果不先把输入层标准化，平台虽然能“按规则装配”，但还不够像“AI 生成业务系统的平台”。

## Scope

In scope:

- 定义语言无关的 `AI solution input model` contract
- 定义 `solution input -> app-manifest` 的职责边界和映射原则
- 定义哪些输入字段是必需的，哪些是可选的，哪些只影响 UI 参考而不直接影响模块选择
- 将新的 input model 接入 AI context、quickstart 和 playbooks
- 为后续实现准备 machine-readable schema、示例和验证要求

Out of scope:

- 不直接实现“自然语言自动转 solution input”
- 不直接实现“solution input 自动生成完整业务代码”
- 不替换现有 `app-manifest`；它仍然保留为 assembly 层输入
- 不在这次 change 里扩大产品边界或引入新的运行时依赖

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- 当前 `app-manifest` 仍然是 repository-owned assembly tooling 的直接输入
- 当前 `module-registry`、compatibility suite、generated-app verification contract 和 lifecycle / upgrade contracts 仍然有效
- 当前 `platform-tool.sh`、Java assembly / verifier、upgrade tooling 的工作流顺序不变
- 前端仍然保留 `Node/bun`，平台工具仍然保留 `Java` 主路径
