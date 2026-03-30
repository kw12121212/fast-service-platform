# Design: introduce-structured-app-template-system

## Approach

新增一层 machine-readable 的 template system contract，位于现有 assembly contract 和 lifecycle / upgrade contract 之间，描述生成输出的结构化边界：

1. `template contract`
   定义模板单元、slot、模块片段、平台管理区和派生应用定制区。

2. `output classification`
   对当前 generated output 中的重要路径进行分类，明确哪些文件是：
   - stable template
   - module-contributed fragment
   - customizable slot host
   - derived-app owned customization area

3. `upgrade alignment`
   让 upgrade planning / execution 不再只以“managed asset / manual intervention”粗粒度工作，而是能依赖模板层语义来判断哪些变更可安全更新。

4. `AI guidance`
   更新 AI context、quickstart 和 playbook，让 AI 知道：
   - 什么时候只装配模块
   - 什么时候改 slot
   - 什么时候属于派生应用定制，不能被平台默认覆盖

第一版优先做 contract、classification 和 guidance，不直接上新的模板引擎实现。

## Key Decisions

- 先标准化模板语义，不先做新引擎。
  理由：当前最缺的是边界定义，而不是另一套实现机制。

- 以 generated output 的 observable path / section 为单位定义模板边界。
  理由：这样能和现有 compatibility、verification、upgrade workflow 保持一致。

- slot 只作为 contract 概念存在，第一版不要求所有文件都物理拆成模板文件。
  理由：避免把 change 扩大成大规模文件重构。

- 模板 contract 要同时服务 assembly 和 upgrade。
  理由：模板边界的长期价值主要体现在可升级性，而不仅是首次生成。

## Alternatives Considered

- 直接引入完整模板引擎或 DSL。
  否决原因：实现代价高，而且会把仓库带向新的生成魔法。

- 继续只靠 prose 文档说明哪些文件可改。
  否决原因：AI 可消费性不足，也难以稳定支持 upgrade path。

- 完全依赖 git diff / manual intervention 处理模板边界。
  否决原因：这只能看到变化，不能表达哪些变化在 contract 上本来就允许或禁止。
