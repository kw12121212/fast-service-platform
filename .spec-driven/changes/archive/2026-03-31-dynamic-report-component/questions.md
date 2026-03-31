# Questions: dynamic-report-component

## Open

<!-- No open questions -->

## Resolved

- [x] Q: V1 需要支持哪些基础图表类型？
  Context: 这决定 descriptor 边界、实现复杂度和测试范围。
  A: `bar`、`line`、`pie` 三种都做。

- [x] Q: `DynamicReport` 消费的是原始行数据，还是调用方提供的已聚合结果？
  Context: 这决定组件是否只是展示层，还是会膨胀成聚合计算引擎。
  A: 只消费已聚合结果。

- [x] Q: 这次 proposal 是否需要包含一个仓库内最小真实示例？
  Context: 这决定 change 是只停留在可复用组件层，还是同时提供一个可发现、可验证的接入样板。
  A: 需要，优先复用现有 dashboard 作为示例入口。
