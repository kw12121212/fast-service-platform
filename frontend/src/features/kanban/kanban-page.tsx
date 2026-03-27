import { type FormEvent, startTransition, useState } from 'react'
import { RefreshCcw } from 'lucide-react'

import { MutationStatus } from '@/components/admin/mutation-status'
import { PageHeader } from '@/components/admin/page-header'
import { ProjectScopeSelect } from '@/components/admin/project-scope-select'
import { ResourceState } from '@/components/admin/resource-state'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  useCreateKanbanAction,
  useKanbansResource,
  useProjectsResource,
  useTicketsResource,
} from '@/lib/api/hooks'

export function KanbanPage() {
  const projects = useProjectsResource()
  const [projectId, setProjectId] = useState<number | null>(null)
  const activeProjectId = projectId ?? projects.data[0]?.id ?? null
  const kanbans = useKanbansResource(activeProjectId)
  const tickets = useTicketsResource(activeProjectId)
  const createKanban = useCreateKanbanAction()
  const [boardName, setBoardName] = useState('')

  async function handleCreateBoard(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (activeProjectId === null) {
      return
    }

    try {
      await createKanban.submit({
        projectId: activeProjectId,
        boardName,
      })
      setBoardName('')
      kanbans.reload()
    } catch {
      return
    }
  }

  return (
    <div className="space-y-8">
      <PageHeader
        eyebrow="Delivery"
        title="Kanban management"
        description="The current backend exposes a minimal board model and ticket state flow. This page groups tickets by board and keeps the workflow intentionally small."
        actions={
          <>
            {projects.data.length > 0 ? (
              <ProjectScopeSelect
                projects={projects.data}
                projectId={activeProjectId}
                onProjectIdChange={(nextProjectId) =>
                  startTransition(() => setProjectId(nextProjectId))
                }
              />
            ) : null}
            <Button variant="outline" onClick={kanbans.reload}>
              <RefreshCcw className="mr-2 size-4" />
              Refresh boards
            </Button>
          </>
        }
      />

      <ResourceState
        status={projects.status}
        error={projects.error}
        empty={projects.data.length === 0}
        emptyTitle="No project scope available"
        emptyMessage="Kanban management needs at least one project because the backend board endpoint is project-scoped."
        onRetry={projects.reload}
        skeletonCount={2}
      >
        <div className="space-y-4">
          <Card className="bg-card/96">
            <CardHeader>
              <CardTitle className="text-lg">Create kanban board</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <form className="grid gap-4 md:grid-cols-[1fr_auto]" onSubmit={handleCreateBoard}>
                <div className="space-y-2">
                  <Label htmlFor="create-board-name">Board name</Label>
                  <Input
                    id="create-board-name"
                    value={boardName}
                    onChange={(event) => setBoardName(event.target.value)}
                    placeholder="Iteration Board"
                    required
                  />
                </div>

                <div className="flex items-end">
                  <Button
                    type="submit"
                    disabled={
                      activeProjectId === null ||
                      createKanban.status === 'submitting'
                    }
                  >
                    Create board
                  </Button>
                </div>
              </form>

              <MutationStatus
                status={createKanban.status}
                error={createKanban.error}
                submittingMessage="Creating board through the backend service..."
                successMessage="Board created and the board list has been refreshed."
              />
            </CardContent>
          </Card>

          <ResourceState
            status={kanbans.status}
            error={kanbans.error}
            empty={kanbans.data.length === 0}
            emptyTitle="No boards returned"
            emptyMessage="Create a kanban board in the backend core or enable demo data to see the delivery board."
            onRetry={kanbans.reload}
          >
            <div className="grid gap-4 xl:grid-cols-2">
              {kanbans.data.map((board) => {
                const boardTickets = tickets.data.filter(
                  (ticket) => ticket.kanbanId === board.id,
                )

                return (
                  <Card key={board.id} className="bg-card/96">
                    <CardHeader className="flex flex-row items-start justify-between gap-4">
                      <div>
                        <CardTitle className="text-lg">{board.name}</CardTitle>
                        <div className="mt-2 text-sm text-muted-foreground">
                          Board id {board.id}
                        </div>
                      </div>
                      <Badge className="bg-primary/12 text-primary">
                        {boardTickets.length} tickets
                      </Badge>
                    </CardHeader>
                    <CardContent className="space-y-4">
                      <div className="grid gap-3 sm:grid-cols-3">
                        <div className="rounded-[20px] border border-border/60 bg-muted/35 p-4">
                          <div className="text-xs font-medium uppercase tracking-[0.18em] text-muted-foreground">
                            TODO
                          </div>
                          <div className="mt-2 text-2xl font-semibold">
                            {
                              boardTickets.filter((ticket) => ticket.state === 'TODO')
                                .length
                            }
                          </div>
                        </div>
                        <div className="rounded-[20px] border border-border/60 bg-muted/35 p-4">
                          <div className="text-xs font-medium uppercase tracking-[0.18em] text-muted-foreground">
                            IN PROGRESS
                          </div>
                          <div className="mt-2 text-2xl font-semibold">
                            {
                              boardTickets.filter(
                                (ticket) => ticket.state === 'IN_PROGRESS',
                              ).length
                            }
                          </div>
                        </div>
                        <div className="rounded-[20px] border border-border/60 bg-muted/35 p-4">
                          <div className="text-xs font-medium uppercase tracking-[0.18em] text-muted-foreground">
                            DONE
                          </div>
                          <div className="mt-2 text-2xl font-semibold">
                            {
                              boardTickets.filter((ticket) => ticket.state === 'DONE')
                                .length
                            }
                          </div>
                        </div>
                      </div>

                      <div className="space-y-3">
                        {boardTickets.map((ticket) => (
                          <div
                            key={ticket.id}
                            className="rounded-[20px] border border-border/60 bg-background/70 p-4"
                          >
                            <div className="flex flex-wrap items-center justify-between gap-3">
                              <div>
                                <div className="font-medium">
                                  {ticket.key} · {ticket.title}
                                </div>
                                <div className="mt-1 text-sm text-muted-foreground">
                                  Ticket id {ticket.id}
                                </div>
                              </div>
                              <Badge variant="outline" className="rounded-full">
                                {ticket.state}
                              </Badge>
                            </div>
                          </div>
                        ))}
                      </div>
                    </CardContent>
                  </Card>
                )
              })}
            </div>
          </ResourceState>
        </div>
      </ResourceState>
    </div>
  )
}
