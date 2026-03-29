#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TARGET_DIR="${1:-$(pwd)}"

java "$ROOT_DIR/scripts/VerifyDerivedApp.java" "$TARGET_DIR"
