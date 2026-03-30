# add-repository-owned-baseline-demo

## What

在仓库内新增一个专门的 `demo/` 区域，提供一个 repository-owned 的 baseline demo derived app，用来向人类直接展示“本项目作为基础库可以构建出什么样的 baseline 企业管理应用”。

这次 change 不是再补一份演示文案，而是把 demo 本身纳入仓库事实：

- demo 目录内置在当前仓库中
- demo 应来自本项目现有的 manifest-driven assembly 路径，而不是手工拼一个平行应用
- demo 要能被正常启动、演示、使用，并有固定验证路径
- demo 要配一份 `GUIDE`，说明它如何基于本项目生成、如何重新生成、如何启动、如何验证

## Why

当前仓库已经有 runnable baseline、assembly tooling、generated-app verification、upgrade tooling 和 AI-ready 指南，但对人类演示者来说，仓库里还缺一个“拿起来就能讲”的成果物。

现在的问题主要有三类：

- README 和 specs 能说明平台能力，但不直接等于可展示 demo
- `verify-fullstack.sh` 更偏 smoke 验证，不是面向人类展示的演示入口
- 虽然仓库已经能派生应用，但没有一个 repository-owned 的 baseline demo 作为标准展示样本和再生成样本

如果没有这个 demo，平台更像“有很多 contract 和工具的基础库”，而不是“已经能稳定产出一个可展示 baseline 应用”的仓库。

## Scope

In scope:

- 定义一个 repository-owned baseline demo derived app 的仓库内行为边界
- 在仓库中引入专门的 `demo/` 目录，用于承载 baseline demo 和对应 `GUIDE`
- 要求 demo 的来源是 repository-owned assembly tooling 和明确的 assembly input
- 要求 demo 对人类演示者可发现、可启动、可验证、可再生成
- 要求 `GUIDE` 明确说明如何以本项目为基础库创建该 demo、如何运行它、如何验证它
- 要求 demo 可以展示当前 baseline 的核心页面和最小写工作流

Out of scope:

- 不新增新的业务域、页面体系或产品边界
- 不把 demo 演化成独立维护的第二主应用
- 不引入新的技术栈、模板引擎或额外外部依赖
- 不在这次 change 里顺手补 worktree / merge / sandbox 等后续工程支持组件
- 不要求一次性支持多个不同主题的 demo 目录

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- 当前平台的 `solution input -> app-manifest -> assembly tooling` 主路径保持不变
- 现有 runnable baseline、默认模块边界和 current backend/frontend workspace 不应被 demo 目录替代
- repository-owned assembly、generated-app verification、upgrade evaluation 和 unified tooling façade 仍然是平台主路径
- demo 必须复用当前平台已有 contract、manifest、module registry 和验证入口，而不是创造一条脱离平台的特殊生成路径
