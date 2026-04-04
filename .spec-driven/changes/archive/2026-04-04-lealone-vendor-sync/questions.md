# Questions: lealone-vendor-sync

## Open

<!-- No open questions -->

## Resolved

- [x] Q: `vendor/lealone` 是否有本地未推送的 patch 需要 rebase？
  Context: 若有本地修改，git pull 前需要先处理，否则会产生冲突。
  A: 没有，可以直接 pull。

- [x] Q: 是否需要在 `AGENTS.md` / `README.md` 中更新 Tomcat 版本说明？
  Context: 文档与实际依赖版本保持一致，便于 AI agent 和贡献者参考。
  A: 需要，在 `AGENTS.md` 中补充。
