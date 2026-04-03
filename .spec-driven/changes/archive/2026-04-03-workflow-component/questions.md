# Questions: workflow-component

## Open

<!-- No open questions -->

## Resolved

- [x] Q: 首版是否要求在当前 runnable admin frontend 中提供一个真实 workflow 示例页面或示例区？
  Context: 这会决定 proposal 是否只要求平台组件 contract，还是同时要求当前基线前端提供可运行示例来证明集成方式。
  A: 需要。

- [x] Q: `reassign` 是否属于 V1 必选动作，还是仅要求支持“调用方声明的有界动作集合”即可？
  Context: 这会影响 descriptor 的最小字段约束，以及测试和示例需要覆盖的动作范围。
  A: 属于 V1 必选动作。

- [x] Q: 评论输入在 V1 中应为必备能力还是可选能力？
  Context: 这会影响组件接口、最小可见能力，以及是否需要把“无评论工作流”也作为明确支持场景写入实现和测试。
  A: 必备能力。

- [x] Q: 工作流组件是否应该自己获取数据或执行后端流转？
  Context: 这决定组件是平台化渲染层，还是新的流程执行层。
  A: 不应该。组件保持 render-only + action handoff，数据获取和流转执行继续由调用方与现有 data-access 路径负责。
