package com.fastservice.platform.backend.generated.service.executor;

import java.util.Map;

import com.fastservice.platform.backend.kanban.KanbanServiceImpl;
import com.lealone.db.service.ServiceExecutor;
import com.lealone.db.value.Value;
import com.lealone.db.value.ValueLong;
import com.lealone.db.value.ValueNull;
import com.lealone.db.value.ValueString;
import com.lealone.plugins.orm.json.JsonArray;

public class KanbanServiceExecutor implements ServiceExecutor {

    private final KanbanServiceImpl service = new KanbanServiceImpl();

    @Override
    public Value executeService(String methodName, Value[] methodArgs) {
        return switch (methodName) {
        case "CREATEKANBAN" -> {
            Long result = service.createkanban(methodArgs[0].getLong(), methodArgs[1].getString());
            yield result == null ? ValueNull.INSTANCE : ValueLong.get(result);
        }
        case "LISTKANBANSBYPROJECT" -> {
            String result = service.listkanbansbyproject(methodArgs[0].getLong());
            yield result == null ? ValueNull.INSTANCE : ValueString.get(result);
        }
        default -> throw noMethodException(methodName);
        };
    }

    @Override
    public Object executeService(String methodName, Map<String, Object> methodArgs) {
        return switch (methodName) {
        case "CREATEKANBAN" -> service.createkanban(
                toLong("PROJECTID", methodArgs),
                toString("BOARDNAME", methodArgs));
        case "LISTKANBANSBYPROJECT" -> service.listkanbansbyproject(toLong("PROJECTID", methodArgs));
        default -> throw noMethodException(methodName);
        };
    }

    @Override
    public Object executeService(String methodName, String json) {
        return switch (methodName) {
        case "CREATEKANBAN" -> {
            JsonArray ja = new JsonArray(json);
            yield service.createkanban(Long.valueOf(ja.getValue(0).toString()), ja.getString(1));
        }
        case "LISTKANBANSBYPROJECT" -> {
            JsonArray ja = new JsonArray(json);
            yield service.listkanbansbyproject(Long.valueOf(ja.getValue(0).toString()));
        }
        default -> throw noMethodException(methodName);
        };
    }
}
