# Frontend Workspace

This workspace hosts the first runnable PC admin console for Fast Service Platform.

## Stack

- Vite 8
- React 19
- shadcn/ui
- Tailwind CSS 4
- Node 24 via `nvm`
- `bun` as the package manager

## Commands

```bash
cd frontend
bun install
bun run dev
```

The Vite dev server proxies `/service/*` to `http://127.0.0.1:8080`, so the backend core should be running locally for real data.

## Current scope

The first frontend baseline provides:

- Admin dashboard
- User management
- Role permission management
- Software project management
- Ticket management
- Kanban management

The frontend intentionally matches the current backend contract. For example, role permissions are loaded by `roleId`, and tickets or kanban boards are loaded by `projectId`.
