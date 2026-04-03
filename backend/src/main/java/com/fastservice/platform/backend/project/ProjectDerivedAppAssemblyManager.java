package com.fastservice.platform.backend.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.fastservice.platform.backend.common.db.JdbcSupport;
import com.fastservice.platform.backend.common.json.SimpleJson;

final class ProjectDerivedAppAssemblyManager {

    private static final String STATUS_AVAILABLE = "AVAILABLE";
    private static final String STATUS_RESTRICTED = "RESTRICTED";
    private static final String OUTCOME_SUCCESS = "SUCCESS";
    private static final String OUTCOME_FAILED = "FAILED";
    private static final String CATEGORY_VALIDATION = "REQUEST_VALIDATION";
    private static final String CATEGORY_EXECUTION = "ASSEMBLY_EXECUTION";

    String readAssemblyContext(long projectId, String repositoryRootPath) {
        AssemblyRecord persisted = readRecord(projectId);
        return buildContextJson(projectId, repositoryRootPath, persisted);
    }

    String requestAssembly(long projectId, String repositoryRootPath, String manifestJson, String outputDirectory) {
        String sourceRepositoryPath = normalizeAbsolutePath(repositoryRootPath, "Repository path").toString();
        Path outputDir = null;
        Map<String, Object> manifest = null;
        String manifestAppId = null;
        String manifestName = null;

        try {
            outputDir = normalizeAbsolutePath(outputDirectory, "Output directory");
            manifest = SimpleJson.parseObject(manifestJson);
            manifestAppId = readManifestApplicationField(manifest, "id");
            manifestName = readManifestApplicationField(manifest, "name");
        } catch (IllegalArgumentException error) {
            persistOutcome(projectId, new AssemblyRecord(
                    STATUS_AVAILABLE,
                    false,
                    null,
                    sourceRepositoryPath,
                    OUTCOME_FAILED,
                    CATEGORY_VALIDATION,
                    error.getMessage(),
                    outputDirectory,
                    manifestAppId,
                    manifestName,
                    manifestJson,
                    outputDirectory,
                    Instant.now().toString()));
            throw error;
        }

        RepoRootResolution repoRoot = resolveRepoRoot();
        if (repoRoot.root() == null) {
            String message = repoRoot.restriction();
            persistOutcome(projectId, new AssemblyRecord(
                    STATUS_RESTRICTED,
                    true,
                    message,
                    sourceRepositoryPath,
                    OUTCOME_FAILED,
                    CATEGORY_EXECUTION,
                    message,
                    outputDir.toString(),
                    manifestAppId,
                    manifestName,
                    manifestJson,
                    outputDir.toString(),
                    Instant.now().toString()));
            throw new IllegalStateException(message);
        }

        try {
            validateManifestRequest(manifest, repoRoot.root());
            validateOutputDirectory(outputDir);
        } catch (IllegalArgumentException error) {
            persistOutcome(projectId, new AssemblyRecord(
                    STATUS_AVAILABLE,
                    false,
                    null,
                    sourceRepositoryPath,
                    OUTCOME_FAILED,
                    CATEGORY_VALIDATION,
                    error.getMessage(),
                    outputDir.toString(),
                    manifestAppId,
                    manifestName,
                    manifestJson,
                    outputDir.toString(),
                    Instant.now().toString()));
            throw error;
        }

        Path manifestPath = writeManifestRequest(projectId, manifestJson);

        try {
            CommandResult result = runAssembly(repoRoot.root(), manifestPath, outputDir);
            if (result.exitCode() != 0) {
                String message = normalizeProcessMessage(result.output(), "Repository-owned assembly tooling failed");
                persistOutcome(projectId, new AssemblyRecord(
                        STATUS_AVAILABLE,
                        false,
                        null,
                        sourceRepositoryPath,
                        OUTCOME_FAILED,
                        CATEGORY_EXECUTION,
                        message,
                        outputDir.toString(),
                        manifestAppId,
                        manifestName,
                        manifestJson,
                        outputDir.toString(),
                        Instant.now().toString()));
                throw new IllegalStateException(message);
            }

            persistOutcome(projectId, new AssemblyRecord(
                    STATUS_AVAILABLE,
                    false,
                    null,
                    sourceRepositoryPath,
                    OUTCOME_SUCCESS,
                    CATEGORY_EXECUTION,
                    "Derived app assembly completed through repository-owned tooling.",
                    outputDir.toString(),
                    manifestAppId,
                    manifestName,
                    manifestJson,
                    outputDir.toString(),
                    Instant.now().toString()));

            return buildContextJson(projectId, repositoryRootPath, readRecord(projectId));
        } finally {
            deleteIfExists(manifestPath);
        }
    }

