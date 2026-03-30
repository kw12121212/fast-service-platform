import { type FormEvent, useState } from 'react'
import { RefreshCcw } from 'lucide-react'

import { MutationStatus } from '@/components/admin/mutation-status'
import { PageHeader } from '@/components/admin/page-header'
import { ResourceState } from '@/components/admin/resource-state'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  useBindProjectRepositoryAction,
  useCreateProjectWorktreeAction,
  useCreateProjectAction,
  useDeleteProjectWorktreeAction,
  useProjectsResource,
  usePruneProjectWorktreesAction,
  useRepairProjectWorktreesAction,
  useSwitchProjectBranchAction,
} from '@/lib/api/hooks'
import type { ProjectRepositorySummary, ProjectWorktreeSummary, SoftwareProject } from '@/lib/api/types'

function repositoryStateTone(
  workingTreeState: string,
  headState: string | null | undefined,
) {
  if (headState === 'DETACHED') {
    return 'bg-rose-500/12 text-rose-700'
  }

  if (workingTreeState === 'DIRTY') {
    return 'bg-amber-500/14 text-amber-700'
  }

  return 'bg-emerald-500/12 text-emerald-700'
}

function currentRefLabel(project: SoftwareProject) {
  if (!project.repository) {
    return null
  }

  if (project.repository.headState === 'DETACHED') {
    return 'Detached HEAD'
  }

  return project.repository.branch
}

function branchSwitchRestriction(project: SoftwareProject) {
  if (!project.repository) {
    return 'Bind a repository first to inspect local branches and switch Git context.'
  }

  if (project.repository.headState === 'DETACHED') {
    return 'Branch switching is unavailable while the repository is in detached HEAD state.'
  }

  if (project.repository.workingTreeState === 'DIRTY') {
    return 'Branch switching is unavailable while the working tree has uncommitted changes.'
  }

  if (project.repository.availableBranches.length === 0) {
    return 'No local branches are available for this repository.'
  }

  return null
}

function availableWorktreeBranches(repository: ProjectRepositorySummary) {
  const assignedBranches = new Set(
    repository.worktrees
      .map((worktree) => worktree.branch)
      .filter((branch): branch is string => branch !== null),
  )

  return repository.availableBranches.filter((branch) => !assignedBranches.has(branch))
}

function worktreeCreationRestriction(repository: ProjectRepositorySummary | null) {
  if (!repository) {
    return 'Bind a repository first to create and maintain project worktrees.'
  }

  if (repository.headState === 'DETACHED') {
    return 'Worktree creation is unavailable while the repository is in detached HEAD state.'
  }

  if (availableWorktreeBranches(repository).length === 0) {
    return 'Every visible local branch already has a worktree.'
  }

  return null
}

function worktreeTone(worktree: ProjectWorktreeSummary) {
  if (worktree.main) {
    return 'bg-primary/12 text-primary'
  }
  if (worktree.stale) {
    return 'bg-rose-500/12 text-rose-700'
  }
  if (worktree.workingTreeState === 'DIRTY' || worktree.hasUnpushedCommits) {
    return 'bg-amber-500/14 text-amber-700'
  }
  return 'bg-emerald-500/12 text-emerald-700'
}

function worktreeStatusLabel(worktree: ProjectWorktreeSummary) {
  if (worktree.main) {
    return 'Main worktree'
  }
  if (worktree.stale) {
    return 'Stale record'
  }
  if (worktree.workingTreeState === 'DIRTY') {
    return 'Dirty'
  }
  if (worktree.hasUnpushedCommits) {
    return 'Unpushed commits'
  }
  if (!worktree.hasUpstream) {
    return 'No upstream'
  }
  return 'Linked worktree'
}

function worktreeRefLabel(worktree: ProjectWorktreeSummary) {
  if (worktree.headState === 'DETACHED') {
    return 'Detached HEAD'
  }
  return worktree.branch ?? 'Unknown ref'
}

type ProjectRepositoryCardProps = {
  project: SoftwareProject
  onRepositoryBound: () => void
}

