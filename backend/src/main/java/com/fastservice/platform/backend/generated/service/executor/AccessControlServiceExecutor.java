package com.fastservice.platform.backend.generated.service.executor;

import java.util.Map;

import com.fastservice.platform.backend.access.AccessControlServiceImpl;
import com.lealone.db.service.ServiceExecutor;
import com.lealone.db.value.Value;
import com.lealone.db.value.ValueLong;
import com.lealone.db.value.ValueNull;
import com.lealone.db.value.ValueString;
import com.lealone.plugins.orm.json.JsonArray;

public class AccessControlServiceExecutor implements ServiceExecutor {

    private final AccessControlServiceImpl service = new AccessControlServiceImpl();

    @Override
    public Value executeService(String methodName, Value[] methodArgs) {
        return switch (methodName) {
        case "CREATEROLE" -> {
            Long result = service.createrole(methodArgs[0].getString(), methodArgs[1].getString());
            yield result == null ? ValueNull.INSTANCE : ValueLong.get(result);
        }
        case "CREATEPERMISSION" -> {
            Long result = service.createpermission(
                    methodArgs[0].getString(),
                    methodArgs[1].getString(),
                    methodArgs[2].getString());
            yield result == null ? ValueNull.INSTANCE : ValueLong.get(result);
        }
        case "ASSIGNPERMISSIONTOROLE" -> {
            service.assignpermissiontorole(methodArgs[0].getLong(), methodArgs[1].getLong());
            yield ValueNull.INSTANCE;
        }
        case "ASSIGNROLETOUSER" -> {
            service.assignroletouser(methodArgs[0].getLong(), methodArgs[1].getLong());
            yield ValueNull.INSTANCE;
        }
        case "LISTPERMISSIONSFORROLE" -> {
            String result = service.listpermissionsforrole(methodArgs[0].getLong());
            yield result == null ? ValueNull.INSTANCE : ValueString.get(result);
        }
        default -> throw noMethodException(methodName);
        };
    }

    @Override
    public Object executeService(String methodName, Map<String, Object> methodArgs) {
        return switch (methodName) {
        case "CREATEROLE" -> service.createrole(
                toString("ROLECODE", methodArgs),
                toString("ROLENAME", methodArgs));
        case "CREATEPERMISSION" -> service.createpermission(
                toString("PERMISSIONCODE", methodArgs),
                toString("PERMISSIONNAME", methodArgs),
                toString("SCOPE", methodArgs));
        case "ASSIGNPERMISSIONTOROLE" -> {
            service.assignpermissiontorole(
                    toLong("ROLEID", methodArgs),
                    toLong("PERMISSIONID", methodArgs));
            yield NO_RETURN_VALUE;
        }
        case "ASSIGNROLETOUSER" -> {
            service.assignroletouser(
                    toLong("USERID", methodArgs),
                    toLong("ROLEID", methodArgs));
            yield NO_RETURN_VALUE;
        }
        case "LISTPERMISSIONSFORROLE" -> service.listpermissionsforrole(toLong("ROLEID", methodArgs));
        default -> throw noMethodException(methodName);
        };
    }

    @Override
    public Object executeService(String methodName, String json) {
        return switch (methodName) {
        case "CREATEROLE" -> {
            JsonArray ja = new JsonArray(json);
            yield service.createrole(ja.getString(0), ja.getString(1));
        }
        case "CREATEPERMISSION" -> {
            JsonArray ja = new JsonArray(json);
            yield service.createpermission(ja.getString(0), ja.getString(1), ja.getString(2));
        }
        case "ASSIGNPERMISSIONTOROLE" -> {
            JsonArray ja = new JsonArray(json);
            service.assignpermissiontorole(
                    Long.valueOf(ja.getValue(0).toString()),
                    Long.valueOf(ja.getValue(1).toString()));
            yield NO_RETURN_VALUE;
        }
        case "ASSIGNROLETOUSER" -> {
            JsonArray ja = new JsonArray(json);
            service.assignroletouser(
                    Long.valueOf(ja.getValue(0).toString()),
                    Long.valueOf(ja.getValue(1).toString()));
            yield NO_RETURN_VALUE;
        }
        case "LISTPERMISSIONSFORROLE" -> {
            JsonArray ja = new JsonArray(json);
            yield service.listpermissionsforrole(Long.valueOf(ja.getValue(0).toString()));
        }
        default -> throw noMethodException(methodName);
        };
    }
}
