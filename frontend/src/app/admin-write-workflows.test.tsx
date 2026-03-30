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
  repository: {
    rootPath: string
    headState: 'BRANCH' | 'DETACHED'
    branch: string | null
    workingTreeState: 'CLEAN' | 'DIRTY'
    latestCommitSummary: string
    availableBranches: string[]
    recentCommits: Array<{
      hash: string
      summary: string
    }>
    worktrees: Array<{
      path: string
      main: boolean
      headState: 'BRANCH' | 'DETACHED'
      branch: string | null
      workingTreeState: 'CLEAN' | 'DIRTY' | 'UNAVAILABLE'
      hasUpstream: boolean
      hasUnpushedCommits: boolean
      stale: boolean
      deletionAllowed: boolean
      deletionRestriction: string | null
    }>
  } | null
}

type MockRole = {
  id: number
  code: string
  name: string
}

type MockPermission = {
  id: number
  code: string
  name: string
  scope: 'MENU' | 'FUNCTION'
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
      repository: null,
    },
  ]

  const roles: MockRole[] = [
    {
      id: 200,
      code: 'ADMIN',
      name: 'Administrator',
    },
  ]

  const permissions: MockPermission[] = [
    {
      id: 300,
      code: 'dashboard:view',
      name: 'View Dashboard',
      scope: 'MENU',
    },
  ]

  const userRoles = [{ userId: 100, roleId: 200 }]
  const rolePermissions = [{ roleId: 200, permissionId: 300 }]

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

  return {
    users,
    roles,
    permissions,
    userRoles,
    rolePermissions,
    projects,
    kanbans,
    tickets,
  }
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

function errorResponse(status: number, body: string) {
  return new Response(body, {
    status,
    headers: {
      'Content-Type': 'text/plain',
    },
  })
}

