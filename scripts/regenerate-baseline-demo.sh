#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
MANIFEST_PATH="$ROOT_DIR/demo/baseline-demo.manifest.json"
TARGET_DIR="$ROOT_DIR/demo/baseline-demo"
TEMP_DIR="$(mktemp -d "${TMPDIR:-/tmp}/fsp-baseline-demo-XXXXXX")"

cleanup() {
  rm -rf "$TEMP_DIR"
}

trap cleanup EXIT

printf '[demo] Regenerating baseline demo from %s\n' "$MANIFEST_PATH"
"$ROOT_DIR/scripts/platform-tool.sh" assembly scaffold "$MANIFEST_PATH" "$TEMP_DIR"

rm -rf "$TARGET_DIR"
mkdir -p "$(dirname "$TARGET_DIR")"
cp -R "$TEMP_DIR" "$TARGET_DIR"

printf '[demo] Baseline demo regenerated at %s\n' "$TARGET_DIR"
