package com.fastservice.platform.backend.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fastservice.platform.backend.common.db.JdbcSupport;
import com.fastservice.platform.backend.common.json.SimpleJson;

final class ProjectDerivedAppUpgradeSupportManager {

    private static final String STATUS_AVAILABLE = "AVAILABLE";
    private static final String STATUS_RESTRICTED = "RESTRICTED";
    private static final String OUTCOME_SUCCESS = "SUCCESS";
    private static final String OUTCOME_FAILED = "FAILED";
    private static final String CATEGORY_VALIDATION = "REQUEST_VALIDATION";
    private static final String CATEGORY_EXECUTION = "UPGRADE_EXECUTION";
    private static final String SOURCE_CONTEXT_TYPE = "BOUND_MAIN_REPOSITORY";
    private static final String TARGET_CONTEXT_TYPE = "LATEST_SUCCESSFUL_ASSEMBLY_OUTPUT";
    private static final String REQUEST_SUPPORTED_TARGETS = "SUPPORTED_TARGETS";
    private static final String REQUEST_ADVISORY = "ADVISORY";
    private static final String REQUEST_EVALUATE = "EVALUATE";
    private static final String REQUEST_DRY_RUN_EXECUTE = "DRY_RUN_EXECUTE";

    String readUpgradeSupportContext(long projectId, String repositoryRootPath) {
        UpgradeSupportRecord persisted = readRecord(projectId);
        return buildContextJson(projectId, repositoryRootPath, persisted);
    }

    String requestUpgradeSupport(
            long projectId,
            String repositoryRootPath,
            String requestType,
            String targetReleaseId) {
        String sourceRepositoryPath = normalizeAbsolutePath(repositoryRootPath, "Repository path").toString();
        AssemblyTarget target = resolveAssemblyTarget(projectId);
        RepoRootResolution repoRoot = resolveRepoRoot();

        RequestInput requestInput;
        try {
            requestInput = validateRequest(requestType, targetReleaseId);
        } catch (IllegalArgumentException error) {
            persistOutcome(projectId, new UpgradeSupportRecord(
                    STATUS_RESTRICTED,
                    true,
                    target.restriction(),
                    sourceRepositoryPath,
                    target.outputDirectory(),
                    OUTCOME_FAILED,
                    CATEGORY_VALIDATION,
                    requestType,
                    error.getMessage(),
                    targetReleaseId,
                    null,
                    Instant.now().toString()));
            throw error;
        }

        String restriction = buildRestriction(repositoryRootPath, repoRoot, target);
        if (restriction != null) {
            persistOutcome(projectId, new UpgradeSupportRecord(
                    STATUS_RESTRICTED,
                    true,
                    restriction,
                    sourceRepositoryPath,
                    target.outputDirectory(),
                    OUTCOME_FAILED,
                    CATEGORY_VALIDATION,
                    requestInput.requestType(),
                    restriction,
                    targetReleaseId,
                    null,
                    Instant.now().toString()));
            throw new IllegalStateException(restriction);
        }

        Path targetDirectory = normalizeAbsolutePath(target.outputDirectory(), "Latest successful assembly output directory");
        Map<String, Object> supportedTargets = runAndParseUpgradeCommand(
                repoRoot.root(),
                "upgrade",
                "targets",
                targetDirectory,
                null,
                "Unable to start repository-owned upgrade target lookup");

        String selectedTargetReleaseId;
        try {
            selectedTargetReleaseId = resolveSelectedTargetReleaseId(requestInput.requestType(), targetReleaseId, supportedTargets);
        } catch (IllegalArgumentException error) {
            persistOutcome(projectId, new UpgradeSupportRecord(
                    STATUS_AVAILABLE,
                    false,
                    null,
                    sourceRepositoryPath,
                    targetDirectory.toString(),
                    OUTCOME_FAILED,
                    CATEGORY_VALIDATION,
                    requestInput.requestType(),
                    error.getMessage(),
                    targetReleaseId,
                    null,
                    Instant.now().toString()));
            throw error;
        }

        try {
            Map<String, Object> payload = switch (requestInput.requestType()) {
                case REQUEST_SUPPORTED_TARGETS -> supportedTargets;
                case REQUEST_ADVISORY -> runAndParseUpgradeCommand(
                        repoRoot.root(),
                        "upgrade",
                        "advisory",
                        targetDirectory,
                        null,
                        "Unable to start repository-owned release advisory lookup");
                case REQUEST_EVALUATE -> runAndParseUpgradeCommand(
                        repoRoot.root(),
                        "upgrade",
                        "evaluate",
                        targetDirectory,
                        null,
                        "Unable to start repository-owned upgrade evaluation");
                case REQUEST_DRY_RUN_EXECUTE -> runAndParseUpgradeCommand(
                        repoRoot.root(),
                        "upgrade",
                        "execute",
                        targetDirectory,
                        selectedTargetReleaseId,
                        "Unable to start repository-owned upgrade dry-run execution");
                default -> throw new IllegalArgumentException("Unsupported project upgrade support request type: "
                        + requestInput.requestType());
            };

            persistOutcome(projectId, new UpgradeSupportRecord(
                    STATUS_AVAILABLE,
                    false,
                    null,
                    sourceRepositoryPath,
                    targetDirectory.toString(),
                    OUTCOME_SUCCESS,
                    CATEGORY_EXECUTION,
                    requestInput.requestType(),
                    successMessage(requestInput.requestType()),
                    selectedTargetReleaseId,
                    SimpleJson.stringify(payload),
                    Instant.now().toString()));
            return buildContextJson(projectId, repositoryRootPath, readRecord(projectId));
        } catch (IllegalStateException error) {
            persistOutcome(projectId, new UpgradeSupportRecord(
                    STATUS_AVAILABLE,
                    false,
                    null,
                    sourceRepositoryPath,
                    targetDirectory.toString(),
                    OUTCOME_FAILED,
                    CATEGORY_EXECUTION,
                    requestInput.requestType(),
                    error.getMessage(),
                    selectedTargetReleaseId,
                    null,
                    Instant.now().toString()));
            throw error;
        }
    }

