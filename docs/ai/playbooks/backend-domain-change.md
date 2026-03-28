# Playbook: Backend Domain Change

适用场景：

- 新增一个 backend 领域 service 方法
- 给现有领域加读写能力
- 补领域约束、demo 数据或测试

## 先确认范围

1. 到 `.spec-driven/specs/` 里找到对应主 spec
2. 没有现成 spec 时，先补 proposal / delta spec
3. 确认这次改动没有扩到新业务域或新依赖

## 典型修改顺序

1. 看 `backend/src/main/resources/sql/services.sql`
2. 看 `backend/src/main/resources/sql/tables.sql`
3. 看对应 Java 实现目录  
   例如：`backend/src/main/java/com/fastservice/platform/backend/user/`
4. 看已有测试  
   优先：
   - `backend/src/test/java/com/fastservice/platform/backend/service/`
   - `backend/src/test/java/com/fastservice/platform/backend/integration/`
5. 如需 demo 数据，再看：
   - `backend/src/main/resources/sql/demo.sql`
   - `backend/src/main/java/com/fastservice/platform/backend/demo/DemoDataSupport.java`

## 文件映射

### 改 service 契约

- 改 `backend/src/main/resources/sql/services.sql`
- 确认生成的 service executor 仍与 Java 实现匹配

### 改存储结构

- 改 `backend/src/main/resources/sql/tables.sql`
- 如果是 demo 数据相关改动，同时评估 `demo.sql` 和 `DemoDataSupport.java`

### 改业务逻辑

- 改 `backend/src/main/java/com/fastservice/platform/backend/<domain>/`
- 保持按领域组织，不要回退成按 controller/service/repository 的大杂烩层次

### 改测试

- 领域行为优先放进 `EnterpriseServicesTest`
- 启动或 demo 数据行为优先放进 `BackendBootstrapIntegrationTest`

## 最低验证

从仓库根目录执行：

```bash
./scripts/verify-backend.sh
```

如果这次改动会影响 frontend 联调，再补：

```bash
./scripts/verify-fullstack.sh
```

## 常见坑

- 改了 `services.sql` 但忘了同步 Java 实现签名
- 只改了 demo SQL，没处理 Java 侧的幂等 demo 初始化
- 测试只覆盖 happy path，没有覆盖关系校验或非法输入
- 以为 frontend 只看 mock，实际上它直接对接当前 backend `/service/*`