function createMainWorktree(path: string, branch: string): NonNullable<MockProject['repository']>['worktrees'][number] {
  return {
    path,
    main: true,
    headState: 'BRANCH',
    branch,
    workingTreeState: 'CLEAN',
    hasUpstream: true,
    hasUnpushedCommits: false,
    stale: false,
    deletionAllowed: false,
    deletionRestriction: 'Main repository worktree cannot be removed from the project view',
  }
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
        repository: null,
      })
      return jsonResponse(nextId)
    }

    if (path === '/service/project_service/bindProjectRepository') {
      const projectId = Number(url.searchParams.get('projectId'))
      const repositoryPath = url.searchParams.get('repositoryPath') ?? ''
      const project = state.projects.find((entry) => entry.id === projectId)
      if (project) {
        const detached = repositoryPath.includes('detached')
        project.repository = {
          rootPath: repositoryPath,
          headState: detached ? 'DETACHED' : 'BRANCH',
          branch: detached ? null : 'repo-test',
          workingTreeState: 'CLEAN',
          latestCommitSummary: 'a1b2c3d Initial platform repo',
          availableBranches: [
            'feature/api-preview',
            'feature-preview',
            'feature/error',
            'repo-test',
          ],
          recentCommits: [
            {
              hash: 'a1b2c3d',
              summary: 'Initial platform repo',
            },
            {
              hash: 'd4e5f6g',
              summary: 'Add Git management baseline',
            },
          ],
          worktrees: [createMainWorktree(repositoryPath, 'repo-test')],
        }
      }
      return jsonResponse(repositoryPath)
    }

    if (path === '/service/project_service/switchProjectBranch') {
      const projectId = Number(url.searchParams.get('projectId'))
      const branchName = url.searchParams.get('branchName') ?? ''
      const project = state.projects.find((entry) => entry.id === projectId)
      if (project?.repository) {
        project.repository = {
          ...project.repository,
          headState: 'BRANCH',
          branch: branchName,
          workingTreeState: 'CLEAN',
          latestCommitSummary: `switched ${branchName}`,
          recentCommits: [
            {
              hash: '9f8e7d6',
              summary: `Switch to ${branchName}`,
            },
            ...project.repository.recentCommits,
          ].slice(0, 3),
          worktrees: project.repository.worktrees.map((worktree) =>
            worktree.main
              ? {
                  ...worktree,
                  branch: branchName,
                }
              : worktree,
          ),
        }
      }
      return textResponse(branchName)
    }

    if (path === '/service/project_service/createProjectWorktree') {
      const projectId = Number(url.searchParams.get('projectId'))
      const branchName = url.searchParams.get('branchName') ?? ''
      const project = state.projects.find((entry) => entry.id === projectId)
      if (!project?.repository) {
        return errorResponse(409, 'Project repository is not bound')
      }
      if (branchName === 'feature/error') {
        return errorResponse(409, 'Worktree creation rejected by backend')
      }
      if (project.repository.worktrees.some((worktree) => worktree.branch === branchName)) {
        return errorResponse(409, `Branch already has a worktree: ${branchName}`)
      }
      const sanitizedBranch = branchName.replaceAll(/[^A-Za-z0-9._-]+/g, '-')
      const worktreePath = `${project.repository.rootPath}-worktrees/${sanitizedBranch}`
      const deletable = branchName !== 'feature-preview'
      project.repository.worktrees.push({
        path: worktreePath,
        main: false,
        headState: 'BRANCH',
        branch: branchName,
        workingTreeState: 'CLEAN',
        hasUpstream: deletable,
        hasUnpushedCommits: false,
        stale: false,
        deletionAllowed: deletable,
        deletionRestriction: deletable ? null : 'Worktree has no upstream branch',
      })
      return textResponse(worktreePath)
    }

    if (path === '/service/project_service/deleteProjectWorktree') {
      const projectId = Number(url.searchParams.get('projectId'))
      const worktreePath = url.searchParams.get('worktreePath') ?? ''
      const project = state.projects.find((entry) => entry.id === projectId)
      const worktree = project?.repository?.worktrees.find(
        (entry) => entry.path === worktreePath,
      )
      if (!project?.repository || !worktree) {
        return errorResponse(404, 'Worktree is not managed by the bound repository')
      }
      if (!worktree.deletionAllowed) {
        return errorResponse(
          409,
          worktree.deletionRestriction ?? 'This linked worktree must be handled manually before deletion.',
        )
      }
      project.repository.worktrees = project.repository.worktrees.filter(
        (entry) => entry.path !== worktreePath,
      )
      return textResponse(worktreePath)
    }

    if (path === '/service/project_service/repairProjectWorktrees') {
      const projectId = Number(url.searchParams.get('projectId'))
      const project = state.projects.find((entry) => entry.id === projectId)
      if (!project?.repository) {
        return errorResponse(409, 'Project repository is not bound')
      }
      if (project.repository.headState === 'DETACHED') {
        return errorResponse(
          409,
          'Cannot repair worktrees while repository is in detached HEAD state',
        )
      }
      project.repository.latestCommitSummary = 'repair worktree metadata'
      return textResponse(project.repository.rootPath)
    }

    if (path === '/service/project_service/pruneProjectWorktrees') {
      const projectId = Number(url.searchParams.get('projectId'))
      const project = state.projects.find((entry) => entry.id === projectId)
      if (!project?.repository) {
        return errorResponse(409, 'Project repository is not bound')
      }
      project.repository.worktrees = project.repository.worktrees.filter(
        (entry) => !entry.stale,
      )
      project.repository.latestCommitSummary = 'pruned stale worktree records'
      return textResponse(project.repository.rootPath)
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

    if (path === '/service/access_control_service/listRoles') {
      return jsonResponse(state.roles)
    }

    if (path === '/service/access_control_service/listPermissions') {
      return jsonResponse(state.permissions)
    }

    if (path === '/service/access_control_service/createRole') {
      const nextId =
        state.roles.length > 0 ? state.roles[state.roles.length - 1]!.id + 1 : 200
      state.roles.push({
        id: nextId,
        code: url.searchParams.get('roleCode') ?? '',
        name: url.searchParams.get('roleName') ?? '',
      })
      return jsonResponse(nextId)
    }

    if (path === '/service/access_control_service/createPermission') {
      const nextId =
        state.permissions.length > 0
          ? state.permissions[state.permissions.length - 1]!.id + 1
          : 300
      state.permissions.push({
        id: nextId,
        code: url.searchParams.get('permissionCode') ?? '',
        name: url.searchParams.get('permissionName') ?? '',
        scope: (url.searchParams.get('scope') as MockPermission['scope']) ?? 'MENU',
      })
      return jsonResponse(nextId)
    }

    if (path === '/service/access_control_service/assignPermissionToRole') {
      state.rolePermissions.push({
        roleId: Number(url.searchParams.get('roleId')),
        permissionId: Number(url.searchParams.get('permissionId')),
      })
      return textResponse('')
    }

    if (path === '/service/access_control_service/assignRoleToUser') {
      state.userRoles.push({
        userId: Number(url.searchParams.get('userId')),
        roleId: Number(url.searchParams.get('roleId')),
      })
      return textResponse('')
    }

    if (path === '/service/access_control_service/listRolesForUser') {
      const userId = Number(url.searchParams.get('userId'))
      return jsonResponse(
        state.userRoles
          .filter((entry) => entry.userId === userId)
          .map((entry) => state.roles.find((role) => role.id === entry.roleId))
          .filter((role): role is MockRole => role !== undefined),
      )
    }

    if (path === '/service/access_control_service/listPermissionsForRole') {
      const roleId = Number(url.searchParams.get('roleId'))
      return jsonResponse(
        state.rolePermissions
          .filter((entry) => entry.roleId === roleId)
          .map((entry) =>
            state.permissions.find(
              (permission) => permission.id === entry.permissionId,
            ),
          )
          .filter((permission): permission is MockPermission => permission !== undefined),
      )
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

  it('binds a repository from the projects page', async () => {
    renderRoute('/projects')

    await screen.findByText('Software project management')
    expect(await screen.findByText(/No repository bound\./)).toBeInTheDocument()

    fireEvent.change(screen.getByLabelText('Repository path'), {
      target: { value: '/workspace/fast-service-platform' },
    })

    fireEvent.click(screen.getByRole('button', { name: 'Bind repository' }))

    expect(
      await screen.findByText(
        'Repository bound and the project list has been refreshed.',
      ),
    ).toBeInTheDocument()
    expect(
      await screen.findAllByText('/workspace/fast-service-platform'),
    ).not.toHaveLength(0)
    expect(await screen.findByDisplayValue('repo-test')).toBeInTheDocument()
    expect(await screen.findByText('a1b2c3d Initial platform repo')).toBeInTheDocument()
    expect(
      await screen.findAllByRole('option', { name: 'feature-preview' }),
    ).not.toHaveLength(0)
    expect(await screen.findByText('Worktree inventory')).toBeInTheDocument()
    expect(await screen.findByText('Main worktree')).toBeInTheDocument()
    expect(await screen.findByText('Add Git management baseline')).toBeInTheDocument()
  })

  it('switches to an existing local branch from the projects page', async () => {
    renderRoute('/projects')

    await screen.findByText('Software project management')

    fireEvent.change(screen.getByLabelText('Repository path'), {
      target: { value: '/workspace/fast-service-platform' },
    })
    fireEvent.click(screen.getByRole('button', { name: 'Bind repository' }))

    expect(
      await screen.findAllByText('/workspace/fast-service-platform'),
    ).not.toHaveLength(0)

    fireEvent.change(screen.getByLabelText('Local branch'), {
      target: { value: 'feature-preview' },
    })
    fireEvent.click(screen.getByRole('button', { name: 'Switch branch' }))

    expect(
      await screen.findByText('Branch switched and the project list has been refreshed.'),
    ).toBeInTheDocument()
    expect(await screen.findByText('Switch to feature-preview')).toBeInTheDocument()
  })

  it('creates and deletes a worktree from the projects page', async () => {
    renderRoute('/projects')

    await screen.findByText('Software project management')

    fireEvent.change(screen.getByLabelText('Repository path'), {
      target: { value: '/workspace/fast-service-platform' },
    })
    fireEvent.click(screen.getByRole('button', { name: 'Bind repository' }))

    expect(
      await screen.findAllByText('/workspace/fast-service-platform'),
    ).not.toHaveLength(0)

    fireEvent.change(screen.getByLabelText('Worktree branch'), {
      target: { value: 'feature/api-preview' },
    })
    fireEvent.click(screen.getByRole('button', { name: 'Create worktree' }))

    expect(
      await screen.findByText('Worktree created and the project list has been refreshed.'),
    ).toBeInTheDocument()
    expect(
      await screen.findByText('/workspace/fast-service-platform-worktrees/feature-api-preview'),
    ).toBeInTheDocument()

    fireEvent.click(screen.getByRole('button', { name: 'Delete worktree' }))

    expect(
      await screen.findByText('Worktree removed and the project list has been refreshed.'),
    ).toBeInTheDocument()
    expect(
      screen.queryByText('/workspace/fast-service-platform-worktrees/feature-api-preview'),
    ).not.toBeInTheDocument()
  })

  it('shows restricted deletion state and rejected worktree mutations', async () => {
    renderRoute('/projects')

    await screen.findByText('Software project management')

    fireEvent.change(screen.getByLabelText('Repository path'), {
      target: { value: '/workspace/fast-service-platform' },
    })
    fireEvent.click(screen.getByRole('button', { name: 'Bind repository' }))

    expect(
      await screen.findAllByText('/workspace/fast-service-platform'),
    ).not.toHaveLength(0)

    fireEvent.change(screen.getByLabelText('Worktree branch'), {
      target: { value: 'feature-preview' },
    })
    fireEvent.click(screen.getByRole('button', { name: 'Create worktree' }))

    expect(
      await screen.findByText('/workspace/fast-service-platform-worktrees/feature-preview'),
    ).toBeInTheDocument()
    expect(await screen.findByText('Worktree has no upstream branch')).toBeInTheDocument()

    fireEvent.change(screen.getByLabelText('Worktree branch'), {
      target: { value: 'feature/error' },
    })
    fireEvent.click(screen.getByRole('button', { name: 'Create worktree' }))

    expect(
      await screen.findByText('Backend request failed with 409: Worktree creation rejected by backend'),
    ).toBeInTheDocument()
  })

  it('shows detached repair restrictions and supports prune from the projects page', async () => {
    renderRoute('/projects')

    await screen.findByText('Software project management')

    fireEvent.change(screen.getByLabelText('Repository path'), {
      target: { value: '/workspace/detached-project' },
    })
    fireEvent.click(screen.getByRole('button', { name: 'Bind repository' }))

    expect(await screen.findAllByText('/workspace/detached-project')).not.toHaveLength(
      0,
    )

    expect(
      await screen.findByText(
        'Worktree creation is unavailable while the repository is in detached HEAD state.',
      ),
    ).toBeInTheDocument()
    expect(
      await screen.findByText(
        'Repair is unavailable while the repository is in detached HEAD state, but prune remains available for stale records.',
      ),
    ).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Repair metadata' })).toBeDisabled()

    const bindAgainInput = screen.getByLabelText('Repository path')
    fireEvent.change(bindAgainInput, {
      target: { value: '/workspace/fast-service-platform' },
    })
    fireEvent.click(screen.getByRole('button', { name: 'Bind repository' }))

    expect(
      await screen.findAllByText('/workspace/fast-service-platform'),
    ).not.toHaveLength(0)
    fireEvent.click(screen.getByRole('button', { name: 'Prune stale records' }))

    expect(
      await screen.findByText(
        'Stale worktree records pruned and the project list has been refreshed.',
      ),
    ).toBeInTheDocument()
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
    const refreshedTicketCell = await screen.findByText('FSP-9')
    const refreshedTicketRow = refreshedTicketCell.closest('tr')
    expect(
      within(refreshedTicketRow as HTMLElement).getByText('IN_PROGRESS'),
    ).toBeInTheDocument()
  })

  it('manages the minimum RBAC workflows from the roles page', async () => {
    renderRoute('/roles')

    await screen.findByText('Role permission management')
    await screen.findByText('Available roles')

    fireEvent.change(screen.getByLabelText('Role code'), {
      target: { value: 'PROJECT_ADMIN' },
    })
    fireEvent.change(screen.getByLabelText('Role name'), {
      target: { value: 'Project Administrator' },
    })

    fireEvent.click(screen.getByRole('button', { name: 'Create role' }))

    expect(
      await screen.findByText('Role created and the RBAC views have been refreshed.'),
    ).toBeInTheDocument()
    expect((await screen.findAllByText('Project Administrator')).length).toBeGreaterThan(0)

    fireEvent.change(screen.getByLabelText('Permission code'), {
      target: { value: 'project.manage' },
    })
    fireEvent.change(screen.getByLabelText('Permission name'), {
      target: { value: 'Manage Project' },
    })
    fireEvent.change(screen.getByLabelText('Permission scope'), {
      target: { value: 'FUNCTION' },
    })

    fireEvent.click(screen.getByRole('button', { name: 'Create permission' }))

    expect(
      await screen.findByText(
        'Permission created and the permission list has been refreshed.',
      ),
    ).toBeInTheDocument()
    expect((await screen.findAllByText('Manage Project')).length).toBeGreaterThan(0)

    fireEvent.change(screen.getByLabelText('Permission to grant'), {
      target: { value: '301' },
    })
    fireEvent.click(screen.getByRole('button', { name: 'Grant permission' }))

    expect(
      await screen.findByText('Role permission assignments have been refreshed.'),
    ).toBeInTheDocument()
    expect((await screen.findAllByText('project.manage')).length).toBeGreaterThan(0)

    fireEvent.change(screen.getByLabelText('Role to assign'), {
      target: { value: '201' },
    })
    fireEvent.click(screen.getByRole('button', { name: 'Assign role' }))

    expect(
      await screen.findByText('User role assignments have been refreshed.'),
    ).toBeInTheDocument()
    const userRoleList = screen.getByTestId('selected-user-role-list')
    expect(await within(userRoleList).findByText('PROJECT_ADMIN')).toBeInTheDocument()
  })
})
