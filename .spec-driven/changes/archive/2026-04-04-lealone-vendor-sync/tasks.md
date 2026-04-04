# Tasks: lealone-vendor-sync

## Implementation

- [x] 在 `vendor/lealone` 中执行 `git pull origin master`，拉取至 Apr 3, 2026 最新提交
- [x] 将 `backend/pom.xml` 中 `tomcat-embed-core` 版本从 `11.0.15` 更新为 `11.0.18`
- [x] 将 `backend/pom.xml` 中 `tomcat-annotations-api` 版本从 `11.0.15` 更新为 `11.0.18`
- [x] 在 `AGENTS.md` 的环境说明区块补充 Tomcat 当前版本（`11.0.18`）
- [x] 运行 `scripts/install-lealone-source-deps.sh` 完成本地重建

## Testing

- [x] `scripts/verify-backend.sh` 通过（编译 + 单元测试）

## Verification

- [x] `vendor/lealone` 最新提交 hash 与上游 master 一致
- [x] `backend/pom.xml` 中 Tomcat 版本为 `11.0.18`
- [x] `AGENTS.md` 中已补充 Tomcat 版本说明
