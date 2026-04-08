# Design: solution-input-module-recommendation

## Approach

这次 change 在现有 `solution input -> plan -> manifest` 路径上，再补一层显式 recommendation asset，但保持它是独立、可选、可审计的 guidance。

核心路径变为：

1. AI 或人工贡献者先准备合法的 `solution input`
2. 仓库拥有的 planning artifact 先产出 deterministic planning 输出
3. recommendation artifact 读取：
   - `solution input`
   - deterministic planning 输出
   - module registry 与 assembly contract 中已有的仓库事实
4. recommendation artifact 输出：
   - 推荐纳入或延后的 optional modules
   - 建议的 manifest shaping 提示
   - 每条推荐的 basis、confidence、prerequisites 和 boundary notes
   - 哪些字段仍需贡献者显式确认或手工决定
5. 贡献者基于 planning 与 recommendation 共同产出独立 `app-manifest`
6. assembly tooling 继续只消费 `app-manifest`

实现上，这个 change 预计会补充：

- 一个 machine-readable recommendation contract
- 对应 schema
- 至少一个 repository-owned example recommendation artifact
- AI context / quickstart / playbook 中对 recommendation 层的入口说明
- 与 recommendation asset 一致的测试或验证覆盖

## Key Decisions

- recommendation 使用独立 artifact，而不是作为 `solution-to-manifest plan` 的可选区块。
  理由：已确认 deterministic planning 和 recommendation 必须保持清晰边界，独立 artifact 更容易审计、版本化和在无推荐场景下复用 planning。

- recommendation 保持为可选 guidance，不进入 assembly runtime contract。
  理由：现有 assembly path、project-scoped assembly 和兼容性契约都建立在 standalone `app-manifest` 上，不应再引入第二个 runtime input。

- recommendation 必须显式暴露 basis、confidence 和 prerequisite，而不是只输出“推荐结果”。
  理由：仓库拥有的 recommendation 不能变成黑盒启发式；AI 贡献者需要知道哪些是硬约束延伸，哪些只是建议。

- 这次只定义 contract/schema/example/playbook，不要求 CLI。
  理由：当前目标是先固定 recommendation 的规范与边界，避免在没有稳定 contract 前提前扩大到执行型 tooling。

- recommendation 依赖 planning 输出，而不是直接绕过 planning 从 solution input 自行推导一切。
  理由：planning 已经承担 deterministic facts 汇总职责，recommendation 应建立在该稳定事实层之上，而不是重新复制规则。

## Alternatives Considered

- 把 recommendation 字段直接塞进 `solution-to-manifest plan`。
  否决原因：会模糊 mandatory planning facts 与 optional guidance 的边界，也会让 planning artifact 语义膨胀。

- 本次同时要求一个 repository-owned CLI 来产出 recommendation。
  否决原因：会让 change 范围从 contract/guidance 扩大到执行实现，当前没有必要。

- 继续只用 quickstart 或 playbook prose 表达 recommendation 建议。
  否决原因：这无法形成 machine-readable、可验证、可示例化的仓库事实来源。

- 让 recommendation 直接成为 assembly tooling 的上游输入。
  否决原因：这会弱化 `app-manifest` 的直接输入边界，并引入第二套 runtime contract。
