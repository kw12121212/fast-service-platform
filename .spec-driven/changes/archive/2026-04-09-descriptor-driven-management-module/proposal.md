# descriptor-driven-management-module

## What

定义一个受边界约束的 `descriptor-driven management-module` 生成主路径。

这次 change 会把 repository-owned 的 planning / recommendation 输出继续收敛成一个 machine-readable 的管理模块 descriptor，并为至少一种窄范围管理模块形态提供可验证的生成路径。第一条路径只证明：

- 一个管理模块 descriptor 可以表达受限的模块形态
- 该 descriptor 可以被翻译成独立的 `app-manifest` 与受控的模块生成输入
- 生成结果复用现有 `dynamic form`、`dynamic report` 和 structured template ownership
- 整条路径仍然服从现有 repository-owned assembly / verification contracts

这次 change 不把 descriptor 变成新的 assembly runtime contract，也不把它扩展成通用 CRUD / low-code 引擎。

## Why

当前仓库已经完成了：

- `solution input`
- `solution -> manifest planning`
- `solution -> manifest recommendation`
- repository-owned assembly / verification / lifecycle contracts

但缺少 descriptor-driven business module generation 的第一条真实主路径。结果是：

- AI 贡献者仍然需要手工拼接业务模块页面和交互，而不是复用 repository-owned 的 descriptor surface
- V1 无法证明 `solution input -> planning -> recommendation -> descriptor-driven module -> manifest -> derived app` 这条完整路径
- `platform-v1-integration-and-closure` 也因此被直接阻塞

这次 change 的目标是补上最小可验证的 descriptor-driven module spine，为后续的 report / workflow 专项生成和 V1 end-to-end closure 提供可运行前提。

## Scope

In scope:

- 定义 machine-readable 的 management-module descriptor 及至少一个 repository-owned example
- 明确 descriptor 与 planning / recommendation 输出、`app-manifest`、module selection、template ownership 之间的关系
- 为一种窄范围 management-module shape 定义 descriptor-driven 生成路径
- 要求这条生成路径复用现有 `dynamic form` 与 `dynamic report` 能力
- 更新 AI-facing guidance、AI context、tool orchestration 和相关 playbook，使 descriptor-driven path 可发现且可验证
- 增加与 descriptor-driven module 相关的 repository-owned validation coverage

Out of scope:

- 不在这次 change 中纳入 `descriptor-driven-report-page-generation`
- 不在这次 change 中纳入 `descriptor-driven-workflow-page-generation`
- 不把 descriptor system 扩成任意 CRUD / low-code / no-code generation 平台
- 不把 descriptor artifact 变成 repository-owned assembly tooling 的直接 runtime input
- 不引入新的运行时 AI chat、AI UI、外部集成面或 V1 边界外的输出形态
- 不扩展 module registry 为开放注册系统

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- `app-manifest` 仍然是 repository-owned assembly tooling 的直接输入
- planning 与 recommendation 仍然是上游 machine-readable assets，而不是 assembly runtime contract
- V1 仍然只支持企业内部管理单体应用，不支持多租户、分布式输出或 runtime AI features
- descriptor-driven path 必须复用现有 `dynamic form`、`dynamic report`、structured template boundaries，而不是创建平行的一次性实现
- `descriptor-driven-workflow-page-generation` 仍然保持为后续独立 roadmap item
