import assert from 'node:assert/strict'
import { spawnSync } from 'node:child_process'
import { readFileSync } from 'node:fs'
import { chmod, cp, mkdir, mkdtemp, readFile, rm, writeFile } from 'node:fs/promises'
import os from 'node:os'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const SCRIPT_DIR = path.dirname(fileURLToPath(import.meta.url))
export const REPO_ROOT = path.resolve(SCRIPT_DIR, '..')
const DERIVED_APP_LIFECYCLE_METADATA_PATH = 'docs/ai/derived-app-lifecycle.json'
const STRUCTURED_APP_TEMPLATE_CONTRACT_PATH = 'docs/ai/structured-app-template-contract.json'
const DERIVED_APP_TEMPLATE_MAP_PATH =
  'docs/ai/template-classifications/default-derived-app-template-map.json'
const DYNAMIC_GENERATED_DOCS = new Set([
  'docs/ai/context.json',
  DERIVED_APP_LIFECYCLE_METADATA_PATH
])

const ROUTE_MODULES = [
  {
    id: 'admin-shell',
    route: '/dashboard',
    importName: 'DashboardPage',
    importPath: '@/features/dashboard/dashboard-page',
    nav: {
      label: 'Dashboard',
      eyebrow: 'Operations',
      icon: 'LayoutDashboard',
      title: 'Assembly Dashboard',
      description:
        'Inspect the selected module baseline for this derived admin application.'
    }
  },
  {
    id: 'user-management',
    route: '/users',
    importName: 'UsersPage',
    importPath: '@/features/users/users-page',
    nav: {
      label: 'Users',
      eyebrow: 'Identity',
      icon: 'Users',
      title: 'User Management',
      description:
        'Manage application users through the backend user service.'
    }
  },
  {
    id: 'role-permission-management',
    route: '/roles',
    importName: 'RolePermissionsPage',
    importPath: '@/features/roles/role-permissions-page',
    nav: {
      label: 'Roles & Permissions',
      eyebrow: 'Identity',
      icon: 'ShieldCheck',
      title: 'Role Permission Management',
      description:
        'Manage roles, permissions, and user-role assignments.'
    }
  },
  {
    id: 'project-management',
    route: '/projects',
    importName: 'ProjectsPage',
    importPath: '@/features/projects/projects-page',
    nav: {
      label: 'Projects',
      eyebrow: 'Delivery',
      icon: 'FolderKanban',
      title: 'Project Management',
      description:
        'Manage software projects and project-scope workflows.'
    }
  },
  {
    id: 'ticket-management',
    route: '/tickets',
    importName: 'TicketsPage',
    importPath: '@/features/tickets/tickets-page',
    nav: {
      label: 'Tickets',
      eyebrow: 'Delivery',
      icon: 'Ticket',
      title: 'Ticket Management',
      description:
        'Manage tickets and minimal state progression.'
    }
  },
  {
    id: 'kanban-management',
    route: '/kanban',
    importName: 'KanbanPage',
    importPath: '@/features/kanban/kanban-page',
    nav: {
      label: 'Kanban',
      eyebrow: 'Delivery',
      icon: 'KanbanSquare',
      title: 'Kanban Management',
      description:
        'Manage boards scoped to the selected project baseline.'
    }
  }
]

const BACKEND_TABLES = [
  {
    module: 'user-management',
    sql: `create table if not exists app_user (
  id long auto_increment primary key,
  username varchar,
  display_name varchar,
  email varchar,
  enabled boolean
) package @packageName generate code @modelSrcDir;`
  },
  {
    module: 'role-permission-management',
    sql: `create table if not exists app_role (
  id long auto_increment primary key,
  role_code varchar,
  role_name varchar
) package @packageName generate code @modelSrcDir;

create table if not exists app_permission (
  id long auto_increment primary key,
  permission_code varchar,
  permission_name varchar,
  scope varchar
) package @packageName generate code @modelSrcDir;

create table if not exists app_user_role (
  user_id long,
  role_id long
) package @packageName generate code @modelSrcDir;

create table if not exists app_role_permission (
  role_id long,
  permission_id long
) package @packageName generate code @modelSrcDir;`
  },
  {
    module: 'project-management',
    sql: `create table if not exists software_project (
  id long auto_increment primary key,
  project_key varchar,
  project_name varchar,
  project_description varchar,
  active boolean
) package @packageName generate code @modelSrcDir;`
  },
  {
    module: 'project-repository-management',
    sql: `create table if not exists project_repository_binding (
  project_id long primary key,
  repository_root_path varchar
) package @packageName generate code @modelSrcDir;
`
  },
  {
    module: 'kanban-management',
    sql: `create table if not exists kanban_board (
  id long auto_increment primary key,
  project_id long,
  board_name varchar
) package @packageName generate code @modelSrcDir;`
  },
  {
    module: 'ticket-management',
    sql: `create table if not exists ticket (
  id long auto_increment primary key,
  project_id long,
  kanban_id long,
  ticket_key varchar,
  title varchar,
  description varchar,
  state varchar,
  assignee_user_id long
) package @packageName generate code @modelSrcDir;`
  }
]

const BACKEND_SERVICES = [
  {
    module: 'user-management',
    sql: `create service if not exists user_service (
  createUser(username varchar, displayName varchar, email varchar) long,
  listUsers() varchar
)
package @packageName
implement by 'com.fastservice.platform.backend.user.UserServiceImpl'
generate code @serviceSrcDir;`
  },
  {
    module: 'role-permission-management',
    sql: `create service if not exists access_control_service (
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
generate code @serviceSrcDir;`
  },
  {
    module: 'kanban-management',
    sql: `create service if not exists kanban_service (
  createKanban(projectId long, boardName varchar) long,
  listKanbansByProject(projectId long) varchar
)
package @packageName
implement by 'com.fastservice.platform.backend.kanban.KanbanServiceImpl'
generate code @serviceSrcDir;`
  },
  {
    module: 'ticket-management',
    sql: `create service if not exists ticket_service (
  createTicket(projectId long, kanbanId long, ticketKey varchar, title varchar, description varchar, assigneeUserId long) long,
  moveTicket(ticketId long, targetState varchar) varchar,
  listTicketsByProject(projectId long) varchar
)
package @packageName
implement by 'com.fastservice.platform.backend.ticket.TicketServiceImpl'
generate code @serviceSrcDir;`
  }
]

const DEMO_SQL_BY_MODULE = [
  {
    module: 'user-management',
    sql: `insert into app_user(id, username, display_name, email, enabled)
values(100, 'admin', 'Administrator', 'admin@fastservice.local', true);`
  },
  {
    module: 'role-permission-management',
    sql: `insert into app_role(id, role_code, role_name)
values(200, 'ADMIN', 'Administrator');

insert into app_permission(id, permission_code, permission_name, scope)
values(300, 'dashboard:view', 'View Dashboard', 'MENU');

insert into app_user_role(user_id, role_id)
values(100, 200);

insert into app_role_permission(role_id, permission_id)
values(200, 300);`
  },
  {
    module: 'project-management',
    sql: `insert into app_permission(id, permission_code, permission_name, scope)
values(301, 'project:manage', 'Manage Projects', 'FUNCTION');

insert into app_role_permission(role_id, permission_id)
values(200, 301);`
  },
  {
    module: 'project-repository-management',
    sql: `insert into app_permission(id, permission_code, permission_name, scope)
values(304, 'project:repository:manage', 'Manage Project Repositories', 'FUNCTION');

insert into app_role_permission(role_id, permission_id)
values(200, 304);`
  },
  {
    module: 'ticket-management',
    sql: `insert into app_permission(id, permission_code, permission_name, scope)
values(302, 'ticket:manage', 'Manage Tickets', 'FUNCTION');

insert into app_role_permission(role_id, permission_id)
values(200, 302);`
  },
  {
    module: 'kanban-management',
    sql: `insert into app_permission(id, permission_code, permission_name, scope)
values(303, 'kanban:view', 'View Kanban', 'MENU');

insert into app_role_permission(role_id, permission_id)
values(200, 303);`
  }
]

const FRONTEND_SHARED_PATHS = [
  'frontend/bun.lock',
  'frontend/components.json',
  'frontend/eslint.config.js',
  'frontend/index.html',
  'frontend/package.json',
  'frontend/public',
  'frontend/README.md',
  'frontend/src/App.tsx',
  'frontend/src/app/admin-shell.tsx',
  'frontend/src/components/admin',
  'frontend/src/components/ui',
  'frontend/src/index.css',
  'frontend/src/lib/api',
  'frontend/src/lib/utils.ts',
  'frontend/src/main.tsx',
  'frontend/tsconfig.app.json',
  'frontend/tsconfig.json',
  'frontend/tsconfig.node.json',
  'frontend/vite.config.ts'
]

const FRONTEND_OPTIONAL_FEATURES = {
  'project-management': 'frontend/src/features/projects',
  'ticket-management': 'frontend/src/features/tickets',
  'kanban-management': 'frontend/src/features/kanban'
}

const BACKEND_SHARED_PATHS = [
  'backend/pom.xml',
  'backend/README.md',
  'backend/src/main/java/com/fastservice/platform/backend/BackendApplication.java',
  'backend/src/main/java/com/fastservice/platform/backend/bootstrap',
  'backend/src/main/java/com/fastservice/platform/backend/common',
  'backend/src/main/java/com/fastservice/platform/backend/engineering'
]

const BACKEND_ALWAYS_DOMAIN_PATHS = [
  'backend/src/main/java/com/fastservice/platform/backend/access',
  'backend/src/main/java/com/fastservice/platform/backend/user',
  'backend/src/main/java/com/fastservice/platform/backend/generated/service/executor/AccessControlServiceExecutor.java',
  'backend/src/main/java/com/fastservice/platform/backend/generated/service/executor/UserServiceExecutor.java'
]

const BACKEND_OPTIONAL_PATHS = {
  'project-management': [
    'backend/src/main/java/com/fastservice/platform/backend/project',
    'backend/src/main/java/com/fastservice/platform/backend/generated/service/executor/ProjectServiceExecutor.java'
  ],
  'kanban-management': [
    'backend/src/main/java/com/fastservice/platform/backend/kanban',
    'backend/src/main/java/com/fastservice/platform/backend/generated/service/executor/KanbanServiceExecutor.java'
  ],
  'ticket-management': [
    'backend/src/main/java/com/fastservice/platform/backend/ticket',
    'backend/src/main/java/com/fastservice/platform/backend/generated/service/executor/TicketServiceExecutor.java'
  ]
}

function moduleMap(registry) {
  return new Map(registry.modules.map((module) => [module.id, module]))
}

export async function readJson(filePath) {
  return JSON.parse(await readFile(filePath, 'utf8'))
}

export async function loadModuleRegistry(rootDir = REPO_ROOT) {
  return readJson(path.join(rootDir, 'docs', 'ai', 'module-registry.json'))
}

export async function loadAssemblyContract(rootDir = REPO_ROOT) {
  return readJson(path.join(rootDir, 'docs', 'ai', 'app-assembly-contract.json'))
}

