#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/scripts/_verify-lib.sh"

require_command java
require_command "$MVN_BIN"

TARGET_DIR="${1:-$(pwd)}"

log "Preparing Java generated-app verifier runtime..."
(
  cd "$ROOT_DIR/tools/java-generated-app-verifier"
  "$MVN_BIN" -q -DskipTests package dependency:build-classpath \
    -Dmdep.outputFile=target/runtime-classpath.txt \
    -DincludeScope=runtime
)

CLASSPATH="$ROOT_DIR/tools/java-generated-app-verifier/target/classes:$(cat "$ROOT_DIR/tools/java-generated-app-verifier/target/runtime-classpath.txt")"

java -cp "$CLASSPATH" \
  com.fastservice.platform.tools.generatedappverifier.GeneratedAppVerifierCli \
  --target "$TARGET_DIR"
