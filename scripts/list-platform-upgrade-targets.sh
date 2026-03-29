#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/scripts/_verify-lib.sh"

require_command java

TARGET_DIR="${1:-}"

if [[ -n "$TARGET_DIR" ]]; then
  TARGET_DIR="$(cd "$TARGET_DIR" && pwd)"
  java "$ROOT_DIR/scripts/PlatformTooling.java" upgrade-targets --repo-root "$ROOT_DIR" --target "$TARGET_DIR"
else
  java "$ROOT_DIR/scripts/PlatformTooling.java" upgrade-targets --repo-root "$ROOT_DIR"
fi
