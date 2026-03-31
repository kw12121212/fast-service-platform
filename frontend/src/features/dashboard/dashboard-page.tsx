import { Activity, FolderKanban, ShieldCheck, Ticket, Users } from 'lucide-react'

import { DynamicReport } from '@/components/admin'
import type { ReportDescriptor, ReportResults } from '@/components/admin'
import { PageHeader } from '@/components/admin/page-header'
import { ResourceState } from '@/components/admin/resource-state'
import { StatCard } from '@/components/admin/stat-card'
import { Badge } from '@/components/ui/badge'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import {
  useKanbansResource,
  useProjectsResource,
  useRolePermissionsResource,
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
    </div>
  )
}