function ProjectRepositoryCard({
  project,
  onRepositoryBound,
}: ProjectRepositoryCardProps) {
  const bindProjectRepository = useBindProjectRepositoryAction()
  const switchProjectBranch = useSwitchProjectBranchAction()
  const createProjectWorktree = useCreateProjectWorktreeAction()
  const deleteProjectWorktree = useDeleteProjectWorktreeAction()
  const repairProjectWorktrees = useRepairProjectWorktreesAction()
  const pruneProjectWorktrees = usePruneProjectWorktreesAction()
  const [repositoryPath, setRepositoryPath] = useState(
    project.repository?.rootPath ?? '',
  )
  const [targetBranch, setTargetBranch] = useState(
    project.repository?.branch ?? project.repository?.availableBranches[0] ?? '',
  )
  const [worktreeBranch, setWorktreeBranch] = useState(
    project.repository ? availableWorktreeBranches(project.repository)[0] ?? '' : '',
  )
  const repositoryPathValue = repositoryPath || project.repository?.rootPath || ''
  const targetBranchValue =
    project.repository && project.repository.availableBranches.includes(targetBranch)
      ? targetBranch
      : project.repository?.branch ?? project.repository?.availableBranches[0] ?? ''
  const creatableWorktreeBranches = project.repository
    ? availableWorktreeBranches(project.repository)
    : []
  const worktreeBranchValue = creatableWorktreeBranches.includes(worktreeBranch)
    ? worktreeBranch
    : (creatableWorktreeBranches[0] ?? '')

  async function handleBindRepository(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    try {
      const boundRootPath = await bindProjectRepository.submit({
        projectId: project.id,
        repositoryPath: repositoryPathValue.trim(),
      })
      setRepositoryPath(boundRootPath)
      onRepositoryBound()
    } catch {
      return
    }
  }

  async function handleSwitchBranch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (!targetBranchValue.trim()) {
      return
    }

    try {
      await switchProjectBranch.submit({
        projectId: project.id,
        branchName: targetBranchValue,
      })
      onRepositoryBound()
    } catch {
      return
    }
  }

  async function handleCreateWorktree(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (!worktreeBranchValue.trim()) {
      return
    }

    try {
      await createProjectWorktree.submit({
        projectId: project.id,
        branchName: worktreeBranchValue,
      })
      onRepositoryBound()
    } catch {
      return
    }
  }

  async function handleDeleteWorktree(worktreePath: string) {
    try {
      await deleteProjectWorktree.submit({
        projectId: project.id,
        worktreePath,
      })
      onRepositoryBound()
    } catch {
      return
    }
  }

  async function handleRepairWorktrees() {
    try {
      await repairProjectWorktrees.submit({
        projectId: project.id,
      })
      onRepositoryBound()
    } catch {
      return
    }
  }

  async function handlePruneWorktrees() {
    try {
      await pruneProjectWorktrees.submit({
        projectId: project.id,
      })
      onRepositoryBound()
    } catch {
      return
    }
  }

  const branchRestriction = branchSwitchRestriction(project)
  const currentRef = currentRefLabel(project)
  const createRestriction = worktreeCreationRestriction(project.repository)

  return (
    <Card className="overflow-hidden border-border/70 bg-[linear-gradient(155deg,rgba(255,255,255,0.96),rgba(246,245,238,0.92))]">
      <CardContent className="space-y-6 p-6">
        <div className="flex flex-wrap items-start justify-between gap-4">
          <div>
            <div className="text-xs font-medium uppercase tracking-[0.2em] text-muted-foreground">
              Project key
            </div>
            <div className="mt-2 text-2xl font-semibold tracking-tight">
              {project.key}
            </div>
            <div className="mt-1 text-base text-muted-foreground">
              {project.name}
            </div>
          </div>
          <Badge
            className={
              project.active
                ? 'bg-primary/12 text-primary'
                : 'bg-muted text-muted-foreground'
            }
          >
            {project.active ? 'Active' : 'Inactive'}
          </Badge>
        </div>

        <div className="rounded-[22px] border border-border/60 bg-muted/35 p-4 text-sm leading-6 text-muted-foreground">
          Project id <span className="font-semibold text-foreground">{project.id}</span>{' '}
          is used by the current ticket and kanban endpoints as the project scope
          boundary.
        </div>

        {project.repository ? (
          <div className="rounded-[22px] border border-border/60 bg-background/80 p-4">
            <div className="flex flex-wrap items-start justify-between gap-3">
              <div>
                <div className="text-xs font-medium uppercase tracking-[0.2em] text-muted-foreground">
                  Bound repository
                </div>
                <div className="mt-2 text-sm font-medium text-foreground">
                  {project.repository.rootPath}
                </div>
              </div>
              <Badge
                className={repositoryStateTone(
                  project.repository.workingTreeState,
                  project.repository.headState,
                )}
              >
                {project.repository.headState === 'DETACHED'
                  ? 'DETACHED HEAD'
                  : project.repository.workingTreeState}
              </Badge>
            </div>

            <div className="mt-4 grid gap-3 text-sm text-muted-foreground">
              <div>
                Current ref{' '}
                <span className="font-medium text-foreground">
                  {currentRef}
                </span>
              </div>
              <div>
                Latest commit{' '}
                <span className="font-medium text-foreground">
                  {project.repository.latestCommitSummary}
                </span>
              </div>
            </div>

            <div className="mt-5 space-y-3 rounded-[20px] border border-border/60 bg-muted/28 p-4">
              <div className="text-xs font-medium uppercase tracking-[0.2em] text-muted-foreground">
                Recent commits
              </div>
              {project.repository.recentCommits.length > 0 ? (
                <div className="space-y-2">
                  {project.repository.recentCommits.map((commit) => (
                    <div
                      key={`${project.id}:${commit.hash}`}
                      className="flex flex-wrap items-center gap-2 text-sm"
                    >
                      <span className="rounded-full bg-background px-2 py-0.5 font-mono text-xs text-foreground">
                        {commit.hash}
                      </span>
                      <span className="text-muted-foreground">{commit.summary}</span>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-sm text-muted-foreground">
                  No commits available for this repository yet.
                </div>
              )}
            </div>

            <div className="space-y-4 rounded-[20px] border border-border/60 bg-background/80 p-4">
              <div className="flex flex-wrap items-start justify-between gap-3">
                <div>
                  <div className="text-xs font-medium uppercase tracking-[0.2em] text-muted-foreground">
                    Local branches
                  </div>
                  <div className="mt-2 text-sm text-muted-foreground">
                    Select an existing local branch to move this project&apos;s Git
                    context.
                  </div>
                </div>
                <Badge className="bg-primary/10 text-primary">
                  {project.repository.availableBranches.length} branches
                </Badge>
              </div>

              <div className="flex flex-wrap gap-2">
                {project.repository.availableBranches.map((branch) => (
                  <Badge
                    key={`${project.id}:${branch}`}
                    className={
                      branch === project.repository?.branch
                        ? 'bg-primary/12 text-primary'
                        : 'bg-muted text-muted-foreground'
                    }
                  >
                    {branch}
                  </Badge>
                ))}
              </div>

              {branchRestriction ? (
                <div className="rounded-[18px] border border-dashed border-border/70 bg-muted/24 px-3 py-2 text-sm text-muted-foreground">
                  {branchRestriction}
                </div>
              ) : (
                <form className="space-y-4" onSubmit={handleSwitchBranch}>
                  <div className="space-y-2">
                    <Label htmlFor={`switch-project-branch-${project.id}`}>
                      Local branch
                    </Label>
                    <select
                      id={`switch-project-branch-${project.id}`}
                      className="h-10 w-full rounded-lg border border-input bg-background px-3 text-sm outline-none ring-0 transition-colors focus-visible:border-ring"
                      value={targetBranchValue}
                      onChange={(event) => setTargetBranch(event.target.value)}
                    >
                      {project.repository.availableBranches.map((branch) => (
                        <option key={branch} value={branch}>
                          {branch}
                        </option>
                      ))}
                    </select>
                  </div>

                  <MutationStatus
                    status={switchProjectBranch.status}
                    error={switchProjectBranch.error}
                    submittingMessage="Switching project branch through the backend project service..."
                    successMessage="Branch switched and the project list has been refreshed."
                  />

                  <Button
                    type="submit"
                    disabled={switchProjectBranch.status === 'submitting'}
                  >
                    Switch branch
                  </Button>
                </form>
              )}
            </div>

            <div className="space-y-4 rounded-[20px] border border-border/60 bg-background/80 p-4">
              <div className="flex flex-wrap items-start justify-between gap-3">
                <div>
                  <div className="text-xs font-medium uppercase tracking-[0.2em] text-muted-foreground">
                    Worktree inventory
                  </div>
                  <div className="mt-2 text-sm text-muted-foreground">
                    Inspect linked worktrees, create branch-specific workspaces,
                    and run repair or prune from the current Projects experience.
                  </div>
                </div>
                <Badge className="bg-primary/10 text-primary">
                  {project.repository.worktrees.length} worktrees
                </Badge>
              </div>

              <div className="space-y-3">
                {project.repository.worktrees.map((worktree) => (
                  <div
                    key={`${project.id}:${worktree.path}`}
                    className="rounded-[18px] border border-border/60 bg-muted/24 p-4"
                  >
                    <div className="flex flex-wrap items-start justify-between gap-3">
                      <div>
                        <div className="text-sm font-medium text-foreground">
                          {worktreeRefLabel(worktree)}
                        </div>
                        <div className="mt-1 break-all text-sm text-muted-foreground">
                          {worktree.path}
                        </div>
                      </div>
                      <Badge className={worktreeTone(worktree)}>
                        {worktreeStatusLabel(worktree)}
                      </Badge>
                    </div>

                    <div className="mt-3 grid gap-2 text-sm text-muted-foreground">
                      <div>
                        Working tree{' '}
                        <span className="font-medium text-foreground">
                          {worktree.workingTreeState}
                        </span>
                      </div>
                      <div>
                        Upstream{' '}
                        <span className="font-medium text-foreground">
                          {worktree.hasUpstream ? 'Configured' : 'Missing'}
                        </span>
                      </div>
                      <div>
                        Push state{' '}
                        <span className="font-medium text-foreground">
                          {worktree.hasUnpushedCommits ? 'Ahead of upstream' : 'Fully pushed'}
                        </span>
                      </div>
                    </div>

                    {worktree.main ? (
                      <div className="mt-3 rounded-[16px] border border-dashed border-border/70 bg-background/60 px-3 py-2 text-sm text-muted-foreground">
                        The main repository worktree stays attached to the project and
                        cannot be removed from this view.
                      </div>
                    ) : worktree.deletionAllowed ? (
                      <div className="mt-3 flex flex-wrap items-center justify-between gap-3">
                        <div className="text-sm text-muted-foreground">
                          This linked worktree is clean and fully pushed.
                        </div>
                        <Button
                          variant="outline"
                          onClick={() => handleDeleteWorktree(worktree.path)}
                          disabled={deleteProjectWorktree.status === 'submitting'}
                        >
                          Delete worktree
                        </Button>
                      </div>
                    ) : (
                      <div className="mt-3 flex flex-wrap items-center justify-between gap-3">
                        <div className="text-sm text-muted-foreground">
                          {worktree.deletionRestriction ??
                            'This linked worktree must be handled manually before deletion.'}
                        </div>
                        <Button variant="outline" disabled>
                          Delete worktree
                        </Button>
                      </div>
                    )}
                  </div>
                ))}
              </div>

              <div className="flex flex-wrap gap-3">
                <Button
                  variant="outline"
                  onClick={handleRepairWorktrees}
                  disabled={
                    repairProjectWorktrees.status === 'submitting' ||
                    project.repository.headState === 'DETACHED'
                  }
                >
                  Repair metadata
                </Button>
                <Button
                  variant="outline"
                  onClick={handlePruneWorktrees}
                  disabled={pruneProjectWorktrees.status === 'submitting'}
                >
                  Prune stale records
                </Button>
              </div>

              {project.repository.headState === 'DETACHED' ? (
                <div className="rounded-[18px] border border-dashed border-border/70 bg-muted/24 px-3 py-2 text-sm text-muted-foreground">
                  Repair is unavailable while the repository is in detached HEAD
                  state, but prune remains available for stale records.
                </div>
              ) : null}

              <MutationStatus
                status={repairProjectWorktrees.status}
                error={repairProjectWorktrees.error}
                submittingMessage="Repairing project worktree metadata through the backend project service..."
                successMessage="Worktree metadata repaired and the project list has been refreshed."
              />

              <MutationStatus
                status={pruneProjectWorktrees.status}
                error={pruneProjectWorktrees.error}
                submittingMessage="Pruning stale worktree records through the backend project service..."
                successMessage="Stale worktree records pruned and the project list has been refreshed."
              />

              {createRestriction ? (
                <div className="rounded-[18px] border border-dashed border-border/70 bg-muted/24 px-3 py-2 text-sm text-muted-foreground">
                  {createRestriction}
                </div>
              ) : (
                <form className="space-y-4" onSubmit={handleCreateWorktree}>
                  <div className="space-y-2">
                    <Label htmlFor={`create-project-worktree-${project.id}`}>
                      Worktree branch
                    </Label>
                    <select
                      id={`create-project-worktree-${project.id}`}
                      className="h-10 w-full rounded-lg border border-input bg-background px-3 text-sm outline-none ring-0 transition-colors focus-visible:border-ring"
                      value={worktreeBranchValue}
                      onChange={(event) => setWorktreeBranch(event.target.value)}
                    >
                      {creatableWorktreeBranches.map((branch) => (
                        <option key={branch} value={branch}>
                          {branch}
                        </option>
                      ))}
                    </select>
                  </div>

                  <MutationStatus
                    status={createProjectWorktree.status}
                    error={createProjectWorktree.error}
                    submittingMessage="Creating project worktree through the backend project service..."
                    successMessage="Worktree created and the project list has been refreshed."
                  />

                  <MutationStatus
                    status={deleteProjectWorktree.status}
                    error={deleteProjectWorktree.error}
                    submittingMessage="Deleting project worktree through the backend project service..."
                    successMessage="Worktree removed and the project list has been refreshed."
                  />

                  <Button
                    type="submit"
                    disabled={createProjectWorktree.status === 'submitting'}
                  >
                    Create worktree
                  </Button>
                </form>
              )}
            </div>
          </div>
        ) : (
          <div className="rounded-[22px] border border-dashed border-border/70 bg-background/55 p-4 text-sm leading-6 text-muted-foreground">
            No repository bound. Connect an absolute local Git repository path to
            link this project to a real engineering workspace.
          </div>
        )}

        <form className="space-y-4" onSubmit={handleBindRepository}>
          <div className="space-y-2">
            <Label htmlFor={`bind-project-repository-${project.id}`}>
              Repository path
            </Label>
            <Input
              id={`bind-project-repository-${project.id}`}
              value={repositoryPathValue}
              onChange={(event) => setRepositoryPath(event.target.value)}
              placeholder="/absolute/path/to/repository"
              required
            />
          </div>

          <MutationStatus
            status={bindProjectRepository.status}
            error={bindProjectRepository.error}
            submittingMessage="Binding project repository through the backend project service..."
            successMessage="Repository bound and the project list has been refreshed."
          />

          <Button
            type="submit"
            disabled={bindProjectRepository.status === 'submitting'}
          >
            Bind repository
          </Button>
        </form>
      </CardContent>
    </Card>
  )
}

export function ProjectsPage() {
  const projects = useProjectsResource()
  const createProject = useCreateProjectAction()
  const [projectKey, setProjectKey] = useState('')
  const [projectName, setProjectName] = useState('')
  const [description, setDescription] = useState('')

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    try {
      await createProject.submit({
        projectKey,
        projectName,
        description,
      })
      setProjectKey('')
      setProjectName('')
      setDescription('')
      projects.reload()
    } catch {
      return
    }
  }

  return (
    <div className="space-y-8">
      <PageHeader
        eyebrow="Delivery"
        title="Software project management"
        description="Projects are displayed from the backend project service and provide the scope anchor for kanban and ticket pages."
        actions={
          <Button variant="outline" onClick={projects.reload}>
            <RefreshCcw className="mr-2 size-4" />
            Refresh
          </Button>
        }
      />

      <div className="grid gap-4 xl:grid-cols-[0.82fr_1.18fr]">
        <Card className="bg-card/96">
          <CardHeader>
            <CardTitle className="text-lg">Create project</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <form className="space-y-4" onSubmit={handleSubmit}>
              <div className="space-y-2">
                <Label htmlFor="create-project-key">Project key</Label>
                <Input
                  id="create-project-key"
                  value={projectKey}
                  onChange={(event) => setProjectKey(event.target.value)}
                  placeholder="FSP"
                  required
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="create-project-name">Project name</Label>
                <Input
                  id="create-project-name"
                  value={projectName}
                  onChange={(event) => setProjectName(event.target.value)}
                  placeholder="Fast Service Platform"
                  required
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="create-project-description">Description</Label>
                <textarea
                  id="create-project-description"
                  className="min-h-28 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none transition-colors focus-visible:border-ring focus-visible:ring-3 focus-visible:ring-ring/50"
                  value={description}
                  onChange={(event) => setDescription(event.target.value)}
                  placeholder="Describe the delivery scope this project anchors."
                  required
                />
              </div>

              <MutationStatus
                status={createProject.status}
                error={createProject.error}
                submittingMessage="Creating project through the backend service..."
                successMessage="Project created and the project list has been refreshed."
              />

              <Button type="submit" disabled={createProject.status === 'submitting'}>
                Create project
              </Button>
            </form>
          </CardContent>
        </Card>

        <ResourceState
          status={projects.status}
          error={projects.error}
          empty={projects.data.length === 0}
          emptyTitle="No projects returned"
          emptyMessage="Add a software project through the backend core or enable demo data to seed the first project."
          onRetry={projects.reload}
        >
          <div className="grid gap-4 xl:grid-cols-2">
            {projects.data.map((project) => (
              <ProjectRepositoryCard
                key={project.id}
                project={project}
                onRepositoryBound={projects.reload}
              />
            ))}
          </div>
        </ResourceState>
      </div>
    </div>
  )
}
