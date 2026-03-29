#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TARGET_DIR="${1:-$(pwd)}"

node "$ROOT_DIR/scripts/verify-derived-app.mjs" "$TARGET_DIR"
