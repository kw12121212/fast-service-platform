import { Activity, FolderKanban, ShieldCheck, Ticket, Users } from 'lucide-react'
import { useState } from 'react'

import { DynamicReport, WorkflowPanel } from '@/components/admin'
import type { ReportDescriptor, ReportResults, WorkflowDescriptor } from '@/components/admin'
import { PageHeader } from '@/components/admin/page-header'
import { ResourceState } from '@/components/admin/resource-state'
import { StatCard } from '@/components/admin/stat-card'
import { Badge } from '@/components/ui/badge'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import {
  useKanbansResource,
  useProjectsResource,
  useExecuteTicketWorkflowAction,
  useRolePermissionsResource,
  useTicketWorkflowResource,
  useTicketsResource,
  useUsersResource,
} from '@/lib/api/hooks'

export function DashboardPage() {
  const users = useUsersResource()
  const projects = useProjectsResource()
  const selectedProject = projects.data[0] ?? null
  const permissions = useRolePermissionsResource(200)
  const kanbans = useKanbansResource(selectedProject?.id ?? null)
  const tickets = useTicketsResource(selectedProject?.id ?? null)
  const [selectedWorkflowTicketId, setSelectedWorkflowTicketId] = useState<number | null>(null)
  const activeWorkflowTicketId = selectedWorkflowTicketId ?? tickets.data[0]?.id ?? null
  const ticketWorkflow = useTicketWorkflowResource(activeWorkflowTicketId)
  const executeWorkflowAction = useExecuteTicketWorkflowAction()

  const workflowDescriptor: WorkflowDescriptor = {
    entityName: 'Ticket workflow',
    stateLabel: 'Current state',
    assigneeLabel: 'Current assignee',
    commentLabel: 'Comment',
    commentPlaceholder: 'Explain why this workflow action is being taken.',
    historyTitle: 'Workflow history',
    actions: [
      { action: 'submit', label: 'Submit' },
      { action: 'approve', label: 'Approve' },
      { action: 'reject', label: 'Reject' },
      { action: 'reassign', label: 'Reassign', requiresAssignee: true },
    ],
  }

  const ticketStateReport: ReportDescriptor = {
    sections: [
      {
        type: 'chart',
        sectionKey: 'ticket-states',
        chartType: 'bar',
        title: 'Ticket state breakdown',
      },
    ],
  }

  const ticketStateResults: ReportResults = {
    'ticket-states': [
      { label: 'TODO', value: tickets.data.filter((t) => t.state === 'TODO').length },
      { label: 'In Progress', value: tickets.data.filter((t) => t.state === 'IN_PROGRESS').length },
      { label: 'Done', value: tickets.data.filter((t) => t.state === 'DONE').length },
    ],
  }

  return (
    <div className="space-y-8">
      <PageHeader
        eyebrow="Overview"
        title="Current V1 enterprise baseline"
        description="This dashboard reads directly from the current backend core and shows the minimum delivery surface expected from the first generated enterprise admin."
      />

      <div className="grid gap-4 xl:grid-cols-5">
        <StatCard
          label="Users"
          value={String(users.data.length)}
          detail="Live users returned by the backend user service."
          icon={<Users className="size-3.5" />}
        />
        <StatCard
          label="Projects"
          value={String(projects.data.length)}
          detail="Software projects currently available to the admin shell."
          tone="accent"
          icon={<FolderKanban className="size-3.5" />}
        />
        <StatCard
          label="Role permissions"
          value={String(permissions.data.length)}
          detail="Permissions exposed for the default demo administrator role."
          icon={<ShieldCheck className="size-3.5" />}
        />
        <StatCard
          label="Tickets"
          value={String(tickets.data.length)}
          detail="Current tickets for the selected baseline project."
          icon={<Ticket className="size-3.5" />}
        />
        <StatCard
          label="Kanban boards"
          value={String(kanbans.data.length)}
          detail="Boards returned by the current kanban service."
          icon={<Activity className="size-3.5" />}
        />
      </div>

      <div className="grid gap-4 xl:grid-cols-[1.15fr_0.85fr]">
        <Card className="bg-card/95">
          <CardHeader>
            <CardTitle className="text-lg">Backend signal</CardTitle>
          </CardHeader>
          <CardContent>
            <ResourceState
              status={projects.status}
              error={projects.error}
              empty={projects.data.length === 0}
              emptyTitle="No projects available"
              emptyMessage="Start the backend with demo data or create a project in the backend core to populate the dashboard."
              onRetry={projects.reload}
              skeletonCount={2}
            >
              <div className="space-y-4">
                <div className="rounded-[24px] border border-border/60 bg-background/70 p-5">
                  <div className="flex flex-wrap items-center gap-2">
                    <Badge className="rounded-full bg-primary/12 text-primary">
                      Active project focus
                    </Badge>
                    <Badge
                      variant="outline"
                      className="rounded-full bg-background/72 text-muted-foreground"
                    >
                      {selectedProject ? selectedProject.key : 'none'}
                    </Badge>
                  </div>
                  <div className="mt-4 text-xl font-semibold tracking-tight">
                    {selectedProject
                      ? `${selectedProject.key} · ${selectedProject.name}`
                      : 'No active project'}
                  </div>
                  <p className="mt-3 max-w-2xl text-sm leading-6 text-muted-foreground">
                    The dashboard uses the first available project as the baseline scope for kanban and ticket metrics, which matches the current backend service contract.
                  </p>
                </div>

                <div className="grid gap-3 md:grid-cols-3">
                  <div className="rounded-[22px] border border-border/60 bg-muted/35 p-4">
                    <div className="text-xs font-medium uppercase tracking-[0.18em] text-muted-foreground">
                      TODO
                    </div>
                    <div className="mt-2 text-2xl font-semibold">
                      {tickets.data.filter((ticket) => ticket.state === 'TODO').length}
                    </div>
                  </div>
                  <div className="rounded-[22px] border border-border/60 bg-muted/35 p-4">
                    <div className="text-xs font-medium uppercase tracking-[0.18em] text-muted-foreground">
                      IN PROGRESS
                    </div>
                    <div className="mt-2 text-2xl font-semibold">
                      {
                        tickets.data.filter(
                          (ticket) => ticket.state === 'IN_PROGRESS',
                        ).length
                      }
                    </div>
                  </div>
                  <div className="rounded-[22px] border border-border/60 bg-muted/35 p-4">
                    <div className="text-xs font-medium uppercase tracking-[0.18em] text-muted-foreground">
                      DONE
                    </div>
                    <div className="mt-2 text-2xl font-semibold">
                      {tickets.data.filter((ticket) => ticket.state === 'DONE').length}
                    </div>
                  </div>
                </div>
              </div>
            </ResourceState>
          </CardContent>
        </Card>

        <Card className="bg-card/95">
          <CardHeader>
            <CardTitle className="text-lg">Role and board highlights</CardTitle>
          </CardHeader>
          <CardContent className="grid gap-4">
            <ResourceState
              status={permissions.status}
              error={permissions.error}
              empty={permissions.data.length === 0}
              emptyTitle="No permissions returned"
              emptyMessage="The current backend only exposes permissions by role id. The dashboard reads the default admin role id 200."
              onRetry={permissions.reload}
              skeletonCount={2}
            >
              <div className="space-y-4">
                <div className="rounded-[22px] border border-border/60 bg-muted/35 p-4">
                  <div className="text-xs font-medium uppercase tracking-[0.18em] text-muted-foreground">
                    Default role id
                  </div>
                  <div className="mt-2 text-xl font-semibold">200 · ADMIN</div>
                </div>

                <div className="space-y-3">
                  {permissions.data.map((permission) => (
                    <div
                      key={permission.code}
                      className="flex items-start justify-between gap-4 rounded-[20px] border border-border/60 bg-background/70 p-4"
                    >
                      <div>
                        <div className="font-medium">{permission.name}</div>
                        <div className="mt-1 text-sm text-muted-foreground">
                          {permission.code}
                        </div>
                      </div>
                      <Badge variant="outline" className="rounded-full">
                        {permission.scope}
                      </Badge>
                    </div>
                  ))}
                </div>
              </div>
            </ResourceState>
          </CardContent>
        </Card>
      </div>

      <DynamicReport descriptor={ticketStateReport} results={ticketStateResults} />

      <Card className="bg-card/95">
        <CardHeader>
          <CardTitle className="text-lg">Workflow example</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <p className="max-w-3xl text-sm leading-6 text-muted-foreground">
            This dashboard example uses the reusable workflow component against the current backend ticket service, including workflow detail, bounded actions, reassignment, required comments, and visible history.
          </p>

          {tickets.data.length > 0 ? (
            <div className="space-y-2">
              <label className="text-sm font-medium" htmlFor="workflow-ticket-select">
                Workflow ticket
              </label>
              <select
                id="workflow-ticket-select"
                className="h-10 w-full rounded-lg border border-input bg-background px-3 text-sm outline-none transition-colors focus-visible:border-ring"
                value={String(activeWorkflowTicketId ?? '')}
                onChange={(event) => setSelectedWorkflowTicketId(Number(event.target.value))}
              >
                {tickets.data.map((ticket) => (
                  <option key={ticket.id} value={ticket.id}>
                    {ticket.key} · {ticket.title}
                  </option>
                ))}
              </select>
            </div>
          ) : null}

          <ResourceState
            status={ticketWorkflow.status}
            error={ticketWorkflow.error}
            empty={ticketWorkflow.data === null}
            emptyTitle="No workflow example ticket available"
            emptyMessage="Create or load at least one ticket to view the reusable workflow panel against the backend ticket workflow path."
            onRetry={ticketWorkflow.reload}
          >
            {ticketWorkflow.data ? (
              <WorkflowPanel
                descriptor={workflowDescriptor}
                instance={ticketWorkflow.data}
                availableAssignees={users.data.map((user) => ({
                  userId: user.id,
                  displayName: user.displayName,
                  username: user.username,
                }))}
                mutationStatus={executeWorkflowAction.status}
                mutationError={executeWorkflowAction.error}
                submittingMessage="Executing backend ticket workflow action..."
                successMessage={`Workflow action completed with result ${executeWorkflowAction.data ?? 'success'}.`}
                onAction={async ({ action, comment, assigneeUserId }) => {
                  if (!ticketWorkflow.data) {
                    return
                  }

                  await executeWorkflowAction.submit({
                    ticketId: ticketWorkflow.data.ticketId,
                    actionName: action,
                    actorUserId: users.data[0]?.id ?? ticketWorkflow.data.assignee.userId,
                    comment,
                    assigneeUserId,
                  })
                  ticketWorkflow.reload()
                  tickets.reload()
                }}
              />
            ) : null}
          </ResourceState>
        </CardContent>
      </Card>
    </div>
  )
}
