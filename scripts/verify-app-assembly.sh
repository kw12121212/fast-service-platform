#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/scripts/_verify-lib.sh"

require_command java
require_command "$MVN_BIN"

log "Validating app assembly compatibility suite against the Java repository-owned implementation..."
java "$ROOT_DIR/scripts/PlatformTooling.java" assembly-compatibility --repo-root "$ROOT_DIR" >/dev/null
log "App assembly verification passed."
