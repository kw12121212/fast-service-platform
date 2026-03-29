# Playbook: Execute Derived App Upgrade

适用场景：

- 已经完成 derived-app upgrade evaluation 和 release advisory 阅读
- 需要先看升级计划，再决定是否应用仓库拥有的升级动作
- 需要区分自动可应用项和人工介入项

## 先读什么

1. `docs/ai/context.yaml`
2. `docs/ai/derived-app-upgrade-execution-contract.json`
3. `docs/ai/derived-app-lifecycle-contract.json`
4. `docs/ai/platform-release.json`
5. `docs/ai/platform-release-history.json`
6. `docs/ai/platform-release-advisory.json`
7. `docs/ai/schemas/derived-app-upgrade-execution-contract.schema.json`
8. 派生应用里的：
   - `app-manifest.json`
   - `docs/ai/context.json`
   - `docs/ai/derived-app-lifecycle.json`

如果还没确认 target release，先执行：

```bash
./scripts/platform-tool.sh upgrade targets /absolute/path/to/derived-app
```

## 标准执行路径

先看 dry-run plan：

```bash
./scripts/platform-tool.sh upgrade execute /absolute/path/to/derived-app
```

确认后再应用仓库拥有的升级动作：

```bash
./scripts/platform-tool.sh upgrade execute /absolute/path/to/derived-app --apply
```

## 第一版会做什么

- 生成 machine-readable upgrade plan
- 更新仓库拥有的 machine-readable 合同资产
- 刷新 `docs/ai/context.json`
- 刷新 `docs/ai/derived-app-lifecycle.json`
- 刷新本地 verifier 脚本

## 第一版不会做什么

- 不自动 merge Git 历史
- 不自动解决业务代码冲突
- 不自动修改你已经人工扩展过的业务 README、页面或 Java/TS 业务实现

## 执行后必须复验

```bash
./scripts/platform-tool.sh upgrade targets /absolute/path/to/derived-app
./scripts/platform-tool.sh generated-app verify /absolute/path/to/derived-app
./scripts/platform-tool.sh upgrade evaluate /absolute/path/to/derived-app
./scripts/platform-tool.sh upgrade advisory /absolute/path/to/derived-app
```
