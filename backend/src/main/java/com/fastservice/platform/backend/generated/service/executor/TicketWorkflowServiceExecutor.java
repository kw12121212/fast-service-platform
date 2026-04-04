package com.fastservice.platform.backend.generated.service.executor;

import java.util.Map;

import com.fastservice.platform.backend.ticket.TicketWorkflowServiceImpl;
import com.lealone.db.service.ServiceExecutor;
import com.lealone.db.value.Value;
import com.lealone.db.value.ValueNull;
import com.lealone.db.value.ValueString;
import com.lealone.orm.json.JsonArray;

public class TicketWorkflowServiceExecutor implements ServiceExecutor {

    private final TicketWorkflowServiceImpl service = new TicketWorkflowServiceImpl();

    @Override
    public Value executeService(String methodName, Value[] methodArgs) {
        return switch (methodName) {
        case "GETWORKFLOW" -> {
            String result = service.getworkflow(methodArgs[0].getLong());
            yield result == null ? ValueNull.INSTANCE : ValueString.get(result);
        }
        case "EXECUTEWORKFLOWACTION" -> {
            String result = service.executeworkflowaction(
                    methodArgs[0].getLong(),
                    methodArgs[1].getString(),
                    methodArgs[2].getLong(),
                    methodArgs[3].getString(),
                    methodArgs[4] == ValueNull.INSTANCE ? null : methodArgs[4].getLong());
            yield result == null ? ValueNull.INSTANCE : ValueString.get(result);
        }
        default -> throw noMethodException(methodName);
        };
    }

    @Override
    public Object executeService(String methodName, Map<String, Object> methodArgs) {
        return switch (methodName) {
        case "GETWORKFLOW" -> service.getworkflow(toLong("TICKETID", methodArgs));
        case "EXECUTEWORKFLOWACTION" -> service.executeworkflowaction(
                toLong("TICKETID", methodArgs),
                toString("ACTIONNAME", methodArgs),
                toLong("ACTORUSERID", methodArgs),
                toString("COMMENT", methodArgs),
                methodArgs.get("ASSIGNEEUSERID") == null ? null : Long.valueOf(methodArgs.get("ASSIGNEEUSERID").toString()));
        default -> throw noMethodException(methodName);
        };
    }

    @Override
    public Object executeService(String methodName, String json) {
        return switch (methodName) {
        case "GETWORKFLOW" -> {
            JsonArray ja = new JsonArray(json);
            yield service.getworkflow(Long.valueOf(ja.getValue(0).toString()));
        }
        case "EXECUTEWORKFLOWACTION" -> {
            JsonArray ja = new JsonArray(json);
            yield service.executeworkflowaction(
                    Long.valueOf(ja.getValue(0).toString()),
                    ja.getString(1),
                    Long.valueOf(ja.getValue(2).toString()),
                    ja.getString(3),
                    ja.getValue(4) == null ? null : Long.valueOf(ja.getValue(4).toString()));
        }
        default -> throw noMethodException(methodName);
        };
    }
}