export async function loadDerivedAppLifecycleContract(rootDir = REPO_ROOT) {
  return readJson(path.join(rootDir, 'docs', 'ai', 'derived-app-lifecycle-contract.json'))
}

export async function loadDerivedAppUpgradeExecutionContract(rootDir = REPO_ROOT) {
  return readJson(path.join(rootDir, 'docs', 'ai', 'derived-app-upgrade-execution-contract.json'))
}

export async function loadPlatformReleaseMetadata(rootDir = REPO_ROOT) {
  return readJson(path.join(rootDir, 'docs', 'ai', 'platform-release.json'))
}

export async function loadPlatformReleaseHistory(rootDir = REPO_ROOT) {
  return readJson(path.join(rootDir, 'docs', 'ai', 'platform-release-history.json'))
}

export async function loadPlatformReleaseAdvisory(rootDir = REPO_ROOT) {
  return readJson(path.join(rootDir, 'docs', 'ai', 'platform-release-advisory.json'))
}

export async function loadGeneratedAppVerificationContract(rootDir = REPO_ROOT) {
  return readJson(path.join(rootDir, 'docs', 'ai', 'generated-app-verification-contract.json'))
}

export async function loadCompatibilitySuite(rootDir = REPO_ROOT) {
  return readJson(path.join(rootDir, 'docs', 'ai', 'compatibility', 'app-assembly-suite.json'))
}

function getNormativeAssetPaths(contract) {
  return Object.values(contract.normativeAssets ?? {})
}

function getRequiredGeneratedFiles(contract) {
  return contract.outputInvariants?.requiredFiles ?? []
}

function getGeneratedContractAssetPaths(contract) {
  return getRequiredGeneratedFiles(contract).filter(
    (relativePath) => relativePath.startsWith('docs/ai/') && !DYNAMIC_GENERATED_DOCS.has(relativePath)
  )
}

function getModuleIdsByRole(registry, role) {
  return registry.modules
    .filter((module) => module.role === role)
    .map((module) => module.id)
}

function getFrontendRoutesByModule(registry) {
  return new Map(
    registry.modules
      .filter((module) => Array.isArray(module.frontend?.routes))
      .map((module) => [module.id, module.frontend.routes])
  )
}

function getBackendServicesByModule(registry) {
  return new Map(
    registry.modules
      .filter((module) => Array.isArray(module.backend?.services))
      .map((module) => [module.id, module.backend.services])
  )
}

function getBackendTablesByModule(registry) {
  return new Map(
    registry.modules
      .filter((module) => Array.isArray(module.backend?.tables))
      .map((module) => [module.id, module.backend.tables])
  )
}

function getGeneratedAppCheckIds(verificationContract) {
  return new Set(verificationContract.checks ?? [])
}

function deriveProfileId(registry, selectedModules) {
  const selectedSignature = JSON.stringify(selectedModules)
  for (const [profileId, profile] of Object.entries(registry.profiles ?? {})) {
    if (JSON.stringify(profile.modules ?? []) === selectedSignature) {
      return profileId
    }
  }
  return 'custom'
}

function resolveVerificationInputPath(targetDir, verificationContract, inputKey, issues) {
  const relativePath = verificationContract.normativeInputs?.[inputKey]
  if (typeof relativePath !== 'string' || relativePath.length === 0) {
    issues.push(`Missing verification contract input path: ${inputKey}`)
    return null
  }

  return path.join(targetDir, relativePath)
}

export function validateManifest(manifest, registry, contract = null) {
  assert.equal(manifest.schemaVersion, 'fsp-app-manifest/v1', 'Unsupported manifest schemaVersion')
  assert.ok(manifest.application, 'Manifest must include application')
  assert.match(manifest.application.id, /^[a-z0-9]+(?:-[a-z0-9]+)*$/, 'application.id must be kebab-case')
  assert.ok(manifest.application.name, 'application.name is required')
  assert.match(
    manifest.application.packagePrefix,
    /^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$/,
    'application.packagePrefix must be a dotted Java package prefix'
  )
  assert.ok(Array.isArray(manifest.modules), 'modules must be an array')

  const registryById = moduleMap(registry)
  const selectedModules = [...new Set(manifest.modules)]
  assert.equal(selectedModules.length, manifest.modules.length, 'modules must not contain duplicates')

  for (const moduleId of selectedModules) {
    assert.ok(registryById.has(moduleId), `Unknown module: ${moduleId}`)
  }

  const requiredCore = getModuleIdsByRole(registry, 'required-core')

  for (const requiredModuleId of requiredCore) {
    assert.ok(
      selectedModules.includes(requiredModuleId),
      `Manifest must include required core module: ${requiredModuleId}`
    )
  }

  for (const moduleId of selectedModules) {
    const module = registryById.get(moduleId)
    for (const dependency of module.dependsOn ?? []) {
      assert.ok(
        selectedModules.includes(dependency),
        `Module ${moduleId} requires dependency ${dependency}`
      )
    }
  }

  if (contract !== null) {
    const requiredFields = contract.requiredManifestFields ?? []
    for (const field of requiredFields) {
      if (field === 'schemaVersion') {
        assert.ok(manifest.schemaVersion, 'Manifest must include schemaVersion')
        continue
      }

      let current = manifest
      for (const part of field.split('.')) {
        current = current?.[part]
      }
      assert.ok(current !== undefined && current !== null, `Manifest must include ${field}`)
    }
  }

  return {
    selectedModules,
    registryById
  }
}

function ensureOutsideRepository(outputDir) {
  const relative = path.relative(REPO_ROOT, outputDir)
  const isInsideRepo = relative === '' || (!relative.startsWith('..') && !path.isAbsolute(relative))
  assert.ok(!isInsideRepo, `Output path must be outside repository workspace: ${outputDir}`)
}

async function copyRelativePath(relativePath, outputDir) {
  const sourcePath = path.join(REPO_ROOT, relativePath)
  const targetPath = path.join(outputDir, relativePath)
  await mkdir(path.dirname(targetPath), { recursive: true })
  await cp(sourcePath, targetPath, { recursive: true })
}

async function copyNormativeAssets(contract, outputDir) {
  for (const relativePath of getGeneratedContractAssetPaths(contract)) {
    await copyRelativePath(relativePath, outputDir)
  }
}

function buildTablesSql(selectedModules) {
  const selected = new Set(selectedModules)
  const body = BACKEND_TABLES
    .filter((entry) => selected.has(entry.module))
    .map((entry) => entry.sql)
    .join('\n\n')

  return `set @packageName 'com.fastservice.platform.backend.generated.model';
set @modelSrcDir './target/generated-sources/lealone-model';

${body}
`
}

function buildServicesSql(selectedModules) {
  const selected = new Set(selectedModules)
  const serviceBlocks = [
    ...BACKEND_SERVICES
      .filter((entry) => selected.has(entry.module))
      .map((entry) => entry.sql)
  ]

  if (selected.has('project-management')) {
    serviceBlocks.splice(
      2,
      0,
      buildProjectServiceSql(selected.has('project-repository-management'))
    )
  }

  const body = serviceBlocks.join('\n\n')

  return `set @packageName 'com.fastservice.platform.backend.generated.service';
set @serviceSrcDir './target/generated-sources/lealone-service';

${body}
`
}

function buildProjectServiceSql(includeRepositoryManagement) {
  const repositoryMethods = includeRepositoryManagement
    ? `
  bindProjectRepository(projectId long, repositoryPath varchar) varchar,
  switchProjectBranch(projectId long, branchName varchar) varchar,`
    : ''

  return `create service if not exists project_service (
  createProject(projectKey varchar, projectName varchar, description varchar) long,${repositoryMethods}
  listProjects() varchar
)
package @packageName
implement by 'com.fastservice.platform.backend.project.ProjectServiceImpl'
generate code @serviceSrcDir;`
}

function buildDemoSql(selectedModules) {
  const selected = new Set(selectedModules)
  return `${DEMO_SQL_BY_MODULE
    .filter((entry) => selected.has(entry.module))
    .map((entry) => entry.sql)
    .join('\n\n')}
`
}

function buildDemoDataSupport(selectedModules) {
  const selected = new Set(selectedModules)
  const hasProjects = selected.has('project-management')
  const hasKanban = selected.has('kanban-management')
  const hasTickets = selected.has('ticket-management')

  const imports = [
    "import java.sql.Connection;",
    "import java.sql.PreparedStatement;",
    "import java.sql.ResultSet;",
    "import java.sql.SQLException;",
    "",
    "import com.fastservice.platform.backend.common.db.JdbcSupport;",
    "import com.fastservice.platform.backend.common.sql.SqlScriptExecutor;"
  ]

  if (hasProjects) {
    imports.push("import com.fastservice.platform.backend.project.ProjectServiceImpl;")
  }
  if (hasKanban) {
    imports.push("import com.fastservice.platform.backend.kanban.KanbanServiceImpl;")
  }
  if (hasTickets) {
    imports.push("import com.fastservice.platform.backend.ticket.TicketServiceImpl;")
  }

  const moduleBody = []
  if (hasProjects) {
    moduleBody.push(
      "",
      `        ProjectServiceImpl projectService = new ProjectServiceImpl();
        long projectId = ensureProject(projectService);`
    )
  }
  if (hasKanban) {
    moduleBody.push(
      `        KanbanServiceImpl kanbanService = new KanbanServiceImpl();
        long boardId = ensureBoard(projectId, kanbanService);`
    )
  }
  if (hasTickets) {
    moduleBody.push(
      `        TicketServiceImpl ticketService = new TicketServiceImpl();
        ensureTicket(projectId, boardId, "FSP-1", "Bootstrap backend core",
                "Establish the first backend core", "TODO", ticketService);
        ensureTicket(projectId, boardId, "FSP-2", "Verify demo workflow",
                "Exercise minimal kanban state flow", "IN_PROGRESS", ticketService);`
    )
  }

  const helperMethods = []
  if (hasProjects) {
    helperMethods.push(`
    private static long ensureProject(ProjectServiceImpl projectService) {
        Long existingProjectId = findId("SELECT id FROM software_project WHERE project_key = ?", "FSP");
        if (existingProjectId != null) {
            return existingProjectId;
        }
        return projectService.createProject("FSP", "Fast Service Platform", "Backend core demonstration project");
    }`)
  }
  if (hasKanban) {
    helperMethods.push(`
    private static long ensureBoard(long projectId, KanbanServiceImpl kanbanService) {
        Long existingBoardId = findId(
                "SELECT id FROM kanban_board WHERE project_id = ? AND board_name = ?",
                projectId,
                "Delivery Board");
        if (existingBoardId != null) {
            return existingBoardId;
        }
        return kanbanService.createKanban(projectId, "Delivery Board");
    }`)
  }
  if (hasTickets) {
    helperMethods.push(`
    private static void ensureTicket(long projectId, long boardId, String ticketKey, String title,
            String description, String targetState, TicketServiceImpl ticketService) {
        Long existingTicketId = findId("SELECT id FROM ticket WHERE ticket_key = ?", ticketKey);
        long ticketId = existingTicketId != null
                ? existingTicketId
                : ticketService.createTicket(projectId, boardId, ticketKey, title, description, 100L);

        if (!targetState.equals(findTicketState(ticketId))) {
            ticketService.moveTicket(ticketId, targetState);
        }
    }

    private static String findTicketState(long ticketId) {
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT state FROM ticket WHERE id = ?")) {
            statement.setLong(1, ticketId);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("Ticket not found: " + ticketId);
                }
                return rs.getString(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to read demo ticket state", e);
        }
    }`)
  }

  return `package com.fastservice.platform.backend.demo;

${imports.join('\n')}

public final class DemoDataSupport {

    private DemoDataSupport() {
    }

    public static void load(String databaseName) {
        try (Connection connection = JdbcSupport.getConnection(databaseName)) {
            connection.setAutoCommit(false);
            try {
                if (!hasDemoUser(connection)) {
                    SqlScriptExecutor.executeClasspathResource(connection, "/sql/demo.sql");
                }
                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load demo data", e);
        }${moduleBody.join('\n')}
    }

    private static boolean hasDemoUser(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM app_user WHERE username = ?")) {
            statement.setString(1, "admin");
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                return rs.getLong(1) > 0;
            }
        }
    }
${helperMethods.join('\n')}

    private static Long findId(String sql, Object... parameters) {
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                Object value = parameters[i];
                if (value instanceof Long longValue) {
                    statement.setLong(i + 1, longValue);
                } else {
                    statement.setString(i + 1, String.valueOf(value));
                }
            }
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to look up demo data", e);
        }
    }
}
`
}

