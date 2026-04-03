package com.fastservice.platform.backend.project;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.fastservice.platform.backend.common.db.EntityExistence;
import com.fastservice.platform.backend.common.db.JdbcSupport;
import com.fastservice.platform.backend.common.json.JsonStrings;

public class ProjectServiceImpl {

    private final GitRepositoryInspector gitRepositoryInspector = new GitRepositoryInspector();
    private final ProjectSandboxManager projectSandboxManager = new ProjectSandboxManager();
    private final ProjectDerivedAppAssemblyManager projectDerivedAppAssemblyManager = new ProjectDerivedAppAssemblyManager();

    // Lealone-generated service executors dispatch to lowercase method names.
    public long createproject(String projectKey, String projectName, String description) {
        return createProject(projectKey, projectName, description);
    }

    public String bindprojectrepository(long projectId, String repositoryPath) {
        return bindProjectRepository(projectId, repositoryPath);
    }

    public String switchprojectbranch(long projectId, String branchName) {
        return switchProjectBranch(projectId, branchName);
    }

    public String createprojectworktree(long projectId, String branchName) {
        return createProjectWorktree(projectId, branchName);
    }

    public String mergeprojectworktree(long projectId, String worktreePath, String targetBranch) {
        return mergeProjectWorktree(projectId, worktreePath, targetBranch);
    }

    public String deleteprojectworktree(long projectId, String worktreePath) {
        return deleteProjectWorktree(projectId, worktreePath);
    }

    public String createprojectsandboximage(long projectId, String worktreePath) {
        return createProjectSandboxImage(projectId, worktreePath);
    }

    public String createprojectsandboxcontainer(long projectId, String worktreePath) {
        return createProjectSandboxContainer(projectId, worktreePath);
    }

    public String deleteprojectsandboxcontainer(long projectId, String worktreePath) {
        return deleteProjectSandboxContainer(projectId, worktreePath);
    }

    public String repairprojectworktrees(long projectId) {
        return repairProjectWorktrees(projectId);
    }

    public String pruneprojectworktrees(long projectId) {
        return pruneProjectWorktrees(projectId);
    }

    public String getprojectderivedappassembly(long projectId) {
        return getProjectDerivedAppAssembly(projectId);
    }

