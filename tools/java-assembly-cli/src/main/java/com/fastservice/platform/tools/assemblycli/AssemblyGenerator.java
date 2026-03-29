package com.fastservice.platform.tools.assemblycli;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

final class AssemblyGenerator {

    private static final Pattern APP_ID_PATTERN = Pattern.compile("^[a-z0-9]+(?:-[a-z0-9]+)*$");
    private static final Pattern PACKAGE_PREFIX_PATTERN = Pattern.compile("^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)+$");

    private static final List<String> FRONTEND_SHARED_PATHS = List.of(
            "frontend/bun.lock",
            "frontend/components.json",
            "frontend/eslint.config.js",
            "frontend/index.html",
            "frontend/package.json",
            "frontend/public",
            "frontend/README.md",
            "frontend/src/App.tsx",
            "frontend/src/app/admin-shell.tsx",
            "frontend/src/components/admin",
            "frontend/src/components/ui",
            "frontend/src/index.css",
            "frontend/src/lib/api",
            "frontend/src/lib/utils.ts",
            "frontend/src/main.tsx",
            "frontend/tsconfig.app.json",
            "frontend/tsconfig.json",
            "frontend/tsconfig.node.json",
            "frontend/vite.config.ts");

    private static final Map<String, String> FRONTEND_OPTIONAL_FEATURES = Map.of(
            "software-project-management", "frontend/src/features/projects",
            "ticket-management", "frontend/src/features/tickets",
            "kanban-management", "frontend/src/features/kanban");

    private static final List<String> BACKEND_SHARED_PATHS = List.of(
            "backend/pom.xml",
            "backend/README.md",
            "backend/src/main/java/com/fastservice/platform/backend/BackendApplication.java",
            "backend/src/main/java/com/fastservice/platform/backend/bootstrap",
            "backend/src/main/java/com/fastservice/platform/backend/common",
            "backend/src/main/java/com/fastservice/platform/backend/engineering");

    private static final List<String> BACKEND_ALWAYS_DOMAIN_PATHS = List.of(
            "backend/src/main/java/com/fastservice/platform/backend/access",
            "backend/src/main/java/com/fastservice/platform/backend/user",
            "backend/src/main/java/com/fastservice/platform/backend/generated/service/executor/AccessControlServiceExecutor.java",
            "backend/src/main/java/com/fastservice/platform/backend/generated/service/executor/UserServiceExecutor.java");

    private static final Map<String, List<String>> BACKEND_OPTIONAL_PATHS = Map.of(
            "software-project-management", List.of(
                    "backend/src/main/java/com/fastservice/platform/backend/project",
                    "backend/src/main/java/com/fastservice/platform/backend/generated/service/executor/ProjectServiceExecutor.java"),
            "kanban-management", List.of(
                    "backend/src/main/java/com/fastservice/platform/backend/kanban",
                    "backend/src/main/java/com/fastservice/platform/backend/generated/service/executor/KanbanServiceExecutor.java"),
            "ticket-management", List.of(
                    "backend/src/main/java/com/fastservice/platform/backend/ticket",
                    "backend/src/main/java/com/fastservice/platform/backend/generated/service/executor/TicketServiceExecutor.java"));

    private static final List<RouteModule> ROUTE_MODULES = List.of(
            new RouteModule("admin-shell", "/dashboard", "DashboardPage", "@/features/dashboard/dashboard-page",
                    "Dashboard", "Operations", "LayoutDashboard", "Assembly Dashboard",
                    "Inspect the selected module baseline for this derived admin application."),
            new RouteModule("user-management", "/users", "UsersPage", "@/features/users/users-page",
                    "Users", "Identity", "Users", "User Management",
                    "Manage application users through the backend user service."),
            new RouteModule("role-permission-management", "/roles", "RolePermissionsPage", "@/features/roles/role-permissions-page",
                    "Roles & Permissions", "Identity", "ShieldCheck", "Role Permission Management",
                    "Manage roles, permissions, and user-role assignments."),
            new RouteModule("software-project-management", "/projects", "ProjectsPage", "@/features/projects/projects-page",
                    "Projects", "Delivery", "FolderKanban", "Software Project Management",
                    "Manage software projects and repository binding workflows."),
            new RouteModule("ticket-management", "/tickets", "TicketsPage", "@/features/tickets/tickets-page",
                    "Tickets", "Delivery", "Ticket", "Ticket Management",
                    "Manage tickets and minimal state progression."),
            new RouteModule("kanban-management", "/kanban", "KanbanPage", "@/features/kanban/kanban-page",
                    "Kanban", "Delivery", "KanbanSquare", "Kanban Management",
                    "Manage boards scoped to the selected project baseline."));

