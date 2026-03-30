import { type FormEvent, useState } from 'react'
import { RefreshCcw } from 'lucide-react'

import { MutationStatus } from '@/components/admin/mutation-status'
import { PageHeader } from '@/components/admin/page-header'
import { ResourceState } from '@/components/admin/resource-state'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Label } from '@/components/ui/label'
import {
  useAssignPermissionToRoleAction,
  useAssignRoleToUserAction,
  useCreatePermissionAction,
  useCreateRoleAction,
  usePermissionsResource,
  useRolePermissionsResource,
  useRolesForUserResource,
  useRolesResource,
  useUsersResource,
} from '@/lib/api/hooks'

function permissionScopeTone(scope: string) {
  if (scope === 'MENU') {
    return 'bg-primary/12 text-primary'
  }

  return 'bg-amber-500/14 text-amber-700'
}

export function RolePermissionsPage() {
  const users = useUsersResource()
  const roles = useRolesResource()
  const permissions = usePermissionsResource()
  const [selectedRoleId, setSelectedRoleId] = useState('')
  const [selectedUserId, setSelectedUserId] = useState('')
  const [selectedPermissionId, setSelectedPermissionId] = useState('')
  const [selectedAssignedRoleId, setSelectedAssignedRoleId] = useState('')
  const resolvedSelectedRoleId = roles.data.some(
    (role) => String(role.id) === selectedRoleId,
  )
    ? selectedRoleId
    : String(roles.data[0]?.id ?? '')
  const resolvedSelectedAssignedRoleId = roles.data.some(
    (role) => String(role.id) === selectedAssignedRoleId,
  )
    ? selectedAssignedRoleId
    : String(roles.data[0]?.id ?? '')
  const resolvedSelectedUserId = users.data.some(
    (user) => String(user.id) === selectedUserId,
  )
    ? selectedUserId
    : String(users.data[0]?.id ?? '')
  const resolvedSelectedPermissionId = permissions.data.some(
    (permission) => String(permission.id) === selectedPermissionId,
  )
    ? selectedPermissionId
    : String(permissions.data[0]?.id ?? '')
  const roleId =
    resolvedSelectedRoleId === '' ? null : Number(resolvedSelectedRoleId)
  const userId =
    resolvedSelectedUserId === '' ? null : Number(resolvedSelectedUserId)
  const rolePermissions = useRolePermissionsResource(roleId)
  const userRoles = useRolesForUserResource(userId)
  const createRole = useCreateRoleAction()
  const createPermission = useCreatePermissionAction()
  const assignPermissionToRole = useAssignPermissionToRoleAction()
  const assignRoleToUser = useAssignRoleToUserAction()
  const [roleCode, setRoleCode] = useState('')
  const [roleName, setRoleName] = useState('')
  const [permissionCode, setPermissionCode] = useState('')
  const [permissionName, setPermissionName] = useState('')
  const [permissionScope, setPermissionScope] = useState<'MENU' | 'FUNCTION'>(
    'MENU',
  )

  function reloadAll() {
    users.reload()
    roles.reload()
    permissions.reload()
    rolePermissions.reload()
    userRoles.reload()
  }

  async function handleCreateRole(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    try {
      const createdRoleId = await createRole.submit({
        roleCode,
        roleName,
      })
      setRoleCode('')
      setRoleName('')
      setSelectedRoleId(String(createdRoleId))
      setSelectedAssignedRoleId(String(createdRoleId))
      roles.reload()
      rolePermissions.reload()
      userRoles.reload()
    } catch {
      return
    }
  }

  async function handleCreatePermission(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    try {
      const createdPermissionId = await createPermission.submit({
        permissionCode,
        permissionName,
        scope: permissionScope,
      })
      setPermissionCode('')
      setPermissionName('')
      setPermissionScope('MENU')
      setSelectedPermissionId(String(createdPermissionId))
      permissions.reload()
    } catch {
      return
    }
  }

  async function handleAssignPermission(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (roleId === null || resolvedSelectedPermissionId === '') {
      return
    }

    try {
      await assignPermissionToRole.submit({
        roleId,
        permissionId: Number(resolvedSelectedPermissionId),
      })
      rolePermissions.reload()
    } catch {
      return
    }
  }

  async function handleAssignRole(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (userId === null || resolvedSelectedAssignedRoleId === '') {
      return
    }

    try {
      await assignRoleToUser.submit({
        userId,
        roleId: Number(resolvedSelectedAssignedRoleId),
      })
      userRoles.reload()
    } catch {
      return
    }
  }

  return (
    <div className="space-y-8">
      <PageHeader
        eyebrow="Identity"
        title="Role permission management"
        description="Roles, permissions, and user-role links are managed directly through the backend access-control service so the RBAC baseline matches the rest of the admin shell."
        actions={
          <Button variant="outline" onClick={reloadAll}>
            <RefreshCcw className="mr-2 size-4" />
            Refresh
          </Button>
        }
      />

      <div className="grid gap-4 xl:grid-cols-[0.92fr_1.08fr]">
        <div className="space-y-4">
          <Card className="bg-card/96">
            <CardHeader>
              <CardTitle className="text-lg">Create role</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <form className="space-y-4" onSubmit={handleCreateRole}>
                <div className="space-y-2">
                  <Label htmlFor="create-role-code">Role code</Label>
                  <input
                    id="create-role-code"
                    className="h-10 w-full rounded-lg border border-input bg-background px-3 text-sm outline-none transition-colors focus-visible:border-ring"
                    value={roleCode}
                    onChange={(event) => setRoleCode(event.target.value)}
                    placeholder="PROJECT_ADMIN"
                    required
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="create-role-name">Role name</Label>
                  <input
                    id="create-role-name"
                    className="h-10 w-full rounded-lg border border-input bg-background px-3 text-sm outline-none transition-colors focus-visible:border-ring"
                    value={roleName}
                    onChange={(event) => setRoleName(event.target.value)}
                    placeholder="Project Administrator"
                    required
                  />
                </div>

                <MutationStatus
                  status={createRole.status}
                  error={createRole.error}
                  submittingMessage="Creating role through the backend access-control service..."
                  successMessage="Role created and the RBAC views have been refreshed."
                />

                <Button type="submit" disabled={createRole.status === 'submitting'}>
                  Create role
                </Button>
              </form>
            </CardContent>
          </Card>

          <Card className="bg-card/96">
            <CardHeader>
              <CardTitle className="text-lg">Create permission</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <form className="space-y-4" onSubmit={handleCreatePermission}>
                <div className="space-y-2">
                  <Label htmlFor="create-permission-code">Permission code</Label>
                  <input
                    id="create-permission-code"
                    className="h-10 w-full rounded-lg border border-input bg-background px-3 text-sm outline-none transition-colors focus-visible:border-ring"
                    value={permissionCode}
                    onChange={(event) => setPermissionCode(event.target.value)}
                    placeholder="project.admin"
                    required
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="create-permission-name">Permission name</Label>
                  <input
                    id="create-permission-name"
                    className="h-10 w-full rounded-lg border border-input bg-background px-3 text-sm outline-none transition-colors focus-visible:border-ring"
                    value={permissionName}
                    onChange={(event) => setPermissionName(event.target.value)}
                    placeholder="Manage Project"
                    required
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="create-permission-scope">Permission scope</Label>
                  <select
                    id="create-permission-scope"
                    className="h-10 w-full rounded-lg border border-input bg-background px-3 text-sm outline-none transition-colors focus-visible:border-ring"
                    value={permissionScope}
                    onChange={(event) =>
                      setPermissionScope(event.target.value as 'MENU' | 'FUNCTION')
                    }
                  >
                    <option value="MENU">MENU</option>
                    <option value="FUNCTION">FUNCTION</option>
                  </select>
                </div>

                <MutationStatus
                  status={createPermission.status}
                  error={createPermission.error}
                  submittingMessage="Creating permission through the backend access-control service..."
                  successMessage="Permission created and the permission list has been refreshed."
                />

                <Button
                  type="submit"
                  disabled={createPermission.status === 'submitting'}
                >
                  Create permission
                </Button>
              </form>
            </CardContent>
          </Card>

          <Card className="bg-card/96">
            <CardHeader>
              <CardTitle className="text-lg">Grant permission to role</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <form className="space-y-4" onSubmit={handleAssignPermission}>
                <div className="rounded-[22px] border border-border/60 bg-muted/35 p-4 text-sm leading-6 text-muted-foreground">
                  Current role target:{' '}
                  <span className="font-semibold text-foreground">
                    {roles.data.find((role) => role.id === roleId)?.name ?? 'No role selected'}
                  </span>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="assign-permission-id">Permission to grant</Label>
                  <select
                    id="assign-permission-id"
                    className="h-10 w-full rounded-lg border border-input bg-background px-3 text-sm outline-none transition-colors focus-visible:border-ring"
                    value={resolvedSelectedPermissionId}
                    onChange={(event) => setSelectedPermissionId(event.target.value)}
                    disabled={permissions.data.length === 0 || roleId === null}
                  >
                    {permissions.data.map((permission) => (
                      <option key={permission.id} value={permission.id}>
                        {permission.name} · {permission.scope}
                      </option>
                    ))}
                  </select>
                </div>

                <MutationStatus
                  status={assignPermissionToRole.status}
                  error={assignPermissionToRole.error}
                  submittingMessage="Assigning permission to the selected role..."
                  successMessage="Role permission assignments have been refreshed."
                />

                <Button
                  type="submit"
                  disabled={
                    assignPermissionToRole.status === 'submitting' ||
                    roleId === null ||
                    resolvedSelectedPermissionId === ''
                  }
                >
                  Grant permission
                </Button>
              </form>
            </CardContent>
          </Card>

          <Card className="bg-card/96">
            <CardHeader>
              <CardTitle className="text-lg">Assign role to user</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <form className="space-y-4" onSubmit={handleAssignRole}>
                <div className="space-y-2">
                  <Label htmlFor="inspect-user-roles">Inspect user roles</Label>
                  <select
                    id="inspect-user-roles"
                    className="h-10 w-full rounded-lg border border-input bg-background px-3 text-sm outline-none transition-colors focus-visible:border-ring"
                    value={resolvedSelectedUserId}
                    onChange={(event) => setSelectedUserId(event.target.value)}
                    disabled={users.data.length === 0}
                  >
                    {users.data.map((user) => (
                      <option key={user.id} value={user.id}>
                        {user.displayName} · {user.username}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="assign-role-id">Role to assign</Label>
                  <select
                    id="assign-role-id"
                    className="h-10 w-full rounded-lg border border-input bg-background px-3 text-sm outline-none transition-colors focus-visible:border-ring"
                    value={resolvedSelectedAssignedRoleId}
                    onChange={(event) => setSelectedAssignedRoleId(event.target.value)}
                    disabled={roles.data.length === 0 || userId === null}
                  >
                    {roles.data.map((role) => (
                      <option key={role.id} value={role.id}>
                        {role.name} · {role.code}
                      </option>
                    ))}
                  </select>
                </div>

                <MutationStatus
                  status={assignRoleToUser.status}
                  error={assignRoleToUser.error}
                  submittingMessage="Assigning role to the selected user..."
                  successMessage="User role assignments have been refreshed."
                />

                <Button
                  type="submit"
                  disabled={
                    assignRoleToUser.status === 'submitting' ||
                    userId === null ||
                    resolvedSelectedAssignedRoleId === ''
                  }
                >
                  Assign role
                </Button>
              </form>
            </CardContent>
          </Card>
        </div>

        <div className="space-y-4">
          <Card className="bg-card/96">
            <CardHeader>
              <CardTitle className="text-lg">Available roles</CardTitle>
            </CardHeader>
            <CardContent>
              <ResourceState
                status={roles.status}
                error={roles.error}
                empty={roles.data.length === 0}
                emptyTitle="No roles available"
                emptyMessage="Create the first role through the backend access-control service to establish the RBAC baseline."
                onRetry={roles.reload}
              >
                <div className="grid gap-3">
                  {roles.data.map((role) => {
                    const isSelected = role.id === roleId
                    return (
                      <button
                        key={role.id}
                        type="button"
                        className={`rounded-[22px] border p-4 text-left transition-colors ${
                          isSelected
                            ? 'border-primary/40 bg-primary/8'
                            : 'border-border/60 bg-background/70 hover:border-border'
                        }`}
                        onClick={() => setSelectedRoleId(String(role.id))}
                      >
                        <div className="flex flex-wrap items-center justify-between gap-3">
                          <div>
                            <div className="font-medium">{role.name}</div>
                            <div className="mt-1 text-sm text-muted-foreground">
                              {role.code}
                            </div>
                          </div>
                          <Badge variant={isSelected ? 'default' : 'outline'}>
                            {isSelected ? 'Selected' : `Role #${role.id}`}
                          </Badge>
                        </div>
                      </button>
                    )
                  })}
                </div>
              </ResourceState>
            </CardContent>
          </Card>

          <div className="grid gap-4 xl:grid-cols-2">
            <Card className="bg-card/96">
              <CardHeader>
                <CardTitle className="text-lg">Selected role permissions</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="rounded-[22px] border border-border/60 bg-muted/35 p-4 text-sm leading-6 text-muted-foreground">
                  Inspecting permissions for{' '}
                  <span className="font-semibold text-foreground">
                    {roles.data.find((role) => role.id === roleId)?.name ?? 'No role selected'}
                  </span>
                  .
                </div>

                <ResourceState
                  status={rolePermissions.status}
                  error={rolePermissions.error}
                  empty={rolePermissions.data.length === 0}
                  emptyTitle="No permissions assigned"
                  emptyMessage="Grant a permission to the selected role to make the current RBAC relationship visible."
                  onRetry={rolePermissions.reload}
                >
                  <div className="grid gap-3">
                    {rolePermissions.data.map((permission) => (
                      <div
                        key={`${permission.id}-${permission.code}`}
                        className="rounded-[20px] border border-border/60 bg-background/70 p-4"
                      >
                        <div className="flex flex-wrap items-center justify-between gap-3">
                          <div>
                            <div className="font-medium">{permission.name}</div>
                            <div className="mt-1 text-sm text-muted-foreground">
                              {permission.code}
                            </div>
                          </div>
                          <Badge className={permissionScopeTone(permission.scope)}>
                            {permission.scope}
                          </Badge>
                        </div>
                      </div>
                    ))}
                  </div>
                </ResourceState>
              </CardContent>
            </Card>

            <Card className="bg-card/96">
              <CardHeader>
                <CardTitle className="text-lg">Roles for selected user</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="rounded-[22px] border border-border/60 bg-muted/35 p-4 text-sm leading-6 text-muted-foreground">
                  Inspecting role assignments for{' '}
                  <span className="font-semibold text-foreground">
                    {users.data.find((user) => user.id === userId)?.displayName ??
                      'No user selected'}
                  </span>
                  .
                </div>

                <ResourceState
                  status={userRoles.status}
                  error={userRoles.error}
                  empty={userRoles.data.length === 0}
                  emptyTitle="No roles assigned"
                  emptyMessage="Assign a role to the selected user to surface the current identity relationship."
                  onRetry={userRoles.reload}
                >
                  <div data-testid="selected-user-role-list" className="grid gap-3">
                    {userRoles.data.map((role) => (
                      <div
                        key={`${role.id}-${role.code}`}
                        className="rounded-[20px] border border-border/60 bg-background/70 p-4"
                      >
                        <div className="flex flex-wrap items-center justify-between gap-3">
                          <div>
                            <div className="font-medium">{role.name}</div>
                            <div className="mt-1 text-sm text-muted-foreground">
                              {role.code}
                            </div>
                          </div>
                          <Badge variant="outline" className="rounded-full">
                            Role #{role.id}
                          </Badge>
                        </div>
                      </div>
                    ))}
                  </div>
                </ResourceState>
              </CardContent>
            </Card>
          </div>

          <Card className="bg-card/96">
            <CardHeader>
              <CardTitle className="text-lg">Available permissions</CardTitle>
            </CardHeader>
            <CardContent>
              <ResourceState
                status={permissions.status}
                error={permissions.error}
                empty={permissions.data.length === 0}
                emptyTitle="No permissions available"
                emptyMessage="Create a permission through the backend access-control service to populate the RBAC permission baseline."
                onRetry={permissions.reload}
              >
                <div className="grid gap-3">
                  {permissions.data.map((permission) => (
                    <div
                      key={permission.id}
                      className="rounded-[22px] border border-border/60 bg-background/70 p-4"
                    >
                      <div className="flex flex-wrap items-center justify-between gap-3">
                        <div>
                          <div className="font-medium">{permission.name}</div>
                          <div className="mt-1 text-sm text-muted-foreground">
                            {permission.code}
                          </div>
                        </div>
                        <Badge className={permissionScopeTone(permission.scope)}>
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
    </div>
  )
}
