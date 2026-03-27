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
  useCreateProjectAction,
  useProjectsResource,
} from '@/lib/api/hooks'
import type { SoftwareProject } from '@/lib/api/types'

function repositoryStateTone(workingTreeState: string) {
  if (workingTreeState === 'DIRTY') {
    return 'bg-amber-500/14 text-amber-700'
  }

  return 'bg-emerald-500/12 text-emerald-700'
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
  const [repositoryPath, setRepositoryPath] = useState(
    project.repository?.rootPath ?? '',
  )

  async function handleBindRepository(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    try {
      const boundRootPath = await bindProjectRepository.submit({
        projectId: project.id,
        repositoryPath: repositoryPath.trim(),
      })
      setRepositoryPath(boundRootPath)
      onRepositoryBound()
    } catch {
      return
    }
  }

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
                className={repositoryStateTone(project.repository.workingTreeState)}
              >
                {project.repository.workingTreeState}
              </Badge>
            </div>

            <div className="mt-4 grid gap-3 text-sm text-muted-foreground">
              <div>
                Branch{' '}
                <span className="font-medium text-foreground">
                  {project.repository.branch}
                </span>
              </div>
              <div>
                Latest commit{' '}
                <span className="font-medium text-foreground">
                  {project.repository.latestCommitSummary}
                </span>
              </div>
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
              value={repositoryPath}
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
                key={`${project.id}:${project.repository?.rootPath ?? 'unbound'}`}
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