    private static final List<SqlModuleEntry> TABLE_ENTRIES = List.of(
            new SqlModuleEntry("user-management", """
                    create table if not exists app_user (
                      id long auto_increment primary key,
                      username varchar,
                      display_name varchar,
                      email varchar,
                      enabled boolean
                    ) package @packageName generate code @modelSrcDir;
                    """),
            new SqlModuleEntry("role-permission-management", """
                    create table if not exists app_role (
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
                    ) package @packageName generate code @modelSrcDir;
                    """),
            new SqlModuleEntry("software-project-management", """
                    create table if not exists software_project (
                      id long auto_increment primary key,
                      project_key varchar,
                      project_name varchar,
                      project_description varchar,
                      active boolean
                    ) package @packageName generate code @modelSrcDir;

                    create table if not exists project_repository_binding (
                      project_id long primary key,
                      repository_root_path varchar
                    ) package @packageName generate code @modelSrcDir;
                    """),
            new SqlModuleEntry("kanban-management", """
                    create table if not exists kanban_board (
                      id long auto_increment primary key,
                      project_id long,
                      board_name varchar
                    ) package @packageName generate code @modelSrcDir;
                    """),
            new SqlModuleEntry("ticket-management", """
                    create table if not exists ticket (
                      id long auto_increment primary key,
                      project_id long,
                      kanban_id long,
                      ticket_key varchar,
                      title varchar,
                      description varchar,
                      state varchar,
                      assignee_user_id long
                    ) package @packageName generate code @modelSrcDir;
                    """));

    private static final List<SqlModuleEntry> SERVICE_ENTRIES = List.of(
            new SqlModuleEntry("user-management", """
                    create service if not exists user_service (
                      createUser(username varchar, displayName varchar, email varchar) long,
                      listUsers() varchar
                    )
                    package @packageName
                    implement by 'com.fastservice.platform.backend.user.UserServiceImpl'
                    generate code @serviceSrcDir;
                    """),
            new SqlModuleEntry("role-permission-management", """
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
                    """),
            new SqlModuleEntry("software-project-management", """
                    create service if not exists project_service (
                      createProject(projectKey varchar, projectName varchar, description varchar) long,
                      bindProjectRepository(projectId long, repositoryPath varchar) varchar,
                      switchProjectBranch(projectId long, branchName varchar) varchar,
                      listProjects() varchar
                    )
                    package @packageName
                    implement by 'com.fastservice.platform.backend.project.ProjectServiceImpl'
                    generate code @serviceSrcDir;
                    """),
            new SqlModuleEntry("kanban-management", """
                    create service if not exists kanban_service (
                      createKanban(projectId long, boardName varchar) long,
                      listKanbansByProject(projectId long) varchar
                    )
                    package @packageName
                    implement by 'com.fastservice.platform.backend.kanban.KanbanServiceImpl'
                    generate code @serviceSrcDir;
                    """),
            new SqlModuleEntry("ticket-management", """
                    create service if not exists ticket_service (
                      createTicket(projectId long, kanbanId long, ticketKey varchar, title varchar, description varchar, assigneeUserId long) long,
                      moveTicket(ticketId long, targetState varchar) varchar,
                      listTicketsByProject(projectId long) varchar
                    )
                    package @packageName
                    implement by 'com.fastservice.platform.backend.ticket.TicketServiceImpl'
                    generate code @serviceSrcDir;
                    """));

