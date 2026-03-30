package com.fastservice.platform.backend.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.fastservice.platform.backend.common.db.EntityExistence;
import com.fastservice.platform.backend.common.db.JdbcSupport;
import com.fastservice.platform.backend.common.json.JsonStrings;

public class ProjectServiceImpl {

    private final GitRepositoryInspector gitRepositoryInspector = new GitRepositoryInspector();

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

    public String repairprojectworktrees(long projectId) {
        return repairProjectWorktrees(projectId);
    }

    public String pruneprojectworktrees(long projectId) {
        return pruneProjectWorktrees(projectId);
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
        return gitRepositoryInspector.deleteWorktree(repositoryRootPath, worktreePath);
    }

    public String repairProjectWorktrees(long projectId) {
        EntityExistence.requireExists("software_project", projectId, "Project");
        String repositoryRootPath = requireBoundRepositoryPath(projectId);
        return gitRepositoryInspector.repairWorktrees(repositoryRootPath);
    }

    public String pruneProjectWorktrees(long projectId) {
        EntityExistence.requireExists("software_project", projectId, "Project");
        String repositoryRootPath = requireBoundRepositoryPath(projectId);
        return gitRepositoryInspector.pruneWorktrees(repositoryRootPath);
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
                appendRepositorySummary(builder, rs.getString("repository_root_path"));
                builder.append('}');
            }
            builder.append(']');
            return builder.toString();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to list projects", e);
        }
    }

    private void appendRepositorySummary(StringBuilder builder, String repositoryRootPath) {
        if (repositoryRootPath == null || repositoryRootPath.isBlank()) {
            builder.append("null");
            return;
        }

        ProjectRepositorySummary repositorySummary = gitRepositoryInspector.inspect(repositoryRootPath);
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
        appendWorktrees(builder, repositorySummary.worktrees(), repositorySummary.availableBranches());
        builder.append('}');
    }

    private String requireBoundRepositoryPath(long projectId) {
        String sql = "SELECT repository_root_path FROM project_repository_binding WHERE project_id = ?";
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, projectId);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("Project repository is not bound: " + projectId);
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
            List<ProjectWorktreeSummary> worktrees,
            List<String> availableBranches) {
        builder.append('[');
        for (int i = 0; i < worktrees.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            ProjectWorktreeSummary worktree = worktrees.get(i);
            List<String> mergeTargetBranches = worktree.mergeTargetBranches(availableBranches);
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
            builder.append('}');
        }
        builder.append(']');
    }
}
