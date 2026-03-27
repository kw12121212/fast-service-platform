package com.fastservice.platform.backend.demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fastservice.platform.backend.common.db.JdbcSupport;
import com.fastservice.platform.backend.common.sql.SqlScriptExecutor;
import com.fastservice.platform.backend.kanban.KanbanServiceImpl;
import com.fastservice.platform.backend.project.ProjectServiceImpl;
import com.fastservice.platform.backend.ticket.TicketServiceImpl;

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

        ProjectServiceImpl projectService = new ProjectServiceImpl();
        KanbanServiceImpl kanbanService = new KanbanServiceImpl();
        TicketServiceImpl ticketService = new TicketServiceImpl();

        long projectId = ensureProject(projectService);
        long boardId = ensureBoard(projectId, kanbanService);
        ensureTicket(projectId, boardId, "FSP-1", "Bootstrap backend core",
                "Establish the first backend core", "TODO", ticketService);
        ensureTicket(projectId, boardId, "FSP-2", "Verify demo workflow",
                "Exercise minimal kanban state flow", "IN_PROGRESS", ticketService);
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

    private static long ensureProject(ProjectServiceImpl projectService) {
        Long existingProjectId = findId("SELECT id FROM software_project WHERE project_key = ?", "FSP");
        if (existingProjectId != null) {
            return existingProjectId;
        }
        return projectService.createProject("FSP", "Fast Service Platform", "Backend core demonstration project");
    }

    private static long ensureBoard(long projectId, KanbanServiceImpl kanbanService) {
        Long existingBoardId = findId(
                "SELECT id FROM kanban_board WHERE project_id = ? AND board_name = ?",
                projectId,
                "Delivery Board");
        if (existingBoardId != null) {
            return existingBoardId;
        }
        return kanbanService.createKanban(projectId, "Delivery Board");
    }

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
    }

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
