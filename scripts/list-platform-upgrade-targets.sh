#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TARGET_DIR="${1:-}"

if [[ -n "$TARGET_DIR" ]]; then
  TARGET_DIR="$(cd "$TARGET_DIR" && pwd)"
  node "$ROOT_DIR/scripts/list-platform-upgrade-targets.mjs" "$TARGET_DIR"
else
  node "$ROOT_DIR/scripts/list-platform-upgrade-targets.mjs"
fi
