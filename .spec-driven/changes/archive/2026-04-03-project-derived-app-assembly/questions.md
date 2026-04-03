# Questions: project-derived-app-assembly

## Open

<!-- No open questions -->

## Resolved

- [x] Q: 第一版 project-scoped assembly 对已经存在的输出目录允许到什么程度？
  Context: 这会影响请求校验边界，以及“显式绝对输出目录”在首版里是否允许指向已存在但为空、已存在且非空、或必须不存在的目录。
  A: 可以存在，但必须为空目录。

- [x] Q: “latest visible assembly outcome” 是否需要跨服务重启持久化？
  Context: backend 和 frontend 都需要知道 outcome 只是当前进程可见状态，还是项目级持久状态；这会直接影响结果存储边界。
  A: 需要。

- [x] Q: 第一版 Projects 体验里的 manifest 输入是否只要求直接粘贴或编辑原始 `app-manifest` 内容？
  Context: 这会决定首版前端范围是否只需要一个直接输入表单，还是要支持文件选择等额外交互。
  A: 是。