    private String buildContextJson(long projectId, String repositoryRootPath, AssemblyRecord persisted) {
        Map<String, Object> payload = new LinkedHashMap<>();
        RepoRootResolution repoRoot = resolveRepoRoot();
        boolean restricted = repositoryRootPath == null || repositoryRootPath.isBlank() || repoRoot.root() == null;
        payload.put("available", !restricted);
        payload.put("status", restricted ? STATUS_RESTRICTED : STATUS_AVAILABLE);
        payload.put("restricted", restricted);
        payload.put(
                "restriction",
                repositoryRootPath == null || repositoryRootPath.isBlank()
                        ? "Bind a repository first to run project-scoped derived-app assembly."
                        : repoRoot.restriction());
        payload.put("sourceRepositoryPath", restricted ? null : normalizeAbsolutePath(repositoryRootPath, "Repository path").toString());
        payload.put("sourceContext", Map.of("type", "BOUND_MAIN_REPOSITORY"));
        payload.put("latestOutcome", latestOutcomeMap(persisted));
        return SimpleJson.stringify(payload);
    }

    private Map<String, Object> latestOutcomeMap(AssemblyRecord persisted) {
        if (persisted == null || persisted.latestOutcomeStatus() == null || persisted.latestOutcomeStatus().isBlank()) {
            return null;
        }
        Map<String, Object> latestOutcome = new LinkedHashMap<>();
        latestOutcome.put("status", persisted.latestOutcomeStatus());
        latestOutcome.put("category", persisted.latestOutcomeCategory());
        latestOutcome.put("message", persisted.latestOutcomeMessage());
        latestOutcome.put("outputDirectory", persisted.latestOutputDirectory());
        latestOutcome.put("manifestAppId", persisted.latestManifestAppId());
        latestOutcome.put("manifestName", persisted.latestManifestName());
        latestOutcome.put("requestedManifest", persisted.latestRequestManifest());
        latestOutcome.put("requestedOutputDirectory", persisted.latestRequestOutputDirectory());
        latestOutcome.put("updatedAt", persisted.updatedAt());
        return latestOutcome;
    }

    private void validateManifestRequest(Map<String, Object> manifest, Path repoRoot) {
        Object schemaVersion = manifest.get("schemaVersion");
        if (!(schemaVersion instanceof String version) || version.isBlank()) {
            throw new IllegalArgumentException("Manifest must include schemaVersion");
        }
        Object applicationValue = manifest.get("application");
        if (!(applicationValue instanceof Map<?, ?> application)) {
            throw new IllegalArgumentException("Manifest must include application");
        }
        requireNonBlank(application.get("id"), "Manifest must include application.id");
        requireNonBlank(application.get("name"), "Manifest must include application.name");
        requireNonBlank(application.get("packagePrefix"), "Manifest must include application.packagePrefix");
        Object modulesValue = manifest.get("modules");
        if (!(modulesValue instanceof List<?> modules) || modules.isEmpty()) {
            throw new IllegalArgumentException("Manifest must include at least one module");
        }

        Map<String, Object> registry = readJson(repoRoot.resolve("docs/ai/module-registry.json"));
        Map<String, Object> contract = readJson(repoRoot.resolve("docs/ai/app-assembly-contract.json"));
        validateManifestAgainstContract(manifest, registry, contract);
    }

