# Playbook: Define AI Solution Input

适用场景：

- AI 或人工贡献者需要先描述“应用想做什么”，再进入 manifest-driven assembly
- 需要把自然语言、角色、对象、流程和 UI 参考整理成 machine-readable 的结构化输入
- 需要明确哪些信息属于高层 solution input，哪些信息必须在 assembly 前收敛成 `app-manifest`

## 先读什么

1. `docs/ai/context.yaml`
2. `docs/ai/ai-solution-input-contract.json`
3. `docs/ai/schemas/ai-solution-input.schema.json`
4. `docs/ai/ai-tool-orchestration-contract.json`
5. `docs/ai/app-assembly-contract.json`
6. `docs/ai/module-registry.json`
7. 一个 solution input example：
   - `docs/ai/solution-inputs/core-admin-console.solution-input.json`
8. 一个对应的 manifest example：
   - `docs/ai/manifests/core-admin-app.json`

## 默认原则

- `solution input` 用来描述业务意图，不是 assembly runtime contract。
- `app-manifest` 仍然是 repository-owned assembly tooling 的直接输入。
- AI 应先整理 solution input，再把它收敛成 `app-manifest`，最后调用 `platform-tool.sh assembly scaffold ...`。
- `uiReferences` 只提供 UI 方向，不直接绕过模块选择或 manifest 约束。

## 标准顺序

1. 用 `docs/ai/ai-solution-input-contract.json` 定义结构化输入
2. 结合 `docs/ai/module-registry.json` 判断需要哪些模块
3. 把 solution input 映射成 `app-manifest`
4. 再调用：

```bash
./scripts/platform-tool.sh assembly scaffold <manifest-path> <absolute-output-dir>
```

## solution input 应该包含什么

- 应用名称和业务摘要
- 产品边界：`enterprise-internal-management` + `monolith`
- 关键角色
- 关键领域对象
- 关键流程
- 可选的 UI 参考来源
- 对模块的显式约束

## 哪些内容必须留到 manifest 层

- `application.id`
- `application.packagePrefix`
- `modules`

即使 solution input 给了 `idHint` 或 `packagePrefixHint`，AI 仍然要先产出一个合法的 `app-manifest`，再进入 assembly tooling。

## 常见坑

- 把 solution input 当成可以直接替代 `app-manifest`
- 只写业务摘要，不写角色和流程，导致后续模块选择缺少依据
- 让 `uiReferences` 直接决定模块，而不是通过 solution input -> manifest 的映射过程
- 在没有 manifest 的情况下直接调用 `platform-tool.sh assembly scaffold`
