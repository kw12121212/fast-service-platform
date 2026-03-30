#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
JAVA_HOME="${JAVA_HOME:-$HOME/.sdkman/candidates/java/25.0.2-tem}"
MVN_BIN="${MVN_BIN:-$HOME/.sdkman/candidates/maven/current/bin/mvn}"

export JAVA_HOME
export PATH="$JAVA_HOME/bin:$(dirname "$MVN_BIN"):$PATH"

if [[ ! -d "$ROOT_DIR/vendor/lealone/.git" ]]; then
  echo "Missing vendor/lealone clone" >&2
  exit 1
fi

echo "Installing Lealone core from source..."
"$MVN_BIN" -f "$ROOT_DIR/vendor/lealone/pom.xml" -DskipTests install

echo "Local Lealone source dependencies installed."
