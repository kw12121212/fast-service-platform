#!/usr/bin/env bash
set -euo pipefail

source "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/_verify-lib.sh"

require_command podman

log "Running heavyweight backend sandbox runtime validation..."
(
  cd "$ROOT_DIR/backend"
  "$MVN_BIN" -q \
    -Dtest.groups=heavy-runtime \
    -Dtest.excludedGroups= \
    -Dtest=ProjectSandboxRuntimeTest \
    test
)

log "Backend sandbox runtime verification passed."
