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

public final class VerifyDerivedApp {

    private static final String VERIFIER_ID = "java-generated-app-verifier";
    private static final Pattern APP_ID_PATTERN = Pattern.compile("^[a-z0-9]+(?:-[a-z0-9]+)*$");
    private static final Pattern PACKAGE_PREFIX_PATTERN = Pattern.compile("^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)+$");
    private static final String STRUCTURED_APP_TEMPLATE_CONTRACT_PATH = "docs/ai/structured-app-template-contract.json";
    private static final String DERIVED_APP_TEMPLATE_MAP_PATH =
            "docs/ai/template-classifications/default-derived-app-template-map.json";

    private VerifyDerivedApp() {
    }

    public static void main(String[] args) throws Exception {
        Path targetDir = args.length > 0
                ? Path.of(args[0]).toAbsolutePath().normalize()
                : Path.of(".").toAbsolutePath().normalize();

        VerificationResult result = verify(targetDir);
        if (!result.ok()) {
            System.err.println("Derived app verification failed for " + targetDir);
            for (String issue : result.issues()) {
                System.err.println("- " + issue);
            }
            System.exit(1);
            return;
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("targetDir", targetDir.toString());
        payload.put("contractVersion", result.contractVersion());
        payload.put("verifierId", result.verifierId());
        payload.put("selectedModules", result.selectedModules());
        payload.put("verified", true);
        System.out.println(SimpleJson.stringify(payload));
    }

    static VerificationResult verify(Path targetDir) throws IOException {
        Path resolvedTargetDir = targetDir.toAbsolutePath().normalize();

        if (!Files.exists(resolvedTargetDir)) {
            return VerificationResult.failure(
                    List.of("Target directory does not exist: " + resolvedTargetDir),
                    "unavailable",
                    VERIFIER_ID);
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
        Map<String, Object> normativeInputs = asMap(
                verificationContract.get("normativeInputs"),
                "Verification contract must include normativeInputs");
        Set<String> checks = new LinkedHashSet<>(asStringList(
                verificationContract.get("checks"),
                "Verification contract must include checks"));

        Path contractPath = resolveVerificationInputPath(resolvedTargetDir, normativeInputs, "assemblyContract", issues);
        Path manifestPath = resolveVerificationInputPath(resolvedTargetDir, normativeInputs, "applicationManifest", issues);
        Path registryPath = resolveVerificationInputPath(resolvedTargetDir, normativeInputs, "moduleRegistry", issues);
        Path contextPath = resolveVerificationInputPath(resolvedTargetDir, normativeInputs, "generatedContext", issues);
        Path lifecyclePath = resolveVerificationInputPath(resolvedTargetDir, normativeInputs, "derivedAppLifecycleMetadata", issues);
        Path templateContractPath = resolveVerificationInputPath(resolvedTargetDir, normativeInputs, "structuredAppTemplateContract", issues);
        Path templateMapPath = resolveVerificationInputPath(resolvedTargetDir, normativeInputs, "structuredAppTemplateMap", issues);
        Path routesPath = resolveVerificationInputPath(resolvedTargetDir, normativeInputs, "frontendRoutes", issues);
        Path navigationPath = resolveVerificationInputPath(resolvedTargetDir, normativeInputs, "frontendNavigation", issues);
        Path servicesPath = resolveVerificationInputPath(resolvedTargetDir, normativeInputs, "backendServices", issues);
        Path tablesPath = resolveVerificationInputPath(resolvedTargetDir, normativeInputs, "backendTables", issues);

        if (checks.contains("verification-inputs-present")) {
            for (Map.Entry<String, Object> entry : normativeInputs.entrySet()) {
                Path inputPath = resolvedTargetDir.resolve(asString(entry.getValue()));
                if (!Files.exists(inputPath)) {
                    issues.add("Missing verification input file: " + entry.getKey());
                }
            }
        }

        if (!issues.isEmpty()
                || contractPath == null
                || manifestPath == null
                || registryPath == null
                || contextPath == null
                || lifecyclePath == null
                || templateContractPath == null
                || templateMapPath == null
                || routesPath == null
                || navigationPath == null
                || servicesPath == null
                || tablesPath == null) {
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

        Map<String, Object> manifest = readJson(manifestPath);
        Map<String, Object> registry = readJson(registryPath);
        List<String> selectedModules;
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
        Map<String, Object> lifecycle = readJson(lifecyclePath);

        if (checks.contains("module-selected-routes-match-registry")) {
            for (Map<String, Object> module : modules(registry)) {
                String moduleId = asString(module.get("id"));
                boolean shouldExist = selected.contains(moduleId);
                for (String route : optionalNestedStringList(module, "frontend", "routes")) {
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

        if (checks.contains("template-boundary-assets-present")) {
            Map<String, Object> contractInputs = asMap(
                    generatedContext.get("contractInputs"),
                    "docs/ai/context.json must include contractInputs");
            Map<String, Object> templateSystem = asMap(
                    generatedContext.get("templateSystem"),
                    "docs/ai/context.json must include templateSystem");
            Map<String, Object> lifecycleTemplateSystem = asMap(
                    lifecycle.get("templateSystem"),
                    "docs/ai/derived-app-lifecycle.json must include templateSystem");

            if (!STRUCTURED_APP_TEMPLATE_CONTRACT_PATH.equals(asString(contractInputs.get("structuredAppTemplateContract")))) {
                issues.add("docs/ai/context.json does not expose the structured app template contract");
            }
            if (!DERIVED_APP_TEMPLATE_MAP_PATH.equals(asString(contractInputs.get("structuredAppTemplateMap")))) {
                issues.add("docs/ai/context.json does not expose the structured app template map");
            }
            if (!STRUCTURED_APP_TEMPLATE_CONTRACT_PATH.equals(asString(templateSystem.get("contract")))) {
                issues.add("docs/ai/context.json templateSystem.contract is missing or incorrect");
            }
            if (!DERIVED_APP_TEMPLATE_MAP_PATH.equals(asString(templateSystem.get("classificationMap")))) {
                issues.add("docs/ai/context.json templateSystem.classificationMap is missing or incorrect");
            }
            if (!STRUCTURED_APP_TEMPLATE_CONTRACT_PATH.equals(asString(lifecycleTemplateSystem.get("templateContract")))) {
                issues.add("docs/ai/derived-app-lifecycle.json templateSystem.templateContract is missing or incorrect");
            }
            if (!DERIVED_APP_TEMPLATE_MAP_PATH.equals(asString(lifecycleTemplateSystem.get("templateClassificationMap")))) {
                issues.add("docs/ai/derived-app-lifecycle.json templateSystem.templateClassificationMap is missing or incorrect");
            }
        }

        return new VerificationResult(issues.isEmpty(), issues, selectedModules, contractVersion, VERIFIER_ID);
    }

    private static Path resolveVerificationInputPath(
            Path targetDir,
            Map<String, Object> normativeInputs,
            String inputKey,
            List<String> issues) {
        Object relativePath = normativeInputs.get(inputKey);
        if (!(relativePath instanceof String stringValue) || stringValue.isBlank()) {
            issues.add("Missing verification contract input path: " + inputKey);
            return null;
        }
        return targetDir.resolve(stringValue);
    }

    private static List<String> validateManifest(
            Map<String, Object> manifest,
            Map<String, Object> registry,
            Map<String, Object> assemblyContract) {
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
                require(selectedModules.contains(requiredModuleId),
                        "Manifest must include required core module: " + requiredModuleId);
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

    private static void requireNestedField(Map<String, Object> manifest, String fieldPath) {
        Object current = manifest;
        for (String part : fieldPath.split("\\.")) {
            if (!(current instanceof Map<?, ?> map) || !map.containsKey(part) || map.get(part) == null) {
                throw new IllegalArgumentException("Manifest must include " + fieldPath);
            }
            current = map.get(part);
        }
    }

    private static List<String> getRequiredGeneratedFiles(Map<String, Object> assemblyContract) {
        return asOptionalStringList(
                asMap(assemblyContract.get("outputInvariants"), "Contract must include outputInvariants")
                        .get("requiredFiles"));
    }

    private static List<Map<String, Object>> modules(Map<String, Object> registry) {
        List<Map<String, Object>> values = new ArrayList<>();
        for (Object entry : asList(registry.get("modules"), "Registry must include modules")) {
            values.add(asMap(entry, "Module entries must be objects"));
        }
        return values;
    }

    private static List<String> optionalNestedStringList(Map<String, Object> object, String nestedObject, String nestedList) {
        Object nestedValue = object.get(nestedObject);
        if (!(nestedValue instanceof Map<?, ?>)) {
            return List.of();
        }
        Object listValue = ((Map<?, ?>) nestedValue).get(nestedList);
        return asOptionalStringList(listValue);
    }

    private static Map<String, Object> readJson(Path path) throws IOException {
        return SimpleJson.parseObject(Files.readString(path));
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

    private record VerificationResult(
            boolean ok,
            List<String> issues,
            List<String> selectedModules,
            String contractVersion,
            String verifierId) {
        private static VerificationResult failure(List<String> issues, String contractVersion, String verifierId) {
            return new VerificationResult(false, issues, List.of(), contractVersion, verifierId);
        }
    }

    private static final class SimpleJson {
        private SimpleJson() {
        }

        @SuppressWarnings("unchecked")
        static Map<String, Object> parseObject(String json) {
            Object value = parse(json);
            if (!(value instanceof Map<?, ?> map)) {
                throw new IllegalArgumentException("JSON value is not an object");
            }
            return (Map<String, Object>) map;
        }

        static Object parse(String json) {
            return new Parser(json).parse();
        }

        static String stringify(Object value) {
            StringBuilder builder = new StringBuilder();
            writeValue(builder, value);
            return builder.toString();
        }

        private static void writeValue(StringBuilder builder, Object value) {
            if (value == null) {
                builder.append("null");
                return;
            }
            if (value instanceof String stringValue) {
                builder.append('"');
                for (int index = 0; index < stringValue.length(); index++) {
                    char current = stringValue.charAt(index);
                    switch (current) {
                        case '"' -> builder.append("\\\"");
                        case '\\' -> builder.append("\\\\");
                        case '\b' -> builder.append("\\b");
                        case '\f' -> builder.append("\\f");
                        case '\n' -> builder.append("\\n");
                        case '\r' -> builder.append("\\r");
                        case '\t' -> builder.append("\\t");
                        default -> {
                            if (current < 0x20) {
                                builder.append(String.format("\\u%04x", (int) current));
                            } else {
                                builder.append(current);
                            }
                        }
                    }
                }
                builder.append('"');
                return;
            }
            if (value instanceof Number || value instanceof Boolean) {
                builder.append(value);
                return;
            }
            if (value instanceof Map<?, ?> mapValue) {
                builder.append('{');
                boolean first = true;
                for (Map.Entry<?, ?> entry : mapValue.entrySet()) {
                    if (!first) {
                        builder.append(',');
                    }
                    first = false;
                    writeValue(builder, String.valueOf(entry.getKey()));
                    builder.append(':');
                    writeValue(builder, entry.getValue());
                }
                builder.append('}');
                return;
            }
            if (value instanceof Iterable<?> iterable) {
                builder.append('[');
                boolean first = true;
                for (Object entry : iterable) {
                    if (!first) {
                        builder.append(',');
                    }
                    first = false;
                    writeValue(builder, entry);
                }
                builder.append(']');
                return;
            }
            throw new IllegalArgumentException("Unsupported JSON value type: " + value.getClass().getName());
        }

        private static final class Parser {
            private final String input;
            private int index;

            private Parser(String input) {
                this.input = input;
            }

            private Object parse() {
                skipWhitespace();
                Object value = parseValue();
                skipWhitespace();
                if (index != input.length()) {
                    throw error("Unexpected trailing content");
                }
                return value;
            }

            private Object parseValue() {
                skipWhitespace();
                if (index >= input.length()) {
                    throw error("Unexpected end of input");
                }
                char current = input.charAt(index);
                return switch (current) {
                    case '{' -> parseObject();
                    case '[' -> parseArray();
                    case '"' -> parseString();
                    case 't' -> parseLiteral("true", Boolean.TRUE);
                    case 'f' -> parseLiteral("false", Boolean.FALSE);
                    case 'n' -> parseLiteral("null", null);
                    default -> {
                        if (current == '-' || Character.isDigit(current)) {
                            yield parseNumber();
                        }
                        throw error("Unexpected token: " + current);
                    }
                };
            }

            private Map<String, Object> parseObject() {
                expect('{');
                skipWhitespace();
                Map<String, Object> values = new LinkedHashMap<>();
                if (peek('}')) {
                    index++;
                    return values;
                }
                while (true) {
                    skipWhitespace();
                    String key = parseString();
                    skipWhitespace();
                    expect(':');
                    Object value = parseValue();
                    values.put(key, value);
                    skipWhitespace();
                    if (peek('}')) {
                        index++;
                        return values;
                    }
                    expect(',');
                }
            }

            private List<Object> parseArray() {
                expect('[');
                skipWhitespace();
                List<Object> values = new ArrayList<>();
                if (peek(']')) {
                    index++;
                    return values;
                }
                while (true) {
                    values.add(parseValue());
                    skipWhitespace();
                    if (peek(']')) {
                        index++;
                        return values;
                    }
                    expect(',');
                }
            }

            private String parseString() {
                expect('"');
                StringBuilder builder = new StringBuilder();
                while (index < input.length()) {
                    char current = input.charAt(index++);
                    if (current == '"') {
                        return builder.toString();
                    }
                    if (current != '\\') {
                        builder.append(current);
                        continue;
                    }
                    if (index >= input.length()) {
                        throw error("Unexpected end of input in string escape");
                    }
                    char escaped = input.charAt(index++);
                    switch (escaped) {
                        case '"', '\\', '/' -> builder.append(escaped);
                        case 'b' -> builder.append('\b');
                        case 'f' -> builder.append('\f');
                        case 'n' -> builder.append('\n');
                        case 'r' -> builder.append('\r');
                        case 't' -> builder.append('\t');
                        case 'u' -> builder.append(parseUnicode());
                        default -> throw error("Invalid string escape: \\" + escaped);
                    }
                }
                throw error("Unterminated string");
            }

            private char parseUnicode() {
                if (index + 4 > input.length()) {
                    throw error("Incomplete unicode escape");
                }
                String hex = input.substring(index, index + 4);
                index += 4;
                try {
                    return (char) Integer.parseInt(hex, 16);
                } catch (NumberFormatException ignored) {
                    throw error("Invalid unicode escape: " + hex);
                }
            }

            private Object parseLiteral(String token, Object value) {
                if (!input.startsWith(token, index)) {
                    throw error("Unexpected token");
                }
                index += token.length();
                return value;
            }

            private Number parseNumber() {
                int start = index;
                if (input.charAt(index) == '-') {
                    index++;
                }
                while (index < input.length() && Character.isDigit(input.charAt(index))) {
                    index++;
                }
                if (index < input.length() && input.charAt(index) == '.') {
                    index++;
                    while (index < input.length() && Character.isDigit(input.charAt(index))) {
                        index++;
                    }
                    return Double.parseDouble(input.substring(start, index));
                }
                return Long.parseLong(input.substring(start, index));
            }

            private void skipWhitespace() {
                while (index < input.length() && Character.isWhitespace(input.charAt(index))) {
                    index++;
                }
            }

            private void expect(char expected) {
                if (index >= input.length() || input.charAt(index) != expected) {
                    throw error("Expected '" + expected + "'");
                }
                index++;
            }

            private boolean peek(char expected) {
                return index < input.length() && input.charAt(index) == expected;
            }

            private IllegalArgumentException error(String message) {
                return new IllegalArgumentException(message + " at index " + index);
            }
        }
    }
}
