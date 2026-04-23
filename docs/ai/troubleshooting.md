# AI Troubleshooting

这个文档只覆盖当前仓库最常见的本地失败，不替代完整日志分析。

## `vendor/lealone` 缺失

症状：

- `./scripts/verify-backend.sh` 或 `./scripts/verify-fullstack.sh` 在 Maven 阶段失败
- 本地缺少 `com.lealone.*` 相关依赖

处理：

1. 初始化 git submodule：
   ```bash
   git submodule update --init vendor/lealone
   ```
2. 执行：

```bash
./scripts/install-lealone-source-deps.sh
```

## Java 或 Maven 路径不一致

症状：

- Maven 编译报 Java 版本不匹配
- 脚本里找不到 `java` 或 `mvn`

处理：

- 默认约定：
  - `JAVA_HOME=$HOME/.sdkman/candidates/java/25.0.2-tem`
  - `MVN_BIN=$HOME/.sdkman/candidates/maven/current/bin/mvn`
- 如果本机不同，用环境变量覆盖再执行脚本

示例：

```bash
JAVA_HOME=/path/to/java25 MVN_BIN=/path/to/mvn ./scripts/verify-backend.sh
```

## `bun` 或 Node 版本不一致

症状：

- `./scripts/verify-frontend.sh` 里找不到 `bun`
- frontend 安装或构建时报 Node 版本相关错误

处理：

- 确认当前 Node 基线是 `Node 24`
- 确认 `bun` 已安装并在 `PATH` 中
- 如依赖不完整，先在 `frontend/` 下执行：

```bash
bun install --frozen-lockfile
```

## backend runtime classpath 缺失

症状：

- full-stack smoke 在启动 backend 时提示 `target/runtime-classpath.txt` 缺失
- Java 启动时找不到 Lealone 运行时类

处理：

- 重新执行：

```bash
./scripts/verify-fullstack.sh
```

说明：

- 该脚本会先通过 Maven 准备 `backend/target/runtime-classpath.txt`

## `8080` 或 `4173` 端口冲突

症状：

- full-stack smoke 无法启动 backend 或 frontend dev server
- 日志里出现 bind / address already in use

处理：

- 先检查本机是否已有旧进程占用
- 如果 `8080` 上已有可用 backend，smoke 脚本会优先复用它
- 如果 `4173` 被占用，先释放端口后重试，因为 smoke 需要固定端口验证代理链路

## `/service/*` 返回失败

症状：

- frontend 页面报 backend request failed
- smoke 脚本能起服务，但代理请求失败

处理顺序：

1. 先直连 backend 检查 `http://127.0.0.1:8080/service/user_service/listUsers`
2. 再检查 frontend dev server 的 `http://127.0.0.1:4173/service/user_service/listUsers`
3. 如果前者通、后者不通，问题通常在 Vite proxy
4. 如果两者都不通，问题通常在 backend 启动或数据初始化

## demo 数据看起来缺失

症状：

- 页面只看到空数据态
- smoke 依赖的列表接口返回空数组

处理：

- 确认 backend 以 `-Dfsp.demo-data=true` 启动
- 检查 `DemoDataSupport` 是否仍保持幂等加载
- 如果你修改了 demo 数据逻辑，重新跑 backend 测试和 full-stack smoke
