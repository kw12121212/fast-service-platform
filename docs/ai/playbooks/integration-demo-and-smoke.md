# Playbook: Integration, Demo Data, And Smoke

适用场景：

- 改动影响 frontend 和 backend 的联调边界
- 改动依赖 demo 数据可见性
- 需要验证 `/service/*` 代理路径是否真的可用

## 联调原则

- frontend 不是纯静态 mock，它直接代理 backend `/service/*`
- 涉及联调的改动，优先验证代理链路，而不是只验证单边测试
- 如果 smoke 只打 backend 直连接口，那不算完成当前仓库的联调验证

## 当前标准 smoke path

从仓库根目录执行：

```bash
./scripts/verify-fullstack.sh
```

脚本会做这些事：

1. 准备 backend runtime classpath
2. 用 demo data 启动 backend
3. 启动 Vite dev server
4. 通过 frontend dev server 的 `/service/*` 代理请求 backend
5. 校验用户列表和项目列表接口返回 JSON 数组

## Derived app 的 smoke path

如果你要验证一个 derived app，而不是当前仓库主 workspace，从源平台仓库根目录执行：

```bash
./scripts/platform-tool.sh generated-app smoke /absolute/path/to/derived-app
```

这条路径会：

1. 准备 generated backend runtime classpath
2. 启动 generated backend
3. 启动 generated frontend dev server
4. 通过 generated frontend 的 `/service/*` 代理请求 generated backend
5. 校验最小读接口返回有效 JSON

注意：

- 这条路径和 `generated-app verify` 分层存在
- `generated-app verify` 证明结构与 contract 资产
- `generated-app smoke` 证明真实运行链路

## 什么时候需要补看 demo 数据

- 页面依赖非空数据态才可观察
- backend 读接口需要最小实体关系才能通过
- 新功能希望在第一次 smoke 里就能被看见

优先检查：

- `backend/src/main/resources/sql/demo.sql`
- `backend/src/main/java/com/fastservice/platform/backend/demo/DemoDataSupport.java`

## 修改 demo 数据时的要求

- 保持幂等
- 不要破坏现有 demo 主键或最小实体关系，除非 spec 明确要求
- 如果 demo 数据路径变化，重新跑 `./scripts/verify-fullstack.sh`

## 常见坑

- backend 能起，但 frontend 代理没通
- 只跑了 frontend 的 `test/build/lint`，没有验证真实 `/service/*`
- backend 没有 demo data，页面空态被误认为接口失败
- backend 运行时 classpath 文件缺失，导致 smoke 启动阶段就失败
