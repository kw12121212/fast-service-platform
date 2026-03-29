#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/scripts/_verify-lib.sh"

require_command java

TARGET_DIR="${1:-$(pwd)}"

log "Evaluating derived-app upgrade compatibility..."
java "$ROOT_DIR/scripts/PlatformTooling.java" upgrade-evaluate --repo-root "$ROOT_DIR" --target "$TARGET_DIR"
