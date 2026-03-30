# Design: standardize-ai-solution-input-model

## Approach

新增一个独立的 `AI solution input model` 规范层，放在现有 `AI consumption / repository readiness / app assembly contract` 之间：

1. `solution input contract`
   定义 AI 在装配前可消费的结构化输入，覆盖应用目标、业务域、角色、关键对象、关键流程、约束和 UI 参考来源。

2. `mapping boundary`
   明确 `solution input` 不是直接装配输入；它必须先映射成 `app-manifest`，再进入现有 assembly tooling。

3. `AI context integration`
   在 `docs/ai/context.yaml`、quickstart 和 playbooks 中暴露新的事实来源，确保 AI 知道先读 input contract，再决定如何生成或补全 manifest。

4. `verification boundary`
   这次只定义 contract、schema、示例和映射要求；不把“自然语言直接解析成 solution input”的推理实现塞进仓库运行时。

## Key Decisions

- 保留 `app-manifest`，不直接用 `solution input` 替代它。
  理由：`app-manifest` 已经是稳定的 assembly 输入，替换它会扩大影响面。

- `solution input` 定位为更高层的 AI 需求输入，而不是新的运行时 DSL。
  理由：目标是让 AI 和人都能稳定表达需求，不是引入另一套复杂配置系统。

- 输入字段先覆盖“目标、角色、对象、流程、UI 参考、约束”，不直接覆盖每个页面或每个数据库字段。
  理由：先建立稳定层级，再逐步细化，避免第一版过重。

- 仍然要求 AI 通过 repository-owned tooling 走装配流程。
  理由：这和现有 `AI tool orchestration contract` 一致，避免 AI 绕开工具直接重做 workflow。

## Alternatives Considered

- 直接扩展 `app-manifest`，把所有需求字段都塞进去。
  否决原因：会把装配层输入和需求层输入混在一起，边界不清。

- 不定义新 contract，只在 playbook 里写 prose 说明。
  否决原因：AI 可消费性不足，无法成为稳定事实来源。

- 直接做“自然语言 -> manifest”的实现。
  否决原因：会把这次 change 从 contract 标准化扩大成推理实现，风险过高。
