# solution-input-module-recommendation

## What

定义一个 repository-owned、machine-readable 的 `solution-to-manifest recommendation` 层，放在现有 deterministic planning 与最终 `app-manifest` 之间，作为可选 guidance 资产存在。

这次 change 会把 recommendation 设计成独立 artifact，而不是把推荐字段塞回已有 `solution-to-manifest plan`。recommendation 输出需要表达：

- 对可选模块和 manifest shaping 的推荐结论
- 每条推荐对应的可观察依据、适用前提和置信度
- 哪些结论来自显式约束，因此不能被当作可选建议
- 哪些结论只是 repository-owned heuristic guidance，贡献者可以接受、调整或拒绝

同时，这次 change 会补齐 recommendation contract、schema、至少一个 repository-owned example，以及 AI-facing guidance，使 AI 贡献者能够沿着 `solution input -> planning -> recommendation -> manifest -> assembly` 的仓库拥有路径工作。

## Why

`solution-input-to-manifest-planning` 已经把 deterministic planning 标准化，但当前仓库仍缺少一层显式 recommendation guidance，帮助 AI 贡献者在不违背当前 product boundary 和 module dependency facts 的前提下，更稳定地收敛可选模块与 manifest shaping 决策。

如果不把 recommendation 层显式化：

- AI 仍然需要从分散文档里自行猜测哪些 optional modules 更适合当前 solution input
- deterministic planning 和 heuristic recommendation 的边界会重新变模糊
- 后续 descriptor-driven business module generation 缺少一个 repository-owned 的推荐输入面，只能继续依赖 prose 或隐式经验

这次 change 的目标不是让 recommendation 取代 planning，而是在 planning 已经稳定的前提下，把“建议但非强制”的层次补成可审计的仓库资产。

## Scope

In scope:

- 定义 machine-readable 的 `solution-to-manifest recommendation` contract、schema 和至少一个 repository-owned example
- 明确 recommendation artifact 与 `solution input`、deterministic planning output、module registry、`app-manifest` 之间的关系
- 定义 recommendation 输出中必须可观察的 recommendation basis、confidence、prerequisites、accepted constraints 和 unresolved follow-up guidance
- 更新主 specs，明确 recommendation 是独立于 planning 的可选 guidance 层，而不是 assembly runtime contract
- 更新 AI context、orchestration guidance、quickstart 与相关 playbook，使 recommendation 入口可发现且与 planning 顺序一致
- 增加与 recommendation asset 相关的验证或测试覆盖，确保仓库验证入口能够识别它

Out of scope:

- 不把 recommendation artifact 变成 assembly tooling 的直接输入
- 不把 recommendation 回写为已有 planning artifact 的必填区块
- 不在这次 change 中要求 repository-owned CLI 或新的执行型 tooling entrypoint
- 不引入新的运行时 AI 功能、prompt intake、in-repo chat 或自动执行代理
- 不扩展 module registry 的产品边界，也不直接进入 descriptor-driven business module generation 的实现

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- `app-manifest` 仍然是 repository-owned assembly tooling 的直接输入
- deterministic planning 仍然可以在没有 recommendation 的情况下独立使用
- recommendation 仍然是可选 guidance，而不是强制 runtime contract
- 当前产品边界仍然限定在企业内部管理单体应用，不扩展到仓库内 AI 运行时功能
- 当前 module registry、assembly contract、compatibility suite 和 generated-app verification contract 继续保持有效
