# Design: add-repository-owned-baseline-demo

## Approach

这个 change 把 baseline demo 视为一个 repository-owned 的 derived app，而不是把当前 `frontend/` / `backend/` 直接包装成“演示模式”。

实现思路分四层：

1. `demo artifact`
   在仓库根目录提供专门的 `demo/` 区域，其中包含一个 committed baseline demo derived app，作为人类演示和回归校验的固定样本。

2. `provenance`
   demo 必须保留明确来源，能让贡献者看出：
   - 它基于哪个 manifest / baseline 选择
   - 它通过哪个 repository-owned assembly entrypoint 生成
   - 它不是手工偏离平台主路径维护出来的特殊样本

3. `guide`
   在 `demo/` 区域提供一份人类优先的 `GUIDE`，说明：
   - demo 要展示什么
   - 如何从当前平台重新生成这个 demo
   - 如何启动 backend / frontend 或 demo 自带工作区
   - 如何验证 demo 可演示、可使用且没有明显错误

4. `verification`
   demo 不只要求“能打开”，还要求有 repository-owned 的验证路径。第一版至少要把 generated-app verify、启动检查和面向演示的最小 workflow 检查串起来。

## Key Decisions

- 提交一个仓库内置 demo 成果物，而不是只提供生成脚本。
  理由：用户目标是“向人类展示效果”，需要一个开箱即看的结果，而不是要求演示者先理解 assembly 流程再自己生成。

- demo 定位为 derived app，而不是第二套主开发工作区。
  理由：这样才能证明“本项目作为基础库能生成什么”，而不是只证明当前仓库 baseline 自己能跑。

- demo 必须保留 assembly provenance。
  理由：如果没有明确 provenance，demo 很容易退化成手工维护的分叉样本，失去“基于本项目创建”的可追溯性。

- 第一版使用 baseline 组合，而不是 `core-admin` 精简组合。
  理由：用户要的是 baseline demo，用于展示当前完整企业管理基线，而不是仅展示平台核心。

- `GUIDE` 面向人类优先，但仍要复用平台已有 contract 和 tooling 名称。
  理由：这样既适合现场演示，也不会偏离 AI-ready 和 repository-owned workflow 的统一入口。

## Alternatives Considered

- 只补 README 和演示步骤，不提交 demo 目录。
  否决原因：这不能形成仓库内事实，也不足以证明本项目真的能稳定产出一个可展示样本。

- 只提供运行时生成脚本，在本地临时生成 demo。
  否决原因：对演示者不够直接，也不利于做标准展示样本和回归检查。

- 直接把当前 `frontend/` / `backend/` 当作 demo，不再生成 derived app。
  否决原因：这会弱化“本项目作为基础库可派生应用”的核心价值。

- 单独建一个仓库外部 demo repo。
  否决原因：会把展示路径和平台主路径分离，增加漂移和维护成本。
