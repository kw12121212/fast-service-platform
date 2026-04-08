# solution-input-to-manifest-planning

## What

定义一个 repository-owned、machine-readable 的 `solution input -> app-manifest` planning 层。

这次 change 会在现有 `AI solution input contract`、`module-registry` 和 `app-manifest` 之间补上一个显式 planning artifact，用来表达：

- 结构化 `solution input` 如何被收敛成明确的模块选择结论
- 哪些 manifest 字段已经可以确定，哪些仍需要显式补全
- 哪些模块决策来自显式约束、依赖关系或可观察的业务事实

这个 planning artifact 是上游规划接口，不是新的 assembly runtime contract。仓库仍然要求贡献者在调用 repository-owned assembly tooling 之前产出独立的 `app-manifest`。

同时，这次 change 会把 planning 层接入 AI-facing guidance，包括相关 spec、AI context、orchestration guidance、quickstart 和 playbook，使 AI 贡献者可以沿着仓库拥有的路径完成 `solution input -> planning -> manifest -> assembly`。

## Why

当前仓库已经有：

- `solution input` contract 和 schema
- `module-registry`
- `app-manifest` contract
- repository-owned assembly tooling

但 `solution input` 到 `app-manifest` 之间仍然主要靠高层 mapping guidance 和 prose playbook。里程碑 6 的目标要求这条路径更显式、更可重复，而且当前里程碑风险已经明确指出：`solution input` 和 manifest 之间的中间接口必须先定义清楚，否则后续 descriptor-driven business module generation 会建立在不稳定的 prose 假设上。

如果不先补 planning 层：

- AI 仍然需要从分散文档里自行拼接模块决策逻辑
- `solution input` 的结构化价值无法稳定沉淀为显式 planning 输出
- 后续的 module recommendation 很容易和 deterministic planning 边界混在一起

这次 change 先把 planning 层标准化，再把 recommendation 留给后续单独 change。

## Scope

In scope:

- 定义 machine-readable 的 `solution input -> manifest` planning artifact、相关 schema 和至少一个 repository-owned example
- 定义 planning artifact 的输入、输出、可观察决策依据，以及未决 manifest 字段或冲突的表达方式
- 更新主 specs，明确 planning 层与 `solution input`、`module-registry`、`app-manifest`、AI orchestration 和 AI context 的关系
- 更新 quickstart、AI context 和 solution-input / derivation playbook，暴露 repository-owned planning 路径
- 增加与 planning artifact 相关的验证或测试覆盖，保证它作为 AI-readable asset 被仓库验证入口识别

Out of scope:

- 不在这次 change 中实现 repository-owned 的模块推荐启发式或自动推荐评分
- 不把 planning artifact 变成 assembly tooling 的直接输入
- 不引入新的运行时 AI 功能、prompt intake 或 in-repo chat
- 不扩展 module registry 的产品边界，也不新增业务模块
- 不直接进入 descriptor-driven business module generation 的实现

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- `app-manifest` 仍然是 repository-owned assembly tooling 的直接输入
- `solution input` 仍然是业务意图层，不直接替代 assembly runtime contract
- 当前 module registry、assembly contract、compatibility suite 和 generated-app verification contract 继续保持有效
- 模块推荐逻辑仍然作为后续独立 roadmap item 处理，不在本次 change 中落地
- 当前产品边界仍然限定在企业内部管理单体应用，不扩展到仓库内 AI 运行时功能
