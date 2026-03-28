# Playbook: Frontend Admin Change

适用场景：

- 新增一个管理页
- 给现有页面补列表、详情或写操作
- 调整与 backend 契约相关的页面数据流

## 先确认范围

1. 到 `.spec-driven/specs/frontend/` 看对应主 spec
2. 确认这次改动不是在引入新的状态层或请求模式
3. 保持数据访问逻辑继续集中在 `frontend/src/lib/api/`

## 典型修改顺序

1. 看 `frontend/src/app/router.tsx` 和 `frontend/src/app/navigation.ts`
2. 看 `frontend/src/features/<domain>/`
3. 看 `frontend/src/lib/api/hooks.ts`
4. 看 `frontend/src/lib/api/client.ts` 和 `frontend/src/lib/api/types.ts`
5. 如需通用后台组件，再看 `frontend/src/components/admin/`
6. 最后看相关测试：
   - `frontend/src/app/router.test.tsx`
   - `frontend/src/app/admin-write-workflows.test.tsx`

## 文件映射

### 新增页面

- 页面组件放 `frontend/src/features/<domain>/`
- 路由入口放 `frontend/src/app/router.tsx`
- 导航补充放 `frontend/src/app/navigation.ts`

### 新增数据读取

- 优先加到 `frontend/src/lib/api/hooks.ts`
- 共享类型放 `frontend/src/lib/api/types.ts`
- 不要把 `fetch` 重新打散回页面组件

### 新增写操作

- 仍走 `frontend/src/lib/api/hooks.ts`
- 在页面里只处理表单状态和交互
- 统一复用已有 mutation status / resource state 模式

## 最低验证

从仓库根目录执行：

```bash
./scripts/verify-frontend.sh
```

如果改动依赖真实 backend 契约，再补：

```bash
./scripts/verify-fullstack.sh
```

## 常见坑

- 页面里直接写请求逻辑，绕开 `frontend/src/lib/api/`
- 新增页面但忘了加导航或路由测试
- 只验证本地静态状态，没有验证 `/service/*` 代理联调
- 引入新的前端抽象层，破坏当前 AI 友好的结构
