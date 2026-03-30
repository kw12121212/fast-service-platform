package com.fastservice.platform.backend.generated.service.executor;

import java.util.Map;

import com.fastservice.platform.backend.project.ProjectServiceImpl;
import com.lealone.db.service.ServiceExecutor;
import com.lealone.db.value.Value;
import com.lealone.db.value.ValueLong;
import com.lealone.db.value.ValueNull;
import com.lealone.db.value.ValueString;
import com.lealone.orm.json.JsonArray;

public class ProjectServiceExecutor implements ServiceExecutor {

    private final ProjectServiceImpl service = new ProjectServiceImpl();

    @Override
    public Value executeService(String methodName, Value[] methodArgs) {
        return switch (methodName) {
        case "CREATEPROJECT" -> {
            Long result = service.createproject(
                    methodArgs[0].getString(),
                    methodArgs[1].getString(),
                    methodArgs[2].getString());
            yield result == null ? ValueNull.INSTANCE : ValueLong.get(result);
        }
        case "BINDPROJECTREPOSITORY" -> {
            String result = service.bindprojectrepository(
                    methodArgs[0].getLong(),
                    methodArgs[1].getString());
            yield result == null ? ValueNull.INSTANCE : ValueString.get(result);
        }
        case "SWITCHPROJECTBRANCH" -> {
            String result = service.switchprojectbranch(
                    methodArgs[0].getLong(),
                    methodArgs[1].getString());
            yield result == null ? ValueNull.INSTANCE : ValueString.get(result);
        }
        case "CREATEPROJECTWORKTREE" -> {
            String result = service.createprojectworktree(
                    methodArgs[0].getLong(),
                    methodArgs[1].getString());
            yield result == null ? ValueNull.INSTANCE : ValueString.get(result);
        }
        case "MERGEPROJECTWORKTREE" -> {
            String result = service.mergeprojectworktree(
                    methodArgs[0].getLong(),
                    methodArgs[1].getString(),
                    methodArgs[2].getString());
            yield result == null ? ValueNull.INSTANCE : ValueString.get(result);
        }
        case "DELETEPROJECTWORKTREE" -> {
            String result = service.deleteprojectworktree(
                    methodArgs[0].getLong(),
                    methodArgs[1].getString());
            yield result == null ? ValueNull.INSTANCE : ValueString.get(result);
        }
        case "CREATEPROJECTSANDBOXIMAGE" -> {
            String result = service.createprojectsandboximage(
                    methodArgs[0].getLong(),
                    methodArgs[1].getString());
            yield result == null ? ValueNull.INSTANCE : ValueString.get(result);
        }
        case "CREATEPROJECTSANDBOXCONTAINER" -> {
            String result = service.createprojectsandboxcontainer(
                    methodArgs[0].getLong(),
                    methodArgs[1].getString());
            yield result == null ? ValueNull.INSTANCE : ValueString.get(result);
        }
        case "DELETEPROJECTSANDBOXCONTAINER" -> {
            String result = service.deleteprojectsandboxcontainer(
                    methodArgs[0].getLong(),
                    methodArgs[1].getString());
            yield result == null ? ValueNull.INSTANCE : ValueString.get(result);
        }
        case "REPAIRPROJECTWORKTREES" -> {
            String result = service.repairprojectworktrees(methodArgs[0].getLong());
            yield result == null ? ValueNull.INSTANCE : ValueString.get(result);
        }
        case "PRUNEPROJECTWORKTREES" -> {
            String result = service.pruneprojectworktrees(methodArgs[0].getLong());
            yield result == null ? ValueNull.INSTANCE : ValueString.get(result);
        }
        case "LISTPROJECTS" -> {
            String result = service.listprojects();
            yield result == null ? ValueNull.INSTANCE : ValueString.get(result);
        }
        default -> throw noMethodException(methodName);
        };
    }

