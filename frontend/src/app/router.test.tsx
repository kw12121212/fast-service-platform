import { render, screen } from '@testing-library/react'
import { RouterProvider, createMemoryRouter } from 'react-router-dom'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'

import { adminRoutes } from '@/app/router'

const defaultResponses = new Map<string, unknown>([
  ['/service/user_service/listUsers', []],
  ['/service/project_service/listProjects', []],
  ['/service/access_control_service/listRoles', []],
  ['/service/access_control_service/listPermissions', []],
  ['/service/kanban_service/listKanbansByProject?projectId=1', []],
  ['/service/ticket_service/listTicketsByProject?projectId=1', []],
])

function mockFetch() {
  const fetchMock = vi.fn(async (input: string | URL | Request) => {
    const url = typeof input === 'string'
      ? new URL(input, 'http://localhost')
      : input instanceof URL
        ? input
        : new URL(input.url)
    const payload = defaultResponses.get(`${url.pathname}${url.search}`) ?? []
    return new Response(JSON.stringify(payload), {
      status: 200,
      headers: {
        'Content-Type': 'application/json',
      },
    })
  })

  vi.stubGlobal('fetch', fetchMock)
  return fetchMock
}

describe('admin router', () => {
  beforeEach(() => {
    mockFetch()
  })

  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it.each([
    ['/dashboard', 'Current V1 enterprise baseline'],
    ['/users', 'User management'],
    ['/roles', 'Role permission management'],
    ['/projects', 'Software project management'],
    ['/tickets', 'Ticket management'],
    ['/kanban', 'Kanban management'],
  ])('renders %s', async (path, heading) => {
    const router = createMemoryRouter(adminRoutes, {
      initialEntries: [path],
    })

    render(<RouterProvider router={router} />)

    expect(await screen.findByText(heading)).toBeInTheDocument()
  })

  it('shows manageable empty RBAC state instead of a manual role-id lookup', async () => {
    const router = createMemoryRouter(adminRoutes, {
      initialEntries: ['/roles'],
    })

    render(<RouterProvider router={router} />)

    expect(await screen.findByText('No roles available')).toBeInTheDocument()
    expect(screen.queryByLabelText('Role id')).not.toBeInTheDocument()
    expect(screen.queryByText('Refreshing backend data')).not.toBeInTheDocument()
  })
})
