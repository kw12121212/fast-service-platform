# Questions: add-derived-app-runtime-smoke

## Open

<!-- No open questions -->

## Resolved

- [x] Q: 下一步是否优先补 derived app 的 runtime trust gap，而不是继续按旧 roadmap 顺序先做新的 UI 能力面？
  Context: 这决定 proposal 是优先补 `dynamic-report-component`，还是先补 derived app 的运行证明能力。
  A: 优先补 derived app runtime smoke，对应 roadmap 里的 trust gap。

- [x] Q: 第一版 runtime smoke 是否先收敛为最小 backend/frontend `/service/*` 代理证明，而不是扩成浏览器级 E2E？
  Context: 这决定 proposal 的实现边界、成本和失败面。
  A: 是，V1 只要求最小运行证明和可定位失败，不扩成完整 UI 自动化。
