package com.fastservice.platform.backend.ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fastservice.platform.backend.common.db.EntityExistence;
import com.fastservice.platform.backend.common.db.JdbcSupport;
import com.fastservice.platform.backend.common.json.JsonStrings;
import com.fastservice.platform.backend.common.kanban.KanbanStateMachine;

public class TicketServiceImpl {

    private final TicketWorkflowService workflowService = new TicketWorkflowService();

    // Lealone-generated service executors dispatch to lowercase method names.
    public long createticket(long projectId, long kanbanId, String ticketKey, String title, String description, long assigneeUserId) {
        return createTicket(projectId, kanbanId, ticketKey, title, description, assigneeUserId);
    }

    public String moveticket(long ticketId, String targetState) {
        return moveTicket(ticketId, targetState);
    }

    public String listticketsbyproject(long projectId) {
        return listTicketsByProject(projectId);
    }

    public String getworkflow(long ticketId) {
        return getWorkflow(ticketId);
    }

    public String executeworkflowaction(long ticketId, String actionName, long actorUserId, String comment, Long assigneeUserId) {
        return executeWorkflowAction(ticketId, actionName, actorUserId, comment, assigneeUserId);
    }

    public long createTicket(long projectId, long kanbanId, String ticketKey, String title, String description, long assigneeUserId) {
        validateProjectAndKanban(projectId, kanbanId);
        EntityExistence.requireExists("app_user", assigneeUserId, "User");
        String sql = """
                INSERT INTO ticket(project_id, kanban_id, ticket_key, title, description, state, assignee_user_id)
                VALUES(?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, projectId);
            statement.setLong(2, kanbanId);
            statement.setString(3, ticketKey);
            statement.setString(4, title);
            statement.setString(5, description);
            statement.setString(6, "TODO");
            statement.setLong(7, assigneeUserId);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            throw new IllegalStateException("No generated key returned for ticket insert");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to create ticket", e);
        }
    }

    public String moveTicket(long ticketId, String targetState) {
        String currentState = findCurrentState(ticketId);
        KanbanStateMachine.ensureTransition(currentState, targetState);
        String sql = "UPDATE ticket SET state = ? WHERE id = ?";
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, targetState.toUpperCase());
            statement.setLong(2, ticketId);
            statement.executeUpdate();
            return targetState.toUpperCase();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to move ticket", e);
        }
    }

    public String listTicketsByProject(long projectId) {
        String sql = """
                SELECT id, ticket_key, title, state, kanban_id
                FROM ticket
                WHERE project_id = ?
                ORDER BY id
                """;
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
                    builder.append(",\"key\":").append(JsonStrings.quote(rs.getString("ticket_key")));
                    builder.append(",\"title\":").append(JsonStrings.quote(rs.getString("title")));
                    builder.append(",\"state\":").append(JsonStrings.quote(rs.getString("state")));
                    builder.append(",\"kanbanId\":").append(rs.getLong("kanban_id")).append('}');
                }
                builder.append(']');
                return builder.toString();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to list tickets", e);
        }
    }

    public String getWorkflow(long ticketId) {
        return workflowService.getWorkflow(ticketId);
    }

    public String executeWorkflowAction(long ticketId, String actionName, long actorUserId, String comment, Long assigneeUserId) {
        return workflowService.executeAction(ticketId, actionName, actorUserId, comment, assigneeUserId);
    }

    private String findCurrentState(long ticketId) {
        String sql = "SELECT state FROM ticket WHERE id = ?";
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, ticketId);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("Ticket not found: " + ticketId);
                }
                return rs.getString(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load current ticket state", e);
        }
    }

    private void validateProjectAndKanban(long projectId, long kanbanId) {
        EntityExistence.requireExists("software_project", projectId, "Project");
        String sql = "SELECT project_id FROM kanban_board WHERE id = ?";
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, kanbanId);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("Kanban board not found: " + kanbanId);
                }
                long boardProjectId = rs.getLong(1);
                if (boardProjectId != projectId) {
                    throw new IllegalArgumentException(
                            "Kanban board " + kanbanId + " does not belong to project " + projectId);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to validate project and kanban relationship", e);
        }
    }
}
