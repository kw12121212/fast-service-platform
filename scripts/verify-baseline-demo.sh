#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/scripts/_verify-lib.sh"

DEMO_DIR="${1:-$ROOT_DIR/demo/baseline-demo}"
BACKEND_DIR="$DEMO_DIR/backend"
FRONTEND_DIR="$DEMO_DIR/frontend"

require_command java
require_command "$MVN_BIN"
require_command bun

if [[ ! -d "$DEMO_DIR" ]]; then
  printf 'Baseline demo not found: %s\n' "$DEMO_DIR" >&2
  printf 'Run ./scripts/regenerate-baseline-demo.sh first.\n' >&2
  exit 1
fi

log "Verifying generated-app contract for $DEMO_DIR..."
"$ROOT_DIR/scripts/platform-tool.sh" generated-app verify "$DEMO_DIR"

log "Running derived-app runtime smoke validation..."
"$ROOT_DIR/scripts/platform-tool.sh" generated-app smoke "$DEMO_DIR"

log "Running generated backend tests..."
(
  cd "$BACKEND_DIR"
  "$MVN_BIN" -q test
)

log "Running generated frontend install, build, and lint..."
(
  cd "$FRONTEND_DIR"
  bun install --frozen-lockfile
  bun run build
  bun run lint
)

log "Baseline demo verification passed."
