# Playbook: Select Derived App Upgrade Target

适用场景：

- 已经有一个从平台派生出去的独立应用骨架
- 需要先判断仓库正式支持哪些 target release
- 需要在做 upgrade evaluation 或 execution 之前确认 lineage 和 upgrade path

## 先读什么

1. `docs/ai/context.yaml`
2. `docs/ai/platform-release.json`
3. `docs/ai/platform-release-history.json`
4. `docs/ai/platform-release-advisory.json`
5. `docs/ai/schemas/platform-release-history.schema.json`
6. 派生应用里的：
   - `docs/ai/derived-app-lifecycle.json`
   - `docs/ai/context.json`

优先级：

- `platform-release-history.json` 是 release lineage 和 supported upgrade path 的事实来源
- `platform-release-advisory.json` 解释某个 target release 的 observable delta 和 follow-up checks
- 这一步的目标是“选择仓库支持的 target release”，不是直接执行升级

## 标准选择路径

从平台仓库根目录执行：

```bash
./scripts/list-platform-upgrade-targets.sh /absolute/path/to/derived-app
```

如果你只想看仓库当前识别的 release history，也可以不传派生应用目录：

```bash
./scripts/list-platform-upgrade-targets.sh
```

## 输出怎么理解

- `recognizedReleases`
  表示仓库当前 lifecycle / upgrade 工作流认可的 release 集合。
- `availableTargetReleases`
  表示对当前派生应用来源 release 来说，仓库正式支持的 target releases。
- `supportedUpgradePaths`
  表示 source-to-target 的路径定义、path type、support status 和 advisory 引用。

如果已经选定 target release，再转到：

- `docs/ai/playbooks/evaluate-derived-app-upgrade.md`