    private static final List<SqlModuleEntry> DEMO_ENTRIES = List.of(
            new SqlModuleEntry("user-management", """
                    insert into app_user(id, username, display_name, email, enabled)
                    values(100, 'admin', 'Administrator', 'admin@fastservice.local', true);
                    """),
            new SqlModuleEntry("role-permission-management", """
                    insert into app_role(id, role_code, role_name)
                    values(200, 'ADMIN', 'Administrator');

                    insert into app_permission(id, permission_code, permission_name, scope)
                    values(300, 'dashboard:view', 'View Dashboard', 'MENU');

                    insert into app_user_role(user_id, role_id)
                    values(100, 200);

                    insert into app_role_permission(role_id, permission_id)
                    values(200, 300);
                    """),
            new SqlModuleEntry("software-project-management", """
                    insert into app_permission(id, permission_code, permission_name, scope)
                    values(301, 'project:manage', 'Manage Projects', 'FUNCTION');

                    insert into app_role_permission(role_id, permission_id)
                    values(200, 301);
                    """),
            new SqlModuleEntry("ticket-management", """
                    insert into app_permission(id, permission_code, permission_name, scope)
                    values(302, 'ticket:manage', 'Manage Tickets', 'FUNCTION');

                    insert into app_role_permission(role_id, permission_id)
                    values(200, 302);
                    """),
            new SqlModuleEntry("kanban-management", """
                    insert into app_permission(id, permission_code, permission_name, scope)
                    values(303, 'kanban:view', 'View Kanban', 'MENU');

                    insert into app_role_permission(role_id, permission_id)
                    values(200, 303);
                    """));

    private final Path repoRoot;

    AssemblyGenerator(Path repoRoot) {
        this.repoRoot = repoRoot.toAbsolutePath().normalize();
    }

    AssemblyResult scaffold(Path manifestPath, Path outputDir) throws IOException {
        Path resolvedManifestPath = manifestPath.toAbsolutePath().normalize();
        Path resolvedOutputDir = outputDir.toAbsolutePath().normalize();

        Map<String, Object> registry = loadRegistry();
        Map<String, Object> contract = loadContract();
        String rawManifest = Files.readString(resolvedManifestPath);
        Map<String, Object> manifest = SimpleJson.parseObject(rawManifest);
        ManifestSelection selection = validateManifest(manifest, registry, contract);

        ensureOutsideRepository(resolvedOutputDir);
        deleteRecursively(resolvedOutputDir);
        Files.createDirectories(resolvedOutputDir);

        for (String relativePath : FRONTEND_SHARED_PATHS) {
            copyRelativePath(relativePath, resolvedOutputDir);
        }
        for (String relativePath : BACKEND_SHARED_PATHS) {
            copyRelativePath(relativePath, resolvedOutputDir);
        }
        for (String relativePath : BACKEND_ALWAYS_DOMAIN_PATHS) {
            copyRelativePath(relativePath, resolvedOutputDir);
        }
        copyRelativePath("frontend/src/features/users", resolvedOutputDir);
        copyRelativePath("frontend/src/features/roles", resolvedOutputDir);

        for (Map.Entry<String, String> entry : FRONTEND_OPTIONAL_FEATURES.entrySet()) {
            if (selection.selectedModules().contains(entry.getKey())) {
                copyRelativePath(entry.getValue(), resolvedOutputDir);
            }
        }
        for (Map.Entry<String, List<String>> entry : BACKEND_OPTIONAL_PATHS.entrySet()) {
            if (!selection.selectedModules().contains(entry.getKey())) {
                continue;
            }
            for (String relativePath : entry.getValue()) {
                copyRelativePath(relativePath, resolvedOutputDir);
            }
        }

        Files.createDirectories(resolvedOutputDir.resolve("docs/ai"));
        Files.createDirectories(resolvedOutputDir.resolve("scripts"));
        Files.createDirectories(resolvedOutputDir.resolve("backend/src/main/resources/sql"));
        Files.createDirectories(resolvedOutputDir.resolve("backend/src/main/java/com/fastservice/platform/backend/demo"));
        Files.createDirectories(resolvedOutputDir.resolve("frontend/src/app"));
        Files.createDirectories(resolvedOutputDir.resolve("frontend/src/features/dashboard"));

        writeGeneratedApp(resolvedOutputDir, rawManifest, manifest, registry, contract, selection.selectedModules());
        rewriteWorkspaceMetadata(resolvedOutputDir, applicationId(manifest), applicationName(manifest), applicationPackagePrefix(manifest));

        return new AssemblyResult(resolvedOutputDir, selection.selectedModules());
    }