    private String buildContextJson(long projectId, String repositoryRootPath, UpgradeSupportRecord persisted) {
        Map<String, Object> payload = new LinkedHashMap<>();
        RepoRootResolution repoRoot = resolveRepoRoot();
        AssemblyTarget target = resolveAssemblyTarget(projectId);
        boolean repositoryBound = repositoryRootPath != null && !repositoryRootPath.isBlank();
        boolean restricted = !repositoryBound || repoRoot.root() == null || target.outputDirectory() == null || target.restriction() != null;
        payload.put("available", !restricted);
        payload.put("status", restricted ? STATUS_RESTRICTED : STATUS_AVAILABLE);
        payload.put("restricted", restricted);
        payload.put("restriction", buildRestriction(repositoryRootPath, repoRoot, target));
        payload.put("sourceRepositoryPath", repositoryBound ? normalizeAbsolutePath(repositoryRootPath, "Repository path").toString() : null);
        payload.put("sourceContext", Map.of("type", SOURCE_CONTEXT_TYPE));
        Map<String, Object> targetContext = new LinkedHashMap<>();
        targetContext.put("type", TARGET_CONTEXT_TYPE);
        targetContext.put("outputDirectory", target.outputDirectory());
        payload.put("targetContext", targetContext);
        payload.put("supportedRequestTypes", List.of(
                REQUEST_SUPPORTED_TARGETS,
                REQUEST_ADVISORY,
                REQUEST_EVALUATE,
                REQUEST_DRY_RUN_EXECUTE));
        payload.put("latestOutcome", latestOutcomeMap(persisted));
        return SimpleJson.stringify(payload);
    }

    private String buildRestriction(String repositoryRootPath, RepoRootResolution repoRoot, AssemblyTarget target) {
        if (repositoryRootPath == null || repositoryRootPath.isBlank()) {
            return "Bind a repository first to run project-scoped derived-app upgrade support.";
        }
        if (repoRoot.root() == null) {
            return repoRoot.restriction();
        }
        return target.restriction();
    }

