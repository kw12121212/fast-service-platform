import { useEffect, useEffectEvent, useReducer, useRef, useState } from 'react'

import { getJson, invokeService } from '@/lib/api/client'
import type {
  AccessPermission,
  AppRole,
  BackendMutation,
  AppUser,
  BackendResource,
  KanbanBoard,
  SoftwareProject,
  Ticket,
} from '@/lib/api/types'

type ResourceOptions<T> = {
  key: string
  enabled?: boolean
  initialData: T
  load: () => Promise<T>
}

type MutationOptions<TArgs, TResult> = {
  run: (args: TArgs) => Promise<TResult>
}

type ResourceState<T> = {
  status: 'loading' | 'refreshing' | 'success' | 'error'
  data: T
  error: string | null
}

type MutationState<TResult> = {
  status: 'idle' | 'submitting' | 'success' | 'error'
  data: TResult | null
  error: string | null
}

type ResourceAction<T> =
  | {
      type: 'loading'
      payload: {
        keepCurrentData: boolean
        initialData: T
      }
    }
  | {
      type: 'success'
      payload: T
    }
  | {
      type: 'error'
      payload: string
    }

const EMPTY_USERS: AppUser[] = []
const EMPTY_ROLES: AppRole[] = []
const EMPTY_PERMISSIONS: AccessPermission[] = []
const EMPTY_PROJECTS: SoftwareProject[] = []
const EMPTY_KANBANS: KanbanBoard[] = []
const EMPTY_TICKETS: Ticket[] = []

function resourceReducer<T>(
  state: ResourceState<T>,
  action: ResourceAction<T>,
): ResourceState<T> {
  if (action.type === 'loading') {
    return {
      status: action.payload.keepCurrentData ? 'refreshing' : 'loading',
      data: action.payload.keepCurrentData ? state.data : action.payload.initialData,
      error: null,
    }
  }

  if (action.type === 'success') {
    return {
      status: 'success',
      data: action.payload,
      error: null,
    }
  }

  return {
    status: 'error',
    data: state.data,
    error: action.payload,
  }
}

function useBackendMutation<TArgs, TResult>({
  run,
}: MutationOptions<TArgs, TResult>): BackendMutation<TArgs, TResult> {
  const [state, setState] = useState<MutationState<TResult>>({
    status: 'idle',
    data: null,
    error: null,
  })

  async function submit(args: TArgs) {
    setState({
      status: 'submitting',
      data: null,
      error: null,
    })

    try {
      const result = await run(args)
      setState({
        status: 'success',
        data: result,
        error: null,
      })
      return result
    } catch (error) {
      const message =
        error instanceof Error ? error.message : 'Unknown request error'
      setState({
        status: 'error',
        data: null,
        error: message,
      })
      throw error
    }
  }

  return {
    status: state.status,
    data: state.data,
    error: state.error,
    submit,
    reset: () =>
      setState({
        status: 'idle',
        data: null,
        error: null,
      }),
  }
}

function useBackendResource<T>({
  key,
  enabled = true,
  initialData,
  load,
}: ResourceOptions<T>): BackendResource<T> {
  const [reloadVersion, setReloadVersion] = useState(0)
  const hasResolvedRef = useRef(false)
  const [state, dispatch] = useReducer(resourceReducer<T>, {
    status: 'loading',
    data: initialData,
    error: null,
  })

  const loadLatest = useEffectEvent(async () => {
    return load()
  })

  useEffect(() => {
    let active = true

    if (!enabled) {
      return
    }

    dispatch({
      type: 'loading',
      payload: {
        keepCurrentData: hasResolvedRef.current,
        initialData,
      },
    })

    void loadLatest()
      .then((nextData) => {
        if (!active) {
          return
        }
        dispatch({
          type: 'success',
          payload: nextData,
        })
        hasResolvedRef.current = true
      })
      .catch((loadError) => {
        if (!active) {
          return
        }

        dispatch({
          type: 'error',
          payload:
            loadError instanceof Error ? loadError.message : 'Unknown error',
        })
      })

    return () => {
      active = false
    }
  }, [enabled, initialData, key, reloadVersion])

  if (!enabled) {
    return {
      status: 'idle',
      data: initialData,
      error: null,
      reload: () => setReloadVersion((current) => current + 1),
    }
  }

  return {
    status: state.status,
    data: state.data,
    error: state.error,
    reload: () => setReloadVersion((current) => current + 1),
  }
}

export function useUsersResource() {
  return useBackendResource({
    key: 'users',
    initialData: EMPTY_USERS,
    load: () => getJson<AppUser[]>('user_service/listUsers'),
  })
}

export function useProjectsResource() {
  return useBackendResource({
    key: 'projects',
    initialData: EMPTY_PROJECTS,
    load: () => getJson<SoftwareProject[]>('project_service/listProjects'),
  })
}

export function useRolesResource() {
  return useBackendResource({
    key: 'roles',
    initialData: EMPTY_ROLES,
    load: () => getJson<AppRole[]>('access_control_service/listRoles'),
  })
}

