#!/usr/bin/env bash
set -euo pipefail

source "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/_verify-lib.sh"

log "Running backend test baseline..."
(
  cd "$ROOT_DIR/backend"
  "$MVN_BIN" -q -DforkedProcessExitTimeoutInSeconds=5 test
)

log "Backend verification passed."
