# Fast Service Platform Baseline Demo

This application skeleton was derived from Fast Service Platform.

## Selected Modules

- `admin-shell`
- `user-management`
- `role-permission-management`
- `project-management`
- `project-repository-management`
- `kanban-management`
- `ticket-management`

## AI Tooling

When an AI agent works with this generated application against the source platform repository:

- Read `docs/ai/ai-tool-orchestration-contract.json` from the source platform repository first
- Read `docs/ai/structured-app-template-contract.json` and `docs/ai/template-classifications/default-derived-app-template-map.json` before customizing generated output
- Prefer `./scripts/platform-tool.sh` in the source platform repository before using workflow-specific wrappers
- Stop and report blockers when the repository-owned façade and allowed fallback wrappers are both unavailable

## Validation

Run inside this generated application:

```bash
./scripts/verify-derived-app.sh
```

Or from the source platform repository:

```bash
./scripts/platform-tool.sh generated-app verify /absolute/path/to/fast-service-platform-baseline-demo
```

## Upgrade Evaluation

Evaluate this derived application against the current platform release from the source repository:

```bash
./scripts/platform-tool.sh upgrade evaluate /absolute/path/to/fast-service-platform-baseline-demo
```

Read the current platform release advisory from the source repository:

```bash
./scripts/platform-tool.sh upgrade advisory /absolute/path/to/fast-service-platform-baseline-demo
```

Inspect the repository-supported upgrade targets for this derived application:

```bash
./scripts/platform-tool.sh upgrade targets /absolute/path/to/fast-service-platform-baseline-demo
```

Preview the repository-owned upgrade plan:

```bash
./scripts/platform-tool.sh upgrade execute /absolute/path/to/fast-service-platform-baseline-demo
```

Apply the supported repository-owned upgrade actions:

```bash
./scripts/platform-tool.sh upgrade execute /absolute/path/to/fast-service-platform-baseline-demo --apply
```
