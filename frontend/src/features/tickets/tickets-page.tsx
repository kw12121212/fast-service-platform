import { type FormEvent, startTransition, useEffect, useState } from 'react'
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
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import {
  useCreateTicketAction,
  useKanbansResource,
  useMoveTicketAction,
  useProjectsResource,
  useTicketsResource,
  useUsersResource,
} from '@/lib/api/hooks'
import type { TicketState } from '@/lib/api/types'

function ticketStateTone(state: TicketState) {
  if (state === 'DONE') {
    return 'bg-emerald-500/12 text-emerald-700'
  }

  if (state === 'IN_PROGRESS') {
    return 'bg-amber-500/14 text-amber-700'
  }

  return 'bg-slate-500/12 text-slate-700'
}

function nextTicketState(state: TicketState) {
  if (state === 'TODO') {
    return 'IN_PROGRESS'
  }

  if (state === 'IN_PROGRESS') {
    return 'DONE'
  }

  return null
}

export function TicketsPage() {
  const projects = useProjectsResource()
  const [projectId, setProjectId] = useState<number | null>(null)
  const activeProjectId = projectId ?? projects.data[0]?.id ?? null
  const kanbans = useKanbansResource(activeProjectId)
  const tickets = useTicketsResource(activeProjectId)
  const users = useUsersResource()
  const createTicket = useCreateTicketAction()
  const moveTicket = useMoveTicketAction()
  const [ticketKey, setTicketKey] = useState('')
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [kanbanId, setKanbanId] = useState('')
  const [assigneeUserId, setAssigneeUserId] = useState('')
  const [movingTicketId, setMovingTicketId] = useState<number | null>(null)

  useEffect(() => {
    const firstKanbanId = kanbans.data[0]?.id
    if (!firstKanbanId) {
      setKanbanId('')
      return
    }

    const hasSelectedKanban = kanbans.data.some(
      (board) => String(board.id) === kanbanId,
    )

    if (!hasSelectedKanban) {
      setKanbanId(String(firstKanbanId))
    }
  }, [kanbanId, kanbans.data])

  useEffect(() => {
    const firstUserId = users.data[0]?.id
    if (!firstUserId) {
      setAssigneeUserId('')
      return
    }

    const hasSelectedUser = users.data.some(
      (user) => String(user.id) === assigneeUserId,
    )

    if (!hasSelectedUser) {
      setAssigneeUserId(String(firstUserId))
    }
  }, [assigneeUserId, users.data])

  async function handleCreateTicket(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (activeProjectId === null || kanbanId === '' || assigneeUserId === '') {
      return
    }

    try {
      await createTicket.submit({
        projectId: activeProjectId,
        kanbanId: Number(kanbanId),
        ticketKey,
        title,
        description,
        assigneeUserId: Number(assigneeUserId),
      })
      setTicketKey('')
      setTitle('')
      setDescription('')
      tickets.reload()
    } catch {
      return
    }
  }

  async function handleMoveTicket(ticketId: number, state: TicketState) {
    const targetState = nextTicketState(state)

    if (!targetState) {
      return
    }

    setMovingTicketId(ticketId)

    try {
      await moveTicket.submit({
        ticketId,
        targetState,
      })
      tickets.reload()
    } catch {
      return
    } finally {
      setMovingTicketId(null)
    }
  }

  return (
    <div className="space-y-8">
      <PageHeader
        eyebrow="Delivery"
        title="Ticket management"
        description="Tickets are fetched by project id from the backend ticket service. The project selector keeps the current backend contract explicit."
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
            <Button variant="outline" onClick={tickets.reload}>
              <RefreshCcw className="mr-2 size-4" />
              Refresh
            </Button>
          </>
        }
      />

      <ResourceState
        status={projects.status}
        error={projects.error}
        empty={projects.data.length === 0}
        emptyTitle="No project scope available"
        emptyMessage="Ticket management needs at least one software project because the backend ticket endpoint is project-scoped."
        onRetry={projects.reload}
        skeletonCount={2}
      >
        <div className="grid gap-4 xl:grid-cols-[0.86fr_1.14fr]">
          <Card className="bg-card/96">
            <CardHeader>
              <CardTitle className="text-lg">Create ticket</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <form className="space-y-4" onSubmit={handleCreateTicket}>
                <div className="grid gap-4 md:grid-cols-2">
                  <div className="space-y-2">
                    <Label htmlFor="create-ticket-key">Ticket key</Label>
                    <Input
                      id="create-ticket-key"
                      value={ticketKey}
                      onChange={(event) => setTicketKey(event.target.value)}
                      placeholder="FSP-3"
                      required
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="create-ticket-title">Title</Label>
                    <Input
                      id="create-ticket-title"
                      value={title}
                      onChange={(event) => setTitle(event.target.value)}
                      placeholder="Add operable admin workflow"
                      required
                    />
                  </div>
                </div>

                <div className="grid gap-4 md:grid-cols-2">
                  <div className="space-y-2">
                    <Label htmlFor="create-ticket-board">Board</Label>
                    <select
                      id="create-ticket-board"
                      className="h-10 w-full rounded-lg border border-input bg-background px-3 text-sm outline-none transition-colors focus-visible:border-ring"
                      value={kanbanId}
                      onChange={(event) => setKanbanId(event.target.value)}
                      disabled={kanbans.data.length === 0}
                    >
                      {kanbans.data.map((board) => (
                        <option key={board.id} value={board.id}>
                          {board.name}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="create-ticket-assignee">Assignee</Label>
                    <select
                      id="create-ticket-assignee"
                      className="h-10 w-full rounded-lg border border-input bg-background px-3 text-sm outline-none transition-colors focus-visible:border-ring"
                      value={assigneeUserId}
                      onChange={(event) => setAssigneeUserId(event.target.value)}
                      disabled={users.data.length === 0}
                    >
                      {users.data.map((user) => (
                        <option key={user.id} value={user.id}>
                          {user.displayName} · {user.username}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="create-ticket-description">Description</Label>
                  <textarea
                    id="create-ticket-description"
                    className="min-h-28 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none transition-colors focus-visible:border-ring focus-visible:ring-3 focus-visible:ring-ring/50"
                    value={description}
                    onChange={(event) => setDescription(event.target.value)}
                    placeholder="Describe the work item that should appear in the current project backlog."
                    required
                  />
                </div>

                {kanbans.data.length === 0 ? (
                  <div className="rounded-[18px] border border-dashed border-border/70 px-3 py-2 text-sm text-muted-foreground">
                    Create a kanban board for the selected project before creating tickets.
                  </div>
                ) : null}

                {users.data.length === 0 ? (
                  <div className="rounded-[18px] border border-dashed border-border/70 px-3 py-2 text-sm text-muted-foreground">
                    Create a user before assigning tickets through the backend contract.
                  </div>
                ) : null}

                <MutationStatus
                  status={createTicket.status}
                  error={createTicket.error}
                  submittingMessage="Creating ticket through the backend service..."
                  successMessage="Ticket created and the ticket list has been refreshed."
                />

                <MutationStatus
                  status={moveTicket.status}
                  error={moveTicket.error}
                  submittingMessage="Updating ticket state through the backend service..."
                  successMessage={`Ticket state updated to ${moveTicket.data ?? 'the requested state'}.`}
                />

                <Button
                  type="submit"
                  disabled={
                    activeProjectId === null ||
                    kanbanId === '' ||
                    assigneeUserId === '' ||
                    createTicket.status === 'submitting'
                  }
                >
                  Create ticket
                </Button>
              </form>
            </CardContent>
          </Card>

          <Card className="bg-card/96">
            <CardContent className="p-6">
              <ResourceState
                status={tickets.status}
                error={tickets.error}
                empty={tickets.data.length === 0}
                emptyTitle="No tickets returned"
                emptyMessage="This project has no tickets yet, or backend demo data is not enabled."
                onRetry={tickets.reload}
              >
                <div className="overflow-x-auto rounded-[24px] border border-border/60 bg-background/65">
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead>ID</TableHead>
                        <TableHead>Ticket key</TableHead>
                        <TableHead>Title</TableHead>
                        <TableHead>Board id</TableHead>
                        <TableHead>State</TableHead>
                        <TableHead>Action</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {tickets.data.map((ticket) => {
                        const targetState = nextTicketState(ticket.state)

                        return (
                          <TableRow key={ticket.id}>
                            <TableCell className="font-medium">{ticket.id}</TableCell>
                            <TableCell>{ticket.key}</TableCell>
                            <TableCell>{ticket.title}</TableCell>
                            <TableCell>{ticket.kanbanId}</TableCell>
                            <TableCell>
                              <Badge className={ticketStateTone(ticket.state)}>
                                {ticket.state}
                              </Badge>
                            </TableCell>
                            <TableCell>
                              {targetState ? (
                                <Button
                                  size="sm"
                                  variant="outline"
                                  disabled={
                                    moveTicket.status === 'submitting' &&
                                    movingTicketId === ticket.id
                                  }
                                  onClick={() =>
                                    handleMoveTicket(ticket.id, ticket.state)
                                  }
                                >
                                  Move to {targetState}
                                </Button>
                              ) : (
                                <span className="text-sm text-muted-foreground">
                                  Final state
                                </span>
                              )}
                            </TableCell>
                          </TableRow>
                        )
                      })}
                    </TableBody>
                  </Table>
                </div>
              </ResourceState>
            </CardContent>
          </Card>
        </div>
      </ResourceState>
    </div>
  )
}
