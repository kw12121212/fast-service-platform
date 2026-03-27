package com.fastservice.platform.backend.generated.service.executor;

import java.util.Map;

import com.fastservice.platform.backend.user.UserServiceImpl;
import com.lealone.db.service.ServiceExecutor;
import com.lealone.db.value.Value;
import com.lealone.db.value.ValueLong;
import com.lealone.db.value.ValueNull;
import com.lealone.db.value.ValueString;
import com.lealone.plugins.orm.json.JsonArray;

public class UserServiceExecutor implements ServiceExecutor {

    private final UserServiceImpl service = new UserServiceImpl();

    @Override
    public Value executeService(String methodName, Value[] methodArgs) {
        return switch (methodName) {
        case "CREATEUSER" -> {
            Long result = service.createuser(
                    methodArgs[0].getString(),
                    methodArgs[1].getString(),
                    methodArgs[2].getString());
            yield result == null ? ValueNull.INSTANCE : ValueLong.get(result);
        }
        case "LISTUSERS" -> {
            String result = service.listusers();
            yield result == null ? ValueNull.INSTANCE : ValueString.get(result);
        }
        default -> throw noMethodException(methodName);
        };
    }

    @Override
    public Object executeService(String methodName, Map<String, Object> methodArgs) {
        return switch (methodName) {
        case "CREATEUSER" -> service.createuser(
                toString("USERNAME", methodArgs),
                toString("DISPLAYNAME", methodArgs),
                toString("EMAIL", methodArgs));
        case "LISTUSERS" -> service.listusers();
        default -> throw noMethodException(methodName);
        };
    }

    @Override
    public Object executeService(String methodName, String json) {
        return switch (methodName) {
        case "CREATEUSER" -> {
            JsonArray ja = new JsonArray(json);
            yield service.createuser(ja.getString(0), ja.getString(1), ja.getString(2));
        }
        case "LISTUSERS" -> service.listusers();
        default -> throw noMethodException(methodName);
        };
    }
}
