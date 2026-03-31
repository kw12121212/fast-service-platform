# Tasks: dynamic-report-component

## Implementation

- [ ] 定义 `ReportDescriptor`、section descriptor 以及 summary cards、table、chart 所需的 TypeScript 类型
- [ ] 实现 `DynamicReport` 组件，按 descriptor 顺序渲染 summary cards、table、`bar`、`line`、`pie` section
- [ ] 以平台自有 SVG/HTML 渲染方式实现基础图表，不引入新的外部图表依赖
- [ ] 保持 `DynamicReport` 为纯展示组件：只消费调用方传入的已聚合结果，不在组件内发起 backend 请求
- [ ] 从平台组件入口导出 `DynamicReport` 及其 descriptor 类型
- [ ] 在现有 dashboard 中补一个最小真实示例，使用当前 backend-backed 数据生成至少一个动态报表 section

## Testing

- [ ] Unit test：`DynamicReport` 能从 descriptor 渲染 summary cards、table、`bar`、`line`、`pie` 五类 V1 section
- [ ] Unit test：组件使用调用方提供的已聚合结果渲染内容，而不是依赖内部请求
- [ ] Unit test：当 descriptor 更新或调用方传入新的报表结果时，渲染结果随之更新
- [ ] Unit test：dashboard 示例能渲染动态报表区块并消费现有 backend-backed 资源结果
- [ ] `bun run test` passes
- [ ] `bun run lint` passes
- [ ] `bun run build` passes

## Verification

- [ ] `DynamicReport` 可从平台组件入口识别和导入
- [ ] 报表组件能在单页内组合展示 summary cards、table 和三种基础图表
- [ ] 调用方仍通过现有 data-access 模式取数，组件本身不引入新的请求路径
- [ ] 当前 dashboard 至少有一个真实动态报表示例，且仍然展示真实 backend 数据
- [ ] 现有 admin 页面和写流程没有因为引入动态报表而退化
