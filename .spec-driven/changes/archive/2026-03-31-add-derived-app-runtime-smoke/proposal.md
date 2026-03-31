# add-derived-app-runtime-smoke

## What

为 derived app 增加 repository-owned 的 runtime smoke validation 路径，用来证明“派生出来的应用不只是结构正确，也能实际启动并通过 frontend `/service/*` 代理链路完成最小联调”。

这次 change 会把 runtime smoke 明确收敛为一个独立验证层：

- 面向 derived app 目录，而不是只验证当前仓库主 workspace
- 通过 generated frontend 的 dev server 代理请求 generated backend
- 至少验证最小 `/service/*` JSON 接口链路可用
- 失败时给出可定位的阶段性错误，而不是只报一个笼统失败
- baseline demo 要复用同一条 smoke path，而不是保留一条只做 build 的演示验证路径

## Why

当前仓库已经具备：

- generated-app contract verification
- repository-owned baseline demo
- 主 workspace 的 `verify-fullstack.sh`
- assembly、upgrade、release 和 template 相关 contract

但 derived app 这一层仍有一个明显缺口：仓库能证明“生成结果结构正确”，却还不能稳定证明“生成结果真的能跑起来，并通过 frontend 代理连上 backend”。

这会带来两个问题：

- AI 和人类贡献者对 derived app 的信任仍停在结构层，缺少 runtime 级证据
- `verify-baseline-demo.sh` 当前更接近 contract + build 检查，还不足以证明 demo 的真实联调链路可用

roadmap 里把这一类问题描述为“increasing trust that derived applications are runnable, verifiable, and maintainable”。相比继续扩展新的 UI 能力面，这个缺口更接近当前平台的信任瓶颈。

## Scope

In scope:

- 定义 derived app runtime smoke validation 的 repository-owned 行为边界
- 要求 smoke path 针对一个 derived app 目录执行，而不是依赖当前仓库主应用路径
- 要求 smoke path 通过 generated frontend 的 `/service/*` 代理验证 generated backend，而不是只打 backend 直连接口
- 要求 smoke path 至少检查最小读接口返回有效 JSON，作为 V1 的最小运行证明
- 要求 runtime smoke 和现有 generated-app contract verification 分层存在，而不是互相替代
- 要求 baseline demo 的验证入口纳入这条 runtime smoke path
- 要求 AI quickstart / context / demo guide 等入口能发现这条验证路径

Out of scope:

- 不引入浏览器级 UI 自动化或完整 E2E 测试框架
- 不把 runtime smoke 扩成覆盖所有页面和全部业务写流程
- 不在这次 change 里顺手实现 `dynamic-report-component`
- 不要求一次性验证所有可能的 derived app 变体矩阵
- 不修改当前 generated-app verification contract 的核心语义，使其从结构验证变成运行时测试合同
- 不引入新的大型外部依赖栈

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):

- 现有主 workspace 的 `verify-fullstack.sh` 仍然是仓库当前 baseline app 的联调 smoke 路径
- generated-app verification 仍然负责结构和合同层验证，不被 runtime smoke 替代
- baseline demo 仍然必须来自 repository-owned assembly 路径，而不是手工维护的平行应用
- 平台当前产品边界、模块边界和 unified tooling façade 不因这次 change 扩大