    private void validateManifestAgainstContract(
            Map<String, Object> manifest,
            Map<String, Object> registry,
            Map<String, Object> contract) {
        require("fsp-app-manifest/v1".equals(asString(manifest.get("schemaVersion"))), "Unsupported manifest schemaVersion");
        Map<String, Object> application = asMap(manifest.get("application"), "Manifest must include application");
        require(!asString(application.get("id")).isBlank(), "Manifest must include application.id");
        require(!asString(application.get("name")).isBlank(), "Manifest must include application.name");
        require(!asString(application.get("packagePrefix")).isBlank(), "Manifest must include application.packagePrefix");

        List<String> selectedModules = asStringList(manifest.get("modules"), "modules must be an array");
        require(!selectedModules.isEmpty(), "Manifest must include at least one module");
        require(new LinkedHashSet<>(selectedModules).size() == selectedModules.size(), "modules must not contain duplicates");

        Map<String, Map<String, Object>> registryById = registryById(registry);
        for (String moduleId : selectedModules) {
            require(registryById.containsKey(moduleId), "Unknown module: " + moduleId);
        }

        for (String requiredCoreId : requiredCoreModuleIds(registry)) {
            require(selectedModules.contains(requiredCoreId), "Manifest must include required core module: " + requiredCoreId);
        }

        for (String moduleId : selectedModules) {
            for (String dependency : asOptionalStringList(registryById.get(moduleId).get("dependsOn"))) {
                require(selectedModules.contains(dependency), "Module " + moduleId + " requires dependency " + dependency);
            }
        }

        for (String fieldPath : asOptionalStringList(contract.get("requiredManifestFields"))) {
            requireNestedField(manifest, fieldPath);
        }
    }

    private Map<String, Object> readJson(Path filePath) {
        try {
            return SimpleJson.parseObject(Files.readString(filePath));
        } catch (IOException error) {
            throw new IllegalStateException("Unable to read assembly contract asset: " + filePath, error);
        }
    }

    private Map<String, Map<String, Object>> registryById(Map<String, Object> registry) {
        Map<String, Map<String, Object>> byId = new LinkedHashMap<>();
        for (Object entry : asList(registry.get("modules"), "Registry must include modules")) {
            Map<String, Object> module = asMap(entry, "Registry module must be an object");
            byId.put(asString(module.get("id")), module);
        }
        return byId;
    }

    private List<String> requiredCoreModuleIds(Map<String, Object> registry) {
        List<String> required = new ArrayList<>();
        for (Object entry : asList(registry.get("modules"), "Registry must include modules")) {
            Map<String, Object> module = asMap(entry, "Registry module must be an object");
            if ("required-core".equals(asString(module.get("role")))) {
                required.add(asString(module.get("id")));
            }
        }
        return required;
    }

