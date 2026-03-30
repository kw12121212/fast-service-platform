import { createBrowserRouter, Navigate } from 'react-router-dom'
import type { RouteObject } from 'react-router-dom'

import AdminShell from '@/app/admin-shell'
import { DashboardPage } from '@/features/dashboard/dashboard-page'
import { UsersPage } from '@/features/users/users-page'
import { RolePermissionsPage } from '@/features/roles/role-permissions-page'
import { ProjectsPage } from '@/features/projects/projects-page'
import { TicketsPage } from '@/features/tickets/tickets-page'
import { KanbanPage } from '@/features/kanban/kanban-page'

function MissingRoutePage() {
  return <Navigate replace to="/dashboard" />
}

export const adminRoutes: RouteObject[] = [
  {
    path: '/',
    element: <AdminShell />,
    children: [
      {
        index: true,
        element: <Navigate replace to="/dashboard" />,
      },
      {
        path: 'dashboard',
        element: <DashboardPage />,
      },
      {
        path: 'users',
        element: <UsersPage />,
      },
      {
        path: 'roles',
        element: <RolePermissionsPage />,
      },
      {
        path: 'projects',
        element: <ProjectsPage />,
      },
      {
        path: 'tickets',
        element: <TicketsPage />,
      },
      {
        path: 'kanban',
        element: <KanbanPage />,
      },
      {
        path: '*',
        element: <MissingRoutePage />,
      },
    ],
  },
]

export const router = createBrowserRouter(adminRoutes)