    Map<String, Object> loadRegistry() throws IOException {
        return SimpleJson.parseObject(Files.readString(repoRoot.resolve("docs/ai/module-registry.json")));
    }

    Map<String, Object> loadContract() throws IOException {
        return SimpleJson.parseObject(Files.readString(repoRoot.resolve("docs/ai/app-assembly-contract.json")));
    }

    ManifestSelection validateManifest(Map<String, Object> manifest, Map<String, Object> registry, Map<String, Object> contract) {
        require("fsp-app-manifest/v1".equals(asString(manifest.get("schemaVersion"))), "Unsupported manifest schemaVersion");
        Map<String, Object> application = asMap(manifest.get("application"), "Manifest must include application");
        String appId = asString(application.get("id"));
        String appName = asString(application.get("name"));
        String packagePrefix = asString(application.get("packagePrefix"));

        require(APP_ID_PATTERN.matcher(appId).matches(), "application.id must be kebab-case");
        require(!appName.isBlank(), "application.name is required");
        require(PACKAGE_PREFIX_PATTERN.matcher(packagePrefix).matches(),
                "application.packagePrefix must be a dotted Java package prefix");

        List<String> selectedModules = asStringList(manifest.get("modules"), "modules must be an array");
        require(!selectedModules.isEmpty(), "modules must be an array");
        require(new LinkedHashSet<>(selectedModules).size() == selectedModules.size(), "modules must not contain duplicates");

        Map<String, Map<String, Object>> registryById = registryById(registry);
        for (String moduleId : selectedModules) {
            require(registryById.containsKey(moduleId), "Unknown module: " + moduleId);
        }

        for (String requiredCoreId : requiredCoreModuleIds(registry)) {
            require(selectedModules.contains(requiredCoreId), "Manifest must include required core module: " + requiredCoreId);
        }

        for (String moduleId : selectedModules) {
            for (String dependency : asOptionalStringList(registryById.get(moduleId).get("dependsOn"))) {
                require(selectedModules.contains(dependency), "Module " + moduleId + " requires dependency " + dependency);
            }
        }

        for (String field : asOptionalStringList(contract.get("requiredManifestFields"))) {
            requireNestedField(manifest, field);
        }

        return new ManifestSelection(selectedModules, registryById);
    }

    private void requireNestedField(Map<String, Object> manifest, String fieldPath) {
        Object current = manifest;
        for (String part : fieldPath.split("\\.")) {
            if (!(current instanceof Map<?, ?> map) || !map.containsKey(part) || map.get(part) == null) {
                throw new IllegalArgumentException("Manifest must include " + fieldPath);
            }
            current = map.get(part);
        }
    }

    private Map<String, Map<String, Object>> registryById(Map<String, Object> registry) {
        Map<String, Map<String, Object>> byId = new LinkedHashMap<>();
        for (Object entry : asList(registry.get("modules"), "Registry must include modules")) {
            Map<String, Object> module = asMap(entry, "Registry module must be an object");
            byId.put(asString(module.get("id")), module);
        }
        return byId;
    }

    private List<String> requiredCoreModuleIds(Map<String, Object> registry) {
        List<String> required = new ArrayList<>();
        for (Object entry : asList(registry.get("modules"), "Registry must include modules")) {
            Map<String, Object> module = asMap(entry, "Registry module must be an object");
            if ("required-core".equals(asString(module.get("role")))) {
                required.add(asString(module.get("id")));
            }
        }
        return required;
    }

    private void ensureOutsideRepository(Path outputDir) {
        require(!outputDir.startsWith(repoRoot), "Output path must be outside repository workspace: " + outputDir);
    }

