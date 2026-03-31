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
import { useCreateUserAction, useUsersResource } from '@/lib/api/hooks'

const createUserDescriptor: FormDescriptor = {
  entityName: 'User',
  fields: [
    { key: 'username', label: 'Username', widget: 'text', required: true },
    { key: 'displayName', label: 'Display name', widget: 'text', required: true },
    { key: 'email', label: 'Email', widget: 'text', required: true },
  ],
}

export function UsersPage() {
  const users = useUsersResource()
  const createUser = useCreateUserAction()
  const [formKey, setFormKey] = useState(0)

  async function handleCreateUser(values: Record<string, string | number | boolean>) {
    await createUser.submit({
      username: String(values.username),
      displayName: String(values.displayName),
      email: String(values.email),
    })
    setFormKey((k) => k + 1)
    users.reload()
  }

  return (
    <div className="space-y-8">
      <PageHeader
        eyebrow="Identity"
        title="User management"
        description="The first admin shell reads users directly from the backend user service and exposes the minimal identity baseline expected by V1."
        actions={
          <Button variant="outline" onClick={users.reload}>
            <RefreshCcw className="mr-2 size-4" />
            Refresh
          </Button>
        }
      />

      <div className="grid gap-4 xl:grid-cols-[0.78fr_1.22fr]">
        <Card className="bg-card/96">
          <CardHeader>
            <CardTitle className="text-lg">Create user</CardTitle>
          </CardHeader>
          <CardContent>
            <DynamicForm
              key={formKey}
              descriptor={createUserDescriptor}
              onSubmit={handleCreateUser}
              mutationStatus={createUser.status}
              mutationError={createUser.error}
              submittingMessage="Creating user through the backend service..."
              successMessage="User created and the user list has been refreshed."
              submitLabel="Create user"
            />
          </CardContent>
        </Card>

        <Card className="bg-card/96">
          <CardContent className="p-6">
            <ResourceState
              status={users.status}
              error={users.error}
              empty={users.data.length === 0}
              emptyTitle="No users available"
              emptyMessage="Enable backend demo data or add application users through the backend service to populate this page."
              onRetry={users.reload}
            >
              <div className="overflow-x-auto rounded-[24px] border border-border/60 bg-background/65">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>ID</TableHead>
                      <TableHead>Username</TableHead>
                      <TableHead>Display name</TableHead>
                      <TableHead>Email</TableHead>
                      <TableHead>Status</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {users.data.map((user) => (
                      <TableRow key={user.id}>
                        <TableCell className="font-medium">{user.id}</TableCell>
                        <TableCell>{user.username}</TableCell>
                        <TableCell>{user.displayName}</TableCell>
                        <TableCell>{user.email}</TableCell>
                        <TableCell>
                          <Badge
                            className={
                              user.enabled
                                ? 'bg-primary/12 text-primary'
                                : 'bg-muted text-muted-foreground'
                            }
                          >
                            {user.enabled ? 'Enabled' : 'Disabled'}
                          </Badge>
                        </TableCell>
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
