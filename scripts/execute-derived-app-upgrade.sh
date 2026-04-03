#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/scripts/_verify-lib.sh"

require_command java

TARGET_DIR="${1:-$(pwd)}"
MODE="${2:-}"
TARGET_RELEASE_ID="${FSP_TARGET_RELEASE_ID:-}"

log "Preparing derived-app upgrade execution plan..."

target_release_args=()
if [[ -n "$TARGET_RELEASE_ID" ]]; then
  target_release_args=(--target-release "$TARGET_RELEASE_ID")
fi

if [[ "$MODE" == "--apply" ]]; then
  java "$ROOT_DIR/scripts/PlatformTooling.java" upgrade-execute --repo-root "$ROOT_DIR" --target "$TARGET_DIR" "${target_release_args[@]}" --apply
else
  java "$ROOT_DIR/scripts/PlatformTooling.java" upgrade-execute --repo-root "$ROOT_DIR" --target "$TARGET_DIR" "${target_release_args[@]}"
fi
