# Playbook: Evaluate Derived App Upgrade

适用场景：

- 已经有一个从平台派生出去的独立应用骨架
- 需要判断它是否仍然兼容当前平台发布
- 需要为后续升级准备输入，而不是直接自动 merge 或 rebase

## 先读什么

1. `docs/ai/context.yaml`
2. `docs/ai/ai-tool-orchestration-contract.json`
3. `docs/ai/derived-app-lifecycle-contract.json`
4. `docs/ai/platform-release.json`
5. `docs/ai/platform-release-history.json`
6. `docs/ai/platform-release-advisory.json`
7. `docs/ai/schemas/derived-app-lifecycle-contract.schema.json`
8. `docs/ai/schemas/derived-app-lifecycle-metadata.schema.json`
9. `docs/ai/schemas/platform-release-history.schema.json`
10. `docs/ai/schemas/platform-release-advisory.schema.json`
11. 派生应用里的：
   - `app-manifest.json`
   - `docs/ai/context.json`
   - `docs/ai/derived-app-lifecycle.json`

优先级：

- 规范事实来源是 lifecycle contract、platform release metadata、platform release history 和生成应用自带的 lifecycle metadata
- AI 默认先读 orchestration contract，并按 `targets -> advisory -> evaluate` 的顺序编排仓库工具
- release advisory 是“当前平台发布改了什么、影响哪些模块、建议先检查什么”的机器可读说明
- release history 负责回答“这个来源 release 是否被识别、允许升到哪些 target release”
- 这一步的目标是“评估升级兼容性”，不是直接做自动代码合并
- repository-owned evaluator 是默认入口，不要把某个实现内部逻辑当成规范

## 标准评估路径

从平台仓库根目录执行：

```bash
./scripts/platform-tool.sh upgrade evaluate /absolute/path/to/derived-app
```

先看支持的 upgrade target：

```bash
./scripts/platform-tool.sh upgrade targets /absolute/path/to/derived-app
```

查看当前平台发布的 advisory：

```bash
./scripts/platform-tool.sh upgrade advisory /absolute/path/to/derived-app
```

## evaluator 会检查什么

- 派生应用是否暴露必需的 lifecycle metadata
- lifecycle metadata 是否声明了平台来源和 contract 版本
- 派生应用的 source release 是否在标准化 release history 里被识别
- source release 到目标 release 的 upgrade path 是否被仓库声明为支持
- 当前平台 release 是否支持该 lifecycle metadata 版本
- 当前平台 release 是否仍支持派生应用声明的 assembly contract 版本
- 派生应用选择的模块是否仍然是当前平台已知模块

## 输出怎么理解

- `compatible: true`
  表示该派生应用满足当前仓库定义的升级输入和兼容性前提，可以进入后续人工升级或差异审查。
- `compatible: false`
  表示至少有一项 contract 级输入不满足，通常需要先补 lifecycle metadata、手工迁移，或重新派生。
- `recommendedAction`
  表示仓库建议的下一步动作，不等于自动执行升级。

advisory 输出补充回答：

- 当前 release 相对前一个 release 改了什么
- 哪些模块和 contract 被影响
- 仓库建议先做哪些检查

如果决定继续执行受控升级，转到：

- `docs/ai/playbooks/execute-derived-app-upgrade.md`

## 当前边界

- 这条路径只负责评估和准备升级输入
- 它不自动 merge、rebase 或解决冲突
- 它不承诺不同平台版本之间一定存在无损自动迁移
