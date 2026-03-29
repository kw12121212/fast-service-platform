#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/scripts/_verify-lib.sh"

require_command node
require_command java
require_command "$MVN_BIN"

log "Validating app assembly compatibility suite against the Node and Java compatible implementations..."
node "$ROOT_DIR/scripts/verify-app-assembly-compatibility.mjs" >/dev/null
log "App assembly verification passed."