function buildModuleSelectionTs(selectedModules) {
  const selected = new Set(selectedModules)
  return `export const moduleSelection = {
  project: ${selected.has('project-management')},
  ticket: ${selected.has('ticket-management')},
  kanban: ${selected.has('kanban-management')},
} as const
`
}

function buildRouterTsx(selectedModules) {
  const selected = new Set(selectedModules)
  const routeModules = ROUTE_MODULES.filter((module) => selected.has(module.id))
  const imports = routeModules
    .map((module) => `import { ${module.importName} } from '${module.importPath}'`)
    .join('\n')
  const routes = routeModules
    .map((module) => `      {
        path: '${module.route.slice(1)}',
        element: <${module.importName} />,
      },`)
    .join('\n')

  return `import { createBrowserRouter, Navigate } from 'react-router-dom'
import type { RouteObject } from 'react-router-dom'

import AdminShell from '@/app/admin-shell'
${imports}

function MissingRoutePage() {
  return <Navigate replace to="/dashboard" />
}

export const adminRoutes: RouteObject[] = [
  {
    path: '/',
    element: <AdminShell />,
    children: [
      {
        index: true,
        element: <Navigate replace to="/dashboard" />,
      },
${routes}
      {
        path: '*',
        element: <MissingRoutePage />,
      },
    ],
  },
]

export const router = createBrowserRouter(adminRoutes)
`
}

function buildNavigationTs(selectedModules) {
  const selected = new Set(selectedModules)
  const routeModules = ROUTE_MODULES.filter((module) => selected.has(module.id))
  const iconImports = [...new Set(routeModules.map((module) => module.nav.icon))].join(',\n  ')
  const items = routeModules
    .map((module) => `  {
    to: '${module.route}',
    label: '${module.nav.label}',
    eyebrow: '${module.nav.eyebrow}',
    icon: ${module.nav.icon},
    meta: {
      title: '${module.nav.title}',
      description:
        '${module.nav.description}',
    },
  },`)
    .join('\n')

  return `import {
  ${iconImports},
} from 'lucide-react'
import type { LucideIcon } from 'lucide-react'

export type AdminRouteMeta = {
  title: string
  description: string
}

export type AdminNavigationItem = {
  to: string
  label: string
  eyebrow: string
  icon: LucideIcon
  meta: AdminRouteMeta
}

export const adminNavigation: AdminNavigationItem[] = [
${items}
]

export function findNavigationItem(pathname: string) {
  return adminNavigation.find((item) => pathname === item.to)
}
`
}

function buildDashboardPage(selectedModules) {
  const selected = new Set(selectedModules)
  const imports = [
    "import { ShieldCheck, Users",
    selected.has('project-management') ? ', FolderKanban' : '',
    selected.has('ticket-management') ? ', Ticket' : '',
    selected.has('kanban-management') ? ', Activity' : '',
    " } from 'lucide-react'",
    "",
    "import { PageHeader } from '@/components/admin/page-header'",
    "import { ResourceState } from '@/components/admin/resource-state'",
    "import { StatCard } from '@/components/admin/stat-card'",
    "import { Badge } from '@/components/ui/badge'",
    "import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'",
    "import {",
    "  useRolePermissionsResource,",
    selected.has('project-management') ? "  useProjectsResource," : '',
    selected.has('kanban-management') ? "  useKanbansResource," : '',
    selected.has('ticket-management') ? "  useTicketsResource," : '',
    "  useUsersResource,",
    "} from '@/lib/api/hooks'"
  ]
    .filter(Boolean)
    .join('\n')

  const selectedModuleBadges = selectedModules
    .filter((moduleId) => moduleId !== 'admin-shell')
    .map(
      (moduleId) =>
        `                <Badge key="${moduleId}" variant="outline" className="rounded-full">
                  ${moduleId}
                </Badge>`
    )
    .join('\n')

  const stats = [
    `        <StatCard
          label="Users"
          value={String(users.data.length)}
          detail="Live users returned by the backend user service."
          icon={<Users className="size-3.5" />}
        />`,
    `        <StatCard
          label="Role permissions"
          value={String(permissions.data.length)}
          detail="Permissions exposed for the default admin role."
          icon={<ShieldCheck className="size-3.5" />}
        />`
  ]
  if (selected.has('project-management')) {
    stats.push(`        <StatCard
          label="Projects"
          value={String(projects.data.length)}
          detail="Projects enabled by the selected module set."
          tone="accent"
          icon={<FolderKanban className="size-3.5" />}
        />`)
  }
  if (selected.has('ticket-management')) {
    stats.push(`        <StatCard
          label="Tickets"
          value={String(tickets.data.length)}
          detail="Tickets returned for the active project."
          icon={<Ticket className="size-3.5" />}
        />`)
  }
  if (selected.has('kanban-management')) {
    stats.push(`        <StatCard
          label="Kanban boards"
          value={String(kanbans.data.length)}
          detail="Boards enabled by the current assembly."
          icon={<Activity className="size-3.5" />}
        />`)
  }

  return `${imports}

export function DashboardPage() {
  const users = useUsersResource()
  const permissions = useRolePermissionsResource(200)
${selected.has('project-management') ? '  const projects = useProjectsResource()\n  const selectedProject = projects.data[0] ?? null' : ''}
${selected.has('kanban-management') ? "  const kanbans = useKanbansResource(selectedProject?.id ?? null)" : ''}
${selected.has('ticket-management') ? "  const tickets = useTicketsResource(selectedProject?.id ?? null)" : ''}

  return (
    <div className="space-y-8">
      <PageHeader
        eyebrow="Overview"
        title="Assembly dashboard"
        description="This dashboard reflects the currently selected module assembly rather than assuming the full default baseline application."
      />

      <div className="grid gap-4 xl:grid-cols-${Math.max(2, stats.length)}">
${stats.join('\n')}
      </div>

      <div className="grid gap-4 xl:grid-cols-[1.05fr_0.95fr]">
        <Card className="bg-card/95">
          <CardHeader>
            <CardTitle className="text-lg">Selected modules</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex flex-wrap gap-2">
${selectedModuleBadges}
            </div>
            <p className="text-sm leading-6 text-muted-foreground">
              This derived app keeps the platform core and wires only the selected business modules into the generated frontend navigation and backend SQL contract.
            </p>
          </CardContent>
        </Card>

        <Card className="bg-card/95">
          <CardHeader>
            <CardTitle className="text-lg">Core backend signal</CardTitle>
          </CardHeader>
          <CardContent>
            <ResourceState
              status={users.status}
              error={users.error}
              empty={users.data.length === 0}
              emptyTitle="No users available"
              emptyMessage="Enable demo data or create users to populate the derived application."
              onRetry={users.reload}
              skeletonCount={2}
            >
              <div className="space-y-4">
                <div className="rounded-[22px] border border-border/60 bg-background/70 p-4">
                  <div className="text-xs font-medium uppercase tracking-[0.18em] text-muted-foreground">
                    Default role view
                  </div>
                  <div className="mt-2 text-xl font-semibold">200 · ADMIN</div>
                </div>

                <div className="rounded-[22px] border border-border/60 bg-muted/35 p-4">
                  <div className="text-xs font-medium uppercase tracking-[0.18em] text-muted-foreground">
                    Permission count
                  </div>
                  <div className="mt-2 text-2xl font-semibold">
                    {permissions.data.length}
                  </div>
                </div>
${selected.has('project-management') ? `
                <div className="rounded-[22px] border border-border/60 bg-muted/35 p-4">
                  <div className="text-xs font-medium uppercase tracking-[0.18em] text-muted-foreground">
                    Active project focus
                  </div>
                  <div className="mt-2 text-xl font-semibold">
                    {selectedProject ? \`\${selectedProject.key} · \${selectedProject.name}\` : 'No active project'}
                  </div>
                </div>` : ''}
              </div>
            </ResourceState>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
`
}

function buildProjectServiceImpl(includeRepositoryManagement) {
  if (includeRepositoryManagement) {
    return readFileSync(
      path.join(
        REPO_ROOT,
        'backend/src/main/java/com/fastservice/platform/backend/project/ProjectServiceImpl.java'
      ),
      'utf8'
    )
  }

  return `package com.fastservice.platform.backend.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fastservice.platform.backend.common.db.JdbcSupport;
import com.fastservice.platform.backend.common.json.JsonStrings;

public class ProjectServiceImpl {

    public long createproject(String projectKey, String projectName, String description) {
        return createProject(projectKey, projectName, description);
    }

    public String listprojects() {
        return listProjects();
    }

    public long createProject(String projectKey, String projectName, String description) {
        String sql = "INSERT INTO software_project(project_key, project_name, project_description, active) VALUES(?, ?, ?, true)";
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, projectKey);
            statement.setString(2, projectName);
            statement.setString(3, description);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            throw new IllegalStateException("No generated key returned for project insert");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to create project", e);
        }
    }

    public String listProjects() {
        String sql = """
                SELECT id, project_key, project_name, active
                FROM software_project
                ORDER BY id
                """;
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {
            StringBuilder builder = new StringBuilder("[");
            boolean first = true;
            while (rs.next()) {
                if (!first) {
                    builder.append(',');
                }
                first = false;
                builder.append("{\\"id\\":").append(rs.getLong("id"));
                builder.append(",\\"key\\":").append(JsonStrings.quote(rs.getString("project_key")));
                builder.append(",\\"name\\":").append(JsonStrings.quote(rs.getString("project_name")));
                builder.append(",\\"active\\":").append(rs.getBoolean("active"));
                builder.append(",\\"repository\\":null}");
            }
            builder.append(']');
            return builder.toString();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to list projects", e);
        }
    }
}
`
}

function buildProjectServiceExecutor(includeRepositoryManagement) {
  if (includeRepositoryManagement) {
    return readFileSync(
      path.join(
        REPO_ROOT,
        'backend/src/main/java/com/fastservice/platform/backend/generated/service/executor/ProjectServiceExecutor.java'
      ),
      'utf8'
    )
  }

  return `package com.fastservice.platform.backend.generated.service.executor;

