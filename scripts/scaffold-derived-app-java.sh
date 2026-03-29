#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/scripts/_verify-lib.sh"

require_command java
require_command "$MVN_BIN"

MANIFEST_PATH="${1:-}"
OUTPUT_DIR="${2:-}"

if [[ -z "$MANIFEST_PATH" || -z "$OUTPUT_DIR" ]]; then
  printf 'Usage: %s <manifest-path> <absolute-output-dir>\n' "$0" >&2
  exit 1
fi

log "Preparing Java assembly CLI runtime..."
(
  cd "$ROOT_DIR/tools/java-assembly-cli"
  "$MVN_BIN" -q -DskipTests package dependency:build-classpath \
    -Dmdep.outputFile=target/runtime-classpath.txt \
    -DincludeScope=runtime
)

CLASSPATH="$ROOT_DIR/tools/java-assembly-cli/target/classes:$(cat "$ROOT_DIR/tools/java-assembly-cli/target/runtime-classpath.txt")"

java -cp "$CLASSPATH" \
  com.fastservice.platform.tools.assemblycli.AppAssemblyCli \
  --repo-root "$ROOT_DIR" \
  --manifest "$MANIFEST_PATH" \
  --output "$OUTPUT_DIR"
