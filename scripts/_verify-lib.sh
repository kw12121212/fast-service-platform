#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
JAVA_HOME="${JAVA_HOME:-$HOME/.sdkman/candidates/java/25.0.2-tem}"
MVN_BIN="${MVN_BIN:-$HOME/.sdkman/candidates/maven/current/bin/mvn}"

export JAVA_HOME
export PATH="$JAVA_HOME/bin:$(dirname "$MVN_BIN"):$PATH"

log() {
  printf '[verify] %s\n' "$*"
}

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    printf 'Missing required command: %s\n' "$1" >&2
    exit 1
  fi
}

wait_for_url() {
  local url="$1"
  local attempts="${2:-60}"
  local sleep_seconds="${3:-1}"

  for ((attempt = 1; attempt <= attempts; attempt++)); do
    if curl -fsS --connect-timeout 1 --max-time 2 "$url" >/dev/null 2>&1; then
      return 0
    fi
    sleep "$sleep_seconds"
  done

  printf 'Timed out waiting for URL: %s\n' "$url" >&2
  return 1
}

port_is_open() {
  local host="${1:-127.0.0.1}"
  local port="$2"

  (echo >"/dev/tcp/${host}/${port}") >/dev/null 2>&1
}

ensure_frontend_deps_for_dir() {
  local frontend_dir="$1"
  require_command bun

  if [[ ! -d "$frontend_dir/node_modules" ]]; then
    log "Installing frontend dependencies with bun..."
    (
      cd "$frontend_dir"
      bun install --frozen-lockfile
    )
  fi
}

ensure_frontend_deps() {
  ensure_frontend_deps_for_dir "$ROOT_DIR/frontend"
}

prepare_backend_runtime_for_dir() {
  local backend_dir="$1"
  log "Preparing backend runtime classpath..."
  (
    cd "$backend_dir"
    "$MVN_BIN" -q -DskipTests package dependency:build-classpath \
      -Dmdep.outputFile=target/runtime-classpath.txt \
      -DincludeScope=runtime
  )
}

prepare_backend_runtime() {
  prepare_backend_runtime_for_dir "$ROOT_DIR/backend"
}
