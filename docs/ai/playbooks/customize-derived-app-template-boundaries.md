# Playbook: Customize Derived App Template Boundaries

适用场景：

- AI 或人工贡献者需要在已生成应用里做定制
- 需要判断某个路径是平台管理、模块管理还是派生应用自管
- 需要在升级前判断哪些修改落在安全 customization zone 内

## 先读什么

1. `docs/ai/context.yaml`
2. `docs/ai/structured-app-template-contract.json`
3. `docs/ai/schemas/structured-app-template-contract.schema.json`
4. `docs/ai/template-classifications/default-derived-app-template-map.json`
5. `docs/ai/schemas/derived-app-template-map.schema.json`
6. `docs/ai/app-assembly-contract.json`
7. `docs/ai/derived-app-lifecycle-contract.json`
8. `docs/ai/derived-app-upgrade-execution-contract.json`
9. `docs/ai/ai-tool-orchestration-contract.json`

如果是针对某个已生成应用，再读：

- `app-manifest.json`
- `docs/ai/context.json`
- `docs/ai/derived-app-lifecycle.json`

## 怎么理解 template system

- `stable-template`
  - 平台拥有的稳定输出
  - repository-owned upgrade 默认可以刷新
- `slot-host`
  - 平台拥有的宿主文件
  - 允许模块片段按 contract 注入
  - 不应把它当作任意自由编辑区
- `module-fragment`
  - 某个已选模块贡献出来的输出或片段
  - 如果平台模块升级，通常需要 review
- `customization-zone`
  - 派生应用自管区域
  - repository-owned upgrade 默认不覆盖

## AI 的默认决策顺序

1. 先看 `docs/ai/template-classifications/default-derived-app-template-map.json`
2. 判断目标路径是 `exact` 还是命中了某个 `prefix`
3. 读取该条目的 `unitType`、`ownership`、`upgradeBehavior`
4. 如果是 `platform-managed` 的 `stable-template` 或 `slot-host`
   - 默认不要直接改
   - 先判断是否应通过 assembly / upgrade workflow 刷新
5. 如果是 `module-managed`
   - 先确认对应模块是否真的被选中
   - 变更前评估后续 upgrade 是否需要 manual review
6. 如果是 `derived-managed`
   - 优先把定制放在这些区域

## 推荐的定制策略

- 新增派生应用专有前端能力：
  - 优先放在 `frontend/src/features/custom/`
- 新增派生应用专有后端能力：
  - 优先放在 `backend/src/main/java/com/fastservice/platform/backend/custom/`
- 不要把派生应用特有逻辑直接塞进：
  - `frontend/src/app/router.tsx`
  - `frontend/src/app/navigation.ts`
  - `backend/src/main/resources/sql/tables.sql`
  - `backend/src/main/resources/sql/services.sql`
  - `backend/src/main/resources/sql/demo.sql`

这些路径是 contract 里的 `slot-host`，它们首先属于平台模板边界，不是随意拼接区。

## 升级前怎么检查

先执行：

```bash
./scripts/platform-tool.sh upgrade targets /absolute/path/to/derived-app
./scripts/platform-tool.sh upgrade advisory /absolute/path/to/derived-app
./scripts/platform-tool.sh upgrade evaluate /absolute/path/to/derived-app
```

如果你怀疑自己改了平台管理区，再看：

```bash
./scripts/platform-tool.sh upgrade execute /absolute/path/to/derived-app
```

重点检查 upgrade plan 里自动刷新项对应的路径，确认这些路径没有承载派生应用私有改动。

## 常见坑

- 把 `slot-host` 误当成 `customization-zone`
- 不看 classification map，直接按文件名猜哪个可以改
- 把模块输出文件当成长期稳定的派生应用自管区
- 明明只是派生应用定制，却改了平台 contract 资产
