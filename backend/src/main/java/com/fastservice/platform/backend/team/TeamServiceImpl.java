package com.fastservice.platform.backend.team;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fastservice.platform.backend.common.db.EntityExistence;
import com.fastservice.platform.backend.common.db.JdbcSupport;
import com.fastservice.platform.backend.common.json.JsonStrings;

public class TeamServiceImpl {

    public long createteam(String name, String description) {
        return createTeam(name, description);
    }

    public void updateteam(long teamId, String name, String description) {
        updateTeam(teamId, name, description);
    }

    public void deleteteam(long teamId) {
        deleteTeam(teamId);
    }

    public String listteams() {
        return listTeams();
    }

    public long addmember(long teamId, long userId) {
        return addMember(teamId, userId);
    }

    public void removemember(long memberId) {
        removeMember(memberId);
    }

    public String listmembers(long teamId) {
        return listMembers(teamId);
    }

    public long bindproject(long teamId, long projectId) {
        return bindProject(teamId, projectId);
    }

    public void unbindproject(long bindingId) {
        unbindProject(bindingId);
    }

    public String listprojectteams(long projectId) {
        return listProjectTeams(projectId);
    }

    public void assignteamrole(long memberId, long roleId) {
        assignTeamRole(memberId, roleId);
    }

    public void removeteamrole(long memberId, long roleId) {
        removeTeamRole(memberId, roleId);
    }

