# Design: solution-input-to-manifest-planning

## Approach

这次 change 在现有 `solution input` 与 `app-manifest` 之间增加一个显式 planning 层。

核心路径变为：

1. AI 或人工贡献者先准备合法的 `solution input`
2. 仓库拥有的 planning artifact 读取 `solution input`、`module-registry`、相关 assembly contract facts 和显式约束
3. planning artifact 输出：
   - 已确定的模块选择结果
   - 每个模块纳入或排除的可观察依据
   - 需要补全的 manifest 字段或未决冲突
   - 供贡献者产出最终 `app-manifest` 的 planning guidance
4. 贡献者基于 planning 输出产出独立 `app-manifest`
5. assembly tooling 继续只消费 `app-manifest`

实现上，这个 change 预计会补充：

- 一个 machine-readable planning contract
- 对应 schema
- 至少一个 example planning artifact
- AI context / quickstart / playbook 中对 planning 层的入口说明
- 与 planning asset 一致的测试或验证覆盖

本次设计不把 recommendation 系统一起塞进 planning 层。planning 层必须先做到 deterministic、可审计、可复述，再把“建议但非必需”的推荐行为放到后续 change。

## Key Decisions

- 保留 `app-manifest` 作为直接 assembly runtime input，不允许 planning artifact 直接喂给 assembly tooling。
  理由：现有 assembly path、project-scoped assembly 和兼容性契约都建立在 `app-manifest` 上，替换它会放大影响面。

- planning 层必须是 machine-readable asset，而不是只补 prose playbook。
  理由：里程碑目标是降低 AI contributors 的猜测成本，单纯 prose 不足以成为稳定的仓库事实来源。

- planning 输出必须显式暴露模块决策依据和未决项，而不是只给一个黑盒结果。
  理由：后续 `solution-input-module-recommendation` 需要建立在可审计边界上，不能让 deterministic planning 和 recommendation heuristics 混在一起。

- recommendation 保持为后续独立 change。
  理由：当前任务是先把 planning 接口标准化，如果现在同时做 recommendation，proposal 范围会明显膨胀。

- planning guidance 优先复用现有 solution-input / derivation playbook 入口，必要时再补独立 planning playbook。
  理由：保持 AI-facing 导航面清晰，避免在同一条工作流上制造重复入口。

## Alternatives Considered

- 只在 `docs/ai/playbooks/define-ai-solution-input.md` 里补文字说明，不新增 planning artifact。
  否决原因：这无法把 planning 层沉淀成 machine-readable contract，也不利于后续模块生成里程碑依赖。

- 直接把更多 planning 字段塞进 `app-manifest`。
  否决原因：会把上游业务规划和下游 assembly runtime input 混在一起，削弱现有 manifest 边界。

- 这次同时做 module recommendation。
  否决原因：recommendation 需要在 deterministic planning 边界明确后再做，否则会把“必须规则”和“建议逻辑”混淆。

- 定义 planning artifact 后直接允许 assembly tooling 消费它。
  否决原因：这等于引入第二个 runtime contract，不符合当前已确认的边界。
