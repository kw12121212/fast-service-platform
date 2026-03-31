# dynamic-report-component

## What

新增一个平台级可复用的前端 `DynamicReport` 组件能力。调用方通过声明式 `ReportDescriptor` 和已聚合的报表结果，就可以渲染由 summary cards、table、`bar`/`line`/`pie` 图表组成的单页报表视图，而不需要为每个实体单独手写统计展示结构。

这次 change 同时要求在当前可运行后台里提供一个最小真实示例入口，优先复用现有 dashboard，让贡献者和 AI 都能看到这个组件如何接入真实 backend-backed 数据。

## Why

`dynamic-form-component` 已经补齐了“结构化录入”的平台能力，但平台还缺少与之相邻的“结构化展示”能力。当前如果 AI 生成一个企业管理页面，凡是涉及统计卡片、趋势图、占比图或报表表格，仍然需要逐页手写视图结构、图表绘制和空数据呈现逻辑。

平台级 `DynamicReport` 能把这部分重复工作压缩成“描述报表结构 + 提供已聚合结果”，从而让 AI 在生成内部管理应用时，不必为每个业务模块重新发明报表展示模式，同时也能保持视觉、交互和平台边界的一致性。

## Scope

**In scope:**
- 前端 `DynamicReport` React 组件，接受声明式 `ReportDescriptor`
- `ReportDescriptor` 支持由多个有序 section 组成的单页报表视图
- V1 section 类型：
  - summary cards
  - table
  - `bar` chart
  - `line` chart
  - `pie` chart
- 报表组件只消费调用方提供的“已聚合结果”，不在组件内部做原始数据聚合
- 报表组件不自行请求 backend；数据仍由页面或 `frontend/src/lib/api/` 里的现有 data-access 模式负责获取
- 使用仓库现有 UI 和样式能力实现基础图表与报表容器，不引入新的外部图表依赖作为 V1 前提
- 将 `DynamicReport` 及其相关 descriptor 类型从平台组件入口导出
- 在现有 dashboard 提供一个最小真实示例，证明组件可以展示当前 backend-backed 数据

**Out of scope (V1):**
- 报表设计器、拖拽布局或低代码报表编排器
- 任意公式、表达式编辑器或 ad hoc analytics
- 多数据源 join、跨服务建模或组件内聚合引擎
- drill-down、cross-filter、复杂交互联动
- 组件自己决定 backend 查询方式或自己管理数据获取生命周期
- 新增一个独立“报表模块”业务域

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):
- 现有 dashboard、users、roles、projects、tickets、kanban 页面继续保持当前 backend-backed 行为
- 现有 frontend data-access 约定保持不变；请求逻辑不会重新散落到组件内部
- 现有 `DynamicForm` 和管理写流程不受影响
- 本次 change 不要求 backend API 扩容，也不改变 `/service/*` 代理边界
