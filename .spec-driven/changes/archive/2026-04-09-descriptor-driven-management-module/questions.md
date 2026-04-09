# Questions: descriptor-driven-management-module

## Open

<!-- No open questions -->

## Resolved

- [x] Q: 第一条 descriptor-driven change 是否只做一个窄范围 management-module shape，并优先复用 `dynamic form` 与 `dynamic report`？
  Context: 这决定 proposal 是否同时吞并 report / workflow 专项 generation，还是先建立最小可验证的 descriptor spine。
  A: 是。当前 change 只定义单一、窄范围的 management-module 生成主路径，先复用 `dynamic form` 与 `dynamic report`；workflow 专项 generation 保持为后续独立 planned change。
