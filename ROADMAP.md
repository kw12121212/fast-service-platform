# Roadmap

本路线图用于记录平台下一阶段的优先方向，帮助 AI 和人工贡献者判断“下一步最值得做什么”。

它不是绕过 `.spec-driven/` 的实施许可。任何非平凡变更仍然必须先进入 spec-driven proposal，再实施、验证和归档。

## 当前判断

当前仓库已经具备这些能力：

- `Node` 与 `Java` 两条 app assembly 兼容实现
- `Node` 与 `Java` 两条 generated-app verifier 路径
- 语言无关的 `contract + schemas + compatibility suite`

下一阶段的重点不再只是“再增加一种实现语言”，而是继续提高平台的长期可演进性、兼容性可信度和 AI 可消费性。

## 建议优先级

### P0

#### 1. 派生应用生命周期能力

- 方向：定义派生应用的升级、兼容、平台版本和回收平台变更的标准路径。
- 价值：最高。解决“生成之后怎么升级、怎么持续演进”的核心问题。
- 风险：需要定义平台版本、派生应用版本、兼容矩阵、upgrade / rebase 语义。
- 建议 change name：`define-derived-app-lifecycle-and-upgrade-contract`

### P1

#### 2. 兼容性套件扩容

- 方向：增加更多 manifest、失败样例、模块组合、空模块边界和冲突依赖样例。
- 价值：高。能快速提高标准可信度，减少边界遗漏。
- 风险：测试资产会变多，维护成本会增加。
- 建议 change name：`expand-app-assembly-compatibility-fixtures`

#### 3. 统一工具入口

- 方向：把分散的 `Node`、`Java`、`shell` 入口收敛成一致的仓库命令界面。
- 价值：高。降低工具使用门槛，让文档和自动化入口更清晰。
- 风险：如果设计过度，容易变成一层收益有限的包装。
- 建议 change name：`unify-platform-tooling-entrypoints`

#### 4. AI 直接装配与验证路径

- 方向：让 AI 不依赖现成 `Node/Java` 实现，只根据标准 contract 和 compatibility assets 直接完成装配或验证。
- 价值：中高。能证明规范本身足够稳固，确实可被 AI 直接消费。
- 风险：稳定性和验收难度通常高于代码实现。
- 建议 change name：`add-ai-direct-assembly-and-verification-path`

### P2

#### 5. 模块边界继续下沉

- 方向：把可选业务模块进一步拆细，明确更稳定的模块边界、依赖声明和裁剪规则。
- 价值：中高。会让平台更像真正可组合的基础库。
- 风险：会触碰当前前后端耦合，改动面较大。
- 建议 change name：`decompose-optional-business-modules`

#### 6. 更结构化的 AI 输入层

- 方向：从 `manifest` 驱动继续走向更结构化的需求输入、领域输入和 UI 输入映射。
- 价值：中高。更接近“从需求直接到应用骨架”。
- 风险：如果过早推进，容易把输入契约做得过重。
- 建议 change name：`standardize-ai-solution-input-model`

### P3

#### 7. 生成模板系统升级

- 方向：把当前受控拼装继续演进为更明确的模板层、slot 机制和覆盖点约定。
- 价值：中。适合后续规模扩大时提效。
- 风险：如果设计不克制，容易引入新的模板复杂度和代码生成魔法。
- 建议 change name：`introduce-structured-app-template-system`

## 推荐顺序

建议按下面顺序推进：

1. `define-derived-app-lifecycle-and-upgrade-contract`
2. `expand-app-assembly-compatibility-fixtures`
3. `unify-platform-tooling-entrypoints`
4. `add-ai-direct-assembly-and-verification-path`

原因：

- 当前最缺的不是“还能不能再生成一次”，而是“派生出去以后怎么持续升级和兼容”。
- 在 lifecycle 明确之前，平台更像一个可演示生成器，不像可长期演进的基础库。
- compatibility suite 和 tooling entrypoints 能优先提高标准可信度和日常可用性。
- AI direct path 的战略价值高，但更适合作为前面几项打稳之后的能力验证。

## 使用方式

- 把本路线图视为候选方向清单，而不是自动排期。
- 每个方向真正开始实施前，都要先进入 `.spec-driven/changes/` 形成 proposal。
- 如果仓库真实状态已经变化，应同步更新本文件，避免路线图和实现现实脱节。
