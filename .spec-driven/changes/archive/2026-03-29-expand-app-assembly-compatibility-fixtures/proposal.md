# expand-app-assembly-compatibility-fixtures

## What

扩展 app assembly compatibility suite 的 fixture 集合和对应验证覆盖，让仓库不仅验证最小的 valid / invalid manifest 样例，还能验证更多代表性的模块组合、生成输出边界以及 upgrade-related contract inputs。

本变更会补更多 machine-readable fixtures，扩大 compatibility suite 的有效样本集，并要求 repository-owned 测试和验证入口对这些新增样例给出一致结果。

## Why

当前仓库已经具备：

- 语言无关的 assembly contract、schema 和 compatibility suite
- `Node` 与 `Java` 两条 assembly 兼容实现
- generated-app verifier、lifecycle、advisory、execution 和 release lineage

但 compatibility suite 现在仍然过薄，主要只有：

- 2 个 valid manifests
- 3 个 invalid manifests

这不足以证明标准边界已经被测稳。当前还缺少这些高价值样例：

- 更多代表性的模块组合
- delivery 模块组合的正例
- 更细的无效依赖组合
- 与 release history / upgrade target selection 相邻的 contract 输入样例

如果不先扩 fixture 集，平台会继续更像“几条 happy path 能跑通”，而不是“可长期演进的标准已被系统性验证”。

## Scope

In scope:

- 增加更多 valid fixtures，覆盖更多代表性的模块组合
- 增加更多 invalid fixtures，覆盖依赖冲突、模块组合边界和 contract 输入错误
- 扩展 compatibility suite 资产，让新增 fixtures 成为正式的 repository-owned 验证样例
- 更新测试与验证逻辑，使 `Node` / `Java` assembly 实现都经过新增 fixtures 检查
- 更新 AI 文档中对 compatibility suite 的描述，使其反映更完整的 coverage

Out of scope:

- 新增新的 assembly 实现语言
- 重做 app assembly contract 的基础语义
- 引入全新的 verifier contract
- 直接实现 AI direct assembly path
- 重构整套 tooling 入口

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- 当前 `Node` 与 `Java` 两条 assembly 实现继续指向同一套 compatibility suite
- 当前 generated-app verifier、lifecycle、advisory 和 execution 路径不应回归
- 当前已支持的 valid fixtures 仍然必须保持通过
