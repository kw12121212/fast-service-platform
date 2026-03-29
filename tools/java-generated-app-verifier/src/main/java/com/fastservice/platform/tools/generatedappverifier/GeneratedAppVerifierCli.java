package com.fastservice.platform.tools.generatedappverifier;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class GeneratedAppVerifierCli {

    private GeneratedAppVerifierCli() {
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> options = parseArgs(args);
        Path targetDir = Path.of(options.getOrDefault("target", ".")).toAbsolutePath().normalize();

        GeneratedAppVerifier verifier = new GeneratedAppVerifier();
        GeneratedAppVerifier.VerificationResult result = verifier.verify(targetDir);

        if (!result.ok()) {
            System.err.println("Derived app verification failed for " + targetDir);
            for (String issue : result.issues()) {
                System.err.println("- " + issue);
            }
            System.exit(1);
            return;
        }

        System.out.println(SimpleJson.stringify(Map.of(
                "targetDir", targetDir.toString(),
                "contractVersion", result.contractVersion(),
                "verifierId", result.verifierId(),
                "selectedModules", result.selectedModules(),
                "verified", true)));
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
}
