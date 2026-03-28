#!/usr/bin/env bash
set -euo pipefail

source "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/_verify-lib.sh"

ensure_frontend_deps

log "Running frontend test baseline..."
(
  cd "$ROOT_DIR/frontend"
  bun run test
  bun run build
  bun run lint
)

log "Frontend verification passed."
