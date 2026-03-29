package com.fastservice.platform.tools.generatedappverifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

final class GeneratedAppVerifier {

    static final String VERIFIER_ID = "java-generated-app-verifier-cli";
    private static final Pattern APP_ID_PATTERN = Pattern.compile("^[a-z0-9]+(?:-[a-z0-9]+)*$");
    private static final Pattern PACKAGE_PREFIX_PATTERN = Pattern.compile("^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)+$");

    VerificationResult verify(Path targetDir) throws IOException {
        Path resolvedTargetDir = targetDir.toAbsolutePath().normalize();

        if (!Files.exists(resolvedTargetDir)) {
            return VerificationResult.failure(List.of("Target directory does not exist: " + resolvedTargetDir), "unavailable", VERIFIER_ID);
        }

        Map<String, Object> verificationContract;
        try {
            verificationContract = readJson(resolvedTargetDir.resolve("docs/ai/generated-app-verification-contract.json"));
        } catch (IOException error) {
            return VerificationResult.failure(
                    List.of("Missing required file: docs/ai/generated-app-verification-contract.json"),
                    "unavailable",
                    VERIFIER_ID);
        }

        List<String> issues = new ArrayList<>();
        String contractVersion = asString(verificationContract.get("schemaVersion"));
        Map<String, Object> normativeInputs = asMap(verificationContract.get("normativeInputs"), "Verification contract must include normativeInputs");
        Set<String> checks = new LinkedHashSet<>(asStringList(verificationContract.get("checks"), "Verification contract must include checks"));

        Path contractPath = resolveVerificationInputPath(resolvedTargetDir, normativeInputs, "assemblyContract", issues);
        Path manifestPath = resolveVerificationInputPath(resolvedTargetDir, normativeInputs, "applicationManifest", issues);
        Path registryPath = resolveVerificationInputPath(resolvedTargetDir, normativeInputs, "moduleRegistry", issues);
        Path contextPath = resolveVerificationInputPath(resolvedTargetDir, normativeInputs, "generatedContext", issues);
        Path routesPath = resolveVerificationInputPath(resolvedTargetDir, normativeInputs, "frontendRoutes", issues);
        Path navigationPath = resolveVerificationInputPath(resolvedTargetDir, normativeInputs, "frontendNavigation", issues);
        Path servicesPath = resolveVerificationInputPath(resolvedTargetDir, normativeInputs, "backendServices", issues);
        Path tablesPath = resolveVerificationInputPath(resolvedTargetDir, normativeInputs, "backendTables", issues);

        if (checks.contains("verification-inputs-present")) {
            for (Map.Entry<String, Object> entry : normativeInputs.entrySet()) {
                String inputKey = entry.getKey();
                Path inputPath = resolvedTargetDir.resolve(asString(entry.getValue()));
                if (!Files.exists(inputPath)) {
                    issues.add("Missing verification input file: " + inputKey);
                }
            }
        }

        if (!issues.isEmpty() || contractPath == null || manifestPath == null || registryPath == null
                || contextPath == null || routesPath == null || navigationPath == null
                || servicesPath == null || tablesPath == null) {
            return VerificationResult.failure(issues, contractVersion, VERIFIER_ID);
        }

        Map<String, Object> assemblyContract = readJson(contractPath);

        if (checks.contains("required-files-present")) {
            for (String relativePath : getRequiredGeneratedFiles(assemblyContract)) {
                if (!Files.exists(resolvedTargetDir.resolve(relativePath))) {
                    issues.add("Missing required file: " + relativePath);
                }
            }
        }

        List<String> selectedModules;
        Map<String, Object> manifest = readJson(manifestPath);
        Map<String, Object> registry = readJson(registryPath);
        try {
            selectedModules = validateManifest(manifest, registry, assemblyContract);
        } catch (IllegalArgumentException error) {
            issues.add(error.getMessage());
            return VerificationResult.failure(issues, contractVersion, VERIFIER_ID);
        }

        Set<String> selected = new LinkedHashSet<>(selectedModules);
        String servicesSql = Files.readString(servicesPath);
        String tablesSql = Files.readString(tablesPath);
        String routerTsx = Files.readString(routesPath);
        String navigationTs = Files.readString(navigationPath);
        Map<String, Object> generatedContext = readJson(contextPath);

        if (checks.contains("module-selected-routes-match-registry")) {
            for (Map<String, Object> module : modules(registry)) {
                List<String> routes = optionalNestedStringList(module, "frontend", "routes");
                if (routes.isEmpty()) {
                    continue;
                }
                String moduleId = asString(module.get("id"));
                boolean shouldExist = selected.contains(moduleId);
                for (String route : routes) {
                    if (shouldExist && !routerTsx.contains(route.substring(1))) {
                        issues.add("Missing route wiring for selected module: " + moduleId);
                    }
                    if (!shouldExist && routerTsx.contains(route)) {
                        issues.add("Unexpected route wiring for unselected module: " + moduleId);
                    }
                    if (shouldExist && !navigationTs.contains(route)) {
                        issues.add("Missing navigation entry for selected module: " + moduleId);
                    }
                    if (!shouldExist && navigationTs.contains(route)) {
                        issues.add("Unexpected navigation entry for unselected module: " + moduleId);
                    }
                }
            }
        }

        if (checks.contains("module-selected-backend-services-match-registry")) {
            for (Map<String, Object> module : modules(registry)) {
                String moduleId = asString(module.get("id"));
                boolean shouldExist = selected.contains(moduleId);
                for (String serviceName : optionalNestedStringList(module, "backend", "services")) {
                    String marker = "create service if not exists " + serviceName;
                    if (shouldExist && !servicesSql.contains(marker)) {
                        issues.add("Missing backend service contract for selected module: " + moduleId);
                    }
                    if (!shouldExist && servicesSql.contains(marker)) {
                        issues.add("Unexpected backend service contract for unselected module: " + moduleId);
                    }
                }
            }
        }

        if (checks.contains("module-selected-backend-tables-match-registry")) {
            for (Map<String, Object> module : modules(registry)) {
                String moduleId = asString(module.get("id"));
                boolean shouldExist = selected.contains(moduleId);
                for (String tableName : optionalNestedStringList(module, "backend", "tables")) {
                    String marker = "create table if not exists " + tableName;
                    if (shouldExist && !tablesSql.contains(marker)) {
                        issues.add("Missing backend table contract for selected module: " + moduleId);
                    }
                    if (!shouldExist && tablesSql.contains(marker)) {
                        issues.add("Unexpected backend table contract for unselected module: " + moduleId);
                    }
                }
            }
        }

        if (checks.contains("generated-context-selected-modules-match-manifest")) {
            List<String> contextModules = asOptionalStringList(generatedContext.get("selectedModules"));
            if (!contextModules.equals(selectedModules)) {
                issues.add("docs/ai/context.json does not match app-manifest.json selectedModules");
            }
        }

        return new VerificationResult(issues.isEmpty(), issues, selectedModules, contractVersion, VERIFIER_ID);
    }