    public String requestprojectderivedappassembly(long projectId, String manifestJson, String outputDirectory) {
        return requestProjectDerivedAppAssembly(projectId, manifestJson, outputDirectory);
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

    public String bindProjectRepository(long projectId, String repositoryPath) {
        EntityExistence.requireExists("software_project", projectId, "Project");
        ProjectRepositorySummary repositorySummary = gitRepositoryInspector.inspect(repositoryPath);
        String updateSql = "UPDATE project_repository_binding SET repository_root_path = ? WHERE project_id = ?";
        String insertSql = "INSERT INTO project_repository_binding(project_id, repository_root_path) VALUES(?, ?)";

        try (Connection connection = JdbcSupport.getConnection()) {
            int updatedRows;
            try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                updateStatement.setString(1, repositorySummary.rootPath());
                updateStatement.setLong(2, projectId);
                updatedRows = updateStatement.executeUpdate();
            }

            if (updatedRows == 0) {
                try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                    insertStatement.setLong(1, projectId);
                    insertStatement.setString(2, repositorySummary.rootPath());
                    insertStatement.executeUpdate();
                }
            }

            return repositorySummary.rootPath();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to bind project repository", e);
        }
    }

    public String switchProjectBranch(long projectId, String branchName) {
        EntityExistence.requireExists("software_project", projectId, "Project");
        String repositoryRootPath = requireBoundRepositoryPath(projectId);
        return gitRepositoryInspector.switchBranch(repositoryRootPath, branchName);
    }

    public String createProjectWorktree(long projectId, String branchName) {
        EntityExistence.requireExists("software_project", projectId, "Project");
        String repositoryRootPath = requireBoundRepositoryPath(projectId);
        return gitRepositoryInspector.createWorktree(repositoryRootPath, branchName);
    }

    public String mergeProjectWorktree(long projectId, String worktreePath, String targetBranch) {
        EntityExistence.requireExists("software_project", projectId, "Project");
        String repositoryRootPath = requireBoundRepositoryPath(projectId);
        return gitRepositoryInspector.mergeWorktree(repositoryRootPath, worktreePath, targetBranch);
    }

    public String deleteProjectWorktree(long projectId, String worktreePath) {
        EntityExistence.requireExists("software_project", projectId, "Project");
        String repositoryRootPath = requireBoundRepositoryPath(projectId);
        String deletedPath = gitRepositoryInspector.deleteWorktree(repositoryRootPath, worktreePath);
        projectSandboxManager.cleanupWorktreeResources(projectId, deletedPath);
        return deletedPath;
    }

    public String createProjectSandboxImage(long projectId, String worktreePath) {
        EntityExistence.requireExists("software_project", projectId, "Project");
        String repositoryRootPath = requireBoundRepositoryPath(projectId);
        return projectSandboxManager.createImage(projectId, repositoryRootPath, worktreePath);
    }

    public String createProjectSandboxContainer(long projectId, String worktreePath) {
        EntityExistence.requireExists("software_project", projectId, "Project");
        String repositoryRootPath = requireBoundRepositoryPath(projectId);
        return projectSandboxManager.createContainer(projectId, repositoryRootPath, worktreePath);
    }

    public String deleteProjectSandboxContainer(long projectId, String worktreePath) {
        EntityExistence.requireExists("software_project", projectId, "Project");
        String repositoryRootPath = requireBoundRepositoryPath(projectId);
        return projectSandboxManager.deleteContainer(projectId, repositoryRootPath, worktreePath);
    }

    public String repairProjectWorktrees(long projectId) {
        EntityExistence.requireExists("software_project", projectId, "Project");
        String repositoryRootPath = requireBoundRepositoryPath(projectId);
        return gitRepositoryInspector.repairWorktrees(repositoryRootPath);
    }

    public String pruneProjectWorktrees(long projectId) {
        EntityExistence.requireExists("software_project", projectId, "Project");
        String repositoryRootPath = requireBoundRepositoryPath(projectId);
        String prunedPath = gitRepositoryInspector.pruneWorktrees(repositoryRootPath);
        ProjectRepositorySummary repositorySummary = gitRepositoryInspector.inspect(repositoryRootPath);
        projectSandboxManager.pruneMissingWorktreeResources(projectId, repositorySummary.worktrees());
        return prunedPath;
    }

    public String getProjectDerivedAppAssembly(long projectId) {
        EntityExistence.requireExists("software_project", projectId, "Project");
        return projectDerivedAppAssemblyManager.readAssemblyContext(projectId, readBoundRepositoryPath(projectId));
    }

    public String requestProjectDerivedAppAssembly(long projectId, String manifestJson, String outputDirectory) {
        EntityExistence.requireExists("software_project", projectId, "Project");
        String repositoryRootPath = requireBoundRepositoryPath(projectId);
        return projectDerivedAppAssemblyManager.requestAssembly(projectId, repositoryRootPath, manifestJson, outputDirectory);
    }

    public String listProjects() {
        String sql = """
                SELECT p.id, p.project_key, p.project_name, p.active, b.repository_root_path
                FROM software_project p
                LEFT JOIN project_repository_binding b ON b.project_id = p.id
                ORDER BY p.id
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
                builder.append("{\"id\":").append(rs.getLong("id"));
                builder.append(",\"key\":").append(JsonStrings.quote(rs.getString("project_key")));
                builder.append(",\"name\":").append(JsonStrings.quote(rs.getString("project_name")));
                builder.append(",\"active\":").append(rs.getBoolean("active"));
                builder.append(",\"repository\":");
                appendRepositorySummary(builder, rs.getLong("id"), rs.getString("repository_root_path"));
                builder.append('}');
            }
            builder.append(']');
            return builder.toString();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to list projects", e);
        }
    }

    private void appendRepositorySummary(StringBuilder builder, long projectId, String repositoryRootPath) {
        if (repositoryRootPath == null || repositoryRootPath.isBlank()) {
            builder.append("null");
            return;
        }

        ProjectRepositorySummary repositorySummary = gitRepositoryInspector.inspect(repositoryRootPath);
        Map<String, ProjectWorktreeSandboxRecord> sandboxRecords = projectSandboxManager.readSandboxRecords(projectId);
        builder.append('{');
        builder.append("\"rootPath\":").append(JsonStrings.quote(repositorySummary.rootPath()));
        builder.append(",\"headState\":").append(JsonStrings.quote(repositorySummary.headState().name()));
        builder.append(",\"branch\":").append(JsonStrings.quote(repositorySummary.branch()));
        builder.append(",\"workingTreeState\":").append(JsonStrings.quote(repositorySummary.workingTreeState()));
        builder.append(",\"latestCommitSummary\":").append(JsonStrings.quote(repositorySummary.latestCommitSummary()));
        builder.append(",\"availableBranches\":");
        appendStringArray(builder, repositorySummary.availableBranches());
        builder.append(",\"recentCommits\":");
        appendRecentCommits(builder, repositorySummary.recentCommits());
        builder.append(",\"worktrees\":");
        appendWorktrees(
                builder,
                projectId,
                repositorySummary.rootPath(),
                repositorySummary.worktrees(),
                repositorySummary.availableBranches(),
                sandboxRecords);
        builder.append('}');
    }

    private String requireBoundRepositoryPath(long projectId) {
        String repositoryRootPath = readBoundRepositoryPath(projectId);
        if (repositoryRootPath == null) {
            throw new IllegalArgumentException("Project repository is not bound: " + projectId);
        }
        return repositoryRootPath;
    }

    private String readBoundRepositoryPath(long projectId) {
        String sql = "SELECT repository_root_path FROM project_repository_binding WHERE project_id = ?";
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, projectId);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return rs.getString(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to read bound project repository", e);
        }
    }

    private void appendStringArray(StringBuilder builder, List<String> values) {
        builder.append('[');
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(JsonStrings.quote(values.get(i)));
        }
        builder.append(']');
    }

    private void appendRecentCommits(StringBuilder builder, List<ProjectGitCommitSummary> commits) {
        builder.append('[');
        for (int i = 0; i < commits.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            ProjectGitCommitSummary commit = commits.get(i);
            builder.append('{');
            builder.append("\"hash\":").append(JsonStrings.quote(commit.hash()));
            builder.append(",\"summary\":").append(JsonStrings.quote(commit.summary()));
            builder.append('}');
        }
        builder.append(']');
    }

    private void appendWorktrees(
            StringBuilder builder,
            long projectId,
            String repositoryRootPath,
            List<ProjectWorktreeSummary> worktrees,
            List<String> availableBranches,
            Map<String, ProjectWorktreeSandboxRecord> sandboxRecords) {
        builder.append('[');
        for (int i = 0; i < worktrees.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            ProjectWorktreeSummary worktree = worktrees.get(i);
            List<String> mergeTargetBranches = worktree.mergeTargetBranches(availableBranches);
            ProjectWorktreeSandboxSummary sandboxSummary = projectSandboxManager.summarize(
                    projectId,
                    Path.of(repositoryRootPath),
                    worktree,
                    sandboxRecords.get(Path.of(worktree.path()).normalize().toAbsolutePath().toString()));
            builder.append('{');
            builder.append("\"path\":").append(JsonStrings.quote(worktree.path()));
            builder.append(",\"main\":").append(worktree.main());
            builder.append(",\"headState\":").append(JsonStrings.quote(worktree.headState().name()));
            builder.append(",\"branch\":").append(JsonStrings.quote(worktree.branch()));
            builder.append(",\"workingTreeState\":").append(JsonStrings.quote(worktree.workingTreeState()));
            builder.append(",\"hasUpstream\":").append(worktree.hasUpstream());
            builder.append(",\"hasUnpushedCommits\":").append(worktree.hasUnpushedCommits());
            builder.append(",\"stale\":").append(worktree.stale());
            builder.append(",\"deletionAllowed\":").append(worktree.deletionAllowed());
            builder.append(",\"deletionRestriction\":").append(JsonStrings.quote(worktree.deletionRestriction()));
            builder.append(",\"mergeAllowed\":").append(worktree.mergeAllowed(availableBranches));
            builder.append(",\"mergeRestriction\":").append(JsonStrings.quote(worktree.mergeRestriction(availableBranches)));
            builder.append(",\"mergeTargetBranches\":");
            appendStringArray(builder, mergeTargetBranches);
            builder.append(",\"sandbox\":");
            appendSandboxSummary(builder, sandboxSummary);
            builder.append('}');
        }
        builder.append(']');
    }

    private void appendSandboxSummary(StringBuilder builder, ProjectWorktreeSandboxSummary sandbox) {
        builder.append('{');
        builder.append("\"supported\":").append(sandbox.supported());
        builder.append(",\"restriction\":").append(JsonStrings.quote(sandbox.restriction()));
        builder.append(",\"imageStatus\":").append(JsonStrings.quote(sandbox.imageStatus()));
        builder.append(",\"imageReference\":").append(JsonStrings.quote(sandbox.imageReference()));
        builder.append(",\"imageFailureMessage\":").append(JsonStrings.quote(sandbox.imageFailureMessage()));
        builder.append(",\"imageInitScriptPath\":").append(JsonStrings.quote(sandbox.imageInitScriptPath()));
        builder.append(",\"imageInitScriptSource\":").append(JsonStrings.quote(sandbox.imageInitScriptSource()));
        builder.append(",\"imageActionAllowed\":").append(sandbox.imageActionAllowed());
        builder.append(",\"imageActionRestriction\":").append(JsonStrings.quote(sandbox.imageActionRestriction()));
        builder.append(",\"containerStatus\":").append(JsonStrings.quote(sandbox.containerStatus()));
        builder.append(",\"containerName\":").append(JsonStrings.quote(sandbox.containerName()));
        builder.append(",\"containerFailureMessage\":").append(JsonStrings.quote(sandbox.containerFailureMessage()));
        builder.append(",\"projectInitScriptPath\":").append(JsonStrings.quote(sandbox.projectInitScriptPath()));
        builder.append(",\"projectInitScriptSource\":").append(JsonStrings.quote(sandbox.projectInitScriptSource()));
        builder.append(",\"containerCreateAllowed\":").append(sandbox.containerCreateAllowed());
        builder.append(",\"containerCreateRestriction\":").append(JsonStrings.quote(sandbox.containerCreateRestriction()));
        builder.append(",\"containerDeleteAllowed\":").append(sandbox.containerDeleteAllowed());
        builder.append(",\"containerDeleteRestriction\":").append(JsonStrings.quote(sandbox.containerDeleteRestriction()));
        builder.append('}');
    }
}
