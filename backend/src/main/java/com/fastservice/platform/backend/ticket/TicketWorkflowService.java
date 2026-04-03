package com.fastservice.platform.backend.ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fastservice.platform.backend.common.db.EntityExistence;
import com.fastservice.platform.backend.common.db.JdbcSupport;
import com.fastservice.platform.backend.common.json.JsonStrings;
import com.fastservice.platform.backend.common.kanban.KanbanStateMachine;

final class TicketWorkflowService {

    String getWorkflow(long ticketId) {
        TicketWorkflowRecord ticket = readTicket(ticketId);
        List<TicketWorkflowHistoryEntry> history = readHistory(ticketId);
        return buildWorkflowJson(ticket, history);
    }

    String executeAction(long ticketId, String actionName, long actorUserId, String comment, Long assigneeUserId) {
        TicketWorkflowAction action = TicketWorkflowAction.from(actionName);
        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException("Workflow comment is required");
        }
        EntityExistence.requireExists("app_user", actorUserId, "Actor user");

        return switch (action) {
        case SUBMIT -> advanceState(ticketId, actorUserId, comment, "IN_PROGRESS", action);
        case APPROVE -> advanceState(ticketId, actorUserId, comment, "DONE", action);
        case REJECT -> reject(ticketId, actorUserId, comment, action);
        case REASSIGN -> reassign(ticketId, actorUserId, comment, assigneeUserId);
        };
    }

    private String advanceState(long ticketId, long actorUserId, String comment, String targetState, TicketWorkflowAction action) {
        TicketWorkflowRecord ticket = readTicket(ticketId);
        KanbanStateMachine.ensureTransition(ticket.state(), targetState);
        return writeStateChange(ticket, actorUserId, comment, action, targetState);
    }

    private String reject(long ticketId, long actorUserId, String comment, TicketWorkflowAction action) {
        TicketWorkflowRecord ticket = readTicket(ticketId);
        if (!"IN_PROGRESS".equalsIgnoreCase(ticket.state())) {
            throw new IllegalArgumentException("Reject is only allowed from IN_PROGRESS");
        }
        return writeStateChange(ticket, actorUserId, comment, action, "TODO");
    }

    private String writeStateChange(
            TicketWorkflowRecord ticket,
            long actorUserId,
            String comment,
            TicketWorkflowAction action,
            String targetState) {
        String normalizedTargetState = targetState.toUpperCase();
        try (Connection connection = JdbcSupport.getConnection()) {
            connection.setAutoCommit(false);
            try {
                updateTicketState(connection, ticket.ticketId(), normalizedTargetState);
                insertHistory(
                        connection,
                        ticket.ticketId(),
                        action.name(),
                        ticket.state(),
                        normalizedTargetState,
                        actorUserId,
                        ticket.assigneeUserId(),
                        ticket.assigneeUserId(),
                        comment);
                connection.commit();
                return normalizedTargetState;
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to update ticket workflow state", e);
        }
    }

    private String reassign(long ticketId, long actorUserId, String comment, Long assigneeUserId) {
        if (assigneeUserId == null) {
            throw new IllegalArgumentException("Workflow reassign action requires a target assignee");
        }
        EntityExistence.requireExists("app_user", assigneeUserId, "Assignee user");

        TicketWorkflowRecord ticket = readTicket(ticketId);
        try (Connection connection = JdbcSupport.getConnection()) {
            connection.setAutoCommit(false);
            try {
                updateTicketAssignee(connection, ticket.ticketId(), assigneeUserId);
                insertHistory(
                        connection,
                        ticket.ticketId(),
                        TicketWorkflowAction.REASSIGN.name(),
                        ticket.state(),
                        ticket.state(),
                        actorUserId,
                        ticket.assigneeUserId(),
                        assigneeUserId,
                        comment);
                connection.commit();
                return "REASSIGNED";
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to reassign ticket workflow", e);
        }
    }

    private void updateTicketState(Connection connection, long ticketId, String targetState) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE ticket SET state = ? WHERE id = ?")) {
            statement.setString(1, targetState);
            statement.setLong(2, ticketId);
            statement.executeUpdate();
        }
    }

    private void updateTicketAssignee(Connection connection, long ticketId, long assigneeUserId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE ticket SET assignee_user_id = ? WHERE id = ?")) {
            statement.setLong(1, assigneeUserId);
            statement.setLong(2, ticketId);
            statement.executeUpdate();
        }
    }

    private void insertHistory(
            Connection connection,
            long ticketId,
            String action,
            String fromState,
            String toState,
            long actorUserId,
            Long previousAssigneeUserId,
            Long nextAssigneeUserId,
            String comment) throws SQLException {
        String sql = """
                INSERT INTO ticket_workflow_history(
                    ticket_id,
                    action,
                    from_state,
                    to_state,
                    actor_user_id,
                    previous_assignee_user_id,
                    next_assignee_user_id,
                    comment
                ) VALUES(?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, ticketId);
            statement.setString(2, action);
            statement.setString(3, fromState);
            statement.setString(4, toState);
            statement.setLong(5, actorUserId);
            if (previousAssigneeUserId == null) {
                statement.setNull(6, java.sql.Types.BIGINT);
            } else {
                statement.setLong(6, previousAssigneeUserId);
            }
            if (nextAssigneeUserId == null) {
                statement.setNull(7, java.sql.Types.BIGINT);
            } else {
                statement.setLong(7, nextAssigneeUserId);
            }
            statement.setString(8, comment);
            statement.executeUpdate();
        }
    }

    private TicketWorkflowRecord readTicket(long ticketId) {
        String sql = """
                SELECT t.id, t.ticket_key, t.title, t.state, t.assignee_user_id, u.username, u.display_name
                FROM ticket t
                JOIN app_user u ON u.id = t.assignee_user_id
                WHERE t.id = ?
                """;
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, ticketId);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("Ticket not found: " + ticketId);
                }
                return new TicketWorkflowRecord(
                        rs.getLong("id"),
                        rs.getString("ticket_key"),
                        rs.getString("title"),
                        rs.getString("state"),
                        rs.getLong("assignee_user_id"),
                        rs.getString("username"),
                        rs.getString("display_name"));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load ticket workflow", e);
        }
    }

    private List<TicketWorkflowHistoryEntry> readHistory(long ticketId) {
        String sql = """
                SELECT h.id,
                       h.action,
                       h.from_state,
                       h.to_state,
                       h.actor_user_id,
                       actor.display_name AS actor_display_name,
                       h.previous_assignee_user_id,
                       prev_user.display_name AS previous_assignee_display_name,
                       h.next_assignee_user_id,
                       next_user.display_name AS next_assignee_display_name,
                       h.comment
                FROM ticket_workflow_history h
                JOIN app_user actor ON actor.id = h.actor_user_id
                LEFT JOIN app_user prev_user ON prev_user.id = h.previous_assignee_user_id
                LEFT JOIN app_user next_user ON next_user.id = h.next_assignee_user_id
                WHERE h.ticket_id = ?
                ORDER BY h.id
                """;
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, ticketId);
            try (ResultSet rs = statement.executeQuery()) {
                List<TicketWorkflowHistoryEntry> entries = new ArrayList<>();
                while (rs.next()) {
                    entries.add(new TicketWorkflowHistoryEntry(
                            rs.getLong("id"),
                            rs.getString("action"),
                            rs.getString("from_state"),
                            rs.getString("to_state"),
                            rs.getLong("actor_user_id"),
                            rs.getString("actor_display_name"),
                            readNullableLong(rs, "previous_assignee_user_id"),
                            rs.getString("previous_assignee_display_name"),
                            readNullableLong(rs, "next_assignee_user_id"),
                            rs.getString("next_assignee_display_name"),
                            rs.getString("comment")));
                }
                return entries;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load ticket workflow history", e);
        }
    }

    private Long readNullableLong(ResultSet rs, String column) throws SQLException {
        long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }

    private String buildWorkflowJson(TicketWorkflowRecord ticket, List<TicketWorkflowHistoryEntry> history) {
        StringBuilder builder = new StringBuilder();
        builder.append('{');
        builder.append("\"ticketId\":").append(ticket.ticketId());
        builder.append(",\"ticketKey\":").append(JsonStrings.quote(ticket.ticketKey()));
        builder.append(",\"title\":").append(JsonStrings.quote(ticket.title()));
        builder.append(",\"state\":").append(JsonStrings.quote(ticket.state()));
        builder.append(",\"assignee\":{");
        builder.append("\"userId\":").append(ticket.assigneeUserId());
        builder.append(",\"username\":").append(JsonStrings.quote(ticket.assigneeUsername()));
        builder.append(",\"displayName\":").append(JsonStrings.quote(ticket.assigneeDisplayName()));
        builder.append('}');
        builder.append(",\"availableActions\":[");
        appendQuotedList(builder, availableActionsFor(ticket.state()));
        builder.append(']');
        builder.append(",\"history\":[");
        for (int i = 0; i < history.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            appendHistoryEntry(builder, history.get(i));
        }
        builder.append(']');
        builder.append('}');
        return builder.toString();
    }

    private List<String> availableActionsFor(String state) {
        return switch (state.toUpperCase()) {
        case "TODO" -> List.of("submit", "reassign");
        case "IN_PROGRESS" -> List.of("approve", "reject", "reassign");
        case "DONE" -> List.of("reassign");
        default -> List.of("reassign");
        };
    }

    private void appendQuotedList(StringBuilder builder, List<String> values) {
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(JsonStrings.quote(values.get(i)));
        }
    }

    private void appendHistoryEntry(StringBuilder builder, TicketWorkflowHistoryEntry entry) {
        builder.append('{');
        builder.append("\"id\":").append(entry.id());
        builder.append(",\"action\":").append(JsonStrings.quote(entry.action()));
        builder.append(",\"fromState\":").append(JsonStrings.quote(entry.fromState()));
        builder.append(",\"toState\":").append(JsonStrings.quote(entry.toState()));
        builder.append(",\"actorUserId\":").append(entry.actorUserId());
        builder.append(",\"actorDisplayName\":").append(JsonStrings.quote(entry.actorDisplayName()));
        builder.append(",\"previousAssigneeUserId\":")
                .append(entry.previousAssigneeUserId() == null ? "null" : entry.previousAssigneeUserId());
        builder.append(",\"previousAssigneeDisplayName\":")
                .append(JsonStrings.quote(entry.previousAssigneeDisplayName()));
        builder.append(",\"nextAssigneeUserId\":")
                .append(entry.nextAssigneeUserId() == null ? "null" : entry.nextAssigneeUserId());
        builder.append(",\"nextAssigneeDisplayName\":")
                .append(JsonStrings.quote(entry.nextAssigneeDisplayName()));
        builder.append(",\"comment\":").append(JsonStrings.quote(entry.comment()));
        builder.append('}');
    }
}
