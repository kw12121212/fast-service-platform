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

export type ProjectWorktreeSummary = {
  path: string
  main: boolean
  headState: 'BRANCH' | 'DETACHED' | string
  branch: string | null
  workingTreeState: 'CLEAN' | 'DIRTY' | 'UNAVAILABLE' | string
  hasUpstream: boolean
  hasUnpushedCommits: boolean
  stale: boolean
  deletionAllowed: boolean
  deletionRestriction: string | null
  mergeAllowed: boolean
  mergeRestriction: string | null
  mergeTargetBranches: string[]
  sandbox: {
    supported: boolean
    restriction: string | null
    imageStatus: 'MISSING' | 'READY' | 'FAILED' | string
    imageReference: string
    imageFailureMessage: string | null
    imageInitScriptPath: string
    imageInitScriptSource: 'DEFAULT' | 'WORKTREE_PROPERTY' | string
    imageActionAllowed: boolean
    imageActionRestriction: string | null
    containerStatus: 'INACTIVE' | 'ACTIVE' | 'FAILED' | string
    containerName: string
    containerFailureMessage: string | null
    projectInitScriptPath: string
    projectInitScriptSource: 'DEFAULT' | 'WORKTREE_PROPERTY' | string
    containerCreateAllowed: boolean
    containerCreateRestriction: string | null
    containerDeleteAllowed: boolean
    containerDeleteRestriction: string | null
  }
}

export type ProjectRepositorySummary = {
  rootPath: string
  headState: 'BRANCH' | 'DETACHED' | string
  branch: string | null
  workingTreeState: 'CLEAN' | 'DIRTY' | string
  latestCommitSummary: string
  availableBranches: string[]
  recentCommits: GitCommitSummary[]
  worktrees: ProjectWorktreeSummary[]
}

export type ProjectDerivedAppAssemblyOutcome = {
  status: 'SUCCESS' | 'FAILED' | string
  category: 'REQUEST_VALIDATION' | 'ASSEMBLY_EXECUTION' | string
  message: string
  outputDirectory: string | null
  manifestAppId: string | null
  manifestName: string | null
  requestedManifest: string | null
  requestedOutputDirectory: string | null
  updatedAt: string | null
}

export type ProjectDerivedAppAssemblyContext = {
  available: boolean
  status: 'AVAILABLE' | 'RESTRICTED' | string
  restricted: boolean
  restriction: string | null
  sourceRepositoryPath: string | null
  sourceContext: {
    type: 'BOUND_MAIN_REPOSITORY' | string
  }
  latestOutcome: ProjectDerivedAppAssemblyOutcome | null
}

export type ProjectDerivedAppVerificationStepOutcome = {
  status: 'SUCCESS' | 'FAILED' | 'NOT_RUN' | string
  message: string
}

export type ProjectDerivedAppVerificationOutcome = {
  status: 'SUCCESS' | 'FAILED' | string
  category:
    | 'REQUEST_VALIDATION'
    | 'COMBINED_VALIDATION'
    | 'GENERATED_APP_VERIFICATION'
    | 'RUNTIME_SMOKE'
    | string
  message: string
  targetOutputDirectory: string | null
  generatedAppVerification: ProjectDerivedAppVerificationStepOutcome
  runtimeSmoke: ProjectDerivedAppVerificationStepOutcome
  updatedAt: string | null
}

export type ProjectDerivedAppVerificationContext = {
  available: boolean
  status: 'AVAILABLE' | 'RESTRICTED' | string
  restricted: boolean
  restriction: string | null
  sourceRepositoryPath: string | null
  sourceContext: {
    type: 'BOUND_MAIN_REPOSITORY' | string
  }
  targetContext: {
    type: 'LATEST_SUCCESSFUL_ASSEMBLY_OUTPUT' | string
    outputDirectory: string | null
  }
  latestOutcome: ProjectDerivedAppVerificationOutcome | null
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

export type TicketWorkflowAction = 'submit' | 'approve' | 'reject' | 'reassign'

export type TicketWorkflowAssignee = {
  userId: number
  username: string
  displayName: string
}

export type TicketWorkflowHistoryEntry = {
  id: number
  action: string
  fromState: string
  toState: string
  actorUserId: number
  actorDisplayName: string
  previousAssigneeUserId: number | null
  previousAssigneeDisplayName: string | null
  nextAssigneeUserId: number | null
  nextAssigneeDisplayName: string | null
  comment: string
}

export type TicketWorkflow = {
  ticketId: number
  ticketKey: string
  title: string
  state: TicketState
  assignee: TicketWorkflowAssignee
  availableActions: TicketWorkflowAction[]
  history: TicketWorkflowHistoryEntry[]
}
