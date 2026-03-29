#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/scripts/_verify-lib.sh"

require_command java

TARGET_DIR="${1:-$(pwd)}"
MODE="${2:-}"

log "Preparing derived-app upgrade execution plan..."

if [[ "$MODE" == "--apply" ]]; then
  java "$ROOT_DIR/scripts/PlatformTooling.java" upgrade-execute --repo-root "$ROOT_DIR" --target "$TARGET_DIR" --apply
else
  java "$ROOT_DIR/scripts/PlatformTooling.java" upgrade-execute --repo-root "$ROOT_DIR" --target "$TARGET_DIR"
fi
