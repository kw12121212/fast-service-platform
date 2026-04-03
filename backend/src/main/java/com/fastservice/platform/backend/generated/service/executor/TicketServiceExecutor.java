package com.fastservice.platform.backend.generated.service.executor;

import java.util.Map;

import com.fastservice.platform.backend.ticket.TicketServiceImpl;
import com.lealone.db.service.ServiceExecutor;
import com.lealone.db.value.Value;
import com.lealone.db.value.ValueLong;
import com.lealone.db.value.ValueNull;
import com.lealone.db.value.ValueString;
import com.lealone.orm.json.JsonArray;

public class TicketServiceExecutor implements ServiceExecutor {

    private final TicketServiceImpl service = new TicketServiceImpl();

    @Override
    public Value executeService(String methodName, Value[] methodArgs) {
        return switch (methodName) {
        case "CREATETICKET" -> {
            Long result = service.createticket(
                    methodArgs[0].getLong(),
                    methodArgs[1].getLong(),
                    methodArgs[2].getString(),
                    methodArgs[3].getString(),
                    methodArgs[4].getString(),
                    methodArgs[5].getLong());
            yield result == null ? ValueNull.INSTANCE : ValueLong.get(result);
        }
        case "MOVETICKET" -> {
            String result = service.moveticket(methodArgs[0].getLong(), methodArgs[1].getString());
            yield result == null ? ValueNull.INSTANCE : ValueString.get(result);
        }
        case "LISTTICKETSBYPROJECT" -> {
            String result = service.listticketsbyproject(methodArgs[0].getLong());
            yield result == null ? ValueNull.INSTANCE : ValueString.get(result);
        }
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
        case "CREATETICKET" -> service.createticket(
                toLong("PROJECTID", methodArgs),
                toLong("KANBANID", methodArgs),
                toString("TICKETKEY", methodArgs),
                toString("TITLE", methodArgs),
                toString("DESCRIPTION", methodArgs),
                toLong("ASSIGNEEUSERID", methodArgs));
        case "MOVETICKET" -> service.moveticket(
                toLong("TICKETID", methodArgs),
                toString("TARGETSTATE", methodArgs));
        case "LISTTICKETSBYPROJECT" -> service.listticketsbyproject(toLong("PROJECTID", methodArgs));
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
        case "CREATETICKET" -> {
            JsonArray ja = new JsonArray(json);
            yield service.createticket(
                    Long.valueOf(ja.getValue(0).toString()),
                    Long.valueOf(ja.getValue(1).toString()),
                    ja.getString(2),
                    ja.getString(3),
                    ja.getString(4),
                    Long.valueOf(ja.getValue(5).toString()));
        }
        case "MOVETICKET" -> {
            JsonArray ja = new JsonArray(json);
            yield service.moveticket(
                    Long.valueOf(ja.getValue(0).toString()),
                    ja.getString(1));
        }
        case "LISTTICKETSBYPROJECT" -> {
            JsonArray ja = new JsonArray(json);
            yield service.listticketsbyproject(Long.valueOf(ja.getValue(0).toString()));
        }
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
