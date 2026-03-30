export type BackendQueryStatus =
  | 'idle'
  | 'loading'
  | 'refreshing'
  | 'success'
  | 'error'

export type BackendMutationStatus =
  | 'idle'
  | 'submitting'
  | 'success'
  | 'error'

export type BackendResource<T> = {
  status: BackendQueryStatus
  data: T
  error: string | null
  reload: () => void
}

export type BackendMutation<TArgs, TResult> = {
  status: BackendMutationStatus
  data: TResult | null
  error: string | null
  submit: (args: TArgs) => Promise<TResult>
  reset: () => void
}

export type AppUser = {
  id: number
  username: string
  displayName: string
  email: string
  enabled: boolean
}

export type AppRole = {
  id: number
  code: string
  name: string
}

export type AccessPermission = {
  id: number
  code: string
  name: string
  scope: 'MENU' | 'FUNCTION' | string
}

export type RolePermission = AccessPermission

export type GitCommitSummary = {
  hash: string
  summary: string
}

export type ProjectRepositorySummary = {
  rootPath: string
  headState: 'BRANCH' | 'DETACHED' | string
  branch: string | null
  workingTreeState: 'CLEAN' | 'DIRTY' | string
  latestCommitSummary: string
  availableBranches: string[]
  recentCommits: GitCommitSummary[]
}

export type SoftwareProject = {
  id: number
  key: string
  name: string
  active: boolean
  repository: ProjectRepositorySummary | null
}

export type KanbanBoard = {
  id: number
  name: string
}

export type TicketState = 'TODO' | 'IN_PROGRESS' | 'DONE' | string

export type Ticket = {
  id: number
  key: string
  title: string
  state: TicketState
  kanbanId: number
}
