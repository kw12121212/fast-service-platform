# Playbook: AI Tool Orchestration

适用场景：

- AI 需要在当前仓库里执行 assembly、verification、advisory、lifecycle 或 upgrade 工作流
- 目标是优先复用仓库已有工具，而不是自己重做现有平台逻辑
- 需要知道默认入口、允许 fallback 的场景，以及什么时候应该停止并上报 blocker

## 先读什么

1. `docs/ai/context.yaml`
2. `docs/ai/ai-tool-orchestration-contract.json`
3. `docs/ai/playbooks/define-ai-solution-input.md`
4. `docs/ai/playbooks/prepare-descriptor-driven-management-module.md`
5. `docs/ai/playbooks/derive-app-from-platform.md`
6. `docs/ai/playbooks/select-derived-app-upgrade-target.md`
7. `docs/ai/playbooks/evaluate-derived-app-upgrade.md`
8. `docs/ai/playbooks/execute-derived-app-upgrade.md`

## 默认原则

- AI 的默认角色是 `tool orchestrator`，不是 assembly 或 verification 的替代实现。
- 对已覆盖的工作流，优先走 `./scripts/platform-tool.sh`。
- 只有在 orchestration contract 明确允许时，才回退到兼容 wrapper 或实现特定实现路径。
- 如果仓库工具和允许的 fallback 都不可用，停止并报告 blocker，不要临时发明新的工作流。

## 推荐顺序

### 派生一个新应用

1. 如果当前输入还是业务意图，先读 `docs/ai/ai-solution-input-contract.json`
2. 再读 `docs/ai/solution-to-manifest-planning-contract.json`，按 `docs/ai/playbooks/define-ai-solution-input.md` 产出 `solution-to-manifest plan`
3. 如果需要 repository-owned guidance，再读 `docs/ai/solution-to-manifest-recommendation-contract.json`，按 `docs/ai/playbooks/prepare-solution-to-manifest-recommendation.md` 评估或产出 `solution-to-manifest recommendation`
4. 如果需要 bounded business-module generation，再读 `docs/ai/descriptor-driven-management-module-contract.json`，按 `docs/ai/playbooks/prepare-descriptor-driven-management-module.md` 产出 descriptor
5. 基于 planning output、可选 recommendation 和可选 descriptor 产出 standalone `app-manifest`
6. 再读 `docs/ai/app-assembly-contract.json` 和 `docs/ai/module-registry.json`
7. 执行：

```bash
./scripts/platform-tool.sh assembly scaffold <manifest-path> <absolute-output-dir>
```

8. 生成后立即执行：

```bash
./scripts/platform-tool.sh generated-app verify <generated-app-dir>
```

### 准备派生应用升级

按这个顺序执行：

1. `./scripts/platform-tool.sh upgrade targets [generated-app-dir]`
2. `./scripts/platform-tool.sh upgrade advisory [generated-app-dir]`
3. `./scripts/platform-tool.sh upgrade evaluate <generated-app-dir>`

这三步的目的分别是：

- 先确认仓库支持的 target release
- 再看当前 release 的 observable delta 和建议检查项
- 最后判断当前派生应用是否满足升级前提

### 执行派生应用升级

只有在 target selection、advisory 和 evaluation 都已经完成后，再执行：

```bash
./scripts/platform-tool.sh upgrade execute <generated-app-dir>
```

确认 dry-run plan 后，才允许继续：

```bash
./scripts/platform-tool.sh upgrade execute <generated-app-dir> --apply
```

升级后复验顺序：

1. `./scripts/platform-tool.sh generated-app verify <generated-app-dir>`
2. `./scripts/platform-tool.sh upgrade evaluate <generated-app-dir>`
3. `./scripts/platform-tool.sh upgrade advisory [generated-app-dir]`

## 允许 fallback 的情况

- `platform-tool.sh` 在当前环境不可用，但对应 wrapper 仍存在且 orchestration contract 明确允许。
- 需要明确选择兼容 Java verifier 这类具体兼容路径。
- 需要执行 façade 尚未暴露、但仓库 contract 已正式保留的兼容 wrapper。

## 应该停止并上报的情况

- 必需 contract、schema 或 generated-app metadata 缺失。
- 当前任务不在 orchestration contract 覆盖的 workflow category 内。
- 默认 façade 和允许的 compatible wrapper 都失败或不可用。
- 当前报错意味着 contract 级 blocker，而不是一次普通重试可恢复的执行失败。