    private void copyRelativePath(String relativePath, Path outputDir) throws IOException {
        Path sourcePath = repoRoot.resolve(relativePath);
        Path targetPath = outputDir.resolve(relativePath);
        if (Files.isDirectory(sourcePath)) {
            Files.walkFileTree(sourcePath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path relative = sourcePath.relativize(dir);
                    Files.createDirectories(targetPath.resolve(relative));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path relative = sourcePath.relativize(file);
                    Files.createDirectories(targetPath.resolve(relative).getParent());
                    Files.copy(file, targetPath.resolve(relative), StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });
            return;
        }
        Files.createDirectories(Objects.requireNonNull(targetPath.getParent()));
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    private void writeGeneratedApp(Path outputDir, String rawManifest, Map<String, Object> manifest, Map<String, Object> registry,
            Map<String, Object> contract, List<String> selectedModules) throws IOException {
        writeString(outputDir.resolve("README.md"), buildRootReadme(applicationName(manifest), applicationId(manifest), selectedModules));
        writeString(outputDir.resolve("AGENTS.md"), buildAgentsFile());
        writeString(outputDir.resolve("app-manifest.json"), ensureTrailingNewline(rawManifest));
        writeString(outputDir.resolve("docs/ai/context.json"), ensureTrailingNewline(buildGeneratedContext(manifest, selectedModules, registry)));
        copyNormativeAssets(contract, outputDir);

        writeString(outputDir.resolve("backend/src/main/resources/sql/tables.sql"), buildTablesSql(selectedModules));
        writeString(outputDir.resolve("backend/src/main/resources/sql/services.sql"), buildServicesSql(selectedModules));
        writeString(outputDir.resolve("backend/src/main/resources/sql/demo.sql"), buildDemoSql(selectedModules));
        writeString(outputDir.resolve("backend/src/main/java/com/fastservice/platform/backend/demo/DemoDataSupport.java"),
                buildDemoDataSupport());

        writeString(outputDir.resolve("frontend/src/app/router.tsx"), buildRouterTsx(selectedModules));
        writeString(outputDir.resolve("frontend/src/app/navigation.ts"), buildNavigationTs(selectedModules));
        writeString(outputDir.resolve("frontend/src/features/dashboard/dashboard-page.tsx"), buildDashboardPage(selectedModules));

        copyRelativePath("scripts/app-assembly-lib.mjs", outputDir);
        copyRelativePath("scripts/verify-derived-app.mjs", outputDir);
        writeString(outputDir.resolve("scripts/verify-derived-app.sh"), buildLocalVerifyScript());
        outputDir.resolve("scripts/verify-derived-app.sh").toFile().setExecutable(true, false);
    }

    private void copyNormativeAssets(Map<String, Object> contract, Path outputDir) throws IOException {
        for (String relativePath : requiredGeneratedFiles(contract)) {
            if (relativePath.startsWith("docs/ai/") && !"docs/ai/context.json".equals(relativePath)) {
                copyRelativePath(relativePath, outputDir);
            }
        }
    }

    private List<String> requiredGeneratedFiles(Map<String, Object> contract) {
        return asOptionalStringList(asMap(contract.get("outputInvariants"), "Contract must include outputInvariants").get("requiredFiles"));
    }

    private void rewriteWorkspaceMetadata(Path outputDir, String appId, String appName, String packagePrefix) throws IOException {
        Path frontendPackagePath = outputDir.resolve("frontend/package.json");
        String frontendPackage = Files.readString(frontendPackagePath);
        frontendPackage = frontendPackage.replace("\"name\": \"fast-service-platform-frontend\"", "\"name\": \"" + appId + "-frontend\"");
        writeString(frontendPackagePath, ensureTrailingNewline(frontendPackage));

        Path backendPomPath = outputDir.resolve("backend/pom.xml");
        String backendPom = Files.readString(backendPomPath);
        backendPom = backendPom
                .replace("<groupId>com.fastservice.platform</groupId>", "<groupId>" + packagePrefix + "</groupId>")
                .replace("<artifactId>fast-service-platform-backend</artifactId>", "<artifactId>" + appId + "-backend</artifactId>")
                .replace("<name>fast-service-platform-backend</name>", "<name>" + appId + "-backend</name>")
                .replace("<description>Enterprise component core backend for Fast Service Platform</description>",
                        "<description>Derived backend skeleton for " + escapeXml(appName) + "</description>");
        writeString(backendPomPath, ensureTrailingNewline(backendPom));
    }

    private String buildRootReadme(String appName, String appId, List<String> selectedModules) {
        StringBuilder builder = new StringBuilder();
        builder.append("# ").append(appName).append("\n\n")
                .append("This application skeleton was derived from Fast Service Platform.\n\n")
                .append("## Selected Modules\n\n");
        for (String moduleId : selectedModules) {
            builder.append("- `").append(moduleId).append("`\n");
        }
        builder.append("\n## Validation\n\n")
                .append("Run inside this generated application:\n\n")
                .append("```bash\n./scripts/verify-derived-app.sh\n```\n\n")
                .append("Or from the source platform repository:\n\n")
                .append("```bash\n./scripts/verify-derived-app.sh /absolute/path/to/").append(appId).append("\n```\n\n")
                .append("Or through the repository-owned Java verifier:\n\n")
                .append("```bash\n./scripts/verify-derived-app-java.sh /absolute/path/to/").append(appId).append("\n```\n");
        return builder.toString();
    }

    private String buildAgentsFile() {
        return """
                # AGENTS

                ## Start Here

                - Read `README.md`
                - Read `app-manifest.json`
                - Read `docs/ai/context.json`
                - Read `docs/ai/module-registry.json`
                - Run `./scripts/verify-derived-app.sh` before expanding the generated skeleton

                ## Scope

                - This generated app is a derived skeleton from Fast Service Platform
                - Keep the selected module set in sync with `app-manifest.json`
                - Do not silently widen the dependency boundary
                """;
    }

    private String buildGeneratedContext(Map<String, Object> manifest, List<String> selectedModules, Map<String, Object> registry) {
        Map<String, Object> context = new LinkedHashMap<>();
        context.put("schemaVersion", "fsp-derived-app-context/v1");
        context.put("sourcePlatform", "Fast Service Platform");
        context.put("application", manifest.get("application"));
        context.put("selectedModules", selectedModules);
        context.put("requiredCoreModules", requiredCoreModuleIds(registry));

        Map<String, Object> contractInputs = new LinkedHashMap<>();
        contractInputs.put("manifest", "app-manifest.json");
        contractInputs.put("moduleRegistry", "docs/ai/module-registry.json");
        contractInputs.put("assemblyContract", "docs/ai/app-assembly-contract.json");
        contractInputs.put("generatedAppVerificationContract", "docs/ai/generated-app-verification-contract.json");
        context.put("contractInputs", contractInputs);

        Map<String, Object> validation = new LinkedHashMap<>();
        validation.put("local", "./scripts/verify-derived-app.sh");
        validation.put("repositoryOwned", "./scripts/verify-derived-app.sh <generated-app-dir>");
        validation.put("referenceVerifier", "node ./scripts/verify-derived-app.mjs <generated-app-dir>");
        validation.put("compatibleVerifier", "./scripts/verify-derived-app-java.sh <generated-app-dir>");
        context.put("validation", validation);

        return SimpleJson.stringify(context);
    }

    private String buildLocalVerifyScript() {
        return """
                #!/usr/bin/env bash
                set -euo pipefail

                ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
                TARGET_DIR="${1:-$ROOT_DIR}"

                node "$ROOT_DIR/scripts/verify-derived-app.mjs" "$TARGET_DIR"
                """;
    }

    private String buildTablesSql(List<String> selectedModules) {
        return buildSqlBlock("""
                set @packageName 'com.fastservice.platform.backend.generated.model';
                set @modelSrcDir './target/generated-sources/lealone-model';
                """, selectedModules, TABLE_ENTRIES);
    }

    private String buildServicesSql(List<String> selectedModules) {
        return buildSqlBlock("""
                set @packageName 'com.fastservice.platform.backend.generated.service';
                set @serviceSrcDir './target/generated-sources/lealone-service';
                """, selectedModules, SERVICE_ENTRIES);
    }

    private String buildDemoSql(List<String> selectedModules) {
        Set<String> selected = new LinkedHashSet<>(selectedModules);
        StringBuilder builder = new StringBuilder();
        for (SqlModuleEntry entry : DEMO_ENTRIES) {
            if (selected.contains(entry.moduleId())) {
                builder.append(entry.sql().trim()).append("\n\n");
            }
        }
        return builder.toString();
    }

    private String buildSqlBlock(String header, List<String> selectedModules, List<SqlModuleEntry> entries) {
        Set<String> selected = new LinkedHashSet<>(selectedModules);
        StringBuilder builder = new StringBuilder();
        builder.append(header.strip()).append("\n\n");
        for (SqlModuleEntry entry : entries) {
            if (selected.contains(entry.moduleId())) {
                builder.append(entry.sql().trim()).append("\n\n");
            }
        }
        return builder.toString();
    }

    private String buildDemoDataSupport() {
        return """
                package com.fastservice.platform.backend.demo;

                import java.sql.Connection;
                import java.sql.PreparedStatement;
                import java.sql.ResultSet;
                import java.sql.SQLException;

                import com.fastservice.platform.backend.common.db.JdbcSupport;
                import com.fastservice.platform.backend.common.sql.SqlScriptExecutor;

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
                        }
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
                }
                """;
    }

    private String buildRouterTsx(List<String> selectedModules) {
        List<RouteModule> routeModules = selectedRouteModules(selectedModules);
        StringBuilder imports = new StringBuilder();
        StringBuilder routes = new StringBuilder();
        for (RouteModule module : routeModules) {
            imports.append("import { ").append(module.importName()).append(" } from '").append(module.importPath()).append("'\n");
            routes.append("      {\n")
                    .append("        path: '").append(module.route().substring(1)).append("',\n")
                    .append("        element: <").append(module.importName()).append(" />,\n")
                    .append("      },\n");
        }

        return """
                import { createBrowserRouter, Navigate } from 'react-router-dom'
                import type { RouteObject } from 'react-router-dom'

                import AdminShell from '@/app/admin-shell'
                """ + imports + """

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
                """ + routes + """
                      {
                        path: '*',
                        element: <MissingRoutePage />,
                      },
                    ],
                  },
                ]

                export const router = createBrowserRouter(adminRoutes)
                """;
    }

    private String buildNavigationTs(List<String> selectedModules) {
        List<RouteModule> routeModules = selectedRouteModules(selectedModules);
        LinkedHashSet<String> icons = new LinkedHashSet<>();
        for (RouteModule module : routeModules) {
            icons.add(module.icon());
        }

        StringBuilder iconImports = new StringBuilder();
        for (String icon : icons) {
            if (iconImports.length() > 0) {
                iconImports.append(",\n  ");
            }
            iconImports.append(icon);
        }

        StringBuilder items = new StringBuilder();
        for (RouteModule module : routeModules) {
            items.append("  {\n")
                    .append("    to: '").append(module.route()).append("',\n")
                    .append("    label: '").append(module.label()).append("',\n")
                    .append("    eyebrow: '").append(module.eyebrow()).append("',\n")
                    .append("    icon: ").append(module.icon()).append(",\n")
                    .append("    meta: {\n")
                    .append("      title: '").append(module.title()).append("',\n")
                    .append("      description: '").append(module.description()).append("',\n")
                    .append("    },\n")
                    .append("  },\n");
        }

        return """
                import {
                  """ + iconImports + """
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
                """ + items + """
                ]

                export function findNavigationItem(pathname: string) {
                  return adminNavigation.find((item) => pathname === item.to)
                }
                """;
    }

    private String buildDashboardPage(List<String> selectedModules) {
        StringBuilder badges = new StringBuilder();
        for (String moduleId : selectedModules) {
            if ("admin-shell".equals(moduleId)) {
                continue;
            }
            badges.append("        <Badge key=\"").append(moduleId).append("\" variant=\"outline\" className=\"rounded-full\">\n")
                    .append("          ").append(moduleId).append("\n")
                    .append("        </Badge>\n");
        }

        StringBuilder moduleArray = new StringBuilder();
        for (int i = 0; i < selectedModules.size(); i++) {
            if (i > 0) {
                moduleArray.append(", ");
            }
            moduleArray.append('\'').append(selectedModules.get(i)).append('\'');
        }

        return """
                import { PageHeader } from '@/components/admin/page-header'
                import { Badge } from '@/components/ui/badge'
                import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

                const selectedModules = [""" + moduleArray + "]\n\n" + """
                export function DashboardPage() {
                  return (
                    <div className="space-y-6">
                      <PageHeader
                        eyebrow="Overview"
                        title="Assembly dashboard"
                        description="This dashboard reflects the selected module assembly for the generated application skeleton."
                      />

                      <Card className="bg-card/95">
                        <CardHeader>
                          <CardTitle className="text-lg">Selected modules</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-4">
                          <div className="flex flex-wrap gap-2">
                """ + badges + """
                          </div>
                          <p className="text-sm leading-6 text-muted-foreground">
                            Generated by the Java-compatible app assembly implementation for {selectedModules.length} modules.
                            Keep route wiring, SQL contracts, and this selected module list aligned with app-manifest.json.
                          </p>
                        </CardContent>
                      </Card>
                    </div>
                  )
                }
                """;
    }

    private List<RouteModule> selectedRouteModules(List<String> selectedModules) {
        Set<String> selected = new LinkedHashSet<>(selectedModules);
        List<RouteModule> routeModules = new ArrayList<>();
        for (RouteModule module : ROUTE_MODULES) {
            if (selected.contains(module.id())) {
                routeModules.add(module);
            }
        }
        return routeModules;
    }

    private String applicationId(Map<String, Object> manifest) {
        return asString(asMap(manifest.get("application"), "Manifest must include application").get("id"));
    }

    private String applicationName(Map<String, Object> manifest) {
        return asString(asMap(manifest.get("application"), "Manifest must include application").get("name"));
    }

    private String applicationPackagePrefix(Map<String, Object> manifest) {
        return asString(asMap(manifest.get("application"), "Manifest must include application").get("packagePrefix"));
    }

    private static void writeString(Path path, String content) throws IOException {
        Files.createDirectories(Objects.requireNonNull(path.getParent()));
        Files.writeString(path, ensureTrailingNewline(content));
    }

    private static String ensureTrailingNewline(String value) {
        return value.endsWith("\n") ? value : value + "\n";
    }

    private static String escapeXml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private static void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.deleteIfExists(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.deleteIfExists(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static Map<String, Object> asMap(Object value, String message) {
        if (!(value instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException(message);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            result.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return result;
    }

    private static List<Object> asList(Object value, String message) {
        if (!(value instanceof List<?> list)) {
            throw new IllegalArgumentException(message);
        }
        return new ArrayList<>(list);
    }

    private static List<String> asStringList(Object value, String message) {
        List<Object> values = asList(value, message);
        List<String> result = new ArrayList<>();
        for (Object entry : values) {
            result.add(asString(entry));
        }
        return result;
    }

    private static List<String> asOptionalStringList(Object value) {
        if (value == null) {
            return List.of();
        }
        return asStringList(value, "Expected array of strings");
    }

    private static String asString(Object value) {
        if (!(value instanceof String stringValue)) {
            throw new IllegalArgumentException("Expected string value");
        }
        return stringValue;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    record AssemblyResult(Path outputDir, List<String> selectedModules) {
    }

    record ManifestSelection(List<String> selectedModules, Map<String, Map<String, Object>> registryById) {
    }

    record SqlModuleEntry(String moduleId, String sql) {
    }

    record RouteModule(String id, String route, String importName, String importPath, String label, String eyebrow,
            String icon, String title, String description) {
    }
}
