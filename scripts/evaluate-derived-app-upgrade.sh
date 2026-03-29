#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/scripts/_verify-lib.sh"

require_command node

TARGET_DIR="${1:-$(pwd)}"

log "Evaluating derived-app upgrade compatibility..."
node "$ROOT_DIR/scripts/evaluate-derived-app-upgrade.mjs" "$TARGET_DIR"