    private Map<String, Object> latestOutcomeMap(UpgradeSupportRecord persisted) {
        if (persisted == null || persisted.latestOutcomeStatus() == null || persisted.latestOutcomeStatus().isBlank()) {
            return null;
        }
        Map<String, Object> latestOutcome = new LinkedHashMap<>();
        latestOutcome.put("status", persisted.latestOutcomeStatus());
        latestOutcome.put("category", persisted.latestOutcomeCategory());
        latestOutcome.put("requestType", persisted.latestRequestType());
        latestOutcome.put("message", persisted.latestOutcomeMessage());
        latestOutcome.put("targetReleaseId", persisted.latestTargetReleaseId());
        latestOutcome.put("targetOutputDirectory", persisted.latestTargetOutputDirectory());
        latestOutcome.put("result", parsePersistedJson(persisted.latestResultJson()));
        latestOutcome.put("updatedAt", persisted.updatedAt());
        return latestOutcome;
    }

    private Object parsePersistedJson(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        return SimpleJson.parse(json);
    }

    private RequestInput validateRequest(String requestType, String targetReleaseId) {
        if (requestType == null || requestType.isBlank()) {
            throw new IllegalArgumentException("Project upgrade support requestType is required");
        }
        return switch (requestType) {
            case REQUEST_SUPPORTED_TARGETS, REQUEST_ADVISORY, REQUEST_EVALUATE -> new RequestInput(requestType);
            case REQUEST_DRY_RUN_EXECUTE -> {
                if (targetReleaseId == null || targetReleaseId.isBlank()) {
                    throw new IllegalArgumentException("Dry-run upgrade planning requires a targetReleaseId");
                }
                yield new RequestInput(requestType);
            }
            default -> throw new IllegalArgumentException("Unsupported project upgrade support requestType: " + requestType);
        };
    }

    private String resolveSelectedTargetReleaseId(String requestType, String requestedTargetReleaseId, Map<String, Object> supportedTargets) {
        if (REQUEST_DRY_RUN_EXECUTE.equals(requestType)) {
            if (requestedTargetReleaseId == null || requestedTargetReleaseId.isBlank()) {
                throw new IllegalArgumentException("Dry-run upgrade planning requires a targetReleaseId");
            }
            List<Map<String, Object>> availableTargetReleases = asMapList(supportedTargets.get("availableTargetReleases"));
            boolean found = availableTargetReleases.stream()
                    .anyMatch(entry -> requestedTargetReleaseId.equals(asString(entry.get("releaseId"))));
            if (!found) {
                throw new IllegalArgumentException("Target release is not supported for this project-derived app: "
                        + requestedTargetReleaseId);
            }
            return requestedTargetReleaseId;
        }
        Object defaultTargetReleaseId = supportedTargets.get("defaultTargetReleaseId");
        return defaultTargetReleaseId instanceof String value && !value.isBlank() ? value : null;
    }

    private static String successMessage(String requestType) {
        return switch (requestType) {
            case REQUEST_SUPPORTED_TARGETS -> "Supported target releases were loaded through repository-owned tooling.";
            case REQUEST_ADVISORY -> "Release advisory guidance was loaded through repository-owned tooling.";
            case REQUEST_EVALUATE -> "Upgrade compatibility evaluation completed through repository-owned tooling.";
            case REQUEST_DRY_RUN_EXECUTE -> "Upgrade dry-run planning completed through repository-owned tooling.";
            default -> "Project-scoped upgrade support request completed through repository-owned tooling.";
        };
    }

    private Map<String, Object> runAndParseUpgradeCommand(
            Path repoRoot,
            String group,
            String command,
            Path targetDirectory,
            String extraArg,
            String startupFailureMessage) {
        CommandResult result = runPlatformTool(repoRoot, group, command, targetDirectory, extraArg, startupFailureMessage);
        if (result.exitCode() != 0) {
            throw new IllegalStateException(normalizeProcessMessage(result.output(), "Repository-owned upgrade tooling failed"));
        }
        String jsonPayload = extractJsonPayload(result.output());
        try {
            return SimpleJson.parseObject(jsonPayload);
        } catch (IllegalArgumentException error) {
            throw new IllegalStateException("Repository-owned upgrade tooling returned a non-JSON result", error);
        }
    }

