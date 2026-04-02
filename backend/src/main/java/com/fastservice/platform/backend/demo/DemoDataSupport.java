package com.fastservice.platform.backend.demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fastservice.platform.backend.common.db.JdbcSupport;
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
                ensureDemoSecurityData(connection);
                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load demo data", e);
        }

        if (ModuleSelection.PROJECT) {
            ProjectServiceImpl projectService = new ProjectServiceImpl();
            long projectId = ensureProject(projectService);

            if (ModuleSelection.KANBAN) {
                KanbanServiceImpl kanbanService = new KanbanServiceImpl();
                long boardId = ensureBoard(projectId, kanbanService);

                if (ModuleSelection.TICKET) {
                    TicketServiceImpl ticketService = new TicketServiceImpl();
                    ensureTicket(projectId, boardId, "FSP-1", "Bootstrap backend core",
                            "Establish the first backend core", "TODO", ticketService);
                    ensureTicket(projectId, boardId, "FSP-2", "Verify demo workflow",
                            "Exercise minimal kanban state flow", "IN_PROGRESS", ticketService);
                }
            }
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

    private static void ensureDemoSecurityData(Connection connection) throws SQLException {
        if (!hasDemoUser(connection)) {
            insertIfMissing(
                    connection,
                    "SELECT COUNT(*) FROM app_user WHERE id = ?",
                    new Object[] { 100L },
                    "INSERT INTO app_user(id, username, display_name, email, enabled) VALUES(?, ?, ?, ?, ?)",
                    new Object[] { 100L, "admin", "Administrator", "admin@fastservice.local", true });
        }

        insertIfMissing(
                connection,
                "SELECT COUNT(*) FROM app_role WHERE id = ?",
                new Object[] { 200L },
                "INSERT INTO app_role(id, role_code, role_name) VALUES(?, ?, ?)",
                new Object[] { 200L, "ADMIN", "Administrator" });

        ensurePermission(connection, 300L, "dashboard:view", "View Dashboard", "MENU");
        ensurePermission(connection, 301L, "project:manage", "Manage Projects", "FUNCTION");
        ensurePermission(connection, 302L, "ticket:manage", "Manage Tickets", "FUNCTION");
        ensurePermission(connection, 303L, "kanban:view", "View Kanban", "MENU");

        insertIfMissing(
                connection,
                "SELECT COUNT(*) FROM app_user_role WHERE user_id = ? AND role_id = ?",
                new Object[] { 100L, 200L },
                "INSERT INTO app_user_role(user_id, role_id) VALUES(?, ?)",
                new Object[] { 100L, 200L });

        ensureRolePermission(connection, 200L, 300L);
        ensureRolePermission(connection, 200L, 301L);
        ensureRolePermission(connection, 200L, 302L);
        ensureRolePermission(connection, 200L, 303L);
    }

    private static void ensurePermission(
            Connection connection,
            long permissionId,
            String permissionCode,
            String permissionName,
            String scope) throws SQLException {
        insertIfMissing(
                connection,
                "SELECT COUNT(*) FROM app_permission WHERE id = ?",
                new Object[] { permissionId },
                "INSERT INTO app_permission(id, permission_code, permission_name, scope) VALUES(?, ?, ?, ?)",
                new Object[] { permissionId, permissionCode, permissionName, scope });
    }

    private static void ensureRolePermission(Connection connection, long roleId, long permissionId) throws SQLException {
        insertIfMissing(
                connection,
                "SELECT COUNT(*) FROM app_role_permission WHERE role_id = ? AND permission_id = ?",
                new Object[] { roleId, permissionId },
                "INSERT INTO app_role_permission(role_id, permission_id) VALUES(?, ?)",
                new Object[] { roleId, permissionId });
    }

    private static void insertIfMissing(
            Connection connection,
            String existenceSql,
            Object[] existenceParameters,
            String insertSql,
            Object[] insertParameters) throws SQLException {
        if (rowExists(connection, existenceSql, existenceParameters)) {
            return;
        }

        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
            bindParameters(statement, insertParameters);
            statement.executeUpdate();
        }
    }

    private static boolean rowExists(Connection connection, String sql, Object... parameters) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bindParameters(statement, parameters);
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                return rs.getLong(1) > 0;
            }
        }
    }

    private static void bindParameters(PreparedStatement statement, Object... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            Object value = parameters[i];
            int parameterIndex = i + 1;
            if (value instanceof Long longValue) {
                statement.setLong(parameterIndex, longValue);
            } else if (value instanceof Boolean booleanValue) {
                statement.setBoolean(parameterIndex, booleanValue);
            } else {
                statement.setString(parameterIndex, String.valueOf(value));
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
