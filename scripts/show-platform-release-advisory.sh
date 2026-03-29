#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/scripts/_verify-lib.sh"

require_command node

TARGET_DIR="${1:-}"

log "Reading platform release advisory..."

if [[ -n "$TARGET_DIR" ]]; then
  node "$ROOT_DIR/scripts/show-platform-release-advisory.mjs" "$TARGET_DIR"
else
  node "$ROOT_DIR/scripts/show-platform-release-advisory.mjs"
fi