export function usePermissionsResource() {
  return useBackendResource({
    key: 'permissions',
    initialData: EMPTY_PERMISSIONS,
    load: () =>
      getJson<AccessPermission[]>('access_control_service/listPermissions'),
  })
}

export function useRolePermissionsResource(roleId: number | null) {
  return useBackendResource({
    key: `role-permissions:${roleId ?? 'none'}`,
    enabled: roleId !== null,
    initialData: EMPTY_PERMISSIONS,
    load: () =>
      getJson<AccessPermission[]>('access_control_service/listPermissionsForRole', {
        roleId: roleId ?? undefined,
      }),
  })
}

export function useRolesForUserResource(userId: number | null) {
  return useBackendResource({
    key: `roles-for-user:${userId ?? 'none'}`,
    enabled: userId !== null,
    initialData: EMPTY_ROLES,
    load: () =>
      getJson<AppRole[]>('access_control_service/listRolesForUser', {
        userId: userId ?? undefined,
      }),
  })
}

export function useKanbansResource(projectId: number | null) {
  return useBackendResource({
    key: `kanbans:${projectId ?? 'none'}`,
    enabled: projectId !== null,
    initialData: EMPTY_KANBANS,
    load: () =>
      getJson<KanbanBoard[]>('kanban_service/listKanbansByProject', {
        projectId: projectId ?? undefined,
      }),
  })
}

export function useTicketsResource(projectId: number | null) {
  return useBackendResource({
    key: `tickets:${projectId ?? 'none'}`,
    enabled: projectId !== null,
    initialData: EMPTY_TICKETS,
    load: () =>
      getJson<Ticket[]>('ticket_service/listTicketsByProject', {
        projectId: projectId ?? undefined,
      }),
  })
}

export function useCreateUserAction() {
  return useBackendMutation({
    run: (args: {
      username: string
      displayName: string
      email: string
    }) => invokeService<number>('user_service/createUser', args),
  })
}

export function useCreateProjectAction() {
  return useBackendMutation({
    run: (args: {
      projectKey: string
      projectName: string
      description: string
    }) => invokeService<number>('project_service/createProject', args),
  })
}

export function useBindProjectRepositoryAction() {
  return useBackendMutation({
    run: (args: {
      projectId: number
      repositoryPath: string
    }) => invokeService<string>('project_service/bindProjectRepository', args),
  })
}

export function useSwitchProjectBranchAction() {
  return useBackendMutation({
    run: (args: {
      projectId: number
      branchName: string
    }) => invokeService<string>('project_service/switchProjectBranch', args),
  })
}

export function useCreateProjectWorktreeAction() {
  return useBackendMutation({
    run: (args: {
      projectId: number
      branchName: string
    }) => invokeService<string>('project_service/createProjectWorktree', args),
  })
}

export function useDeleteProjectWorktreeAction() {
  return useBackendMutation({
    run: (args: {
      projectId: number
      worktreePath: string
    }) => invokeService<string>('project_service/deleteProjectWorktree', args),
  })
}

export function useRepairProjectWorktreesAction() {
  return useBackendMutation({
    run: (args: {
      projectId: number
    }) => invokeService<string>('project_service/repairProjectWorktrees', args),
  })
}

export function usePruneProjectWorktreesAction() {
  return useBackendMutation({
    run: (args: {
      projectId: number
    }) => invokeService<string>('project_service/pruneProjectWorktrees', args),
  })
}

export function useCreateRoleAction() {
  return useBackendMutation({
    run: (args: {
      roleCode: string
      roleName: string
    }) => invokeService<number>('access_control_service/createRole', args),
  })
}

export function useCreatePermissionAction() {
  return useBackendMutation({
    run: (args: {
      permissionCode: string
      permissionName: string
      scope: AccessPermission['scope']
    }) => invokeService<number>('access_control_service/createPermission', args),
  })
}

export function useAssignPermissionToRoleAction() {
  return useBackendMutation({
    run: (args: {
      roleId: number
      permissionId: number
    }) =>
      invokeService<void>('access_control_service/assignPermissionToRole', args),
  })
}

export function useAssignRoleToUserAction() {
  return useBackendMutation({
    run: (args: {
      userId: number
      roleId: number
    }) => invokeService<void>('access_control_service/assignRoleToUser', args),
  })
}

export function useCreateKanbanAction() {
  return useBackendMutation({
    run: (args: {
      projectId: number
      boardName: string
    }) => invokeService<number>('kanban_service/createKanban', args),
  })
}

export function useCreateTicketAction() {
  return useBackendMutation({
    run: (args: {
      projectId: number
      kanbanId: number
      ticketKey: string
      title: string
      description: string
      assigneeUserId: number
    }) => invokeService<number>('ticket_service/createTicket', args),
  })
}

export function useMoveTicketAction() {
  return useBackendMutation({
    run: (args: {
      ticketId: number
      targetState: Ticket['state']
    }) => invokeService<string>('ticket_service/moveTicket', args),
  })
}