import java.util.Map;

import com.fastservice.platform.backend.project.ProjectServiceImpl;
import com.lealone.db.service.ServiceExecutor;
import com.lealone.db.value.Value;
import com.lealone.db.value.ValueLong;
import com.lealone.db.value.ValueNull;
import com.lealone.db.value.ValueString;
import com.lealone.orm.json.JsonArray;

public class ProjectServiceExecutor implements ServiceExecutor {

    private final ProjectServiceImpl service = new ProjectServiceImpl();

    @Override
    public Value executeService(String methodName, Value[] methodArgs) {
        return switch (methodName) {
        case "CREATEPROJECT" -> {
            Long result = service.createproject(
                    methodArgs[0].getString(),
                    methodArgs[1].getString(),
                    methodArgs[2].getString());
            yield result == null ? ValueNull.INSTANCE : ValueLong.get(result);
        }
        case "LISTPROJECTS" -> {
            String result = service.listprojects();
            yield result == null ? ValueNull.INSTANCE : ValueString.get(result);
        }
        default -> throw noMethodException(methodName);
        };
    }

    @Override
    public Object executeService(String methodName, Map<String, Object> methodArgs) {
        return switch (methodName) {
        case "CREATEPROJECT" -> service.createproject(
                toString("PROJECTKEY", methodArgs),
                toString("PROJECTNAME", methodArgs),
                toString("DESCRIPTION", methodArgs));
        case "LISTPROJECTS" -> service.listprojects();
        default -> throw noMethodException(methodName);
        };
    }

    @Override
    public Object executeService(String methodName, String json) {
        return switch (methodName) {
        case "CREATEPROJECT" -> {
            JsonArray ja = new JsonArray(json);
            yield service.createproject(ja.getString(0), ja.getString(1), ja.getString(2));
        }
        case "LISTPROJECTS" -> service.listprojects();
        default -> throw noMethodException(methodName);
        };
    }
}
`
}

function buildProjectsPage(includeRepositoryManagement) {
  if (includeRepositoryManagement) {
    return readFileSync(path.join(REPO_ROOT, 'frontend/src/features/projects/projects-page.tsx'), 'utf8')
  }

  return `import { type FormEvent, useState } from 'react'
import { RefreshCcw } from 'lucide-react'

import { MutationStatus } from '@/components/admin/mutation-status'
import { PageHeader } from '@/components/admin/page-header'
import { ResourceState } from '@/components/admin/resource-state'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useCreateProjectAction, useProjectsResource } from '@/lib/api/hooks'