    private Path resolveVerificationInputPath(Path targetDir, Map<String, Object> normativeInputs, String inputKey, List<String> issues) {
        Object relativePath = normativeInputs.get(inputKey);
        if (!(relativePath instanceof String stringValue) || stringValue.isBlank()) {
            issues.add("Missing verification contract input path: " + inputKey);
            return null;
        }
        return targetDir.resolve(stringValue);
    }

    private List<String> validateManifest(Map<String, Object> manifest, Map<String, Object> registry, Map<String, Object> assemblyContract) {
        require("fsp-app-manifest/v1".equals(asString(manifest.get("schemaVersion"))), "Unsupported manifest schemaVersion");
        Map<String, Object> application = asMap(manifest.get("application"), "Manifest must include application");
        String appId = asString(application.get("id"));
        String appName = asString(application.get("name"));
        String packagePrefix = asString(application.get("packagePrefix"));

        require(APP_ID_PATTERN.matcher(appId).matches(), "application.id must be kebab-case");
        require(!appName.isBlank(), "application.name is required");
        require(PACKAGE_PREFIX_PATTERN.matcher(packagePrefix).matches(),
                "application.packagePrefix must be a dotted Java package prefix");

        List<String> selectedModules = asStringList(manifest.get("modules"), "modules must be an array");
        require(!selectedModules.isEmpty(), "modules must be an array");
        require(new LinkedHashSet<>(selectedModules).size() == selectedModules.size(), "modules must not contain duplicates");

        Map<String, Map<String, Object>> registryById = new LinkedHashMap<>();
        for (Map<String, Object> module : modules(registry)) {
            registryById.put(asString(module.get("id")), module);
        }

        for (String moduleId : selectedModules) {
            require(registryById.containsKey(moduleId), "Unknown module: " + moduleId);
        }

        for (Map<String, Object> module : modules(registry)) {
            if ("required-core".equals(asString(module.get("role")))) {
                String requiredModuleId = asString(module.get("id"));
                require(selectedModules.contains(requiredModuleId), "Manifest must include required core module: " + requiredModuleId);
            }
        }

        for (String moduleId : selectedModules) {
            for (String dependency : asOptionalStringList(registryById.get(moduleId).get("dependsOn"))) {
                require(selectedModules.contains(dependency), "Module " + moduleId + " requires dependency " + dependency);
            }
        }

        for (String fieldPath : asOptionalStringList(assemblyContract.get("requiredManifestFields"))) {
            requireNestedField(manifest, fieldPath);
        }

        return selectedModules;
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

    private List<String> getRequiredGeneratedFiles(Map<String, Object> contract) {
        Map<String, Object> outputInvariants = asMap(contract.get("outputInvariants"), "Contract must include outputInvariants");
        return asOptionalStringList(outputInvariants.get("requiredFiles"));
    }

    private List<Map<String, Object>> modules(Map<String, Object> registry) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object entry : asList(registry.get("modules"), "Registry must include modules")) {
            result.add(asMap(entry, "Registry module must be an object"));
        }
        return result;
    }

    private List<String> optionalNestedStringList(Map<String, Object> root, String objectKey, String arrayKey) {
        Object nested = root.get(objectKey);
        if (!(nested instanceof Map<?, ?> map) || !map.containsKey(arrayKey)) {
            return List.of();
        }
        return asOptionalStringList(map.get(arrayKey));
    }

    private Map<String, Object> readJson(Path filePath) throws IOException {
        return SimpleJson.parseObject(Files.readString(filePath));
    }

    private static Map<String, Object> asMap(Object value, String message) {
        if (!(value instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException(message);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            result.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return result;
    }

    private static List<Object> asList(Object value, String message) {
        if (!(value instanceof List<?> list)) {
            throw new IllegalArgumentException(message);
        }
        return new ArrayList<>(list);
    }

    private static List<String> asStringList(Object value, String message) {
        List<Object> values = asList(value, message);
        List<String> result = new ArrayList<>();
        for (Object entry : values) {
            result.add(asString(entry));
        }
        return result;
    }

    private static List<String> asOptionalStringList(Object value) {
        if (value == null) {
            return List.of();
        }
        return asStringList(value, "Expected array of strings");
    }

    private static String asString(Object value) {
        if (!(value instanceof String stringValue)) {
            throw new IllegalArgumentException("Expected string value");
        }
        return stringValue;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    record VerificationResult(boolean ok, List<String> issues, List<String> selectedModules, String contractVersion, String verifierId) {
        static VerificationResult failure(List<String> issues, String contractVersion, String verifierId) {
            return new VerificationResult(false, List.copyOf(issues), List.of(), contractVersion, verifierId);
        }
    }
}