    private void requireNestedField(Map<String, Object> manifest, String fieldPath) {
        Object current = manifest;
        for (String part : fieldPath.split("\\.")) {
            if (!(current instanceof Map<?, ?> map) || !map.containsKey(part) || map.get(part) == null) {
                throw new IllegalArgumentException("Manifest must include " + fieldPath);
            }
            current = map.get(part);
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    private static String asString(Object value) {
        if (value instanceof String stringValue) {
            return stringValue;
        }
        throw new IllegalArgumentException("Expected string value");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object value, String message) {
        if (value instanceof Map<?, ?> mapValue) {
            return (Map<String, Object>) mapValue;
        }
        throw new IllegalArgumentException(message);
    }

    private static List<Object> asList(Object value, String message) {
        if (value instanceof List<?> listValue) {
            return new ArrayList<>(listValue);
        }
        throw new IllegalArgumentException(message);
    }

    private static List<String> asStringList(Object value, String message) {
        List<Object> values = asList(value, message);
        List<String> strings = new ArrayList<>();
        for (Object entry : values) {
            strings.add(asString(entry));
        }
        return strings;
    }

    private static List<String> asOptionalStringList(Object value) {
        if (value == null) {
            return List.of();
        }
        if (!(value instanceof List<?> listValue)) {
            throw new IllegalArgumentException("Expected array value");
        }
        List<String> strings = new ArrayList<>();
        for (Object entry : listValue) {
            strings.add(asString(entry));
        }
        return strings;
    }

    private static String readManifestApplicationField(Map<String, Object> manifest, String field) {
        Object applicationValue = manifest.get("application");
        if (!(applicationValue instanceof Map<?, ?> application)) {
            return null;
        }
        Object value = application.get(field);
        return value instanceof String stringValue && !stringValue.isBlank() ? stringValue : null;
    }

    private static void requireNonBlank(Object value, String message) {
        if (!(value instanceof String stringValue) || stringValue.isBlank()) {
            throw new IllegalArgumentException(message);
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

    private static void validateOutputDirectory(Path outputDir) {
        if (!Files.exists(outputDir)) {
            return;
        }
        if (!Files.isDirectory(outputDir)) {
            throw new IllegalArgumentException("Output directory already exists but is not a directory: " + outputDir);
        }
        try (Stream<Path> entries = Files.list(outputDir)) {
            if (entries.findAny().isPresent()) {
                throw new IllegalArgumentException("Output directory already exists and must be empty: " + outputDir);
            }
        } catch (IOException error) {
            throw new IllegalStateException("Unable to inspect output directory: " + outputDir, error);
        }
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
                "Repository-owned assembly tooling is not available from the current backend runtime.");
    }

    private static Path writeManifestRequest(long projectId, String manifestJson) {
        try {
            Path requestDir = Files.createTempDirectory("fsp-project-assembly-" + projectId + "-");
            Path manifestPath = requestDir.resolve("project-assembly-request.json");
            Files.writeString(manifestPath, manifestJson);
            return manifestPath;
        } catch (IOException error) {
            throw new IllegalStateException("Unable to prepare project assembly request manifest", error);
        }
    }

    private static void deleteIfExists(Path path) {
        if (path == null) {
            return;
        }
        try {
            Files.deleteIfExists(path);
            Path parent = path.getParent();
            if (parent != null) {
                Files.deleteIfExists(parent);
            }
        } catch (IOException ignored) {
            // Best-effort cleanup for temp request files.
        }
    }

    private static CommandResult runAssembly(Path repoRoot, Path manifestPath, Path outputDir) {
        try {
            ProcessBuilder scaffoldProcessBuilder = new ProcessBuilder(
                    "bash",
                    repoRoot.resolve("scripts/platform-tool.sh").toString(),
                    "assembly",
                    "scaffold",
                    manifestPath.toString(),
                    outputDir.toString());
            scaffoldProcessBuilder.directory(repoRoot.toFile());
            scaffoldProcessBuilder.redirectErrorStream(true);
            Process scaffold = scaffoldProcessBuilder.start();
            String output = new String(scaffold.getInputStream().readAllBytes()).trim();
            int exitCode = scaffold.waitFor();
            return new CommandResult(exitCode, output);
        } catch (IOException error) {
            throw new IllegalStateException("Unable to start repository-owned assembly tooling", error);
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for repository-owned assembly tooling", error);
        }
    }

    private static String normalizeProcessMessage(String output, String fallback) {
        if (output == null || output.isBlank()) {
            return fallback;
        }
        return output;
    }

    private AssemblyRecord readRecord(long projectId) {
        String sql = """
                SELECT status,
                       restricted,
                       restriction,
                       source_repository_path,
                       latest_outcome_status,
                       latest_outcome_category,
                       latest_outcome_message,
                       latest_output_directory,
                       latest_manifest_app_id,
                       latest_manifest_name,
                       latest_request_manifest,
                       latest_request_output_directory,
                       updated_at
                FROM project_derived_app_assembly
                WHERE project_id = ?
                """;
        try (Connection connection = JdbcSupport.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, projectId);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new AssemblyRecord(
                        rs.getString("status"),
                        rs.getBoolean("restricted"),
                        rs.getString("restriction"),
                        rs.getString("source_repository_path"),
                        rs.getString("latest_outcome_status"),
                        rs.getString("latest_outcome_category"),
                        rs.getString("latest_outcome_message"),
                        rs.getString("latest_output_directory"),
                        rs.getString("latest_manifest_app_id"),
                        rs.getString("latest_manifest_name"),
                        rs.getString("latest_request_manifest"),
                        rs.getString("latest_request_output_directory"),
                        rs.getString("updated_at"));
            }
        } catch (SQLException error) {
            throw new IllegalStateException("Unable to read project derived-app assembly state", error);
        }
    }

    private void persistOutcome(long projectId, AssemblyRecord record) {
        String updateSql = """
                UPDATE project_derived_app_assembly
                SET status = ?,
                    restricted = ?,
                    restriction = ?,
                    source_repository_path = ?,
                    latest_outcome_status = ?,
                    latest_outcome_category = ?,
                    latest_outcome_message = ?,
                    latest_output_directory = ?,
                    latest_manifest_app_id = ?,
                    latest_manifest_name = ?,
                    latest_request_manifest = ?,
                    latest_request_output_directory = ?,
                    updated_at = ?
                WHERE project_id = ?
                """;
        String insertSql = """
                INSERT INTO project_derived_app_assembly(
                    project_id,
                    status,
                    restricted,
                    restriction,
                    source_repository_path,
                    latest_outcome_status,
                    latest_outcome_category,
                    latest_outcome_message,
                    latest_output_directory,
                    latest_manifest_app_id,
                    latest_manifest_name,
                    latest_request_manifest,
                    latest_request_output_directory,
                    updated_at
                ) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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
            throw new IllegalStateException("Unable to persist project derived-app assembly state", error);
        }
    }

    private void bindRecord(PreparedStatement statement, AssemblyRecord record, boolean includeProjectIdFirst, long projectId)
            throws SQLException {
        int index = 1;
        if (includeProjectIdFirst) {
            statement.setLong(index++, projectId);
        }
        statement.setString(index++, record.status());
        statement.setBoolean(index++, record.restricted());
        statement.setString(index++, record.restriction());
        statement.setString(index++, record.sourceRepositoryPath());
        statement.setString(index++, record.latestOutcomeStatus());
        statement.setString(index++, record.latestOutcomeCategory());
        statement.setString(index++, record.latestOutcomeMessage());
        statement.setString(index++, record.latestOutputDirectory());
        statement.setString(index++, record.latestManifestAppId());
        statement.setString(index++, record.latestManifestName());
        statement.setString(index++, record.latestRequestManifest());
        statement.setString(index++, record.latestRequestOutputDirectory());
        statement.setString(index++, record.updatedAt());
        if (!includeProjectIdFirst) {
            statement.setLong(index, projectId);
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

    private static final class AssemblyRecord {
        private final String status;
        private final boolean restricted;
        private final String restriction;
        private final String sourceRepositoryPath;
        private final String latestOutcomeStatus;
        private final String latestOutcomeCategory;
        private final String latestOutcomeMessage;
        private final String latestOutputDirectory;
        private final String latestManifestAppId;
        private final String latestManifestName;
        private final String latestRequestManifest;
        private final String latestRequestOutputDirectory;
        private final String updatedAt;

        private AssemblyRecord(
                String status,
                boolean restricted,
                String restriction,
                String sourceRepositoryPath,
                String latestOutcomeStatus,
                String latestOutcomeCategory,
                String latestOutcomeMessage,
                String latestOutputDirectory,
                String latestManifestAppId,
                String latestManifestName,
                String latestRequestManifest,
                String latestRequestOutputDirectory,
                String updatedAt) {
            this.status = status;
            this.restricted = restricted;
            this.restriction = restriction;
            this.sourceRepositoryPath = sourceRepositoryPath;
            this.latestOutcomeStatus = latestOutcomeStatus;
            this.latestOutcomeCategory = latestOutcomeCategory;
            this.latestOutcomeMessage = latestOutcomeMessage;
            this.latestOutputDirectory = latestOutputDirectory;
            this.latestManifestAppId = latestManifestAppId;
            this.latestManifestName = latestManifestName;
            this.latestRequestManifest = latestRequestManifest;
            this.latestRequestOutputDirectory = latestRequestOutputDirectory;
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

        private String latestOutcomeStatus() {
            return latestOutcomeStatus;
        }

        private String latestOutcomeCategory() {
            return latestOutcomeCategory;
        }

        private String latestOutcomeMessage() {
            return latestOutcomeMessage;
        }

        private String latestOutputDirectory() {
            return latestOutputDirectory;
        }

        private String latestManifestAppId() {
            return latestManifestAppId;
        }

        private String latestManifestName() {
            return latestManifestName;
        }

        private String latestRequestManifest() {
            return latestRequestManifest;
        }

        private String latestRequestOutputDirectory() {
            return latestRequestOutputDirectory;
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
