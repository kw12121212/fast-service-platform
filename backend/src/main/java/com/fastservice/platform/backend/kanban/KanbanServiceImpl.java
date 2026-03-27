package com.fastservice.platform.backend.kanban;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fastservice.platform.backend.common.db.EntityExistence;
import com.fastservice.platform.backend.common.db.JdbcSupport;
import com.fastservice.platform.backend.common.json.JsonStrings;

public class KanbanServiceImpl {

    // Lealone-generated service executors dispatch to lowercase method names.
    public long createkanban(long projectId, String boardName) {
        return createKanban(projectId, boardName);
    }

    public String listkanbansbyproject(long projectId) {
        return listKanbansByProject(projectId);
    }

    public long createKanban(long projectId, String boardName) {
        EntityExistence.requireExists("software_project", projectId, "Project");
        String sql = "INSERT INTO kanban_board(project_id, board_name) VALUES(?, ?)";
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, projectId);
            statement.setString(2, boardName);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            throw new IllegalStateException("No generated key returned for kanban insert");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to create kanban board", e);
        }
    }

    public String listKanbansByProject(long projectId) {
        String sql = "SELECT id, board_name FROM kanban_board WHERE project_id = ? ORDER BY id";
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, projectId);
            try (ResultSet rs = statement.executeQuery()) {
                StringBuilder builder = new StringBuilder("[");
                boolean first = true;
                while (rs.next()) {
                    if (!first) {
                        builder.append(',');
                    }
                    first = false;
                    builder.append("{\"id\":").append(rs.getLong("id"));
                    builder.append(",\"name\":").append(JsonStrings.quote(rs.getString("board_name"))).append('}');
                }
                builder.append(']');
                return builder.toString();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to list kanban boards", e);
        }
    }
}
