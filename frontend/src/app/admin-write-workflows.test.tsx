import { fireEvent, render, screen, within } from '@testing-library/react'
import { RouterProvider, createMemoryRouter } from 'react-router-dom'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'

import { adminRoutes } from '@/app/router'

type MockUser = {
  id: number
  username: string
  displayName: string
  email: string
  enabled: boolean
}

type MockProject = {
  id: number
  key: string
  name: string
  active: boolean
}

type MockKanban = {
  id: number
  projectId: number
  name: string
}

type MockTicket = {
  id: number
  projectId: number
  kanbanId: number
  key: string
  title: string
  description: string
  assigneeUserId: number
  state: 'TODO' | 'IN_PROGRESS' | 'DONE'
}

function createBackendState() {
  const users: MockUser[] = [
    {
      id: 100,
      username: 'admin',
      displayName: 'Administrator',
      email: 'admin@fastservice.local',
      enabled: true,
    },
  ]

  const projects: MockProject[] = [
    {
      id: 1,
      key: 'FSP',
      name: 'Fast Service Platform',
      active: true,
    },
  ]

  const kanbans: MockKanban[] = [
    {
      id: 10,
      projectId: 1,
      name: 'Delivery Board',
    },
  ]

  const tickets: MockTicket[] = [
    {
      id: 1000,
      projectId: 1,
      kanbanId: 10,
      key: 'FSP-1',
      title: 'Bootstrap admin shell',
      description: 'Existing baseline work item',
      assigneeUserId: 100,
      state: 'TODO',
    },
  ]

  return { users, projects, kanbans, tickets }
}

function jsonResponse(body: unknown) {
  return new Response(JSON.stringify(body), {
    status: 200,
    headers: {
      'Content-Type': 'application/json',
    },
  })
}

function textResponse(body: string) {
  return new Response(body, {
    status: 200,
    headers: {
      'Content-Type': 'application/json',
    },
  })
}

function installBackendMock() {
  const state = createBackendState()

  const fetchMock = vi.fn(async (input: string | URL | Request) => {
    const url = typeof input === 'string'
      ? new URL(input, 'http://localhost')
      : input instanceof URL
        ? input
        : new URL(input.url)

    const path = url.pathname

    if (path === '/service/user_service/listUsers') {
      return jsonResponse(state.users)
    }

    if (path === '/service/user_service/createUser') {
      const nextId =
        state.users.length > 0
          ? state.users[state.users.length - 1]!.id + 1
          : 100
      state.users.push({
        id: nextId,
        username: url.searchParams.get('username') ?? '',
        displayName: url.searchParams.get('displayName') ?? '',
        email: url.searchParams.get('email') ?? '',
        enabled: true,
      })
      return jsonResponse(nextId)
    }

    if (path === '/service/project_service/listProjects') {
      return jsonResponse(state.projects)
    }

    if (path === '/service/project_service/createProject') {
      const nextId =
        state.projects.length > 0
          ? state.projects[state.projects.length - 1]!.id + 1
          : 1
      state.projects.push({
        id: nextId,
        key: url.searchParams.get('projectKey') ?? '',
        name: url.searchParams.get('projectName') ?? '',
        active: true,
      })
      return jsonResponse(nextId)
    }

    if (path === '/service/kanban_service/listKanbansByProject') {
      const projectId = Number(url.searchParams.get('projectId'))
      return jsonResponse(
        state.kanbans
          .filter((board) => board.projectId === projectId)
          .map(({ id, name }) => ({ id, name })),
      )
    }

    if (path === '/service/kanban_service/createKanban') {
      const nextId =
        state.kanbans.length > 0
          ? state.kanbans[state.kanbans.length - 1]!.id + 1
          : 10
      state.kanbans.push({
        id: nextId,
        projectId: Number(url.searchParams.get('projectId')),
        name: url.searchParams.get('boardName') ?? '',
      })
      return jsonResponse(nextId)
    }

    if (path === '/service/ticket_service/listTicketsByProject') {
      const projectId = Number(url.searchParams.get('projectId'))
      return jsonResponse(
        state.tickets
          .filter((ticket) => ticket.projectId === projectId)
          .map(({ id, key, title, state: ticketState, kanbanId }) => ({
            id,
            key,
            title,
            state: ticketState,
            kanbanId,
          })),
      )
    }

    if (path === '/service/ticket_service/createTicket') {
      const nextId =
        state.tickets.length > 0
          ? state.tickets[state.tickets.length - 1]!.id + 1
          : 1000
      state.tickets.push({
        id: nextId,
        projectId: Number(url.searchParams.get('projectId')),
        kanbanId: Number(url.searchParams.get('kanbanId')),
        key: url.searchParams.get('ticketKey') ?? '',
        title: url.searchParams.get('title') ?? '',
        description: url.searchParams.get('description') ?? '',
        assigneeUserId: Number(url.searchParams.get('assigneeUserId')),
        state: 'TODO',
      })
      return jsonResponse(nextId)
    }

    if (path === '/service/ticket_service/moveTicket') {
      const ticketId = Number(url.searchParams.get('ticketId'))
      const targetState = url.searchParams.get('targetState') as MockTicket['state']
      const ticket = state.tickets.find((entry) => entry.id === ticketId)
      if (ticket) {
        ticket.state = targetState
      }
      return textResponse(targetState)
    }

    if (path === '/service/access_control_service/listPermissionsForRole') {
      return jsonResponse([])
    }

    return jsonResponse([])
  })

  vi.stubGlobal('fetch', fetchMock)
  return { fetchMock }
}

