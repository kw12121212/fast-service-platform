package com.fastservice.platform.tools.assemblycli;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class AppAssemblyCli {

    private AppAssemblyCli() {
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> options = parseArgs(args);
        Path repoRoot = Path.of(options.getOrDefault("repo-root", ".")).toAbsolutePath().normalize();
        Path manifestPath = requiredPath(options, "manifest");
        Path outputDir = requiredPath(options, "output");

        AssemblyGenerator generator = new AssemblyGenerator(repoRoot);
        AssemblyGenerator.AssemblyResult result = generator.scaffold(manifestPath, outputDir);
        System.out.println(SimpleJson.stringify(Map.of(
                "implementation", "java-cli",
                "outputDir", result.outputDir().toString(),
                "selectedModules", result.selectedModules(),
                "verifiedBy", "scripts/verify-derived-app.sh")));
    }

    static Map<String, String> parseArgs(String[] args) {
        Map<String, String> options = new HashMap<>();
        for (int index = 0; index < args.length; index++) {
            String argument = args[index];
            if (!argument.startsWith("--")) {
                throw new IllegalArgumentException("Unexpected argument: " + argument);
            }
            String key = argument.substring(2);
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
}
