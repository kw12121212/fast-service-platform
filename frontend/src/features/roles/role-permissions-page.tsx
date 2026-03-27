import { useState } from 'react'
import { RefreshCcw } from 'lucide-react'

import { PageHeader } from '@/components/admin/page-header'
import { ResourceState } from '@/components/admin/resource-state'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Separator } from '@/components/ui/separator'
import { useRolePermissionsResource } from '@/lib/api/hooks'

export function RolePermissionsPage() {
  const [roleInput, setRoleInput] = useState('200')
  const parsedRoleId = Number(roleInput)
  const roleId = Number.isFinite(parsedRoleId) && parsedRoleId > 0 ? parsedRoleId : null
  const permissions = useRolePermissionsResource(roleId)

  return (
    <div className="space-y-8">
      <PageHeader
        eyebrow="Identity"
        title="Role permission management"
        description="The current backend only exposes the permission list for a specific role id. This page keeps that contract visible instead of inventing unsupported frontend behavior."
        actions={
          <Button variant="outline" onClick={permissions.reload}>
            <RefreshCcw className="mr-2 size-4" />
            Refresh
          </Button>
        }
      />

      <div className="grid gap-4 xl:grid-cols-[0.8fr_1.2fr]">
        <Card className="bg-card/96">
          <CardHeader>
            <CardTitle className="text-lg">Role scope</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="role-id">Role id</Label>
              <Input
                id="role-id"
                inputMode="numeric"
                value={roleInput}
                onChange={(event) => setRoleInput(event.target.value)}
                placeholder="200"
              />
            </div>

            <Separator />

            <div className="rounded-[22px] border border-border/60 bg-muted/35 p-4 text-sm leading-6 text-muted-foreground">
              Use role id <span className="font-semibold text-foreground">200</span> for
              the demo administrator role that ships with the backend demo data.
            </div>
          </CardContent>
        </Card>

        <Card className="bg-card/96">
          <CardHeader>
            <CardTitle className="text-lg">Assigned permissions</CardTitle>
          </CardHeader>
          <CardContent>
            <ResourceState
              status={permissions.status}
              error={permissions.error}
              empty={permissions.data.length === 0}
              emptyTitle="No permissions returned"
              emptyMessage="The selected role id does not currently expose permissions through the backend access control service."
              onRetry={permissions.reload}
            >
              <div className="grid gap-3">
                {permissions.data.map((permission) => (
                  <div
                    key={permission.code}
                    className="rounded-[22px] border border-border/60 bg-background/70 p-4"
                  >
                    <div className="flex flex-wrap items-center justify-between gap-3">
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
                  </div>
                ))}
              </div>
            </ResourceState>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
