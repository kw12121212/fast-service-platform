set @packageName 'com.fastservice.platform.backend.generated.service';
set @serviceSrcDir './target/generated-sources/lealone-service';

-- MODULE: user-management
create service if not exists user_service (
  createUser(username varchar, displayName varchar, email varchar) long,
  listUsers() varchar
)
package @packageName
implement by 'com.fastservice.platform.backend.user.UserServiceImpl'
generate code @serviceSrcDir;

-- MODULE: role-permission-management
create service if not exists access_control_service (
  createRole(roleCode varchar, roleName varchar) long,
  createPermission(permissionCode varchar, permissionName varchar, scope varchar) long,
  assignPermissionToRole(roleId long, permissionId long) void,
  assignRoleToUser(userId long, roleId long) void,
  listRoles() varchar,
  listPermissions() varchar,
  listRolesForUser(userId long) varchar,
  listPermissionsForRole(roleId long) varchar
)
package @packageName
implement by 'com.fastservice.platform.backend.access.AccessControlServiceImpl'
generate code @serviceSrcDir;

-- MODULE: project-management
create service if not exists project_service (
  createProject(projectKey varchar, projectName varchar, description varchar) long,
  bindProjectRepository(projectId long, repositoryPath varchar) varchar,
  switchProjectBranch(projectId long, branchName varchar) varchar,
  createProjectWorktree(projectId long, branchName varchar) varchar,
  mergeProjectWorktree(projectId long, worktreePath varchar, targetBranch varchar) varchar,
  deleteProjectWorktree(projectId long, worktreePath varchar) varchar,
  createProjectSandboxImage(projectId long, worktreePath varchar) varchar,
  createProjectSandboxContainer(projectId long, worktreePath varchar) varchar,
  deleteProjectSandboxContainer(projectId long, worktreePath varchar) varchar,
  repairProjectWorktrees(projectId long) varchar,
  pruneProjectWorktrees(projectId long) varchar,
  getProjectDerivedAppAssembly(projectId long) varchar,
  requestProjectDerivedAppAssembly(projectId long, manifestJson varchar, outputDirectory varchar) varchar,
  getProjectDerivedAppVerification(projectId long) varchar,
  requestProjectDerivedAppVerification(projectId long) varchar,
  getProjectDerivedAppUpgradeSupport(projectId long) varchar,
  requestProjectDerivedAppUpgradeSupport(projectId long, requestType varchar, targetReleaseId varchar) varchar,
  listProjects() varchar
)
package @packageName
implement by 'com.fastservice.platform.backend.project.ProjectServiceImpl'
generate code @serviceSrcDir;

-- MODULE: kanban-management
create service if not exists kanban_service (
  createKanban(projectId long, boardName varchar) long,
  listKanbansByProject(projectId long) varchar
)
package @packageName
implement by 'com.fastservice.platform.backend.kanban.KanbanServiceImpl'
generate code @serviceSrcDir;

-- MODULE: kanban-state-machine
create service if not exists kanban_state_machine_service (
  isTransitionAllowed(fromState varchar, toState varchar) boolean,
  ensureTransition(fromState varchar, toState varchar) void
)
package @packageName
implement by 'com.fastservice.platform.backend.common.kanban.KanbanStateMachineServiceImpl'
generate code @serviceSrcDir;

-- MODULE: ticket-workflow
create workflow if not exists ticket_workflow_service (
  getWorkflow(ticketId long) varchar,
  executeWorkflowAction(ticketId long, actionName varchar, actorUserId long, comment varchar, assigneeUserId long) varchar
)
package @packageName
implement by 'com.fastservice.platform.backend.ticket.TicketWorkflowServiceImpl'
generate code @serviceSrcDir;

-- MODULE: ticket-management
create service if not exists ticket_service (
  createTicket(projectId long, kanbanId long, ticketKey varchar, title varchar, description varchar, assigneeUserId long) long,
  moveTicket(ticketId long, targetState varchar) varchar,
  listTicketsByProject(projectId long) varchar
)
package @packageName
implement by 'com.fastservice.platform.backend.ticket.TicketServiceImpl'
generate code @serviceSrcDir;
