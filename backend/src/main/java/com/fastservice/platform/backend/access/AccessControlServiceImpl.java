package com.fastservice.platform.backend.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fastservice.platform.backend.common.db.EntityExistence;
import com.fastservice.platform.backend.common.db.JdbcSupport;
import com.fastservice.platform.backend.common.json.JsonStrings;

public class AccessControlServiceImpl {

    // Lealone-generated service executors dispatch to lowercase method names.
    public long createrole(String roleCode, String roleName) {
        return createRole(roleCode, roleName);
    }

    public long createpermission(String permissionCode, String permissionName, String scope) {
        return createPermission(permissionCode, permissionName, scope);
    }

    public void assignpermissiontorole(long roleId, long permissionId) {
        assignPermissionToRole(roleId, permissionId);
    }

    public void assignroletouser(long userId, long roleId) {
        assignRoleToUser(userId, roleId);
    }

    public String listroles() {
        return listRoles();
    }

    public String listpermissions() {
        return listPermissions();
    }

    public String listrolesforuser(long userId) {
        return listRolesForUser(userId);
    }

    public String listpermissionsforrole(long roleId) {
        return listPermissionsForRole(roleId);
    }

    public long createRole(String roleCode, String roleName) {
        String sql = "INSERT INTO app_role(role_code, role_name) VALUES(?, ?)";
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, roleCode);
            statement.setString(2, roleName);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            throw new IllegalStateException("No generated key returned for role insert");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to create role", e);
        }
    }

    public long createPermission(String permissionCode, String permissionName, String scope) {
        String normalizedScope = normalizeScope(scope);
        String sql = "INSERT INTO app_permission(permission_code, permission_name, scope) VALUES(?, ?, ?)";
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, permissionCode);
            statement.setString(2, permissionName);
            statement.setString(3, normalizedScope);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            throw new IllegalStateException("No generated key returned for permission insert");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to create permission", e);
        }
    }

    public void assignPermissionToRole(long roleId, long permissionId) {
        EntityExistence.requireExists("app_role", roleId, "Role");
        EntityExistence.requireExists("app_permission", permissionId, "Permission");
        String sql = "INSERT INTO app_role_permission(role_id, permission_id) VALUES(?, ?)";
        executeLinkInsert(sql, roleId, permissionId, "permission-to-role");
    }

    public void assignRoleToUser(long userId, long roleId) {
        EntityExistence.requireExists("app_user", userId, "User");
        EntityExistence.requireExists("app_role", roleId, "Role");
        String sql = "INSERT INTO app_user_role(user_id, role_id) VALUES(?, ?)";
        executeLinkInsert(sql, userId, roleId, "role-to-user");
    }

    public String listRoles() {
        String sql = "SELECT id, role_code, role_name FROM app_role ORDER BY id";
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
                builder.append(",\"code\":").append(JsonStrings.quote(rs.getString("role_code")));
                builder.append(",\"name\":").append(JsonStrings.quote(rs.getString("role_name"))).append('}');
            }
            builder.append(']');
            return builder.toString();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to list roles", e);
        }
    }

    public String listPermissions() {
        String sql = "SELECT id, permission_code, permission_name, scope FROM app_permission ORDER BY id";
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
                builder.append(",\"code\":").append(JsonStrings.quote(rs.getString("permission_code")));
                builder.append(",\"name\":").append(JsonStrings.quote(rs.getString("permission_name")));
                builder.append(",\"scope\":").append(JsonStrings.quote(rs.getString("scope"))).append('}');
            }
            builder.append(']');
            return builder.toString();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to list permissions", e);
        }
    }

    public String listRolesForUser(long userId) {
        EntityExistence.requireExists("app_user", userId, "User");
        String sql = """
                SELECT r.id, r.role_code, r.role_name
                FROM app_role r
                JOIN app_user_role ur ON ur.role_id = r.id
                WHERE ur.user_id = ?
                ORDER BY r.id
                """;
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                StringBuilder builder = new StringBuilder("[");
                boolean first = true;
                while (rs.next()) {
                    if (!first) {
                        builder.append(',');
                    }
                    first = false;
                    builder.append("{\"id\":").append(rs.getLong("id"));
                    builder.append(",\"code\":").append(JsonStrings.quote(rs.getString("role_code")));
                    builder.append(",\"name\":").append(JsonStrings.quote(rs.getString("role_name"))).append('}');
                }
                builder.append(']');
                return builder.toString();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to list roles for user", e);
        }
    }

    public String listPermissionsForRole(long roleId) {
        String sql = """
                SELECT p.id, p.permission_code, p.permission_name, p.scope
                FROM app_permission p
                JOIN app_role_permission rp ON rp.permission_id = p.id
                WHERE rp.role_id = ?
                ORDER BY p.id
                """;
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, roleId);
            try (ResultSet rs = statement.executeQuery()) {
                StringBuilder builder = new StringBuilder("[");
                boolean first = true;
                while (rs.next()) {
                    if (!first) {
                        builder.append(',');
                    }
                    first = false;
                    builder.append("{\"id\":").append(rs.getLong("id"));
                    builder.append(",\"code\":").append(JsonStrings.quote(rs.getString("permission_code")));
                    builder.append(",\"name\":").append(JsonStrings.quote(rs.getString("permission_name")));
                    builder.append(",\"scope\":").append(JsonStrings.quote(rs.getString("scope"))).append('}');
                }
                builder.append(']');
                return builder.toString();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to list role permissions", e);
        }
    }

    private void executeLinkInsert(String sql, long leftId, long rightId, String relationName) {
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, leftId);
            statement.setLong(2, rightId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to create " + relationName + " link", e);
        }
    }

    private String normalizeScope(String scope) {
        String normalized = scope == null ? "" : scope.trim().toUpperCase();
        if (!"MENU".equals(normalized) && !"FUNCTION".equals(normalized)) {
            throw new IllegalArgumentException("Permission scope must be MENU or FUNCTION");
        }
        return normalized;
    }
}
