import { type FormEvent, useState } from 'react'
import { RefreshCcw } from 'lucide-react'

import { MutationStatus } from '@/components/admin/mutation-status'
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
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useCreateUserAction, useUsersResource } from '@/lib/api/hooks'

export function UsersPage() {
  const users = useUsersResource()
  const createUser = useCreateUserAction()
  const [username, setUsername] = useState('')
  const [displayName, setDisplayName] = useState('')
  const [email, setEmail] = useState('')

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    try {
      await createUser.submit({
        username,
        displayName,
        email,
      })
      setUsername('')
      setDisplayName('')
      setEmail('')
      users.reload()
    } catch {
      return
    }
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
          <CardContent className="space-y-4">
            <form className="space-y-4" onSubmit={handleSubmit}>
              <div className="space-y-2">
                <Label htmlFor="create-user-username">Username</Label>
                <Input
                  id="create-user-username"
                  value={username}
                  onChange={(event) => setUsername(event.target.value)}
                  placeholder="service-owner"
                  required
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="create-user-display-name">Display name</Label>
                <Input
                  id="create-user-display-name"
                  value={displayName}
                  onChange={(event) => setDisplayName(event.target.value)}
                  placeholder="Service Owner"
                  required
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="create-user-email">Email</Label>
                <Input
                  id="create-user-email"
                  type="email"
                  value={email}
                  onChange={(event) => setEmail(event.target.value)}
                  placeholder="service-owner@example.com"
                  required
                />
              </div>

              <MutationStatus
                status={createUser.status}
                error={createUser.error}
                submittingMessage="Creating user through the backend service..."
                successMessage="User created and the user list has been refreshed."
              />

              <Button type="submit" disabled={createUser.status === 'submitting'}>
                Create user
              </Button>
            </form>
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
