package com.fastservice.platform.backend.generated.service.executor;

import java.util.Map;

import com.fastservice.platform.backend.common.kanban.KanbanStateMachineServiceImpl;
import com.lealone.db.service.ServiceExecutor;
import com.lealone.db.value.Value;
import com.lealone.db.value.ValueBoolean;
import com.lealone.db.value.ValueNull;
import com.lealone.orm.json.JsonArray;

public class KanbanStateMachineServiceExecutor implements ServiceExecutor {

    private final KanbanStateMachineServiceImpl service = new KanbanStateMachineServiceImpl();

    @Override
    public Value executeService(String methodName, Value[] methodArgs) {
        return switch (methodName) {
        case "ISTRANSITIONALLOWED" -> {
            boolean result = service.istransitionallowed(methodArgs[0].getString(), methodArgs[1].getString());
            yield ValueBoolean.get(result);
        }
        case "ENSURETRANSITION" -> {
            service.ensuretransition(methodArgs[0].getString(), methodArgs[1].getString());
            yield ValueNull.INSTANCE;
        }
        default -> throw noMethodException(methodName);
        };
    }

    @Override
    public Object executeService(String methodName, Map<String, Object> methodArgs) {
        return switch (methodName) {
        case "ISTRANSITIONALLOWED" -> service.istransitionallowed(
                toString("FROMSTATE", methodArgs),
                toString("TOSTATE", methodArgs));
        case "ENSURETRANSITION" -> {
            service.ensuretransition(
                    toString("FROMSTATE", methodArgs),
                    toString("TOSTATE", methodArgs));
            yield null;
        }
        default -> throw noMethodException(methodName);
        };
    }

    @Override
    public Object executeService(String methodName, String json) {
        return switch (methodName) {
        case "ISTRANSITIONALLOWED" -> {
            JsonArray ja = new JsonArray(json);
            yield service.istransitionallowed(ja.getString(0), ja.getString(1));
        }
        case "ENSURETRANSITION" -> {
            JsonArray ja = new JsonArray(json);
            service.ensuretransition(ja.getString(0), ja.getString(1));
            yield null;
        }
        default -> throw noMethodException(methodName);
        };
    }
}
