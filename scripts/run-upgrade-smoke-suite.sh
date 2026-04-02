#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/scripts/_verify-lib.sh"

require_command java

log "Validating upgrade smoke suite against the Java repository-owned implementation..."
java "$ROOT_DIR/scripts/PlatformTooling.java" upgrade-smoke --repo-root "$ROOT_DIR" >/dev/null
log "Upgrade smoke suite verification passed."
