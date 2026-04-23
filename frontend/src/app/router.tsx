import { createBrowserRouter, Navigate } from 'react-router-dom'
import type { RouteObject } from 'react-router-dom'

import AdminShell from '@/app/admin-shell'
import { moduleSelection } from '@/app/module-selection'
import { DashboardPage } from '@/features/dashboard/dashboard-page'
import { KanbanPage } from '@/features/kanban/kanban-page'
import { ProjectsPage } from '@/features/projects/projects-page'
import { RolePermissionsPage } from '@/features/roles/role-permissions-page'
import { TeamsPage } from '@/features/teams/teams-page'
import { TicketsPage } from '@/features/tickets/tickets-page'
import { UsersPage } from '@/features/users/users-page'

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
      ...(moduleSelection.project
        ? [{ path: 'projects', element: <ProjectsPage /> }]
        : []),
      ...(moduleSelection.ticket
        ? [{ path: 'tickets', element: <TicketsPage /> }]
        : []),
      ...(moduleSelection.kanban
        ? [{ path: 'kanban', element: <KanbanPage /> }]
        : []),
      ...(moduleSelection.team
        ? [{ path: 'teams', element: <TeamsPage /> }]
        : []),
      {
        path: '*',
        element: <MissingRoutePage />,
      },
    ],
  },
]

export const router = createBrowserRouter(adminRoutes)
