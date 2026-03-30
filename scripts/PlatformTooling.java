import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public final class PlatformTooling {

    private static final Pattern APP_ID_PATTERN = Pattern.compile("^[a-z0-9]+(?:-[a-z0-9]+)*$");
    private static final Pattern PACKAGE_PREFIX_PATTERN = Pattern.compile("^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)+$");
    private static final String DERIVED_APP_LIFECYCLE_METADATA_PATH = "docs/ai/derived-app-lifecycle.json";
    private static final String STRUCTURED_APP_TEMPLATE_CONTRACT_PATH = "docs/ai/structured-app-template-contract.json";
    private static final String DERIVED_APP_TEMPLATE_MAP_PATH =
            "docs/ai/template-classifications/default-derived-app-template-map.json";

    private PlatformTooling() {
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> options = parseOptions(args);
        String command = options.get("_command");
        if (command == null || command.isBlank()) {
            throw new IllegalArgumentException("Missing command");
        }

        Path repoRoot = Path.of(options.getOrDefault("repo-root", ".")).toAbsolutePath().normalize();

        switch (command) {
            case "assembly-compatibility" -> {
                Map<String, Object> result = runCompatibilitySuite(repoRoot);
                if (!Boolean.TRUE.equals(result.get("ok"))) {
                    StringBuilder builder = new StringBuilder("App assembly compatibility verification failed");
                    for (String issue : asOptionalStringList(result.get("issues"))) {
                        builder.append(System.lineSeparator()).append("- ").append(issue);
                    }
                    throw new IllegalStateException(builder.toString());
                }
                System.out.println(SimpleJson.stringify(result));
            }
            case "generated-app-verify" -> {
                Path targetDir = requiredPath(options, "target");
                Map<String, Object> result = verifyGeneratedApp(targetDir, repoRoot);
                if (!Boolean.TRUE.equals(result.get("verified"))) {
                    StringBuilder builder = new StringBuilder("Generated app verification failed");
                    for (String issue : asOptionalStringList(result.get("issues"))) {
                        builder.append(System.lineSeparator()).append("- ").append(issue);
                    }
                    throw new IllegalStateException(builder.toString());
                }
                System.out.println(SimpleJson.stringify(result));
            }
            case "upgrade-targets" -> {
                String target = options.get("target");
                Map<String, Object> result = listPlatformUpgradeTargets(
                        target == null ? null : Path.of(target).toAbsolutePath().normalize(),
                        repoRoot);
                System.out.println(SimpleJson.stringify(result));
            }
            case "upgrade-advisory" -> {
                String target = options.get("target");
                Map<String, Object> result = readPlatformReleaseAdvisory(
                        target == null ? null : Path.of(target).toAbsolutePath().normalize(),
                        repoRoot);
                System.out.println(SimpleJson.stringify(result));
            }
            case "upgrade-evaluate" -> {
                Path targetDir = requiredPath(options, "target");
                Map<String, Object> result = evaluateDerivedAppUpgrade(targetDir, repoRoot);
                System.out.println(SimpleJson.stringify(result));
            }
            case "upgrade-execute" -> {
                Path targetDir = requiredPath(options, "target");
                boolean apply = Boolean.parseBoolean(options.getOrDefault("apply", "false"));
                Map<String, Object> result = executeDerivedAppUpgrade(targetDir, apply, repoRoot);
                System.out.println(SimpleJson.stringify(result));
            }
            default -> throw new IllegalArgumentException("Unknown command: " + command);
        }
    }

    private static Map<String, Object> verifyGeneratedApp(Path targetDir, Path repoRoot)
            throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "java",
                repoRoot.resolve("scripts/VerifyDerivedApp.java").toAbsolutePath().normalize().toString(),
                targetDir.toAbsolutePath().normalize().toString());
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        String output = new String(process.getInputStream().readAllBytes());
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            List<String> issues = output.lines()
                    .filter(line -> line.startsWith("- "))
                    .map(line -> line.substring(2))
                    .toList();
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("verified", false);
            payload.put("issues", issues);
            payload.put("targetDir", targetDir.toString());
            return payload;
        }
        return SimpleJson.parseObject(output);
    }

    private static Map<String, Object> runCompatibilitySuite(Path repoRoot) throws IOException, InterruptedException {
        List<String> issues = new ArrayList<>();
        Map<String, Object> registry = readJson(repoRoot.resolve("docs/ai/module-registry.json"));
        Map<String, Object> contract = readJson(repoRoot.resolve("docs/ai/app-assembly-contract.json"));
        Map<String, Object> suite = readJson(repoRoot.resolve("docs/ai/compatibility/app-assembly-suite.json"));

        for (String relativePath : getNormativeAssetPaths(contract)) {
            if (!Files.exists(repoRoot.resolve(relativePath))) {
                issues.add("Missing normative asset referenced by contract: " + relativePath);
            }
        }

        Path tempRoot = Files.createTempDirectory("fsp-assembly-compatibility-");
        List<Map<String, Object>> implementationResults = new ArrayList<>();

        try {
            List<Map<String, Object>> implementations = asMapList(
                    contract.get("referenceImplementations"),
                    "Contract must include referenceImplementations");
            for (Map<String, Object> implementation : implementations) {
                String implementationId = asString(implementation.get("id"));
                if (!"java-cli".equals(implementationId)) {
                    continue;
                }

                List<String> validFixtures = new ArrayList<>();
                List<String> invalidFixtures = new ArrayList<>();
                JavaRuntime runtime = prepareJavaRuntime(
                        repoRoot.resolve(asString(implementation.get("workspace"))),
                        asString(implementation.get("mainClass")));

                for (Map<String, Object> fixture : asMapList(
                        asMap(suite.get("fixtures"), "Suite must include fixtures").get("invalid"),
                        "Suite invalid fixtures must be an array")) {
                    String fixtureId = asString(fixture.get("id"));
                    String manifestPath = asString(fixture.get("manifestPath"));
                    String expectedError = asString(fixture.get("expectedErrorIncludes"));
                    Path outputDir = tempRoot.resolve(implementationId).resolve("invalid-" + fixtureId);
                    CommandResult result = runAssembly(runtime, repoRoot, repoRoot.resolve(manifestPath), outputDir);
                    if (result.exitCode() == 0) {
                        issues.add("Invalid fixture unexpectedly passed for " + implementationId + ": " + fixtureId);
                    } else if (!result.output().contains(expectedError)) {
                        issues.add("Invalid fixture " + fixtureId + " failed with unexpected error for "
                                + implementationId + ": " + result.output());
                    }
                    invalidFixtures.add(fixtureId);
                }

                for (Map<String, Object> fixture : asMapList(
                        asMap(suite.get("fixtures"), "Suite must include fixtures").get("valid"),
                        "Suite valid fixtures must be an array")) {
                    String fixtureId = asString(fixture.get("id"));
                    Path manifestPath = repoRoot.resolve(asString(fixture.get("manifestPath")));
                    Path outputDir = tempRoot.resolve(implementationId).resolve(fixtureId);
                    CommandResult result = runAssembly(runtime, repoRoot, manifestPath, outputDir);
                    if (result.exitCode() != 0) {
                        issues.add("Valid fixture failed for " + implementationId + ": " + fixtureId
                                + System.lineSeparator() + result.output());
                        validFixtures.add(fixtureId);
                        continue;
                    }

                    Map<String, Object> manifest = readJson(outputDir.resolve("app-manifest.json"));
                    List<String> selectedModules = asOptionalStringList(manifest.get("modules"));
                    List<String> expectedModules = asOptionalStringList(fixture.get("expectedSelectedModules"));
                    if (!selectedModules.equals(expectedModules)) {
                        issues.add("Valid fixture " + fixtureId
                                + " selectedModules did not match compatibility expectation for " + implementationId);
                    }

                    Map<String, Object> profiles = asMap(registry.get("profiles"), "Registry must include profiles");
                    List<String> profileModules = asOptionalStringList(
                            asMap(profiles.get(asString(fixture.get("expectedProfile"))), "Profile must be object")
                                    .get("modules"));
                    if (!profileModules.equals(expectedModules)) {
                        issues.add("Valid fixture " + fixtureId + " did not match registry profile "
                                + asString(fixture.get("expectedProfile")));
                    }

                    Map<String, Object> verification = verifyGeneratedApp(outputDir, repoRoot);
                    if (!Boolean.TRUE.equals(verification.get("verified"))) {
                        for (String issue : asOptionalStringList(verification.get("issues"))) {
                            issues.add(implementationId + "/" + fixtureId + ": " + issue);
                        }
                    }

                    validFixtures.add(fixtureId);
                }

                Map<String, Object> implementationResult = new LinkedHashMap<>();
                implementationResult.put("id", implementationId);
                implementationResult.put("validFixtures", validFixtures);
                implementationResult.put("invalidFixtures", invalidFixtures);
                implementationResults.add(implementationResult);
            }
        } finally {
            deleteRecursively(tempRoot);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("ok", issues.isEmpty());
        result.put("issues", issues);
        result.put("implementations", implementationResults);
        return result;
    }

    private static Map<String, Object> listPlatformUpgradeTargets(Path targetDir, Path repoRoot) throws IOException {
        Map<String, Object> releaseHistory = readJson(repoRoot.resolve("docs/ai/platform-release-history.json"));
        Map<String, Map<String, Object>> releaseIndex = buildReleaseIndex(releaseHistory);

        Map<String, Object> lifecycleMetadata = null;
        String sourceReleaseId = null;
        List<String> selectedModules = null;

        if (targetDir != null) {
            lifecycleMetadata = readJson(targetDir.resolve(DERIVED_APP_LIFECYCLE_METADATA_PATH));
            sourceReleaseId = asStringOrNull(asMap(
                    lifecycleMetadata.get("sourcePlatform"),
                    "Lifecycle metadata must include sourcePlatform").get("releaseId"));
            selectedModules = asOptionalStringList(lifecycleMetadata.get("selectedModules"));
        }

        List<Map<String, Object>> supportedPaths = asMapList(
                releaseHistory.get("supportedUpgradePaths"),
                "Release history supportedUpgradePaths must be an array");
        List<Map<String, Object>> relevantPaths = new ArrayList<>();
        for (Map<String, Object> upgradePath : supportedPaths) {
            if (sourceReleaseId == null || sourceReleaseId.equals(asString(upgradePath.get("sourceReleaseId")))) {
                relevantPaths.add(upgradePath);
            }
        }

        List<Map<String, Object>> availableTargetReleases = new ArrayList<>();
        if (sourceReleaseId != null) {
            for (Map<String, Object> upgradePath : relevantPaths) {
                if (!"supported".equals(asString(upgradePath.get("supportStatus")))) {
                    continue;
                }
                Map<String, Object> release = releaseIndex.getOrDefault(
                        asString(upgradePath.get("targetReleaseId")),
                        Map.of(
                                "releaseId", asString(upgradePath.get("targetReleaseId")),
                                "version", "unavailable",
                                "supportStatus", "unknown",
                                "lineageParentReleaseId", null,
                                "advisoryAsset", asStringOrNull(upgradePath.get("advisoryAsset"))));
                Map<String, Object> payload = new LinkedHashMap<>();
                payload.put("releaseId", release.get("releaseId"));
                payload.put("version", release.get("version"));
                payload.put("supportStatus", release.get("supportStatus"));
                payload.put("lineageParentReleaseId", release.get("lineageParentReleaseId"));
                payload.put("advisoryAsset", release.get("advisoryAsset"));
                availableTargetReleases.add(payload);
            }
        }

        Map<String, Object> compatibilityWindow = asMap(
                releaseHistory.get("compatibilityWindow"),
                "Release history must include compatibilityWindow");
        Map<String, Object> platform = asMap(releaseHistory.get("platform"), "Release history must include platform");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("platformId", platform.get("id"));
        result.put("currentReleaseId", releaseHistory.get("currentReleaseId"));
        result.put("sourceReleaseId", sourceReleaseId);
        result.put("selectedModules", selectedModules);
        result.put("recognizedReleases", asMapList(releaseHistory.get("releases"), "Release history releases must be an array"));
        result.put("supportedUpgradePaths", relevantPaths);
        result.put("availableTargetReleases", availableTargetReleases);
        result.put("defaultTargetReleaseId", compatibilityWindow.get("defaultTargetReleaseId"));
        result.put("lookupEntrypoint", compatibilityWindow.get("upgradeTargetSelectionEntrypoint"));
        return result;
    }

    private static Map<String, Object> evaluateDerivedAppUpgrade(Path targetDir, Path repoRoot) throws IOException {
        List<String> issues = new ArrayList<>();
        Map<String, Object> lifecycleContract = readJson(repoRoot.resolve("docs/ai/derived-app-lifecycle-contract.json"));
        Map<String, Object> platformRelease = readJson(repoRoot.resolve("docs/ai/platform-release.json"));
        Map<String, Object> releaseHistory = readJson(repoRoot.resolve("docs/ai/platform-release-history.json"));
        Map<String, Object> currentRegistry = readJson(repoRoot.resolve("docs/ai/module-registry.json"));

        Map<String, Object> upgradeEvaluation = asMap(
                lifecycleContract.get("upgradeEvaluation"),
                "Lifecycle contract must include upgradeEvaluation");
        for (String relativePath : asOptionalStringList(upgradeEvaluation.get("requiredGeneratedInputs"))) {
            if (!Files.exists(targetDir.resolve(relativePath))) {
                issues.add("Missing upgrade evaluation input: " + relativePath);
            }
        }

        Map<String, Object> currentRelease = asMap(platformRelease.get("currentRelease"), "Platform release must include currentRelease");
        Map<String, Object> upgradeSupport = asMap(platformRelease.get("upgradeSupport"), "Platform release must include upgradeSupport");

        if (!issues.isEmpty()) {
            return failureUpgradeEvaluation(issues, currentRelease, upgradeSupport);
        }

        Map<String, Object> generatedOutput = asMap(
                lifecycleContract.get("generatedOutput"),
                "Lifecycle contract must include generatedOutput");
        Map<String, Object> lifecycleMetadata = readJson(targetDir.resolve(asString(generatedOutput.get("lifecycleMetadata"))));
        Map<String, Object> manifest = readJson(targetDir.resolve(asString(generatedOutput.get("applicationManifest"))));
        Map<String, Object> context = readJson(targetDir.resolve(asString(generatedOutput.get("generatedContext"))));

        for (String fieldPath : asOptionalStringList(lifecycleContract.get("requiredLifecycleFields"))) {
            if (resolveNestedField(lifecycleMetadata, fieldPath) == null) {
                issues.add("Missing lifecycle metadata field: " + fieldPath);
            }
        }

        Map<String, Object> sourcePlatform = asMap(
                lifecycleMetadata.get("sourcePlatform"),
                "Lifecycle metadata must include sourcePlatform");
        String sourceRelease = asStringOrDefault(sourcePlatform.get("releaseId"), "unavailable");
        String targetRelease = asStringOrDefault(
                upgradeSupport.get("defaultTargetReleaseId"),
                asStringOrDefault(asMap(
                        releaseHistory.get("compatibilityWindow"),
                        "Release history must include compatibilityWindow").get("defaultTargetReleaseId"),
                        asStringOrDefault(currentRelease.get("releaseId"), "unavailable")));

        if (!asOptionalStringList(upgradeSupport.get("supportedSourcePlatformIds"))
                .contains(asStringOrDefault(sourcePlatform.get("id"), "unavailable"))) {
            issues.add("Unsupported source platform id: " + asStringOrDefault(sourcePlatform.get("id"), "unavailable"));
        }

        Map<String, Map<String, Object>> releaseIndex = buildReleaseIndex(releaseHistory);
        if (!releaseIndex.containsKey(sourceRelease)) {
            issues.add("Unknown source platform release: " + sourceRelease);
        }

        if (!asOptionalStringList(asMap(releaseHistory.get("compatibilityWindow"), "Release history must include compatibilityWindow")
                .get("supportedSourceReleaseIds")).contains(sourceRelease)) {
            issues.add("Source platform release is outside the supported upgrade window: " + sourceRelease);
        }

        Map<String, Object> supportedUpgradePath = findSupportedUpgradePath(releaseHistory, sourceRelease, targetRelease);
        if (!sourceRelease.equals(targetRelease) && supportedUpgradePath == null) {
            issues.add("Unsupported upgrade path: " + sourceRelease + " -> " + targetRelease);
        }

        if (!asOptionalStringList(upgradeSupport.get("supportedLifecycleMetadataVersions"))
                .contains(asStringOrDefault(lifecycleMetadata.get("schemaVersion"), "unavailable"))) {
            issues.add("Unsupported lifecycle metadata version: "
                    + asStringOrDefault(lifecycleMetadata.get("schemaVersion"), "unavailable"));
        }

        if (!asOptionalStringList(upgradeSupport.get("supportedLifecycleContractVersions"))
                .contains(asStringOrDefault(lifecycleMetadata.get("contractVersion"), "unavailable"))) {
            issues.add("Unsupported lifecycle contract version: "
                    + asStringOrDefault(lifecycleMetadata.get("contractVersion"), "unavailable"));
        }

        if (!asOptionalStringList(upgradeSupport.get("supportedAssemblyContractVersions"))
                .contains(asStringOrDefault(sourcePlatform.get("assemblyContractVersion"), "unavailable"))) {
            issues.add("Unsupported assembly contract version: "
                    + asStringOrDefault(sourcePlatform.get("assemblyContractVersion"), "unavailable"));
        }

        List<String> lifecycleModules = asOptionalStringList(lifecycleMetadata.get("selectedModules"));
        List<String> manifestModules = asOptionalStringList(manifest.get("modules"));
        if (!lifecycleModules.equals(manifestModules)) {
            issues.add("Lifecycle metadata selectedModules do not match app-manifest.json");
        }
        if (!asOptionalStringList(context.get("selectedModules")).equals(manifestModules)) {
            issues.add("docs/ai/context.json selectedModules do not match app-manifest.json");
        }

        Set<String> knownModules = new LinkedHashSet<>();
        for (Map<String, Object> module : asMapList(currentRegistry.get("modules"), "Registry must include modules")) {
            knownModules.add(asString(module.get("id")));
        }
        for (String moduleId : lifecycleModules) {
            if (!knownModules.contains(moduleId)) {
                issues.add("Selected module is unknown to target platform release: " + moduleId);
            }
        }

        boolean compatible = issues.isEmpty();
        String recommendedAction;
        if (sourceRelease.equals(targetRelease)) {
            recommendedAction = "already-on-target-platform-release";
        } else if (compatible) {
            recommendedAction = supportedUpgradePath != null && supportedUpgradePath.get("recommendedAction") != null
                    ? asString(supportedUpgradePath.get("recommendedAction"))
                    : asStringOrDefault(upgradeSupport.get("recommendedCompatibleAction"), "manual-review");
        } else {
            recommendedAction = asStringOrDefault(upgradeSupport.get("recommendedIncompatibleAction"), "manual-review");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("compatible", compatible);
        result.put("issues", issues);
        result.put("sourcePlatformRelease", sourceRelease);
        result.put("targetPlatformRelease", targetRelease);
        result.put("recommendedAction", recommendedAction);
        return result;
    }

    private static Map<String, Object> readPlatformReleaseAdvisory(Path targetDir, Path repoRoot) throws IOException {
        Map<String, Object> advisory = readJson(repoRoot.resolve("docs/ai/platform-release-advisory.json"));
        List<String> selectedModules = null;
        if (targetDir != null) {
            Map<String, Object> lifecycle = readJson(targetDir.resolve(DERIVED_APP_LIFECYCLE_METADATA_PATH));
            selectedModules = asOptionalStringList(lifecycle.get("selectedModules"));
        }

        List<Map<String, Object>> changes = asMapList(advisory.get("changes"), "Advisory changes must be an array");
        List<Map<String, Object>> relevantChanges = new ArrayList<>();
        if (selectedModules == null) {
            relevantChanges.addAll(changes);
        } else {
            for (Map<String, Object> change : changes) {
                for (String moduleId : asOptionalStringList(change.get("impactedModules"))) {
                    if (selectedModules.contains(moduleId)) {
                        relevantChanges.add(change);
                        break;
                    }
                }
            }
        }

        LinkedHashSet<String> recommendedChecks = new LinkedHashSet<>();
        for (Map<String, Object> change : relevantChanges) {
            recommendedChecks.addAll(asOptionalStringList(change.get("recommendedChecks")));
        }

        Map<String, Object> currentRelease = asMap(advisory.get("currentRelease"), "Advisory must include currentRelease");
        Map<String, Object> previousRelease = asMap(advisory.get("previousRelease"), "Advisory must include previousRelease");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("releaseId", currentRelease.get("releaseId"));
        result.put("previousReleaseId", previousRelease.get("releaseId"));
        result.put("overallCompatibilityPosture", advisory.get("overallCompatibilityPosture"));
        result.put("selectedModules", selectedModules);
        result.put("summary", advisory.get("summary"));
        result.put("changes", changes);
        result.put("relevantChanges", relevantChanges);
        result.put("recommendedChecks", new ArrayList<>(recommendedChecks));
        result.put("recommendedNextActions", asOptionalStringList(advisory.get("recommendedNextActions")));
        return result;
    }

    private static Map<String, Object> planDerivedAppUpgrade(Path targetDir, Path repoRoot) throws IOException {
        Map<String, Object> evaluation = evaluateDerivedAppUpgrade(targetDir, repoRoot);
        Map<String, Object> advisory = readPlatformReleaseAdvisory(targetDir, repoRoot);
        ManagedAssets managedAssets = buildDerivedAppManagedAssetMap(targetDir, repoRoot);
        Map<String, Object> templateMap = readJson(repoRoot.resolve(DERIVED_APP_TEMPLATE_MAP_PATH));
        List<Map<String, Object>> autoApplyItems = new ArrayList<>();

        for (Map.Entry<String, String> entry : managedAssets.managedAssets().entrySet()) {
            Path path = targetDir.resolve(entry.getKey());
            String currentContents = Files.exists(path) ? Files.readString(path) : null;
            if (entry.getValue().equals(currentContents)) {
                continue;
            }

            String category = "managed-doc-asset";
            if (entry.getKey().startsWith("docs/ai/schemas/")) {
                category = "managed-schema-asset";
            } else if (STRUCTURED_APP_TEMPLATE_CONTRACT_PATH.equals(entry.getKey())
                    || entry.getKey().startsWith("docs/ai/template-classifications/")) {
                category = "managed-template-metadata";
            } else if (entry.getKey().startsWith("scripts/")) {
                category = "managed-verifier-script";
            } else if ("docs/ai/context.json".equals(entry.getKey())) {
                category = "generated-context-refresh";
            } else if (DERIVED_APP_LIFECYCLE_METADATA_PATH.equals(entry.getKey())) {
                category = "generated-lifecycle-refresh";
            }

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("path", entry.getKey());
            item.put("action", currentContents == null ? "create" : "update");
            item.put("category", category);
            Map<String, Object> templateEntry = matchTemplateEntry(templateMap, entry.getKey());
            if (templateEntry != null) {
                item.put("templateUnitType", templateEntry.get("unitType"));
                item.put("ownership", templateEntry.get("ownership"));
                if (templateEntry.containsKey("slotId")) {
                    item.put("slotId", templateEntry.get("slotId"));
                }
                if (templateEntry.containsKey("moduleId")) {
                    item.put("moduleId", templateEntry.get("moduleId"));
                }
            }
            autoApplyItems.add(item);
        }

        Map<String, Object> plan = new LinkedHashMap<>();
        plan.put("planVersion", "fsp-derived-app-upgrade-plan/v1");
        plan.put("contractVersion", managedAssets.executionContract().get("schemaVersion"));
        plan.put("dryRun", true);
        plan.put("compatible", evaluation.get("compatible"));
        plan.put("sourcePlatformRelease", evaluation.get("sourcePlatformRelease"));
        plan.put("targetPlatformRelease", evaluation.get("targetPlatformRelease"));
        plan.put("selectedModules", managedAssets.selectedModules());
        plan.put("autoApplyItems", autoApplyItems);
        plan.put("manualInterventionItems", buildManualInterventionItems(advisory, evaluation));
        plan.put("postUpgradeValidation", managedAssets.executionContract().get("postUpgradeValidation"));
        plan.put("recommendedNextActions", advisory.get("recommendedNextActions"));
        return plan;
    }

    private static Map<String, Object> executeDerivedAppUpgrade(Path targetDir, boolean apply, Path repoRoot) throws IOException {
        Map<String, Object> plan = planDerivedAppUpgrade(targetDir, repoRoot);
        if (!apply || !Boolean.TRUE.equals(plan.get("compatible"))) {
            Map<String, Object> payload = new LinkedHashMap<>(plan);
            payload.put("dryRun", !apply);
            payload.put("applied", false);
            payload.put("appliedItems", List.of());
            return payload;
        }

        ManagedAssets managedAssets = buildDerivedAppManagedAssetMap(targetDir, repoRoot);
        List<Map<String, Object>> appliedItems = new ArrayList<>();
        for (Map<String, Object> item : asMapList(plan.get("autoApplyItems"), "autoApplyItems must be an array")) {
            String relativePath = asString(item.get("path"));
            Path outputPath = targetDir.resolve(relativePath);
            if (outputPath.getParent() != null) {
                Files.createDirectories(outputPath.getParent());
            }
            Files.writeString(
                    outputPath,
                    managedAssets.managedAssets().get(relativePath),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            if ("scripts/verify-derived-app.sh".equals(relativePath)) {
                outputPath.toFile().setExecutable(true, false);
            }
            appliedItems.add(item);
        }

        Map<String, Object> payload = new LinkedHashMap<>(plan);
        payload.put("dryRun", false);
        payload.put("applied", true);
        payload.put("appliedItems", appliedItems);
        return payload;
    }

    private static ManagedAssets buildDerivedAppManagedAssetMap(Path targetDir, Path repoRoot) throws IOException {
        Map<String, Object> assemblyContract = readJson(repoRoot.resolve("docs/ai/app-assembly-contract.json"));
        Map<String, Object> lifecycleContract = readJson(repoRoot.resolve("docs/ai/derived-app-lifecycle-contract.json"));
        Map<String, Object> executionContract = readJson(repoRoot.resolve("docs/ai/derived-app-upgrade-execution-contract.json"));
        Map<String, Object> platformRelease = readJson(repoRoot.resolve("docs/ai/platform-release.json"));
        Map<String, Object> verificationContract = readJson(repoRoot.resolve("docs/ai/generated-app-verification-contract.json"));
        Map<String, Object> registry = readJson(repoRoot.resolve("docs/ai/module-registry.json"));
        Map<String, Object> manifest = readJson(targetDir.resolve("app-manifest.json"));
        List<String> selectedModules = validateManifest(manifest, registry, assemblyContract);

        LinkedHashMap<String, String> managedAssets = new LinkedHashMap<>();
        for (String relativePath : getManagedUpgradeAssetPaths(assemblyContract)) {
            if ("docs/ai/context.json".equals(relativePath)) {
                managedAssets.put(relativePath, ensureTrailingNewline(buildGeneratedContext(
                        manifest,
                        selectedModules,
                        registry,
                        platformRelease)));
                continue;
            }
            if (DERIVED_APP_LIFECYCLE_METADATA_PATH.equals(relativePath)) {
                managedAssets.put(relativePath, ensureTrailingNewline(buildDerivedAppLifecycle(
                        manifest,
                        selectedModules,
                        registry,
                        assemblyContract,
                        verificationContract,
                        lifecycleContract,
                        platformRelease)));
                continue;
            }
            if ("scripts/verify-derived-app.sh".equals(relativePath)) {
                managedAssets.put(relativePath, buildLocalVerifyScript());
                continue;
            }
            managedAssets.put(relativePath, Files.readString(repoRoot.resolve(relativePath)));
        }

        managedAssets.put(
                "docs/ai/derived-app-upgrade-execution-contract.json",
                Files.readString(repoRoot.resolve("docs/ai/derived-app-upgrade-execution-contract.json")));
        managedAssets.put(
                "docs/ai/schemas/derived-app-upgrade-execution-contract.schema.json",
                Files.readString(repoRoot.resolve("docs/ai/schemas/derived-app-upgrade-execution-contract.schema.json")));

        return new ManagedAssets(managedAssets, selectedModules, executionContract);
    }

    private static List<String> getManagedUpgradeAssetPaths(Map<String, Object> assemblyContract) {
        List<String> results = new ArrayList<>();
        for (String relativePath : getRequiredGeneratedFiles(assemblyContract)) {
            if (relativePath.startsWith("docs/ai/")
                    || "scripts/VerifyDerivedApp.java".equals(relativePath)
                    || "scripts/verify-derived-app.sh".equals(relativePath)) {
                results.add(relativePath);
            }
        }
        return results;
    }

    private static String buildGeneratedContext(
            Map<String, Object> manifest,
            List<String> selectedModules,
            Map<String, Object> registry,
            Map<String, Object> platformRelease) {
        Map<String, Object> context = new LinkedHashMap<>();
        context.put("schemaVersion", "fsp-derived-app-context/v1");

        Map<String, Object> sourcePlatform = new LinkedHashMap<>();
        Map<String, Object> platform = asMap(platformRelease.get("platform"), "platform release must include platform");
        Map<String, Object> currentRelease = asMap(platformRelease.get("currentRelease"), "platform release must include currentRelease");
        sourcePlatform.put("id", platform.get("id"));
        sourcePlatform.put("name", platform.get("name"));
        sourcePlatform.put("releaseId", currentRelease.get("releaseId"));
        sourcePlatform.put("version", currentRelease.get("version"));
        context.put("sourcePlatform", sourcePlatform);
        context.put("application", manifest.get("application"));
        context.put("selectedModules", selectedModules);
        context.put("requiredCoreModules", requiredCoreModuleIds(registry));

        Map<String, Object> contractInputs = new LinkedHashMap<>();
        contractInputs.put("manifest", "app-manifest.json");
        contractInputs.put("aiSolutionInputContract", "docs/ai/ai-solution-input-contract.json");
        contractInputs.put("moduleRegistry", "docs/ai/module-registry.json");
        contractInputs.put("aiToolOrchestrationContract", "docs/ai/ai-tool-orchestration-contract.json");
        contractInputs.put("structuredAppTemplateContract", STRUCTURED_APP_TEMPLATE_CONTRACT_PATH);
        contractInputs.put("structuredAppTemplateMap", DERIVED_APP_TEMPLATE_MAP_PATH);
        contractInputs.put("assemblyContract", "docs/ai/app-assembly-contract.json");
        contractInputs.put("derivedAppLifecycleContract", "docs/ai/derived-app-lifecycle-contract.json");
        contractInputs.put("derivedAppUpgradeExecutionContract", "docs/ai/derived-app-upgrade-execution-contract.json");
        contractInputs.put("derivedAppLifecycleMetadata", DERIVED_APP_LIFECYCLE_METADATA_PATH);
        contractInputs.put("platformReleaseMetadata", "docs/ai/platform-release.json");
        contractInputs.put("platformReleaseHistory", "docs/ai/platform-release-history.json");
        contractInputs.put("platformReleaseAdvisory", "docs/ai/platform-release-advisory.json");
        contractInputs.put("generatedAppVerificationContract", "docs/ai/generated-app-verification-contract.json");
        context.put("contractInputs", contractInputs);

        Map<String, Object> validation = new LinkedHashMap<>();
        validation.put("local", "./scripts/verify-derived-app.sh");
        validation.put("repositoryOwned", "./scripts/platform-tool.sh generated-app verify <generated-app-dir>");
        validation.put("referenceVerifier", "./scripts/platform-tool.sh generated-app verify <generated-app-dir>");
        context.put("validation", validation);

        Map<String, Object> templateSystem = new LinkedHashMap<>();
        templateSystem.put("contract", STRUCTURED_APP_TEMPLATE_CONTRACT_PATH);
        templateSystem.put("classificationMap", DERIVED_APP_TEMPLATE_MAP_PATH);
        templateSystem.put("customizationPlaybook", "docs/ai/playbooks/customize-derived-app-template-boundaries.md");
        context.put("templateSystem", templateSystem);

        Map<String, Object> lifecycle = new LinkedHashMap<>();
        lifecycle.put("metadata", DERIVED_APP_LIFECYCLE_METADATA_PATH);
        lifecycle.put("repositoryOwnedUpgradeEvaluation", "./scripts/platform-tool.sh upgrade evaluate <generated-app-dir>");
        lifecycle.put("repositoryOwnedUpgradeTargetSelection", "./scripts/platform-tool.sh upgrade targets [generated-app-dir]");
        lifecycle.put("repositoryOwnedReleaseAdvisory", "./scripts/platform-tool.sh upgrade advisory [generated-app-dir]");
        lifecycle.put("repositoryOwnedUpgradeExecution", "./scripts/platform-tool.sh upgrade execute <generated-app-dir> [--apply]");
        lifecycle.put("derivedProfile", deriveProfileId(registry, selectedModules));
        context.put("lifecycle", lifecycle);

        return SimpleJson.stringify(context);
    }

    private static String buildDerivedAppLifecycle(
            Map<String, Object> manifest,
            List<String> selectedModules,
            Map<String, Object> registry,
            Map<String, Object> assemblyContract,
            Map<String, Object> verificationContract,
            Map<String, Object> lifecycleContract,
            Map<String, Object> platformRelease) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("schemaVersion", "fsp-derived-app-lifecycle/v1");
        metadata.put("contractVersion", lifecycleContract.get("schemaVersion"));
        metadata.put("application", manifest.get("application"));

        Map<String, Object> sourcePlatform = new LinkedHashMap<>();
        Map<String, Object> platform = asMap(platformRelease.get("platform"), "platform release must include platform");
        Map<String, Object> currentRelease = asMap(platformRelease.get("currentRelease"), "platform release must include currentRelease");
        sourcePlatform.put("id", platform.get("id"));
        sourcePlatform.put("name", platform.get("name"));
        sourcePlatform.put("releaseId", currentRelease.get("releaseId"));
        sourcePlatform.put("version", currentRelease.get("version"));
        sourcePlatform.put("assemblyContractVersion", assemblyContract.get("schemaVersion"));
        sourcePlatform.put("generatedAppVerificationContractVersion", verificationContract.get("schemaVersion"));
        metadata.put("sourcePlatform", sourcePlatform);
        metadata.put("selectedModules", selectedModules);
        metadata.put("requiredCoreModules", requiredCoreModuleIds(registry));
        metadata.put("derivedProfile", deriveProfileId(registry, selectedModules));

        Map<String, Object> templateSystem = new LinkedHashMap<>();
        templateSystem.put("templateContract", STRUCTURED_APP_TEMPLATE_CONTRACT_PATH);
        templateSystem.put("templateClassificationMap", DERIVED_APP_TEMPLATE_MAP_PATH);
        metadata.put("templateSystem", templateSystem);

        Map<String, Object> upgradeEvaluation = new LinkedHashMap<>();
        Map<String, Object> upgradeEvaluationContract = asMap(
                lifecycleContract.get("upgradeEvaluation"),
                "lifecycle contract must include upgradeEvaluation");
        Map<String, Object> normativeAssets = asMap(
                lifecycleContract.get("normativeAssets"),
                "lifecycle contract must include normativeAssets");
        upgradeEvaluation.put("repositoryOwnedEntrypoint", upgradeEvaluationContract.get("repositoryOwnedEntrypoint"));
        upgradeEvaluation.put("repositoryOwnedTargetSelectionEntrypoint", upgradeEvaluationContract.get("repositoryOwnedTargetSelectionEntrypoint"));
        upgradeEvaluation.put("repositoryOwnedAdvisoryEntrypoint", upgradeEvaluationContract.get("repositoryOwnedAdvisoryEntrypoint"));
        upgradeEvaluation.put("repositoryOwnedExecutionEntrypoint", upgradeEvaluationContract.get("repositoryOwnedExecutionEntrypoint"));
        upgradeEvaluation.put("platformReleaseMetadata", normativeAssets.get("platformReleaseMetadata"));
        upgradeEvaluation.put("platformReleaseHistory", normativeAssets.get("platformReleaseHistory"));
        upgradeEvaluation.put("platformReleaseAdvisory", normativeAssets.get("platformReleaseAdvisory"));
        upgradeEvaluation.put("upgradeExecutionContract", normativeAssets.get("upgradeExecutionContract"));
        metadata.put("upgradeEvaluation", upgradeEvaluation);

        return SimpleJson.stringify(metadata);
    }

    private static String buildLocalVerifyScript() {
        return "#!/usr/bin/env bash\n"
                + "set -euo pipefail\n\n"
                + "ROOT_DIR=\"$(cd \"$(dirname \"${BASH_SOURCE[0]}\")/..\" && pwd)\"\n"
                + "TARGET_DIR=\"${1:-$ROOT_DIR}\"\n\n"
                + "java \"$ROOT_DIR/scripts/VerifyDerivedApp.java\" \"$TARGET_DIR\"\n";
    }

    private static List<Map<String, Object>> buildManualInterventionItems(
            Map<String, Object> advisory,
            Map<String, Object> evaluation) {
        List<Map<String, Object>> items = new ArrayList<>();
        if (!Boolean.TRUE.equals(evaluation.get("compatible"))) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", "compatibility-blocker");
            item.put("reason", "upgrade-evaluation-blocked");
            item.put("issues", evaluation.get("issues"));
            items.add(item);
        }
        for (Map<String, Object> change : asMapList(advisory.get("relevantChanges"), "relevantChanges must be array")) {
            if ("additive".equals(asStringOrDefault(change.get("compatibility"), "unknown"))) {
                continue;
            }
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", change.get("id"));
            item.put("reason", change.get("summary"));
            item.put("compatibility", change.get("compatibility"));
            item.put("impactedModules", asOptionalStringList(change.get("impactedModules")));
            item.put("recommendedChecks", asOptionalStringList(change.get("recommendedChecks")));
            items.add(item);
        }
        return items;
    }

    private static Map<String, Object> failureUpgradeEvaluation(
            List<String> issues,
            Map<String, Object> currentRelease,
            Map<String, Object> upgradeSupport) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("compatible", false);
        result.put("issues", issues);
        result.put("sourcePlatformRelease", "unavailable");
        result.put("targetPlatformRelease", currentRelease.get("releaseId"));
        result.put("recommendedAction", upgradeSupport.get("recommendedIncompatibleAction"));
        return result;
    }

    private static JavaRuntime prepareJavaRuntime(Path workspace, String mainClass) throws IOException, InterruptedException {
        String mvnBin = defaultMvnBin();
        CommandResult build = runCommand(
                workspace,
                List.of(
                        mvnBin,
                        "-q",
                        "-DskipTests",
                        "package",
                        "dependency:build-classpath",
                        "-Dmdep.outputFile=target/runtime-classpath.txt",
                        "-DincludeScope=runtime"));
        if (build.exitCode() != 0) {
            throw new IllegalStateException("Unable to prepare Java runtime: " + build.output());
        }
        return new JavaRuntime(workspace, mainClass, workspace.resolve("target/runtime-classpath.txt"));
    }

    private static CommandResult runAssembly(
            JavaRuntime runtime,
            Path repoRoot,
            Path manifestPath,
            Path outputDir) throws IOException, InterruptedException {
        String classpath = "target/classes" + java.io.File.pathSeparator + Files.readString(runtime.runtimeClasspathFile()).trim();
        return runCommand(
                runtime.workspace(),
                List.of(
                        "java",
                        "-cp",
                        classpath,
                        runtime.mainClass(),
                        "--repo-root",
                        repoRoot.toString(),
                        "--manifest",
                        manifestPath.toString(),
                        "--output",
                        outputDir.toString()));
    }

    private static CommandResult runCommand(Path cwd, List<String> command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(cwd.toFile());
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        String output = new String(process.getInputStream().readAllBytes());
        int exitCode = process.waitFor();
        return new CommandResult(exitCode, output.trim());
    }

    private static String defaultMvnBin() {
        String fromEnv = System.getenv("MVN_BIN");
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }
        return Path.of(System.getProperty("user.home"), ".sdkman/candidates/maven/current/bin/mvn").toString();
    }

    private static List<String> getNormativeAssetPaths(Map<String, Object> contract) {
        List<String> results = new ArrayList<>();
        Map<String, Object> normativeAssets = asMap(contract.get("normativeAssets"), "Contract must include normativeAssets");
        for (Object value : normativeAssets.values()) {
            if (value instanceof String stringValue) {
                results.add(stringValue);
            }
        }
        return results;
    }

    private static Map<String, Map<String, Object>> buildReleaseIndex(Map<String, Object> releaseHistory) {
        Map<String, Map<String, Object>> index = new LinkedHashMap<>();
        for (Map<String, Object> release : asMapList(releaseHistory.get("releases"), "Release history releases must be an array")) {
            index.put(asString(release.get("releaseId")), release);
        }
        return index;
    }

    private static Map<String, Object> findSupportedUpgradePath(
            Map<String, Object> releaseHistory,
            String sourceReleaseId,
            String targetReleaseId) {
        for (Map<String, Object> upgradePath : asMapList(
                releaseHistory.get("supportedUpgradePaths"),
                "Release history supportedUpgradePaths must be an array")) {
            if (sourceReleaseId.equals(asString(upgradePath.get("sourceReleaseId")))
                    && targetReleaseId.equals(asString(upgradePath.get("targetReleaseId")))
                    && "supported".equals(asString(upgradePath.get("supportStatus")))) {
                return upgradePath;
            }
        }
        return null;
    }

    private static Map<String, Object> matchTemplateEntry(Map<String, Object> templateMap, String relativePath) {
        for (Map<String, Object> entry : asMapList(templateMap.get("entries"), "Template map entries must be an array")) {
            String matchKind = asString(entry.get("matchKind"));
            String path = asString(entry.get("path"));
            if ("exact".equals(matchKind) && path.equals(relativePath)) {
                return entry;
            }
            if ("prefix".equals(matchKind) && relativePath.startsWith(path)) {
                return entry;
            }
        }
        return null;
    }

    private static Object resolveNestedField(Map<String, Object> root, String fieldPath) {
        Object current = root;
        for (String part : fieldPath.split("\\.")) {
            if (!(current instanceof Map<?, ?> map)) {
                return null;
            }
            current = map.get(part);
        }
        return current;
    }

    private static String deriveProfileId(Map<String, Object> registry, List<String> selectedModules) {
        Object profilesValue = registry.get("profiles");
        if (!(profilesValue instanceof Map<?, ?> profiles)) {
            return "custom";
        }
        for (Map.Entry<?, ?> entry : profiles.entrySet()) {
            Map<String, Object> profile = asMap(entry.getValue(), "Profile must be an object");
            if (selectedModules.equals(asOptionalStringList(profile.get("modules")))) {
                return String.valueOf(entry.getKey());
            }
        }
        return "custom";
    }

    private static List<String> requiredCoreModuleIds(Map<String, Object> registry) {
        List<String> ids = new ArrayList<>();
        for (Map<String, Object> module : asMapList(registry.get("modules"), "Registry must include modules")) {
            if ("required-core".equals(asString(module.get("role")))) {
                ids.add(asString(module.get("id")));
            }
        }
        return ids;
    }

    private static List<String> validateManifest(
            Map<String, Object> manifest,
            Map<String, Object> registry,
            Map<String, Object> contract) {
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
        for (Map<String, Object> module : asMapList(registry.get("modules"), "Registry must include modules")) {
            registryById.put(asString(module.get("id")), module);
        }

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

    private static List<String> getRequiredGeneratedFiles(Map<String, Object> contract) {
        return asOptionalStringList(asMap(contract.get("outputInvariants"), "Contract must include outputInvariants").get("requiredFiles"));
    }

    private static void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        try (var stream = Files.walk(path)) {
            stream.sorted((left, right) -> right.compareTo(left)).forEach(current -> {
                try {
                    Files.deleteIfExists(current);
                } catch (IOException error) {
                    throw new IllegalStateException(error);
                }
            });
        }
    }

    private static Map<String, String> parseOptions(String[] args) {
        Map<String, String> options = new LinkedHashMap<>();
        if (args.length == 0) {
            return options;
        }
        options.put("_command", args[0]);
        for (int index = 1; index < args.length; index++) {
            String argument = args[index];
            if (!argument.startsWith("--")) {
                throw new IllegalArgumentException("Unexpected argument: " + argument);
            }
            String key = argument.substring(2);
            if ("apply".equals(key)) {
                options.put("apply", "true");
                continue;
            }
            if (index + 1 >= args.length) {
                throw new IllegalArgumentException("Missing value for --" + key);
            }
            options.put(key, args[++index]);
        }
        return options;
    }

    private static Path requiredPath(Map<String, String> options, String key) {
        String value = options.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required argument: --" + key);
        }
        return Path.of(value).toAbsolutePath().normalize();
    }

    private static Map<String, Object> readJson(Path path) throws IOException {
        return SimpleJson.parseObject(Files.readString(path));
    }

    private static List<Map<String, Object>> asMapList(Object value, String message) {
        List<Map<String, Object>> results = new ArrayList<>();
        for (Object item : asList(value, message)) {
            results.add(asMap(item, "Expected object"));
        }
        return results;
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
        List<String> result = new ArrayList<>();
        for (Object item : asList(value, message)) {
            result.add(asString(item));
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

    private static String asStringOrDefault(Object value, String fallback) {
        return value instanceof String stringValue ? stringValue : fallback;
    }

    private static String asStringOrNull(Object value) {
        return value instanceof String stringValue ? stringValue : null;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    private static String ensureTrailingNewline(String value) {
        return value.endsWith("\n") ? value : value + "\n";
    }

    private record JavaRuntime(Path workspace, String mainClass, Path runtimeClasspathFile) {
    }

    private record CommandResult(int exitCode, String output) {
    }

    private record ManagedAssets(Map<String, String> managedAssets, List<String> selectedModules, Map<String, Object> executionContract) {
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
                for (int i = 0; i < stringValue.length(); i++) {
                    char current = stringValue.charAt(i);
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