    private String extractJsonPayload(String output) {
        if (output == null || output.isBlank()) {
            throw new IllegalStateException("Repository-owned upgrade tooling returned no output");
        }
        String trimmed = output.trim();
        int objectStart = trimmed.indexOf('{');
        int objectEnd = trimmed.lastIndexOf('}');
        if (objectStart < 0 || objectEnd < objectStart) {
            throw new IllegalStateException("Repository-owned upgrade tooling returned no JSON object");
        }
        return trimmed.substring(objectStart, objectEnd + 1);
    }

    private AssemblyTarget resolveAssemblyTarget(long projectId) {
        String sql = """
                SELECT latest_outcome_status,
                       latest_output_directory
                FROM project_derived_app_assembly
                WHERE project_id = ?
                """;
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, projectId);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return new AssemblyTarget(null, "Run project-scoped derived-app assembly successfully before requesting upgrade support.");
                }
                String latestOutcomeStatus = rs.getString("latest_outcome_status");
                String latestSuccessfulOutputDirectory = rs.getString("latest_output_directory");
                if (!OUTCOME_SUCCESS.equals(latestOutcomeStatus)
                        || latestSuccessfulOutputDirectory == null
                        || latestSuccessfulOutputDirectory.isBlank()) {
                    return new AssemblyTarget(null, "Run project-scoped derived-app assembly successfully before requesting upgrade support.");
                }
                Path targetDirectory;
                try {
                    targetDirectory = normalizeAbsolutePath(latestSuccessfulOutputDirectory, "Latest successful assembly output directory");
                } catch (IllegalArgumentException error) {
                    return new AssemblyTarget(latestSuccessfulOutputDirectory, error.getMessage());
                }
                if (!Files.isDirectory(targetDirectory)) {
                    return new AssemblyTarget(
                            targetDirectory.toString(),
                            "Latest successful project-scoped assembly output is no longer available: " + targetDirectory);
                }
                return new AssemblyTarget(targetDirectory.toString(), null);
            }
        } catch (SQLException error) {
            throw new IllegalStateException("Unable to read latest successful project derived-app assembly output", error);
        }
    }

    private static Path normalizeAbsolutePath(String pathValue, String label) {
        if (pathValue == null || pathValue.isBlank()) {
            throw new IllegalArgumentException(label + " is required");
        }
        Path path = Path.of(pathValue).normalize();
        if (!path.isAbsolute()) {
            throw new IllegalArgumentException(label + " must be absolute: " + pathValue);
        }
        return path.toAbsolutePath();
    }

    private static RepoRootResolution resolveRepoRoot() {
        String configured = System.getProperty("fsp.repo-root", System.getenv("FSP_REPO_ROOT"));
        if (configured != null && !configured.isBlank()) {
            Path repoRoot = Path.of(configured).toAbsolutePath().normalize();
            if (!Files.exists(repoRoot.resolve("scripts/platform-tool.sh"))) {
                return new RepoRootResolution(
                        null,
                        "Configured repository root does not contain scripts/platform-tool.sh: " + repoRoot);
            }
            return new RepoRootResolution(repoRoot, null);
        }

        Path current = Path.of("").toAbsolutePath().normalize();
        while (current != null) {
            if (Files.exists(current.resolve("scripts/platform-tool.sh"))) {
                return new RepoRootResolution(current, null);
            }
            current = current.getParent();
        }
        return new RepoRootResolution(
                null,
                "Repository-owned upgrade tooling is not available from the current backend runtime.");
    }

    private static CommandResult runPlatformTool(
            Path repoRoot,
            String group,
            String command,
            Path targetDirectory,
            String targetReleaseId,
            String startupFailureMessage) {
        try {
            ProcessBuilder processBuilder;
            if (targetDirectory == null) {
                processBuilder = new ProcessBuilder(
                        "bash",
                        repoRoot.resolve("scripts/platform-tool.sh").toString(),
                        group,
                        command);
            } else {
                processBuilder = new ProcessBuilder(
                        "bash",
                        repoRoot.resolve("scripts/platform-tool.sh").toString(),
                        group,
                        command,
                        targetDirectory.toString());
            }
            processBuilder.directory(repoRoot.toFile());
            processBuilder.redirectErrorStream(true);
            if (targetReleaseId != null && !targetReleaseId.isBlank()) {
                processBuilder.environment().put("FSP_TARGET_RELEASE_ID", targetReleaseId);
            } else {
                processBuilder.environment().remove("FSP_TARGET_RELEASE_ID");
            }
            Process process = processBuilder.start();
            String output = new String(process.getInputStream().readAllBytes()).trim();
            int exitCode = process.waitFor();
            return new CommandResult(exitCode, output);
        } catch (IOException error) {
            throw new IllegalStateException(startupFailureMessage, error);
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for repository-owned upgrade tooling", error);
        }
    }

    private static String normalizeProcessMessage(String output, String fallback) {
        if (output == null || output.isBlank()) {
            return fallback;
        }
        return output;
    }

    private UpgradeSupportRecord readRecord(long projectId) {
        String sql = """
                SELECT status,
                       restricted,
                       restriction,
                       source_repository_path,
                       latest_target_output_directory,
                       latest_outcome_status,
                       latest_outcome_category,
                       latest_request_type,
                       latest_outcome_message,
                       latest_target_release_id,
                       latest_result_json,
                       updated_at
                FROM project_derived_app_upgrade_support
                WHERE project_id = ?
                """;
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, projectId);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new UpgradeSupportRecord(
                        rs.getString("status"),
                        rs.getBoolean("restricted"),
                        rs.getString("restriction"),
                        rs.getString("source_repository_path"),
                        rs.getString("latest_target_output_directory"),
                        rs.getString("latest_outcome_status"),
                        rs.getString("latest_outcome_category"),
                        rs.getString("latest_request_type"),
                        rs.getString("latest_outcome_message"),
                        rs.getString("latest_target_release_id"),
                        rs.getString("latest_result_json"),
                        rs.getString("updated_at"));
            }
        } catch (SQLException error) {
            throw new IllegalStateException("Unable to read project derived-app upgrade support state", error);
        }
    }

    private void persistOutcome(long projectId, UpgradeSupportRecord record) {
        String updateSql = """
                UPDATE project_derived_app_upgrade_support
                SET status = ?,
                    restricted = ?,
                    restriction = ?,
                    source_repository_path = ?,
                    latest_target_output_directory = ?,
                    latest_outcome_status = ?,
                    latest_outcome_category = ?,
                    latest_request_type = ?,
                    latest_outcome_message = ?,
                    latest_target_release_id = ?,
                    latest_result_json = ?,
                    updated_at = ?
                WHERE project_id = ?
                """;
        String insertSql = """
                INSERT INTO project_derived_app_upgrade_support(
                    project_id,
                    status,
                    restricted,
                    restriction,
                    source_repository_path,
                    latest_target_output_directory,
                    latest_outcome_status,
                    latest_outcome_category,
                    latest_request_type,
                    latest_outcome_message,
                    latest_target_release_id,
                    latest_result_json,
                    updated_at
                ) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = JdbcSupport.getConnection()) {
            int updatedRows;
            try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
                bindRecord(statement, record, false, projectId);
                updatedRows = statement.executeUpdate();
            }
            if (updatedRows == 0) {
                try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
                    bindRecord(statement, record, true, projectId);
                    statement.executeUpdate();
                }
            }
        } catch (SQLException error) {
            throw new IllegalStateException("Unable to persist project derived-app upgrade support state", error);
        }
    }

    private void bindRecord(PreparedStatement statement, UpgradeSupportRecord record, boolean includeProjectIdFirst, long projectId)
            throws SQLException {
        int index = 1;
        if (includeProjectIdFirst) {
            statement.setLong(index++, projectId);
        }
        statement.setString(index++, record.status());
        statement.setBoolean(index++, record.restricted());
        statement.setString(index++, record.restriction());
        statement.setString(index++, record.sourceRepositoryPath());
        statement.setString(index++, record.latestTargetOutputDirectory());
        statement.setString(index++, record.latestOutcomeStatus());
        statement.setString(index++, record.latestOutcomeCategory());
        statement.setString(index++, record.latestRequestType());
        statement.setString(index++, record.latestOutcomeMessage());
        statement.setString(index++, record.latestTargetReleaseId());
        statement.setString(index++, record.latestResultJson());
        statement.setString(index++, record.updatedAt());
        if (!includeProjectIdFirst) {
            statement.setLong(index, projectId);
        }
    }

    private List<Map<String, Object>> asMapList(Object value) {
        if (!(value instanceof List<?> listValue)) {
            throw new IllegalArgumentException("Expected array value");
        }
        return listValue.stream()
                .map(entry -> {
                    if (!(entry instanceof Map<?, ?> mapValue)) {
                        throw new IllegalArgumentException("Expected object value");
                    }
                    @SuppressWarnings("unchecked")
                    Map<String, Object> cast = (Map<String, Object>) mapValue;
                    return cast;
                })
                .toList();
    }

    private String asString(Object value) {
        if (!(value instanceof String stringValue)) {
            throw new IllegalArgumentException("Expected string value");
        }
        return stringValue;
    }

    private record RequestInput(String requestType) {
    }

    private static final class AssemblyTarget {
        private final String outputDirectory;
        private final String restriction;

        private AssemblyTarget(String outputDirectory, String restriction) {
            this.outputDirectory = outputDirectory;
            this.restriction = restriction;
        }

        private String outputDirectory() {
            return outputDirectory;
        }

        private String restriction() {
            return restriction;
        }
    }

    private static final class CommandResult {
        private final int exitCode;
        private final String output;

        private CommandResult(int exitCode, String output) {
            this.exitCode = exitCode;
            this.output = output;
        }

        private int exitCode() {
            return exitCode;
        }

        private String output() {
            return output;
        }
    }

    private static final class UpgradeSupportRecord {
        private final String status;
        private final boolean restricted;
        private final String restriction;
        private final String sourceRepositoryPath;
        private final String latestTargetOutputDirectory;
        private final String latestOutcomeStatus;
        private final String latestOutcomeCategory;
        private final String latestRequestType;
        private final String latestOutcomeMessage;
        private final String latestTargetReleaseId;
        private final String latestResultJson;
        private final String updatedAt;

        private UpgradeSupportRecord(
                String status,
                boolean restricted,
                String restriction,
                String sourceRepositoryPath,
                String latestTargetOutputDirectory,
                String latestOutcomeStatus,
                String latestOutcomeCategory,
                String latestRequestType,
                String latestOutcomeMessage,
                String latestTargetReleaseId,
                String latestResultJson,
                String updatedAt) {
            this.status = status;
            this.restricted = restricted;
            this.restriction = restriction;
            this.sourceRepositoryPath = sourceRepositoryPath;
            this.latestTargetOutputDirectory = latestTargetOutputDirectory;
            this.latestOutcomeStatus = latestOutcomeStatus;
            this.latestOutcomeCategory = latestOutcomeCategory;
            this.latestRequestType = latestRequestType;
            this.latestOutcomeMessage = latestOutcomeMessage;
            this.latestTargetReleaseId = latestTargetReleaseId;
            this.latestResultJson = latestResultJson;
            this.updatedAt = updatedAt;
        }

        private String status() {
            return status;
        }

        private boolean restricted() {
            return restricted;
        }

        private String restriction() {
            return restriction;
        }

        private String sourceRepositoryPath() {
            return sourceRepositoryPath;
        }

        private String latestTargetOutputDirectory() {
            return latestTargetOutputDirectory;
        }

        private String latestOutcomeStatus() {
            return latestOutcomeStatus;
        }

        private String latestOutcomeCategory() {
            return latestOutcomeCategory;
        }

        private String latestRequestType() {
            return latestRequestType;
        }

        private String latestOutcomeMessage() {
            return latestOutcomeMessage;
        }

        private String latestTargetReleaseId() {
            return latestTargetReleaseId;
        }

        private String latestResultJson() {
            return latestResultJson;
        }

        private String updatedAt() {
            return updatedAt;
        }
    }

    private static final class RepoRootResolution {
        private final Path root;
        private final String restriction;

        private RepoRootResolution(Path root, String restriction) {
            this.root = root;
            this.restriction = restriction;
        }

        private Path root() {
            return root;
        }

        private String restriction() {
            return restriction;
        }
    }
}
