#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/scripts/_verify-lib.sh"

require_command node

TARGET_DIR="${1:-$(pwd)}"
MODE="${2:-}"

log "Preparing derived-app upgrade execution plan..."

if [[ "$MODE" == "--apply" ]]; then
  node "$ROOT_DIR/scripts/execute-derived-app-upgrade.mjs" "$TARGET_DIR" --apply
else
  node "$ROOT_DIR/scripts/execute-derived-app-upgrade.mjs" "$TARGET_DIR"
fi