function renderRoute(path: string) {
  const router = createMemoryRouter(adminRoutes, {
    initialEntries: [path],
  })

  render(<RouterProvider router={router} />)
}

describe('admin write workflows', () => {
  beforeEach(() => {
    installBackendMock()
  })

  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('creates a user from the users page', async () => {
    renderRoute('/users')

    await screen.findByText('User management')

    fireEvent.change(screen.getByLabelText('Username'), {
      target: { value: 'operator' },
    })
    fireEvent.change(screen.getByLabelText('Display name'), {
      target: { value: 'Platform Operator' },
    })
    fireEvent.change(screen.getByLabelText('Email'), {
      target: { value: 'operator@example.com' },
    })

    fireEvent.click(screen.getByRole('button', { name: 'Create user' }))

    expect(
      await screen.findByText('User created and the user list has been refreshed.'),
    ).toBeInTheDocument()
    expect(await screen.findByText('operator')).toBeInTheDocument()
  })

  it('creates a project from the projects page', async () => {
    renderRoute('/projects')

    await screen.findByText('Software project management')

    fireEvent.change(screen.getByLabelText('Project key'), {
      target: { value: 'OPS' },
    })
    fireEvent.change(screen.getByLabelText('Project name'), {
      target: { value: 'Operations Console' },
    })
    fireEvent.change(screen.getByLabelText('Description'), {
      target: { value: 'Manage internal operations workflows.' },
    })

    fireEvent.click(screen.getByRole('button', { name: 'Create project' }))

    expect(
      await screen.findByText('Project created and the project list has been refreshed.'),
    ).toBeInTheDocument()
    expect(await screen.findByText('OPS')).toBeInTheDocument()
  })

  it('creates a kanban board from the kanban page', async () => {
    renderRoute('/kanban')

    await screen.findByText('Kanban management')

    fireEvent.change(screen.getByLabelText('Board name'), {
      target: { value: 'Operations Board' },
    })

    fireEvent.click(screen.getByRole('button', { name: 'Create board' }))

    expect(
      await screen.findByText('Board created and the board list has been refreshed.'),
    ).toBeInTheDocument()
    expect(await screen.findByText('Operations Board')).toBeInTheDocument()
  })

  it('creates a ticket and advances its state from the tickets page', async () => {
    renderRoute('/tickets')

    await screen.findByText('Ticket management')
    await screen.findByRole('option', { name: 'Delivery Board' })
    await screen.findByRole('option', { name: 'Administrator · admin' })

    fireEvent.change(screen.getByLabelText('Ticket key'), {
      target: { value: 'FSP-9' },
    })
    fireEvent.change(screen.getByLabelText('Title'), {
      target: { value: 'Enable write workflows' },
    })
    fireEvent.change(screen.getByLabelText('Description'), {
      target: { value: 'Make the first admin shell operable.' },
    })

    fireEvent.click(screen.getByRole('button', { name: 'Create ticket' }))

    expect(
      await screen.findByText('Ticket created and the ticket list has been refreshed.'),
    ).toBeInTheDocument()

    const ticketCell = await screen.findByText('FSP-9')
    const ticketRow = ticketCell.closest('tr')

    expect(ticketRow).not.toBeNull()
    fireEvent.click(
      within(ticketRow as HTMLElement).getByRole('button', {
        name: 'Move to IN_PROGRESS',
      }),
    )

    expect(
      await screen.findByText('Ticket state updated to IN_PROGRESS.'),
    ).toBeInTheDocument()
    expect(
      within(ticketRow as HTMLElement).getByText('IN_PROGRESS'),
    ).toBeInTheDocument()
  })
})
