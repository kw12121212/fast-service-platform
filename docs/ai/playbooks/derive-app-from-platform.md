# Playbook: Derive App From Platform

适用场景：

- AI 要从当前仓库派生一个独立新应用骨架
- 需要按模块选择生成应用，而不是直接改当前母体应用
- 需要验证模块装配结果是否符合平台契约

## 先读什么

1. `docs/ai/context.yaml`
2. `docs/ai/ai-tool-orchestration-contract.json`
3. `docs/ai/app-assembly-contract.json`
4. `docs/ai/schemas/app-manifest.schema.json`
5. `docs/ai/module-registry.json`
6. `docs/ai/generated-app-verification-contract.json`
7. `docs/ai/derived-app-lifecycle-contract.json`
8. `docs/ai/platform-release.json`
9. `docs/ai/schemas/derived-app-lifecycle-contract.schema.json`
10. `docs/ai/schemas/derived-app-lifecycle-metadata.schema.json`
11. `docs/ai/compatibility/app-assembly-suite.json`
12. 一个示例 manifest：
   - `docs/ai/manifests/default-baseline-app.json`
   - `docs/ai/manifests/core-admin-app.json`

优先级：

- 规范事实来源是 `contract + schema + compatibility suite`
- AI 默认先读 orchestration contract，再决定走 `platform-tool.sh` 的哪个 workflow
- generated app 的验证标准事实来源是 `generated-app verification contract`
- generated app 的生命周期和升级评估事实来源是 `derived-app lifecycle contract + platform release metadata`
- `scripts/scaffold-derived-app.mjs` 是当前参考实现，不是唯一合法实现
- `tools/java-assembly-cli/` 是仓库内的第二个兼容实现
- `scripts/verify-derived-app.mjs` 是当前 reference verifier，不是 generated-app verification contract 本身
- `tools/java-generated-app-verifier/` 是仓库内的 Java compatible verifier

## 标准派生路径

AI 编排顺序：

1. 读 `docs/ai/ai-tool-orchestration-contract.json`
2. 读 assembly contract 和 module registry
3. 通过 `platform-tool.sh assembly scaffold ...` 执行装配
4. 装配完成后立刻执行 `platform-tool.sh generated-app verify ...`

只有在 orchestration contract 明确允许时，才回退到具体 wrapper 或实现专用路径。

从仓库根目录执行：

```bash
./scripts/platform-tool.sh assembly scaffold node \
  docs/ai/manifests/core-admin-app.json \
  ../core-admin-console
```

或者执行 Java CLI 兼容实现：

```bash
./scripts/platform-tool.sh assembly scaffold java \
  docs/ai/manifests/core-admin-app.json \
  ../core-admin-console
```

约束：

- `--output` 必须是仓库外的绝对路径
- 生成目标仍然是 `企业内部管理单体应用`
- 不要引入超出平台边界的新外部软件库

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

如果你要验证某个实现是否符合平台标准，优先看 compatibility suite，而不是看它是否和当前 Node 脚本有相同内部结构。
当前仓库内置的兼容实现是 `Node` 和 `Java CLI` 两条路径。

验证某个已生成应用骨架：

```bash
./scripts/platform-tool.sh generated-app verify ../core-admin-console
```

或者用 Java verifier：

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
- 把 `Node` 参考实现误当成规范本身
- 输出目录放在仓库内，污染当前母体应用工作区
- 以为“可选模块”意味着可以忽略依赖关系
- 只生成 manifest，没有检查路由、SQL 合同和生成目录是否真的反映模块选择
