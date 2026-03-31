# Design: dynamic-report-component

## Approach

定义一组前端侧的声明式报表 descriptor 类型，例如 `ReportDescriptor`、`ReportSectionDescriptor`、`SummaryCardDescriptor`、`ReportTableDescriptor`、`ReportChartDescriptor`。`DynamicReport` 根据 descriptor 顺序渲染多个 section，并把调用方传入的已聚合结果映射为统一的报表展示结构。

V1 的 summary cards 和 table 继续复用现有 admin/card/table 风格；`bar`、`line`、`pie` 图表采用平台自有的 SVG/HTML 渲染实现，结合当前主题里的 chart color tokens，避免为了基础图表就引入新的外部图表库。

数据仍由调用页面通过现有 `frontend/src/lib/api/` 模式获取。页面负责把 backend 返回的数据整理成已聚合结果，再传给 `DynamicReport`。为了降低 adoption 成本，当前 dashboard 会提供一个最小真实示例，用现有 backend-backed 数据展示 summary、table 或 chart 中的若干 section，证明组件不是停留在孤立 demo。

## Key Decisions

- **输入边界固定为“已聚合结果”** — 这样组件保持展示职责，不在内部引入分组、聚合、排序规则，避免 V1 膨胀成报表计算引擎。
- **V1 一次性支持 `bar`、`line`、`pie` 三种基础图表** — 这是本次 brainstorm 明确确认的范围，足以覆盖多数内部管理后台的第一层统计视图。
- **不引入新的外部图表依赖** — 仓库的产品原则是避免无必要的新外部库；当前前端已有主题级 chart tokens，V1 用平台自有 SVG/HTML 能满足基础图表需求。
- **组件不自行发请求** — 保持和现有 `data-access` spec 一致，避免请求逻辑分散到展示组件中。
- **先在现有 dashboard 提供真实示例** — 比单独的孤立 story 或静态示例更能证明组件与当前 backend 数据和 admin shell 的兼容性。

## Alternatives Considered

- **引入现成 chart library**：实现速度可能更快，但会扩大依赖边界，也和当前“优先不加外部库”的仓库原则冲突；V1 先用平台自有实现。
- **让组件接收原始行数据并内置聚合能力**：看起来更“自动”，但会把字段语义、聚合规则、分组方式一起带进组件，范围会迅速滑向 BI/analytics 产品。
- **只做组件，不做真实页面示例**：虽然 proposal 更小，但平台可发现性和可信度不足，AI 也缺少一个仓库内的参考接入点。
