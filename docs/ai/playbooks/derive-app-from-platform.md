# Playbook: Derive App From Platform

适用场景：

- AI 要从当前仓库派生一个独立新应用骨架
- 需要按模块选择生成应用，而不是直接改当前母体应用
- 需要验证模块装配结果是否符合平台契约

## 先读什么

1. `docs/ai/context.yaml`
2. `docs/ai/ai-solution-input-contract.json`
3. `docs/ai/schemas/ai-solution-input.schema.json`
4. `docs/ai/solution-to-manifest-planning-contract.json`
5. `docs/ai/schemas/solution-to-manifest-planning.schema.json`
6. `docs/ai/solution-to-manifest-recommendation-contract.json`
7. `docs/ai/schemas/solution-to-manifest-recommendation.schema.json`
8. `docs/ai/ai-tool-orchestration-contract.json`
9. `docs/ai/app-assembly-contract.json`
10. `docs/ai/schemas/app-manifest.schema.json`
11. `docs/ai/module-registry.json`
12. `docs/ai/generated-app-verification-contract.json`
13. `docs/ai/structured-app-template-contract.json`
14. `docs/ai/template-classifications/default-derived-app-template-map.json`
15. `docs/ai/derived-app-lifecycle-contract.json`
16. `docs/ai/platform-release.json`
17. `docs/ai/schemas/derived-app-lifecycle-contract.schema.json`
18. `docs/ai/schemas/derived-app-lifecycle-metadata.schema.json`
19. `docs/ai/compatibility/app-assembly-suite.json`
20. 一个示例 solution input：
   - `docs/ai/solution-inputs/core-admin-console.solution-input.json`
21. 一个示例 solution-to-manifest plan：
   - `docs/ai/solution-plans/core-admin-console.solution-to-manifest-plan.json`
22. 一个示例 solution-to-manifest recommendation：
   - `docs/ai/solution-recommendations/core-admin-console.solution-to-manifest-recommendation.json`
23. 一个示例 manifest：
   - `docs/ai/manifests/default-baseline-app.json`
   - `docs/ai/manifests/core-admin-app.json`

优先级：

- 规范事实来源是 `contract + schema + compatibility suite`
- 如果当前输入还是业务目标，先用 `ai-solution-input-contract` 收敛为 solution input，再产出 `solution-to-manifest plan`，按需评估 `solution-to-manifest recommendation`，最后映射成 `app-manifest`
- AI 默认先读 orchestration contract，再决定走 `platform-tool.sh` 的哪个 workflow
- generated app 的验证标准事实来源是 `generated-app verification contract`
- generated output 的覆盖边界事实来源是 `structured app template contract + default template classification map`
- generated app 的生命周期和升级评估事实来源是 `derived-app lifecycle contract + platform release metadata`
- repository-owned assembly tooling 默认走 `platform-tool.sh` 和 Java 主路径
- `tools/java-assembly-cli/` 是当前仓库拥有的 assembly 实现工作区
- `scripts/VerifyDerivedApp.java` 是 generated-app verification 的 repository-owned Java 入口
- `tools/java-generated-app-verifier/` 保留为兼容 Java verifier 实现

## 标准派生路径

AI 编排顺序：

1. 读 `docs/ai/ai-tool-orchestration-contract.json`
2. 如果当前还没有 manifest，先读 `docs/ai/playbooks/define-ai-solution-input.md`
3. 读 assembly contract 和 module registry
4. 先把 solution input 映射成 `solution-to-manifest plan`
5. 如果需要 recommendation guidance，再把 planning output 映射成 `solution-to-manifest recommendation`
6. 再把 planning output 和可选 recommendation 映射成 `app-manifest`
7. 通过 `platform-tool.sh assembly scaffold ...` 执行装配
8. 装配完成后立刻执行 `platform-tool.sh generated-app verify ...`
9. 如果当前环境可启动 generated backend/frontend，再执行 `platform-tool.sh generated-app smoke ...` 补 runtime 证明

只有在 orchestration contract 明确允许时，才回退到具体 wrapper 或实现专用路径。

从仓库根目录执行：

```bash
./scripts/platform-tool.sh assembly scaffold \
  docs/ai/manifests/core-admin-app.json \
  ../core-admin-console
```

约束：

- `--output` 必须是仓库外的绝对路径
- 生成目标仍然是 `企业内部管理单体应用`
- 不要引入超出平台边界的新外部软件库
- `solution input` 不是 assembly runtime contract；真正传给 tooling 的仍然是 `app-manifest`
- `solution-to-manifest plan` 也不是 assembly runtime contract；它只是显式 planning 输出
- `solution-to-manifest recommendation` 也不是 assembly runtime contract；它只是可选 guidance 输出
- 生成完成后如果要做定制，先按 template classification map 判断当前路径是 `stable-template`、`slot-host`、`module-fragment` 还是 `customization-zone`

## 模块选择规则

- 必选核心模块由 `docs/ai/module-registry.json` 定义
- 可选业务模块当前包括：
  - `project-management`
  - `project-repository-management`
  - `kanban-management`
  - `ticket-management`
- 依赖关系也以 registry 为准，不要手工猜

## 派生后如何验证

在仓库里验证装配系统本身：

```bash
./scripts/platform-tool.sh assembly verify
```

如果你要验证某个实现是否符合平台标准，优先看 compatibility suite，而不是看它是否和某个旧实现有相同内部结构。
当前仓库内置的 repository-owned 路径是 `Java`；兼容实现保留 `Java CLI` / `Java verifier` 的专用 wrapper。

验证某个已生成应用骨架：

```bash
./scripts/platform-tool.sh generated-app verify ../core-admin-console
```

如果你要验证这个 generated app 的真实运行链路：

```bash
./scripts/platform-tool.sh generated-app smoke ../core-admin-console
```

这个入口会启动 generated backend 和 generated frontend，并通过 frontend `/service/*` 代理验证最小读接口。

如果你要显式走兼容 Java verifier：

```bash
./scripts/platform-tool.sh generated-app verify-java ../core-admin-console
```

这些路径都应该满足 `docs/ai/generated-app-verification-contract.json`，
而不是把某个具体脚本本身视为规范。

或者在生成后的应用目录里执行：

```bash
./scripts/verify-derived-app.sh
```

## 派生后如何准备升级

如果你要判断一个已派生应用是否还能对齐当前平台发布，优先走 repository-owned evaluator：

```bash
./scripts/platform-tool.sh upgrade evaluate ../core-admin-console
```

这个入口读取生成应用自带的 lifecycle metadata，并用当前平台的 `docs/ai/platform-release.json` 做兼容性判断。

## 常见坑

- 只看文档，不读 `module-registry.json`，导致模块依赖判断错误
- 不看 `structured-app-template-contract.json` 和 classification map，就直接编辑生成输出
- 把某个旧的 `Node` 辅助脚本误当成规范本身
- 输出目录放在仓库内，污染当前母体应用工作区
- 以为“可选模块”意味着可以忽略依赖关系
- 只生成 manifest，没有检查路由、SQL 合同和生成目录是否真的反映模块选择