    @Override
    public Object executeService(String methodName, Map<String, Object> methodArgs) {
        return switch (methodName) {
        case "CREATEPROJECT" -> service.createproject(
                toString("PROJECTKEY", methodArgs),
                toString("PROJECTNAME", methodArgs),
                toString("DESCRIPTION", methodArgs));
        case "BINDPROJECTREPOSITORY" -> service.bindprojectrepository(
                toLong("PROJECTID", methodArgs),
                toString("REPOSITORYPATH", methodArgs));
        case "SWITCHPROJECTBRANCH" -> service.switchprojectbranch(
                toLong("PROJECTID", methodArgs),
                toString("BRANCHNAME", methodArgs));
        case "CREATEPROJECTWORKTREE" -> service.createprojectworktree(
                toLong("PROJECTID", methodArgs),
                toString("BRANCHNAME", methodArgs));
        case "MERGEPROJECTWORKTREE" -> service.mergeprojectworktree(
                toLong("PROJECTID", methodArgs),
                toString("WORKTREEPATH", methodArgs),
                toString("TARGETBRANCH", methodArgs));
        case "DELETEPROJECTWORKTREE" -> service.deleteprojectworktree(
                toLong("PROJECTID", methodArgs),
                toString("WORKTREEPATH", methodArgs));
        case "CREATEPROJECTSANDBOXIMAGE" -> service.createprojectsandboximage(
                toLong("PROJECTID", methodArgs),
                toString("WORKTREEPATH", methodArgs));
        case "CREATEPROJECTSANDBOXCONTAINER" -> service.createprojectsandboxcontainer(
                toLong("PROJECTID", methodArgs),
                toString("WORKTREEPATH", methodArgs));
        case "DELETEPROJECTSANDBOXCONTAINER" -> service.deleteprojectsandboxcontainer(
                toLong("PROJECTID", methodArgs),
                toString("WORKTREEPATH", methodArgs));
        case "REPAIRPROJECTWORKTREES" -> service.repairprojectworktrees(
                toLong("PROJECTID", methodArgs));
        case "PRUNEPROJECTWORKTREES" -> service.pruneprojectworktrees(
                toLong("PROJECTID", methodArgs));
        case "LISTPROJECTS" -> service.listprojects();
        default -> throw noMethodException(methodName);
        };
    }

    @Override
    public Object executeService(String methodName, String json) {
        return switch (methodName) {
        case "CREATEPROJECT" -> {
            JsonArray ja = new JsonArray(json);
            yield service.createproject(ja.getString(0), ja.getString(1), ja.getString(2));
        }
        case "BINDPROJECTREPOSITORY" -> {
            JsonArray ja = new JsonArray(json);
            yield service.bindprojectrepository(ja.getLong(0), ja.getString(1));
        }
        case "SWITCHPROJECTBRANCH" -> {
            JsonArray ja = new JsonArray(json);
            yield service.switchprojectbranch(ja.getLong(0), ja.getString(1));
        }
        case "CREATEPROJECTWORKTREE" -> {
            JsonArray ja = new JsonArray(json);
            yield service.createprojectworktree(ja.getLong(0), ja.getString(1));
        }
        case "MERGEPROJECTWORKTREE" -> {
            JsonArray ja = new JsonArray(json);
            yield service.mergeprojectworktree(ja.getLong(0), ja.getString(1), ja.getString(2));
        }
        case "DELETEPROJECTWORKTREE" -> {
            JsonArray ja = new JsonArray(json);
            yield service.deleteprojectworktree(ja.getLong(0), ja.getString(1));
        }
        case "CREATEPROJECTSANDBOXIMAGE" -> {
            JsonArray ja = new JsonArray(json);
            yield service.createprojectsandboximage(ja.getLong(0), ja.getString(1));
        }
        case "CREATEPROJECTSANDBOXCONTAINER" -> {
            JsonArray ja = new JsonArray(json);
            yield service.createprojectsandboxcontainer(ja.getLong(0), ja.getString(1));
        }
        case "DELETEPROJECTSANDBOXCONTAINER" -> {
            JsonArray ja = new JsonArray(json);
            yield service.deleteprojectsandboxcontainer(ja.getLong(0), ja.getString(1));
        }
        case "REPAIRPROJECTWORKTREES" -> {
            JsonArray ja = new JsonArray(json);
            yield service.repairprojectworktrees(ja.getLong(0));
        }
        case "PRUNEPROJECTWORKTREES" -> {
            JsonArray ja = new JsonArray(json);
            yield service.pruneprojectworktrees(ja.getLong(0));
        }
        case "LISTPROJECTS" -> service.listprojects();
        default -> throw noMethodException(methodName);
        };
    }
}