    public long createTeam(String name, String description) {
        String sql = "INSERT INTO team(name, description, status) VALUES(?, ?, 'Active')";
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            throw new IllegalStateException("No generated key returned for team insert");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to create team", e);
        }
    }

    public void updateTeam(long teamId, String name, String description) {
        EntityExistence.requireExists("team", teamId, "Team");
        String sql = "UPDATE team SET name = ?, description = ? WHERE id = ?";
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setLong(3, teamId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to update team", e);
        }
    }

    public void deleteTeam(long teamId) {
        EntityExistence.requireExists("team", teamId, "Team");
        try (Connection connection = JdbcSupport.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM team_member_role WHERE team_member_id IN (SELECT id FROM team_member WHERE team_id = ?)")) {
                statement.setLong(1, teamId);
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM team_member WHERE team_id = ?")) {
                statement.setLong(1, teamId);
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM team_project_binding WHERE team_id = ?")) {
                statement.setLong(1, teamId);
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM team WHERE id = ?")) {
                statement.setLong(1, teamId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to delete team", e);
        }
    }

    public String listTeams() {
        String sql = "SELECT t.id, t.name, t.description, t.status, COALESCE(mc.cnt, 0) AS member_count"
                + " FROM team t LEFT JOIN (SELECT team_id, COUNT(*) AS cnt FROM team_member GROUP BY team_id) mc ON t.id = mc.team_id"
                + " ORDER BY t.id";
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
                builder.append(",\"name\":").append(JsonStrings.quote(rs.getString("name")));
                builder.append(",\"description\":").append(JsonStrings.quote(rs.getString("description")));
                builder.append(",\"status\":").append(JsonStrings.quote(rs.getString("status")));
                builder.append(",\"memberCount\":").append(rs.getLong("member_count")).append('}');
            }
            builder.append(']');
            return builder.toString();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to list teams", e);
        }
    }

    public long addMember(long teamId, long userId) {
        EntityExistence.requireExists("team", teamId, "Team");
        EntityExistence.requireExists("app_user", userId, "User");
        String sql = "INSERT INTO team_member(team_id, user_id) VALUES(?, ?)";
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, teamId);
            statement.setLong(2, userId);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            throw new IllegalStateException("No generated key returned for team member insert");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to add team member", e);
        }
    }

    public void removeMember(long memberId) {
        EntityExistence.requireExists("team_member", memberId, "Team member");
        try (Connection connection = JdbcSupport.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM team_member_role WHERE team_member_id = ?")) {
                statement.setLong(1, memberId);
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM team_member WHERE id = ?")) {
                statement.setLong(1, memberId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to remove team member", e);
        }
    }

    public String listMembers(long teamId) {
        EntityExistence.requireExists("team", teamId, "Team");
        String sql = "SELECT tm.id AS member_id, u.id AS user_id, u.username, u.display_name, u.email"
                + " FROM team_member tm JOIN app_user u ON tm.user_id = u.id"
                + " WHERE tm.team_id = ?"
                + " ORDER BY tm.id";
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, teamId);
            StringBuilder builder = new StringBuilder("[");
            boolean first = true;
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    if (!first) {
                        builder.append(',');
                    }
                    first = false;
                    long memberId = rs.getLong("member_id");
                    builder.append("{\"memberId\":").append(memberId);
                    builder.append(",\"userId\":").append(rs.getLong("user_id"));
                    builder.append(",\"username\":").append(JsonStrings.quote(rs.getString("username")));
                    builder.append(",\"displayName\":").append(JsonStrings.quote(rs.getString("display_name")));
                    builder.append(",\"email\":").append(JsonStrings.quote(rs.getString("email")));
                    builder.append(",\"roles\":").append(loadMemberRoles(connection, memberId));
                    builder.append('}');
                }
            }
            builder.append(']');
            return builder.toString();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to list team members", e);
        }
    }

    public long bindProject(long teamId, long projectId) {
        EntityExistence.requireExists("team", teamId, "Team");
        EntityExistence.requireExists("software_project", projectId, "Project");
        String sql = "INSERT INTO team_project_binding(team_id, project_id) VALUES(?, ?)";
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, teamId);
            statement.setLong(2, projectId);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            throw new IllegalStateException("No generated key returned for team project binding insert");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to bind team to project", e);
        }
    }

    public void unbindProject(long bindingId) {
        EntityExistence.requireExists("team_project_binding", bindingId, "Team project binding");
        String sql = "DELETE FROM team_project_binding WHERE id = ?";
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, bindingId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to unbind team from project", e);
        }
    }

    public String listProjectTeams(long projectId) {
        EntityExistence.requireExists("software_project", projectId, "Project");
        String sql = "SELECT t.id, t.name, t.description, t.status"
                + " FROM team t JOIN team_project_binding tpb ON t.id = tpb.team_id"
                + " WHERE tpb.project_id = ?"
                + " ORDER BY t.id";
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, projectId);
            StringBuilder builder = new StringBuilder("[");
            boolean first = true;
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    if (!first) {
                        builder.append(',');
                    }
                    first = false;
                    builder.append("{\"id\":").append(rs.getLong("id"));
                    builder.append(",\"name\":").append(JsonStrings.quote(rs.getString("name")));
                    builder.append(",\"description\":").append(JsonStrings.quote(rs.getString("description")));
                    builder.append(",\"status\":").append(JsonStrings.quote(rs.getString("status")));
                    builder.append('}');
                }
            }
            builder.append(']');
            return builder.toString();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to list project teams", e);
        }
    }

    public void assignTeamRole(long memberId, long roleId) {
        EntityExistence.requireExists("team_member", memberId, "Team member");
        EntityExistence.requireExists("team_role", roleId, "Team role");
        String sql = "INSERT INTO team_member_role(team_member_id, team_role_id) VALUES(?, ?)";
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, memberId);
            statement.setLong(2, roleId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to assign team role", e);
        }
    }

    public void removeTeamRole(long memberId, long roleId) {
        String sql = "DELETE FROM team_member_role WHERE team_member_id = ? AND team_role_id = ?";
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, memberId);
            statement.setLong(2, roleId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to remove team role", e);
        }
    }

    private String loadMemberRoles(Connection connection, long memberId) throws SQLException {
        String sql = "SELECT tr.role_code, tr.role_name FROM team_role tr"
                + " JOIN team_member_role tmr ON tr.id = tmr.team_role_id"
                + " WHERE tmr.team_member_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, memberId);
            StringBuilder builder = new StringBuilder("[");
            boolean first = true;
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    if (!first) {
                        builder.append(',');
                    }
                    first = false;
                    builder.append("{\"roleCode\":").append(JsonStrings.quote(rs.getString("role_code")));
                    builder.append(",\"roleName\":").append(JsonStrings.quote(rs.getString("role_name")));
                    builder.append('}');
                }
            }
            builder.append(']');
            return builder.toString();
        }
    }
}