export function ProjectsPage() {
  const projects = useProjectsResource()
  const createProject = useCreateProjectAction()
  const [projectKey, setProjectKey] = useState('')
  const [projectName, setProjectName] = useState('')
  const [description, setDescription] = useState('')

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    try {
      await createProject.submit({
        projectKey,
        projectName,
        description,
      })
      setProjectKey('')
      setProjectName('')
      setDescription('')
      projects.reload()
    } catch {
      return
    }
  }

  return (
    <div className="space-y-8">
      <PageHeader
        eyebrow="Delivery"
        title="Project management"
        description="Projects are displayed from the backend project service and provide the scope anchor for downstream delivery modules."
        actions={
          <Button variant="outline" onClick={projects.reload}>
            <RefreshCcw className="mr-2 size-4" />
            Refresh
          </Button>
        }
      />

      <div className="grid gap-4 xl:grid-cols-[0.82fr_1.18fr]">
        <Card className="bg-card/96">
          <CardHeader>
            <CardTitle className="text-lg">Create project</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <form className="space-y-4" onSubmit={handleSubmit}>
              <div className="space-y-2">
                <Label htmlFor="create-project-key">Project key</Label>
                <Input id="create-project-key" value={projectKey} onChange={(event) => setProjectKey(event.target.value)} placeholder="FSP" required />
              </div>

              <div className="space-y-2">
                <Label htmlFor="create-project-name">Project name</Label>
                <Input id="create-project-name" value={projectName} onChange={(event) => setProjectName(event.target.value)} placeholder="Fast Service Platform" required />
              </div>

              <div className="space-y-2">
                <Label htmlFor="create-project-description">Description</Label>
                <textarea
                  id="create-project-description"
                  className="min-h-28 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none transition-colors focus-visible:border-ring focus-visible:ring-3 focus-visible:ring-ring/50"
                  value={description}
                  onChange={(event) => setDescription(event.target.value)}
                  placeholder="Describe the delivery scope this project anchors."
                  required
                />
              </div>

              <MutationStatus
                status={createProject.status}
                error={createProject.error}
                submittingMessage="Creating project through the backend service..."
                successMessage="Project created and the project list has been refreshed."
              />

              <Button type="submit" disabled={createProject.status === 'submitting'}>
                Create project
              </Button>
            </form>
          </CardContent>
        </Card>

        <ResourceState
          status={projects.status}
          error={projects.error}
          empty={projects.data.length === 0}
          emptyTitle="No projects returned"
          emptyMessage="Add a software project through the backend core or enable demo data to seed the first project."
          onRetry={projects.reload}
        >
          <div className="grid gap-4 xl:grid-cols-2">
            {projects.data.map((project) => (
              <Card key={project.id} className="overflow-hidden border-border/70 bg-[linear-gradient(155deg,rgba(255,255,255,0.96),rgba(246,245,238,0.92))]">
                <CardContent className="space-y-6 p-6">
                  <div className="flex flex-wrap items-start justify-between gap-4">
                    <div>
                      <div className="text-xs font-medium uppercase tracking-[0.2em] text-muted-foreground">
                        Project key
                      </div>
                      <div className="mt-2 text-2xl font-semibold tracking-tight">
                        {project.key}
                      </div>
                      <div className="mt-1 text-base text-muted-foreground">
                        {project.name}
                      </div>
                    </div>
                    <Badge className={project.active ? 'bg-primary/12 text-primary' : 'bg-muted text-muted-foreground'}>
                      {project.active ? 'Active' : 'Inactive'}
                    </Badge>
                  </div>

                  <div className="rounded-[22px] border border-border/60 bg-muted/35 p-4 text-sm leading-6 text-muted-foreground">
                    Project id <span className="font-semibold text-foreground">{project.id}</span> is available as the scope anchor for selected downstream delivery modules.
                  </div>

                  <div className="rounded-[22px] border border-dashed border-border/70 bg-background/55 p-4 text-sm leading-6 text-muted-foreground">
                    Repository binding workflows are not enabled for this derived application assembly.
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </ResourceState>
      </div>
    </div>
  )
}
`
}

function buildRootReadme(manifest, selectedModules) {
  return `# ${manifest.application.name}

This application skeleton was derived from Fast Service Platform.

## Selected Modules

${selectedModules.map((moduleId) => `- \`${moduleId}\``).join('\n')}

## AI Tooling

When an AI agent works with this generated application against the source platform repository:

- Read \`docs/ai/ai-tool-orchestration-contract.json\` from the source platform repository first
- Read \`docs/ai/structured-app-template-contract.json\` and \`docs/ai/template-classifications/default-derived-app-template-map.json\` before customizing generated output
- Prefer \`./scripts/platform-tool.sh\` in the source platform repository before using workflow-specific wrappers
- Stop and report blockers when the repository-owned façade and allowed fallback wrappers are both unavailable

## Validation

Run inside this generated application:

\`\`\`bash
./scripts/verify-derived-app.sh
\`\`\`

Or from the source platform repository:

\`\`\`bash
./scripts/platform-tool.sh generated-app verify /absolute/path/to/${manifest.application.id}
\`\`\`

## Upgrade Evaluation

Evaluate this derived application against the current platform release from the source repository:

\`\`\`bash
./scripts/platform-tool.sh upgrade evaluate /absolute/path/to/${manifest.application.id}
\`\`\`

Read the current platform release advisory from the source repository:

\`\`\`bash
./scripts/platform-tool.sh upgrade advisory /absolute/path/to/${manifest.application.id}
\`\`\`

Inspect the repository-supported upgrade targets for this derived application:

\`\`\`bash
./scripts/platform-tool.sh upgrade targets /absolute/path/to/${manifest.application.id}
\`\`\`

Preview the repository-owned upgrade plan:

\`\`\`bash
./scripts/platform-tool.sh upgrade execute /absolute/path/to/${manifest.application.id}
\`\`\`

Apply the supported repository-owned upgrade actions:

\`\`\`bash
./scripts/platform-tool.sh upgrade execute /absolute/path/to/${manifest.application.id} --apply
\`\`\`
`
}

function buildAgentsFile() {
  return `# AGENTS

## Start Here

- Read \`README.md\`
- Read \`app-manifest.json\`
- Read \`docs/ai/context.json\`
- Read \`docs/ai/derived-app-lifecycle.json\`
- Read \`docs/ai/structured-app-template-contract.json\`
- Read \`docs/ai/template-classifications/default-derived-app-template-map.json\`
- Read \`docs/ai/module-registry.json\`
- Run \`./scripts/verify-derived-app.sh\` before expanding the generated skeleton

## Scope

- This generated app is a derived skeleton from Fast Service Platform
- Keep the selected module set in sync with \`app-manifest.json\`
- Keep lifecycle metadata in sync with the generated module set and source platform release
- Prefer declared customization zones over direct edits to platform-managed slot hosts
- Do not silently widen the dependency boundary
`
}

function buildGeneratedContext(manifest, selectedModules, registry, platformRelease) {
  return JSON.stringify(
    {
      schemaVersion: 'fsp-derived-app-context/v1',
      sourcePlatform: {
        id: platformRelease.platform.id,
        name: platformRelease.platform.name,
        releaseId: platformRelease.currentRelease.releaseId,
        version: platformRelease.currentRelease.version
      },
      application: manifest.application,
      selectedModules,
      requiredCoreModules: registry.modules
        .filter((module) => module.role === 'required-core')
        .map((module) => module.id),
      contractInputs: {
        manifest: 'app-manifest.json',
        aiSolutionInputContract: 'docs/ai/ai-solution-input-contract.json',
        solutionToManifestPlanningContract: 'docs/ai/solution-to-manifest-planning-contract.json',
        solutionToManifestRecommendationContract: 'docs/ai/solution-to-manifest-recommendation-contract.json',
        moduleRegistry: 'docs/ai/module-registry.json',
        aiToolOrchestrationContract: 'docs/ai/ai-tool-orchestration-contract.json',
        structuredAppTemplateContract: STRUCTURED_APP_TEMPLATE_CONTRACT_PATH,
        structuredAppTemplateMap: DERIVED_APP_TEMPLATE_MAP_PATH,
        assemblyContract: 'docs/ai/app-assembly-contract.json',
        derivedAppLifecycleContract: 'docs/ai/derived-app-lifecycle-contract.json',
        derivedAppUpgradeExecutionContract: 'docs/ai/derived-app-upgrade-execution-contract.json',
        derivedAppLifecycleMetadata: DERIVED_APP_LIFECYCLE_METADATA_PATH,
        platformReleaseMetadata: 'docs/ai/platform-release.json',
        platformReleaseHistory: 'docs/ai/platform-release-history.json',
        platformReleaseAdvisory: 'docs/ai/platform-release-advisory.json',
        generatedAppVerificationContract: 'docs/ai/generated-app-verification-contract.json'
      },
      validation: {
        local: './scripts/verify-derived-app.sh',
        repositoryOwned: './scripts/platform-tool.sh generated-app verify <generated-app-dir>',
        referenceVerifier: './scripts/platform-tool.sh generated-app verify <generated-app-dir>'
      },
      templateSystem: {
        contract: STRUCTURED_APP_TEMPLATE_CONTRACT_PATH,
        classificationMap: DERIVED_APP_TEMPLATE_MAP_PATH,
        customizationPlaybook: 'docs/ai/playbooks/customize-derived-app-template-boundaries.md'
      },
      lifecycle: {
        metadata: DERIVED_APP_LIFECYCLE_METADATA_PATH,
        repositoryOwnedUpgradeEvaluation:
          './scripts/platform-tool.sh upgrade evaluate <generated-app-dir>',
        repositoryOwnedUpgradeTargetSelection:
          './scripts/platform-tool.sh upgrade targets [generated-app-dir]',
        repositoryOwnedReleaseAdvisory:
          './scripts/platform-tool.sh upgrade advisory [generated-app-dir]',
        repositoryOwnedUpgradeExecution:
          './scripts/platform-tool.sh upgrade execute <generated-app-dir> [--apply]',
        derivedProfile: deriveProfileId(registry, selectedModules)
      }
    },
    null,
    2
  )
}

function buildDerivedAppLifecycle(manifest, selectedModules, registry, assemblyContract, verificationContract, lifecycleContract, platformRelease) {
  return JSON.stringify(
    {
      schemaVersion: 'fsp-derived-app-lifecycle/v1',
      contractVersion: lifecycleContract.schemaVersion,
      application: manifest.application,
      sourcePlatform: {
        id: platformRelease.platform.id,
        name: platformRelease.platform.name,
        releaseId: platformRelease.currentRelease.releaseId,
        version: platformRelease.currentRelease.version,
        assemblyContractVersion: assemblyContract.schemaVersion,
        generatedAppVerificationContractVersion: verificationContract.schemaVersion
      },
      selectedModules,
      requiredCoreModules: registry.modules
        .filter((module) => module.role === 'required-core')
        .map((module) => module.id),
      derivedProfile: deriveProfileId(registry, selectedModules),
      templateSystem: {
        templateContract: STRUCTURED_APP_TEMPLATE_CONTRACT_PATH,
        templateClassificationMap: DERIVED_APP_TEMPLATE_MAP_PATH
      },
      upgradeEvaluation: {
        repositoryOwnedEntrypoint:
          lifecycleContract.upgradeEvaluation.repositoryOwnedEntrypoint,
        repositoryOwnedTargetSelectionEntrypoint:
          lifecycleContract.upgradeEvaluation.repositoryOwnedTargetSelectionEntrypoint,
        repositoryOwnedAdvisoryEntrypoint:
          lifecycleContract.upgradeEvaluation.repositoryOwnedAdvisoryEntrypoint,
        repositoryOwnedExecutionEntrypoint:
          lifecycleContract.upgradeEvaluation.repositoryOwnedExecutionEntrypoint,
        platformReleaseMetadata: lifecycleContract.normativeAssets.platformReleaseMetadata,
        platformReleaseHistory: lifecycleContract.normativeAssets.platformReleaseHistory,
        platformReleaseAdvisory: lifecycleContract.normativeAssets.platformReleaseAdvisory,
        upgradeExecutionContract: lifecycleContract.normativeAssets.upgradeExecutionContract
      }
    },
    null,
    2
  )
}

function buildLocalVerifyScript() {
  return `#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "\${BASH_SOURCE[0]}")/.." && pwd)"
TARGET_DIR="\${1:-$ROOT_DIR}"

java "$ROOT_DIR/scripts/VerifyDerivedApp.java" "$TARGET_DIR"
`
}

async function writeGeneratedApp(outputDir, manifest, registry, contract, selectedModules) {
  const lifecycleContract = await loadDerivedAppLifecycleContract()
  const platformRelease = await loadPlatformReleaseMetadata()
  const verificationContract = await loadGeneratedAppVerificationContract()
  await writeFile(path.join(outputDir, 'README.md'), buildRootReadme(manifest, selectedModules))
  await writeFile(path.join(outputDir, 'AGENTS.md'), buildAgentsFile())
  await writeFile(path.join(outputDir, 'app-manifest.json'), `${JSON.stringify(manifest, null, 2)}\n`)
  await writeFile(
    path.join(outputDir, DERIVED_APP_LIFECYCLE_METADATA_PATH),
    `${buildDerivedAppLifecycle(
      manifest,
      selectedModules,
      registry,
      contract,
      verificationContract,
      lifecycleContract,
      platformRelease
    )}\n`
  )
  await writeFile(
    path.join(outputDir, 'docs/ai/context.json'),
    `${buildGeneratedContext(manifest, selectedModules, registry, platformRelease)}\n`
  )
  await copyNormativeAssets(contract, outputDir)
  await writeFile(
    path.join(outputDir, 'backend/src/main/resources/sql/tables.sql'),
    buildTablesSql(selectedModules)
  )
  await writeFile(
    path.join(outputDir, 'backend/src/main/resources/sql/services.sql'),
    buildServicesSql(selectedModules)
  )
  await writeFile(
    path.join(outputDir, 'backend/src/main/resources/sql/demo.sql'),
    buildDemoSql(selectedModules)
  )
  await writeFile(
    path.join(outputDir, 'backend/src/main/java/com/fastservice/platform/backend/demo/DemoDataSupport.java'),
    buildDemoDataSupport(selectedModules)
  )
  if (selectedModules.includes('project-management')) {
    const includeRepositoryManagement = selectedModules.includes('project-repository-management')
    await writeFile(
      path.join(outputDir, 'backend/src/main/java/com/fastservice/platform/backend/project/ProjectServiceImpl.java'),
      buildProjectServiceImpl(includeRepositoryManagement)
    )
    await writeFile(
      path.join(outputDir, 'backend/src/main/java/com/fastservice/platform/backend/generated/service/executor/ProjectServiceExecutor.java'),
      buildProjectServiceExecutor(includeRepositoryManagement)
    )
  }
  await writeFile(
    path.join(outputDir, 'frontend/src/app/module-selection.ts'),
    buildModuleSelectionTs(selectedModules)
  )
  await writeFile(path.join(outputDir, 'frontend/src/app/router.tsx'), buildRouterTsx(selectedModules))
  await writeFile(path.join(outputDir, 'frontend/src/app/navigation.ts'), buildNavigationTs(selectedModules))
  await writeFile(
    path.join(outputDir, 'frontend/src/features/dashboard/dashboard-page.tsx'),
    buildDashboardPage(selectedModules)
  )
  if (selectedModules.includes('project-management')) {
    await writeFile(
      path.join(outputDir, 'frontend/src/features/projects/projects-page.tsx'),
      buildProjectsPage(selectedModules.includes('project-repository-management'))
    )
  }
  await copyRelativePath('scripts/VerifyDerivedApp.java', outputDir)
  await writeFile(path.join(outputDir, 'scripts/verify-derived-app.sh'), buildLocalVerifyScript())
  await chmod(path.join(outputDir, 'scripts/verify-derived-app.sh'), 0o755)
}

async function rewriteWorkspaceMetadata(outputDir, manifest) {
  const frontendPackagePath = path.join(outputDir, 'frontend/package.json')
  const frontendPackage = await readJson(frontendPackagePath)
  frontendPackage.name = `${manifest.application.id}-frontend`
  await writeFile(frontendPackagePath, `${JSON.stringify(frontendPackage, null, 2)}\n`)

  const backendPomPath = path.join(outputDir, 'backend/pom.xml')
  const backendPom = await readFile(backendPomPath, 'utf8')
  const updatedPom = backendPom
    .replace('<groupId>com.fastservice.platform</groupId>', `<groupId>${manifest.application.packagePrefix}</groupId>`)
    .replace(
      '<artifactId>fast-service-platform-backend</artifactId>',
      `<artifactId>${manifest.application.id}-backend</artifactId>`
    )
    .replace(
      '<name>fast-service-platform-backend</name>',
      `<name>${manifest.application.id}-backend</name>`
    )
    .replace(
      '<description>Enterprise component core backend for Fast Service Platform</description>',
      `<description>Derived backend skeleton for ${manifest.application.name}</description>`
    )
  await writeFile(backendPomPath, updatedPom)
}

export async function scaffoldDerivedApp({ manifestPath, outputDir }) {
  const resolvedManifestPath = path.resolve(manifestPath)
  const resolvedOutputDir = path.resolve(outputDir)
  const registry = await loadModuleRegistry()
  const contract = await loadAssemblyContract()
  const manifest = await readJson(resolvedManifestPath)
  const { selectedModules } = validateManifest(manifest, registry, contract)

  ensureOutsideRepository(resolvedOutputDir)
  await rm(resolvedOutputDir, { recursive: true, force: true })
  await mkdir(resolvedOutputDir, { recursive: true })

  for (const relativePath of FRONTEND_SHARED_PATHS) {
    await copyRelativePath(relativePath, resolvedOutputDir)
  }
  for (const relativePath of BACKEND_SHARED_PATHS) {
    await copyRelativePath(relativePath, resolvedOutputDir)
  }
  for (const relativePath of BACKEND_ALWAYS_DOMAIN_PATHS) {
    await copyRelativePath(relativePath, resolvedOutputDir)
  }
  await copyRelativePath('frontend/src/features/users', resolvedOutputDir)
  await copyRelativePath('frontend/src/features/roles', resolvedOutputDir)

  for (const [moduleId, relativePath] of Object.entries(FRONTEND_OPTIONAL_FEATURES)) {
    if (selectedModules.includes(moduleId)) {
      await copyRelativePath(relativePath, resolvedOutputDir)
    }
  }

  for (const [moduleId, relativePaths] of Object.entries(BACKEND_OPTIONAL_PATHS)) {
    if (!selectedModules.includes(moduleId)) {
      continue
    }
    for (const relativePath of relativePaths) {
      await copyRelativePath(relativePath, resolvedOutputDir)
    }
  }

  await mkdir(path.join(resolvedOutputDir, 'docs/ai'), { recursive: true })
  await mkdir(path.join(resolvedOutputDir, 'scripts'), { recursive: true })
  await mkdir(path.join(resolvedOutputDir, 'backend/src/main/resources/sql'), { recursive: true })
  await mkdir(
    path.join(resolvedOutputDir, 'backend/src/main/java/com/fastservice/platform/backend/demo'),
    { recursive: true }
  )
  await mkdir(path.join(resolvedOutputDir, 'frontend/src/app'), { recursive: true })
  await mkdir(path.join(resolvedOutputDir, 'frontend/src/features/dashboard'), { recursive: true })

  await writeGeneratedApp(resolvedOutputDir, manifest, registry, contract, selectedModules)
  await rewriteWorkspaceMetadata(resolvedOutputDir, manifest)

  return {
    outputDir: resolvedOutputDir,
    selectedModules
  }
}

export async function verifyDerivedApp(targetDir) {
  const resolvedTargetDir = path.resolve(targetDir)
  const issues = []
  let verificationContract

  try {
    verificationContract = await readJson(
      path.join(resolvedTargetDir, 'docs/ai/generated-app-verification-contract.json')
    )
  } catch {
    return {
      ok: false,
      issues: ['Missing required file: docs/ai/generated-app-verification-contract.json'],
      selectedModules: [],
      contractVersion: 'unavailable',
      verifierId: 'java-generated-app-verifier'
    }
  }
  const checkIds = getGeneratedAppCheckIds(verificationContract)
  const verifierId =
    verificationContract.referenceVerifiers?.[0]?.id ?? 'java-generated-app-verifier'

  const contractPath = resolveVerificationInputPath(
    resolvedTargetDir,
    verificationContract,
    'assemblyContract',
    issues
  )
  const manifestPath = resolveVerificationInputPath(
    resolvedTargetDir,
    verificationContract,
    'applicationManifest',
    issues
  )
  const registryPath = resolveVerificationInputPath(
    resolvedTargetDir,
    verificationContract,
    'moduleRegistry',
    issues
  )
  const contextPath = resolveVerificationInputPath(
    resolvedTargetDir,
    verificationContract,
    'generatedContext',
    issues
  )
  const lifecyclePath = resolveVerificationInputPath(
    resolvedTargetDir,
    verificationContract,
    'derivedAppLifecycleMetadata',
    issues
  )
  const templateContractPath = resolveVerificationInputPath(
    resolvedTargetDir,
    verificationContract,
    'structuredAppTemplateContract',
    issues
  )
  const templateMapPath = resolveVerificationInputPath(
    resolvedTargetDir,
    verificationContract,
    'structuredAppTemplateMap',
    issues
  )
  const routesPath = resolveVerificationInputPath(
    resolvedTargetDir,
    verificationContract,
    'frontendRoutes',
    issues
  )
  const navigationPath = resolveVerificationInputPath(
    resolvedTargetDir,
    verificationContract,
    'frontendNavigation',
    issues
  )
  const servicesPath = resolveVerificationInputPath(
    resolvedTargetDir,
    verificationContract,
    'backendServices',
    issues
  )
  const tablesPath = resolveVerificationInputPath(
    resolvedTargetDir,
    verificationContract,
    'backendTables',
    issues
  )

  if (checkIds.has('verification-inputs-present')) {
    for (const [inputKey, relativePath] of Object.entries(verificationContract.normativeInputs ?? {})) {
      try {
        await readFile(path.join(resolvedTargetDir, relativePath), 'utf8')
      } catch {
        issues.push(`Missing verification input file: ${inputKey}`)
      }
    }
  }

  if (
    issues.length > 0 ||
    !contractPath ||
    !manifestPath ||
    !registryPath ||
    !contextPath ||
    !lifecyclePath ||
    !templateContractPath ||
    !templateMapPath ||
    !routesPath ||
    !navigationPath ||
    !servicesPath ||
    !tablesPath
  ) {
    return {
      ok: false,
      issues,
      selectedModules: [],
      contractVersion: verificationContract.schemaVersion,
      verifierId
    }
  }

  const contract = await readJson(contractPath)

  if (checkIds.has('required-files-present')) {
    for (const relativePath of getRequiredGeneratedFiles(contract)) {
      try {
        await readFile(path.join(resolvedTargetDir, relativePath), 'utf8')
      } catch {
        issues.push(`Missing required file: ${relativePath}`)
      }
    }
  }

  const manifest = await readJson(manifestPath)
  const registry = await readJson(registryPath)
  const { selectedModules } = validateManifest(manifest, registry, contract)
  const selected = new Set(selectedModules)

  const servicesSql = await readFile(servicesPath, 'utf8')
  const tablesSql = await readFile(tablesPath, 'utf8')
  const routerTsx = await readFile(routesPath, 'utf8')
  const navigationTs = await readFile(navigationPath, 'utf8')
  const context = await readJson(contextPath)
  const lifecycle = await readJson(lifecyclePath)

  if (checkIds.has('module-selected-routes-match-registry')) {
    for (const [moduleId, routes] of getFrontendRoutesByModule(registry)) {
      const shouldExist = selected.has(moduleId)
      for (const route of routes) {
        if (shouldExist && !routerTsx.includes(route.slice(1))) {
          issues.push(`Missing route wiring for selected module: ${moduleId}`)
        }
        if (!shouldExist && routerTsx.includes(route)) {
          issues.push(`Unexpected route wiring for unselected module: ${moduleId}`)
        }
        if (shouldExist && !navigationTs.includes(route)) {
          issues.push(`Missing navigation entry for selected module: ${moduleId}`)
        }
        if (!shouldExist && navigationTs.includes(route)) {
          issues.push(`Unexpected navigation entry for unselected module: ${moduleId}`)
        }
      }
    }
  }

  if (checkIds.has('module-selected-backend-services-match-registry')) {
    for (const [moduleId, services] of getBackendServicesByModule(registry)) {
      for (const serviceName of services) {
        const marker = `create service if not exists ${serviceName}`
        if (selected.has(moduleId) && !servicesSql.includes(marker)) {
          issues.push(`Missing backend service contract for selected module: ${moduleId}`)
        }
        if (!selected.has(moduleId) && servicesSql.includes(marker)) {
          issues.push(`Unexpected backend service contract for unselected module: ${moduleId}`)
        }
      }
    }
  }

  if (checkIds.has('module-selected-backend-tables-match-registry')) {
    for (const [moduleId, tables] of getBackendTablesByModule(registry)) {
      for (const tableName of tables) {
        const marker = `create table if not exists ${tableName}`
        if (selected.has(moduleId) && !tablesSql.includes(marker)) {
          issues.push(`Missing backend table contract for selected module: ${moduleId}`)
        }
        if (!selected.has(moduleId) && tablesSql.includes(marker)) {
          issues.push(`Unexpected backend table contract for unselected module: ${moduleId}`)
        }
      }
    }
  }

  if (
    checkIds.has('generated-context-selected-modules-match-manifest') &&
    JSON.stringify(context.selectedModules) !== JSON.stringify(selectedModules)
  ) {
    issues.push('docs/ai/context.json does not match app-manifest.json selectedModules')
  }

  if (checkIds.has('template-boundary-assets-present')) {
    if (context.contractInputs?.structuredAppTemplateContract !== STRUCTURED_APP_TEMPLATE_CONTRACT_PATH) {
      issues.push('docs/ai/context.json does not expose the structured app template contract')
    }
    if (context.contractInputs?.structuredAppTemplateMap !== DERIVED_APP_TEMPLATE_MAP_PATH) {
      issues.push('docs/ai/context.json does not expose the structured app template map')
    }
    if (context.templateSystem?.contract !== STRUCTURED_APP_TEMPLATE_CONTRACT_PATH) {
      issues.push('docs/ai/context.json templateSystem.contract is missing or incorrect')
    }
    if (context.templateSystem?.classificationMap !== DERIVED_APP_TEMPLATE_MAP_PATH) {
      issues.push('docs/ai/context.json templateSystem.classificationMap is missing or incorrect')
    }
    if (lifecycle.templateSystem?.templateContract !== STRUCTURED_APP_TEMPLATE_CONTRACT_PATH) {
      issues.push('docs/ai/derived-app-lifecycle.json templateSystem.templateContract is missing or incorrect')
    }
    if (lifecycle.templateSystem?.templateClassificationMap !== DERIVED_APP_TEMPLATE_MAP_PATH) {
      issues.push('docs/ai/derived-app-lifecycle.json templateSystem.templateClassificationMap is missing or incorrect')
    }
  }

  return {
    ok: issues.length === 0,
    issues,
    selectedModules,
    contractVersion: verificationContract.schemaVersion,
    verifierId
  }
}

function resolveNestedField(root, fieldPath) {
  let current = root
  for (const part of fieldPath.split('.')) {
    current = current?.[part]
  }
  return current
}

function buildReleaseIndex(releaseHistory) {
  return new Map((releaseHistory.releases ?? []).map((release) => [release.releaseId, release]))
}

function findSupportedUpgradePath(releaseHistory, sourceReleaseId, targetReleaseId) {
  return (releaseHistory.supportedUpgradePaths ?? []).find(
    (upgradePath) =>
      upgradePath.sourceReleaseId === sourceReleaseId &&
      upgradePath.targetReleaseId === targetReleaseId &&
      upgradePath.supportStatus === 'supported'
  )
}

export async function listPlatformUpgradeTargets(targetDir = null, rootDir = REPO_ROOT) {
  const resolvedRootDir = path.resolve(rootDir)
  const releaseHistory = await loadPlatformReleaseHistory(resolvedRootDir)
  const releaseIndex = buildReleaseIndex(releaseHistory)

  let lifecycleMetadata = null
  let sourceReleaseId = null
  let selectedModules = null

  if (targetDir) {
    lifecycleMetadata = await readJson(
      path.join(path.resolve(targetDir), DERIVED_APP_LIFECYCLE_METADATA_PATH)
    )
    sourceReleaseId = lifecycleMetadata.sourcePlatform?.releaseId ?? null
    selectedModules = lifecycleMetadata.selectedModules ?? []
  }

  const relevantPaths =
    sourceReleaseId === null
      ? releaseHistory.supportedUpgradePaths ?? []
      : (releaseHistory.supportedUpgradePaths ?? []).filter(
          (upgradePath) => upgradePath.sourceReleaseId === sourceReleaseId
        )

  const availableTargetReleases =
    sourceReleaseId === null
      ? []
      : relevantPaths
          .filter((upgradePath) => upgradePath.supportStatus === 'supported')
          .map((upgradePath) => {
            const release = releaseIndex.get(upgradePath.targetReleaseId) ?? {
              releaseId: upgradePath.targetReleaseId,
              version: 'unavailable',
              supportStatus: 'unknown',
              lineageParentReleaseId: null,
              advisoryAsset: upgradePath.advisoryAsset ?? 'unavailable'
            }
            return {
              releaseId: release.releaseId,
              version: release.version,
              supportStatus: release.supportStatus,
              lineageParentReleaseId: release.lineageParentReleaseId ?? null,
              advisoryAsset: release.advisoryAsset
            }
          })

  return {
    platformId: releaseHistory.platform?.id ?? 'unavailable',
    currentReleaseId: releaseHistory.currentReleaseId ?? 'unavailable',
    sourceReleaseId,
    selectedModules,
    recognizedReleases: releaseHistory.releases ?? [],
    supportedUpgradePaths: relevantPaths,
    availableTargetReleases,
    defaultTargetReleaseId: releaseHistory.compatibilityWindow?.defaultTargetReleaseId ?? null,
    lookupEntrypoint:
      releaseHistory.compatibilityWindow?.upgradeTargetSelectionEntrypoint ?? null
  }
}

export async function evaluateDerivedAppUpgrade(targetDir, rootDir = REPO_ROOT) {
  const resolvedTargetDir = path.resolve(targetDir)
  const resolvedRootDir = path.resolve(rootDir)
  const issues = []
  const lifecycleContract = await loadDerivedAppLifecycleContract(resolvedRootDir)
  const platformRelease = await loadPlatformReleaseMetadata(resolvedRootDir)
  const releaseHistory = await loadPlatformReleaseHistory(resolvedRootDir)
  const currentRegistry = await loadModuleRegistry(resolvedRootDir)

  let lifecycleMetadata
  let manifest
  let context

  for (const relativePath of lifecycleContract.upgradeEvaluation.requiredGeneratedInputs ?? []) {
    try {
      await readFile(path.join(resolvedTargetDir, relativePath), 'utf8')
    } catch {
      issues.push(`Missing upgrade evaluation input: ${relativePath}`)
    }
  }

  if (issues.length > 0) {
    return {
      compatible: false,
      issues,
      sourcePlatformRelease: 'unavailable',
      targetPlatformRelease: platformRelease.currentRelease.releaseId,
      recommendedAction: platformRelease.upgradeSupport.recommendedIncompatibleAction
    }
  }

  lifecycleMetadata = await readJson(
    path.join(
      resolvedTargetDir,
      lifecycleContract.generatedOutput.lifecycleMetadata
    )
  )
  manifest = await readJson(
    path.join(
      resolvedTargetDir,
      lifecycleContract.generatedOutput.applicationManifest
    )
  )
  context = await readJson(
    path.join(
      resolvedTargetDir,
      lifecycleContract.generatedOutput.generatedContext
    )
  )

  for (const fieldPath of lifecycleContract.requiredLifecycleFields ?? []) {
    if (resolveNestedField(lifecycleMetadata, fieldPath) === undefined) {
      issues.push(`Missing lifecycle metadata field: ${fieldPath}`)
    }
  }

  const upgradeSupport = platformRelease.upgradeSupport ?? {}
  const sourcePlatform = lifecycleMetadata.sourcePlatform ?? {}
  const targetRelease =
    upgradeSupport.defaultTargetReleaseId ??
    releaseHistory.compatibilityWindow?.defaultTargetReleaseId ??
    platformRelease.currentRelease?.releaseId ??
    'unavailable'
  const sourceRelease = sourcePlatform.releaseId ?? 'unavailable'
  const knownReleaseIds = buildReleaseIndex(releaseHistory)

  if (
    !(upgradeSupport.supportedSourcePlatformIds ?? []).includes(sourcePlatform.id)
  ) {
    issues.push(`Unsupported source platform id: ${sourcePlatform.id ?? 'unavailable'}`)
  }

  if (!knownReleaseIds.has(sourceRelease)) {
    issues.push(`Unknown source platform release: ${sourceRelease}`)
  }

  if (
    !(releaseHistory.compatibilityWindow?.supportedSourceReleaseIds ?? []).includes(sourceRelease)
  ) {
    issues.push(`Source platform release is outside the supported upgrade window: ${sourceRelease}`)
  }

  const supportedUpgradePath = findSupportedUpgradePath(releaseHistory, sourceRelease, targetRelease)
  if (sourceRelease !== targetRelease && !supportedUpgradePath) {
    issues.push(`Unsupported upgrade path: ${sourceRelease} -> ${targetRelease}`)
  }

  if (
    !(upgradeSupport.supportedLifecycleMetadataVersions ?? []).includes(lifecycleMetadata.schemaVersion)
  ) {
    issues.push(`Unsupported lifecycle metadata version: ${lifecycleMetadata.schemaVersion ?? 'unavailable'}`)
  }

  if (
    !(upgradeSupport.supportedLifecycleContractVersions ?? []).includes(lifecycleMetadata.contractVersion)
  ) {
    issues.push(`Unsupported lifecycle contract version: ${lifecycleMetadata.contractVersion ?? 'unavailable'}`)
  }

  if (
    !(upgradeSupport.supportedAssemblyContractVersions ?? []).includes(
      sourcePlatform.assemblyContractVersion
    )
  ) {
    issues.push(
      `Unsupported assembly contract version: ${sourcePlatform.assemblyContractVersion ?? 'unavailable'}`
    )
  }

  if (JSON.stringify(lifecycleMetadata.selectedModules ?? []) !== JSON.stringify(manifest.modules ?? [])) {
    issues.push('Lifecycle metadata selectedModules do not match app-manifest.json')
  }

  if (JSON.stringify(context.selectedModules ?? []) !== JSON.stringify(manifest.modules ?? [])) {
    issues.push('docs/ai/context.json selectedModules do not match app-manifest.json')
  }

  const knownModules = new Set(currentRegistry.modules?.map((module) => module.id) ?? [])
  for (const moduleId of lifecycleMetadata.selectedModules ?? []) {
    if (!knownModules.has(moduleId)) {
      issues.push(`Selected module is unknown to target platform release: ${moduleId}`)
    }
  }

  const compatible = issues.length === 0
  const recommendedAction =
    sourceRelease === targetRelease
      ? 'already-on-target-platform-release'
      : compatible
        ? supportedUpgradePath?.recommendedAction ?? upgradeSupport.recommendedCompatibleAction
        : upgradeSupport.recommendedIncompatibleAction

  return {
    compatible,
    issues,
    sourcePlatformRelease: sourceRelease,
    targetPlatformRelease: targetRelease,
    recommendedAction
  }
}

export async function readPlatformReleaseAdvisory(targetDir = null, rootDir = REPO_ROOT) {
  const resolvedRootDir = path.resolve(rootDir)
  const advisory = await loadPlatformReleaseAdvisory(resolvedRootDir)
  let selectedModules = null

  if (targetDir) {
    const lifecycle = await readJson(path.join(path.resolve(targetDir), DERIVED_APP_LIFECYCLE_METADATA_PATH))
    selectedModules = lifecycle.selectedModules ?? []
  }

  const changes = advisory.changes ?? []
  const relevantChanges =
    selectedModules === null
      ? changes
      : changes.filter((change) =>
          (change.impactedModules ?? []).some((moduleId) => selectedModules.includes(moduleId))
        )

  const recommendedChecks = [
    ...new Set(relevantChanges.flatMap((change) => change.recommendedChecks ?? []))
  ]

  return {
    releaseId: advisory.currentRelease?.releaseId ?? 'unavailable',
    previousReleaseId: advisory.previousRelease?.releaseId ?? 'unavailable',
    overallCompatibilityPosture: advisory.overallCompatibilityPosture ?? 'unknown',
    selectedModules,
    summary: advisory.summary ?? '',
    changes,
    relevantChanges,
    recommendedChecks,
    recommendedNextActions: advisory.recommendedNextActions ?? []
  }
}

function getManagedUpgradeAssetPaths(assemblyContract) {
  return getRequiredGeneratedFiles(assemblyContract).filter(
    (relativePath) =>
      relativePath.startsWith('docs/ai/') ||
      relativePath === 'scripts/VerifyDerivedApp.java' ||
      relativePath === 'scripts/platform-tool.sh' ||
      relativePath === 'scripts/verify-derived-app.sh'
  )
}

async function buildDerivedAppManagedAssetMap(targetDir, rootDir = REPO_ROOT) {
  const resolvedRootDir = path.resolve(rootDir)
  const resolvedTargetDir = path.resolve(targetDir)
  const assemblyContract = await loadAssemblyContract(resolvedRootDir)
  const lifecycleContract = await loadDerivedAppLifecycleContract(resolvedRootDir)
  const executionContract = await loadDerivedAppUpgradeExecutionContract(resolvedRootDir)
  const platformRelease = await loadPlatformReleaseMetadata(resolvedRootDir)
  const verificationContract = await loadGeneratedAppVerificationContract(resolvedRootDir)
  const registry = await loadModuleRegistry(resolvedRootDir)
  const manifest = await readJson(path.join(resolvedTargetDir, 'app-manifest.json'))
  const { selectedModules } = validateManifest(manifest, registry, assemblyContract)

  const managedAssets = new Map()
  for (const relativePath of getManagedUpgradeAssetPaths(assemblyContract)) {
    if (relativePath === 'docs/ai/context.json') {
      managedAssets.set(
        relativePath,
        `${buildGeneratedContext(manifest, selectedModules, registry, platformRelease)}\n`
      )
      continue
    }

    if (relativePath === DERIVED_APP_LIFECYCLE_METADATA_PATH) {
      managedAssets.set(
        relativePath,
        `${buildDerivedAppLifecycle(
          manifest,
          selectedModules,
          registry,
          assemblyContract,
          verificationContract,
          lifecycleContract,
          platformRelease
        )}\n`
      )
      continue
    }

    if (relativePath === 'scripts/verify-derived-app.sh') {
      managedAssets.set(relativePath, buildLocalVerifyScript())
      continue
    }

    if (relativePath === 'scripts/platform-tool.sh') {
      managedAssets.set(relativePath, await readFile(path.join(resolvedRootDir, relativePath), 'utf8'))
      continue
    }

    managedAssets.set(relativePath, await readFile(path.join(resolvedRootDir, relativePath), 'utf8'))
  }

  managedAssets.set(
    'docs/ai/derived-app-upgrade-execution-contract.json',
    await readFile(
      path.join(resolvedRootDir, 'docs/ai/derived-app-upgrade-execution-contract.json'),
      'utf8'
    )
  )
  managedAssets.set(
    'docs/ai/schemas/derived-app-upgrade-execution-contract.schema.json',
    await readFile(
      path.join(
        resolvedRootDir,
        'docs/ai/schemas/derived-app-upgrade-execution-contract.schema.json'
      ),
      'utf8'
    )
  )

  return {
    managedAssets,
    selectedModules,
    executionContract
  }
}

async function loadDerivedAppTemplateMap(rootDir = REPO_ROOT) {
  return readJson(path.join(path.resolve(rootDir), DERIVED_APP_TEMPLATE_MAP_PATH))
}

function matchTemplateEntry(templateMap, relativePath) {
  return (templateMap.entries ?? []).find((entry) => {
    if (entry.matchKind === 'exact') {
      return entry.path === relativePath
    }
    if (entry.matchKind === 'prefix') {
      return relativePath.startsWith(entry.path)
    }
    return false
  })
}

function buildManualInterventionItems(advisory, evaluation) {
  const items = []

  if (!evaluation.compatible) {
    items.push({
      id: 'compatibility-blocker',
      reason: 'upgrade-evaluation-blocked',
      issues: evaluation.issues
    })
  }

  for (const change of advisory.relevantChanges ?? []) {
    if ((change.compatibility ?? 'unknown') === 'additive') {
      continue
    }
    items.push({
      id: change.id,
      reason: change.summary,
      compatibility: change.compatibility,
      impactedModules: change.impactedModules ?? [],
      recommendedChecks: change.recommendedChecks ?? []
    })
  }

  return items
}

export async function planDerivedAppUpgrade(targetDir, rootDir = REPO_ROOT) {
  const resolvedTargetDir = path.resolve(targetDir)
  const evaluation = await evaluateDerivedAppUpgrade(resolvedTargetDir, rootDir)
  const advisory = await readPlatformReleaseAdvisory(resolvedTargetDir, rootDir)
  const { managedAssets, selectedModules, executionContract } =
    await buildDerivedAppManagedAssetMap(resolvedTargetDir, rootDir)
  const templateMap = await loadDerivedAppTemplateMap(rootDir)
  const autoApplyItems = []

  for (const [relativePath, desiredContents] of managedAssets) {
    let currentContents = null
    try {
      currentContents = await readFile(path.join(resolvedTargetDir, relativePath), 'utf8')
    } catch {
      currentContents = null
    }

    if (currentContents === desiredContents) {
      continue
    }

    let category = 'managed-doc-asset'
    if (relativePath.startsWith('docs/ai/schemas/')) {
      category = 'managed-schema-asset'
    } else if (
      relativePath === STRUCTURED_APP_TEMPLATE_CONTRACT_PATH ||
      relativePath.startsWith('docs/ai/template-classifications/')
    ) {
      category = 'managed-template-metadata'
    } else if (relativePath.startsWith('scripts/')) {
      category = 'managed-verifier-script'
    } else if (relativePath === 'docs/ai/context.json') {
      category = 'generated-context-refresh'
    } else if (relativePath === DERIVED_APP_LIFECYCLE_METADATA_PATH) {
      category = 'generated-lifecycle-refresh'
    }

    const templateEntry = matchTemplateEntry(templateMap, relativePath)
    const item = {
      path: relativePath,
      action: currentContents === null ? 'create' : 'update',
      category
    }
    if (templateEntry) {
      item.templateUnitType = templateEntry.unitType
      item.ownership = templateEntry.ownership
      if (templateEntry.slotId) {
        item.slotId = templateEntry.slotId
      }
      if (templateEntry.moduleId) {
        item.moduleId = templateEntry.moduleId
      }
    }
    autoApplyItems.push(item)
  }

  return {
    planVersion: 'fsp-derived-app-upgrade-plan/v1',
    contractVersion: executionContract.schemaVersion,
    dryRun: true,
    compatible: evaluation.compatible,
    sourcePlatformRelease: evaluation.sourcePlatformRelease,
    targetPlatformRelease: evaluation.targetPlatformRelease,
    selectedModules,
    autoApplyItems,
    manualInterventionItems: buildManualInterventionItems(advisory, evaluation),
    postUpgradeValidation: executionContract.postUpgradeValidation,
    recommendedNextActions: advisory.recommendedNextActions ?? []
  }
}

export async function executeDerivedAppUpgrade(targetDir, { apply = false } = {}, rootDir = REPO_ROOT) {
  const resolvedTargetDir = path.resolve(targetDir)
  const plan = await planDerivedAppUpgrade(resolvedTargetDir, rootDir)

  if (!apply || !plan.compatible) {
    return {
      ...plan,
      dryRun: !apply,
      applied: false,
      appliedItems: []
    }
  }

  const { managedAssets } = await buildDerivedAppManagedAssetMap(resolvedTargetDir, rootDir)
  const appliedItems = []

  for (const item of plan.autoApplyItems) {
    const targetPath = path.join(resolvedTargetDir, item.path)
    await mkdir(path.dirname(targetPath), { recursive: true })
    await writeFile(targetPath, managedAssets.get(item.path))
    if (item.path === 'scripts/verify-derived-app.sh') {
      await chmod(targetPath, 0o755)
    }
    appliedItems.push(item)
  }

  return {
    ...plan,
    dryRun: false,
    applied: true,
    appliedItems
  }
}

export async function runCompatibilitySuite(rootDir = REPO_ROOT) {
  const resolvedRootDir = path.resolve(rootDir)
  const issues = []
  const registry = await loadModuleRegistry(resolvedRootDir)
  const contract = await loadAssemblyContract(resolvedRootDir)
  const suite = await loadCompatibilitySuite(resolvedRootDir)
  const implementations = contract.referenceImplementations ?? []

  for (const relativePath of getNormativeAssetPaths(contract)) {
    try {
      await readFile(path.join(resolvedRootDir, relativePath), 'utf8')
    } catch {
      issues.push(`Missing normative asset referenced by contract: ${relativePath}`)
    }
  }

  const tempRoot = await mkdtemp(path.join(os.tmpdir(), 'fsp-assembly-compatibility-'))
  const implementationResults = []

  try {
    for (const implementation of implementations) {
      const validFixtures = []
      const invalidFixtures = []
      const javaRuntime = implementation.language === 'java-cli'
        ? prepareJavaCliRuntime(resolvedRootDir, implementation)
        : null

      for (const fixture of suite.fixtures.invalid) {
        const manifest = await readJson(path.join(resolvedRootDir, fixture.manifestPath))

        if (implementation.id === 'node-scaffolder') {
          try {
            validateManifest(manifest, registry, contract)
            issues.push(`Invalid fixture unexpectedly passed for ${implementation.id}: ${fixture.id}`)
          } catch (error) {
            const message = error instanceof Error ? error.message : String(error)
            if (!message.includes(fixture.expectedErrorIncludes)) {
              issues.push(
                `Invalid fixture ${fixture.id} failed with unexpected error for ${implementation.id}: ${message}`
              )
            }
          }
        } else if (implementation.id === 'java-cli') {
          const outputDir = path.join(tempRoot, implementation.id, `invalid-${fixture.id}`)
          const result = runJavaCli(resolvedRootDir, javaRuntime, fixture.manifestPath, outputDir)
          if (result.status === 0) {
            issues.push(`Invalid fixture unexpectedly passed for ${implementation.id}: ${fixture.id}`)
          } else if (!result.output.includes(fixture.expectedErrorIncludes)) {
            issues.push(
              `Invalid fixture ${fixture.id} failed with unexpected error for ${implementation.id}: ${result.output}`
            )
          }
        } else {
          issues.push(`Unsupported implementation in compatibility suite: ${implementation.id}`)
        }
        invalidFixtures.push(fixture.id)
      }

      for (const fixture of suite.fixtures.valid) {
        const manifestPath = path.join(resolvedRootDir, fixture.manifestPath)
        const outputDir = path.join(tempRoot, implementation.id, fixture.id)

        let selectedModules = []
        if (implementation.id === 'node-scaffolder') {
          const result = await scaffoldDerivedApp({
            manifestPath,
            outputDir
          })
          selectedModules = result.selectedModules
        } else if (implementation.id === 'java-cli') {
          const result = runJavaCli(resolvedRootDir, javaRuntime, fixture.manifestPath, outputDir)
          if (result.status !== 0) {
            issues.push(`Valid fixture failed for ${implementation.id}: ${fixture.id}\n${result.output}`)
            validFixtures.push(fixture.id)
            continue
          }
          const manifest = await readJson(path.join(outputDir, 'app-manifest.json'))
          selectedModules = manifest.modules
        } else {
          issues.push(`Unsupported implementation in compatibility suite: ${implementation.id}`)
          validFixtures.push(fixture.id)
          continue
        }

        if (JSON.stringify(selectedModules) !== JSON.stringify(fixture.expectedSelectedModules)) {
          issues.push(
            `Valid fixture ${fixture.id} selectedModules did not match compatibility expectation for ${implementation.id}`
          )
        }

        const profileModules = registry.profiles?.[fixture.expectedProfile]?.modules ?? []
        if (JSON.stringify(profileModules) !== JSON.stringify(fixture.expectedSelectedModules)) {
          issues.push(`Valid fixture ${fixture.id} did not match registry profile ${fixture.expectedProfile}`)
        }

        const verification = await verifyDerivedApp(outputDir)
        if (!verification.ok) {
          issues.push(
            ...verification.issues.map((issue) => `${implementation.id}/${fixture.id}: ${issue}`)
          )
        }

        validFixtures.push(fixture.id)
      }

      implementationResults.push({
        id: implementation.id,
        validFixtures,
        invalidFixtures
      })
    }
  } finally {
    await rm(tempRoot, { recursive: true, force: true })
  }

  return {
    ok: issues.length === 0,
    issues,
    implementations: implementationResults
  }
}

function defaultMvnBin() {
  return process.env.MVN_BIN || path.join(process.env.HOME ?? '', '.sdkman/candidates/maven/current/bin/mvn')
}

function prepareJavaCliRuntime(rootDir, implementation) {
  const workspace = path.join(rootDir, implementation.workspace)
  const runtimeClasspathFile = path.join(workspace, 'target', 'runtime-classpath.txt')
  const mvnBin = defaultMvnBin()
  const build = spawnSync(
    mvnBin,
    [
      '-q',
      '-DskipTests',
      'package',
      'dependency:build-classpath',
      '-Dmdep.outputFile=target/runtime-classpath.txt',
      '-DincludeScope=runtime'
    ],
    {
      cwd: workspace,
      encoding: 'utf8'
    }
  )

  if (build.status !== 0) {
    throw new Error(`Unable to prepare Java CLI runtime: ${build.stdout}\n${build.stderr}`)
  }

  return {
    workspace,
    mainClass: implementation.mainClass,
    runtimeClasspathFile
  }
}

function runJavaCli(rootDir, runtime, manifestPath, outputDir) {
  const classpath = [`target/classes`, readFileSync(runtime.runtimeClasspathFile, 'utf8').trim()]
    .filter(Boolean)
    .join(path.delimiter)
  const result = spawnSync(
    'java',
    [
      '-cp',
      classpath,
      runtime.mainClass,
      '--repo-root',
      rootDir,
      '--manifest',
      path.join(rootDir, manifestPath),
      '--output',
      outputDir
    ],
    {
      cwd: runtime.workspace,
      encoding: 'utf8'
    }
  )

  return {
    status: result.status ?? 1,
    output: `${result.stdout ?? ''}\n${result.stderr ?? ''}`.trim()
  }
}
