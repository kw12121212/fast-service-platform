import { useState } from 'react'
import { RefreshCcw } from 'lucide-react'

import { DynamicForm } from '@/components/admin'
import type { FormDescriptor } from '@/components/admin'
import { PageHeader } from '@/components/admin/page-header'
import { ResourceState } from '@/components/admin/resource-state'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { useCreateTeamAction, useTeamsResource } from '@/lib/api/hooks'

const createTeamDescriptor: FormDescriptor = {
  entityName: 'Team',
  fields: [
    { key: 'name', label: 'Team Name', widget: 'text', required: true },
    { key: 'description', label: 'Description', widget: 'text', required: false },
  ],
}

export function TeamsPage() {
  const teams = useTeamsResource()
  const createTeam = useCreateTeamAction()
  const [formKey, setFormKey] = useState(0)

  async function handleCreateTeam(values: Record<string, string | number | boolean>) {
    await createTeam.submit({
      name: String(values.name),
      description: String(values.description ?? ''),
    })
    setFormKey((k) => k + 1)
    teams.reload()
  }

  return (
    <div className="space-y-8">
      <PageHeader
        eyebrow="Organization"
        title="Team management"
        description="Create and manage teams, assign members, and bind teams to projects for agile delivery."
        actions={
          <Button variant="outline" onClick={teams.reload}>
            <RefreshCcw className="mr-2 size-4" />
            Refresh
          </Button>
        }
      />

      <div className="grid gap-4 xl:grid-cols-[0.78fr_1.22fr]">
        <Card className="bg-card/96">
          <CardHeader>
            <CardTitle className="text-lg">Create team</CardTitle>
          </CardHeader>
          <CardContent>
            <DynamicForm
              key={formKey}
              descriptor={createTeamDescriptor}
              onSubmit={handleCreateTeam}
              mutationStatus={createTeam.status}
              mutationError={createTeam.error}
              submittingMessage="Creating team through the backend service..."
              successMessage="Team created and the team list has been refreshed."
              submitLabel="Create team"
            />
          </CardContent>
        </Card>

        <Card className="bg-card/96">
          <CardContent className="p-6">
            <ResourceState
              status={teams.status}
              error={teams.error}
              empty={teams.data.length === 0}
              emptyTitle="No teams available"
              emptyMessage="Create a team using the form to get started."
              onRetry={teams.reload}
            >
              <div className="overflow-x-auto rounded-[24px] border border-border/60 bg-background/65">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>ID</TableHead>
                      <TableHead>Name</TableHead>
                      <TableHead>Description</TableHead>
                      <TableHead>Status</TableHead>
                      <TableHead>Members</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {teams.data.map((team) => (
                      <TableRow key={team.id}>
                        <TableCell className="font-medium">{team.id}</TableCell>
                        <TableCell>{team.name}</TableCell>
                        <TableCell>{team.description}</TableCell>
                        <TableCell>
                          <Badge
                            className={
                              team.status === 'Active'
                                ? 'bg-primary/12 text-primary'
                                : 'bg-muted text-muted-foreground'
                            }
                          >
                            {team.status}
                          </Badge>
                        </TableCell>
                        <TableCell>{team.memberCount}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            </ResourceState>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
