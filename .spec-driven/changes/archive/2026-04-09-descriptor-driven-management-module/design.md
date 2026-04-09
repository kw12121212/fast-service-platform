# Design: descriptor-driven-management-module

## Approach

这次 change 先建立一条最小且可验证的 descriptor-driven module spine，而不是一次性覆盖所有业务模块形态。

建议的主路径是：

1. 定义一个 repository-owned `management-module descriptor`
   它表达受限的管理模块形态，例如模块标识、核心实体、表单字段、列表/汇总展示需求、允许的页面结构，以及与现有 platform-owned interaction components 的绑定关系。

2. 将 descriptor 放在 planning / recommendation 之后、assembly 之前
   这条路径读取已有 planning / recommendation 事实，但不会取代 `app-manifest`。descriptor-driven generation 的输出仍然要收敛成独立的 `app-manifest` 和受控的模块生成输入，再进入现有 repository-owned assembly path。

3. 将首个模块形态限制为“管理模块主路径”
   第一版只覆盖窄范围 management-module shape，并显式复用 `dynamic form` 与 `dynamic report`。workflow 专项生成保持拆分，避免 scope 从“证明路径成立”漂移到“覆盖所有页面形态”。

4. 要求输出落在现有 structured template ownership 边界内
   descriptor-driven output 必须映射到现有 platform-managed templates、module fragments、slot boundaries 和 customization zones。不能绕开 template classification 直接生成未分类文件树。

5. 用 repository-owned example + validation 证明路径可运行
   至少提供一个 descriptor example，并把它接入现有 assembly verification / compatibility coverage，让这条路径成为可验证的 repository asset，而不是纯说明文档。

## Key Decisions

- 只做一个窄范围 management-module shape，而不是通用 CRUD generator。
  这样能满足 roadmap 对 first descriptor surface 的要求，同时符合 V1 “not a general low-code platform” 的硬边界。

- descriptor 是上游生成资产，不是新的 assembly runtime contract。
  现有 `app-manifest` 仍然是 assembly 的直接输入；这样可以继续复用既有 assembly、verification、project-scoped lifecycle 和 upgrade contracts。

- 首条路径只强制复用 `dynamic form` 与 `dynamic report`。
  workflow generation 已在 roadmap 中拆成独立 planned change，当前 change 不应提前吞并。

- descriptor-driven output 必须服从 structured template ownership。
  这样后续 upgrade、closure、fixture validation 才能继续基于 repository-owned template boundaries 推进，而不是引入新的隐式生成规则。

- AI-facing guidance 也是这次 change 的一部分。
  roadmap 已明确要求 descriptor-driven generation “discoverable and verifiable”，因此 AI context、orchestration guidance、playbooks 需要同步更新，而不是只补内部实现。

## Alternatives Considered

- 一次性把 report 与 workflow 专项 generation 一起纳入。
  被拒绝，因为 roadmap 已拆分为独立 planned changes，而且这样会让首个 descriptor proof path 失去边界。

- 让 descriptor 直接替代 `app-manifest`。
  被拒绝，因为这会破坏现有 assembly contract，也会让 planning / recommendation / assembly 的职责重新混淆。

- 采用任意实体都能生成的 free-form CRUD DSL。
  被拒绝，因为这会越过 V1 边界，把 descriptor path 变成通用 low-code engine。

- 只补说明文档，不引入 repository-owned example 和 validation coverage。
  被拒绝，因为这样无法支撑后续 `platform-v1-integration-and-closure` 的 end-to-end proof。
