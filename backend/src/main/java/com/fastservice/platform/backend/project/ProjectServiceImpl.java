package com.fastservice.platform.backend.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fastservice.platform.backend.common.db.JdbcSupport;
import com.fastservice.platform.backend.common.json.JsonStrings;

public class ProjectServiceImpl {

    // Lealone-generated service executors dispatch to lowercase method names.
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
        String sql = "SELECT id, project_key, project_name, active FROM software_project ORDER BY id";
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
                builder.append(",\"active\":").append(rs.getBoolean("active")).append('}');
            }
            builder.append(']');
            return builder.toString();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to list projects", e);
        }
    }
}
