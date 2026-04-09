# Solution-To-Manifest Recommendation

这个 playbook 说明如何在已有 deterministic planning output 的前提下，产出可选的 `solution-to-manifest recommendation` 资产。

## 先读这些

1. `docs/ai/context.yaml`
2. `docs/ai/solution-to-manifest-planning-contract.json`
3. `docs/ai/schemas/solution-to-manifest-planning.schema.json`
4. `docs/ai/solution-to-manifest-recommendation-contract.json`
5. `docs/ai/schemas/solution-to-manifest-recommendation.schema.json`
6. `docs/ai/descriptor-driven-management-module-contract.json`
7. `docs/ai/schemas/descriptor-driven-management-module.schema.json`
8. `docs/ai/module-registry.json`
9. 一个 solution-to-manifest plan example:
   - `docs/ai/solution-plans/core-admin-console.solution-to-manifest-plan.json`
10. 一个 solution-to-manifest recommendation example:
   - `docs/ai/solution-recommendations/core-admin-console.solution-to-manifest-recommendation.json`
11. 一个 descriptor example:
   - `docs/ai/management-modules/department-directory.management-module.json`
12. `docs/ai/app-assembly-contract.json`

## 默认原则

- recommendation 是 `optional guidance`，不是 assembly runtime contract。
- recommendation 必须依赖已存在的 `solution-to-manifest plan`，不要绕过 planning 重新发明一套 deterministic 规则。
- 显式约束和 planning 已确定的 mandatory facts，必须继续以 `acceptedConstraints` 暴露，而不是伪装成“可选建议”。
- recommendation 可以建议 manifest shape、profile 倾向或 optional module defer/include，但最终 assembly 输入仍然是 standalone `app-manifest`。
- 如果后续要产出 descriptor，它也只能消费 planning / recommendation 的结果，不能替代 standalone `app-manifest`。

## 标准顺序

1. 先确认 solution input 和 planning output 都合法
2. 从 planning output 提取已确定的 included/excluded modules、decision basis、manifestPreparation 状态
3. 根据 module registry profile 和依赖事实，补充 repository-owned heuristic guidance
4. 把 mandatory constraints 写进 `acceptedConstraints`
5. 把可调整的 guidance 写进 `recommendations`
6. 产出 `manifestShaping` 和 `followUpGuidance`
7. 如果需要 descriptor-driven module path，再把 recommendation guidance 收敛进 bounded descriptor
8. 最后仍然回到 standalone `app-manifest`

## recommendation 至少要回答什么

- 当前哪些结论是已接受的约束，不能只靠 recommendation 覆盖掉
- 当前推荐的 profile 或 manifest shape 是什么
- 当前推荐 defer 或 include 的 optional modules 是什么
- 每条 recommendation 的 basis、confidence、prerequisites 和 boundary notes 是什么
- 如果贡献者要偏离 recommendation，哪些情况只需记录原因，哪些情况必须回到 planning 重新生成

## 常见坑

- 把 recommendation 直接当成 assembly 输入
- 没有 planning output 就开始写 recommendation
- 把显式 excludedModules 或 requiredModules 伪装成可自由接受/拒绝的建议
- recommendation 只给结论，不写 basis、confidence 或 prerequisite
- 以为 recommendation 会自动扩成 workflow-specific descriptor generation
- recommendation 改了，但没有回到 standalone `app-manifest` 做最终收敛
