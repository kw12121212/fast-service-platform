#!/usr/bin/env bash
set -euo pipefail

source "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/_verify-lib.sh"

TARGET_DIR="${1:-$(pwd)}"
TARGET_DIR="$(cd "$TARGET_DIR" && pwd)"
BACKEND_DIR="$TARGET_DIR/backend"
FRONTEND_DIR="$TARGET_DIR/frontend"

require_command curl
require_command node
require_command java
require_command "$MVN_BIN"
require_command bun

if [[ ! -d "$BACKEND_DIR" ]]; then
  printf 'Derived-app runtime smoke failed during setup: missing backend directory: %s\n' "$BACKEND_DIR" >&2
  exit 1
fi

if [[ ! -d "$FRONTEND_DIR" ]]; then
  printf 'Derived-app runtime smoke failed during setup: missing frontend directory: %s\n' "$FRONTEND_DIR" >&2
  exit 1
fi

ensure_frontend_deps_for_dir "$FRONTEND_DIR"
prepare_backend_runtime_for_dir "$BACKEND_DIR"

SMOKE_DIR="$TARGET_DIR/target/derived-app-runtime-smoke"
BACKEND_BASE_DIR="$SMOKE_DIR/lealone-data"
BACKEND_LOG="$SMOKE_DIR/backend.log"
FRONTEND_LOG="$SMOKE_DIR/frontend.log"
FRONTEND_PORT="${FRONTEND_PORT:-4174}"
FRONTEND_URL="http://127.0.0.1:${FRONTEND_PORT}"
BACKEND_DIRECT_URL="http://127.0.0.1:8080/service/user_service/listUsers"
PROXY_USERS_URL="$FRONTEND_URL/service/user_service/listUsers"
PROXY_PROJECTS_URL="$FRONTEND_URL/service/project_service/listProjects"
BACKEND_STARTED=0
FRONTEND_STARTED=0
BACKEND_PID=""
FRONTEND_PID=""

mkdir -p "$SMOKE_DIR"

cleanup() {
  if [[ "$FRONTEND_STARTED" -eq 1 && -n "$FRONTEND_PID" ]] && kill -0 "$FRONTEND_PID" >/dev/null 2>&1; then
    kill "$FRONTEND_PID" >/dev/null 2>&1 || true
    wait "$FRONTEND_PID" 2>/dev/null || true
  fi

  if [[ "$BACKEND_STARTED" -eq 1 && -n "$BACKEND_PID" ]] && kill -0 "$BACKEND_PID" >/dev/null 2>&1; then
    kill "$BACKEND_PID" >/dev/null 2>&1 || true
    wait "$BACKEND_PID" 2>/dev/null || true
  fi
}

report_stage_failure() {
  local stage="$1"
  local message="$2"

  printf 'Derived-app runtime smoke failed during %s: %s\n' "$stage" "$message" >&2
}

print_log_excerpt() {
  local label="$1"
  local log_path="$2"

  if [[ -f "$log_path" ]]; then
    printf 'Recent %s log (%s):\n' "$label" "$log_path" >&2
    tail -n 40 "$log_path" >&2
  fi
}

wait_for_url_or_process() {
  local url="$1"
  local pid="$2"
  local attempts="$3"
  local sleep_seconds="$4"
  local stage="$5"
  local log_path="$6"

  for ((attempt = 1; attempt <= attempts; attempt++)); do
    if curl -fsS --connect-timeout 1 --max-time 2 "$url" >/dev/null 2>&1; then
      return 0
    fi

    if ! kill -0 "$pid" >/dev/null 2>&1; then
      report_stage_failure "$stage" "process exited before ${url} became available"
      print_log_excerpt "$stage" "$log_path"
      return 1
    fi

    sleep "$sleep_seconds"
  done

  report_stage_failure "$stage" "timed out waiting for ${url}"
  print_log_excerpt "$stage" "$log_path"
  return 1
}

trap cleanup EXIT

if port_is_open 127.0.0.1 8080; then
  report_stage_failure \
    "backend startup" \
    "port 8080 is already in use; stop the existing process because generated frontend proxies are fixed to http://127.0.0.1:8080"
  exit 1
fi

if port_is_open 127.0.0.1 "$FRONTEND_PORT"; then
  report_stage_failure \
    "frontend startup" \
    "port ${FRONTEND_PORT} is already in use; free it or rerun with FRONTEND_PORT set to an unused port"
  exit 1
fi

log "Starting derived-app backend with demo data for runtime smoke validation..."
(
  cd "$BACKEND_DIR"
  java \
    -Dfsp.demo-data=true \
    -Dfsp.base-dir="$BACKEND_BASE_DIR" \
    -cp "target/classes:$(cat target/runtime-classpath.txt)" \
    com.fastservice.platform.backend.BackendApplication \
    >"$BACKEND_LOG" 2>&1
) &
BACKEND_PID=$!
BACKEND_STARTED=1

wait_for_url_or_process "$BACKEND_DIRECT_URL" "$BACKEND_PID" 90 1 "backend startup" "$BACKEND_LOG"

log "Starting derived-app frontend dev server on port $FRONTEND_PORT..."
(
  cd "$FRONTEND_DIR"
  ./node_modules/.bin/vite --host 127.0.0.1 --port "$FRONTEND_PORT" --strictPort \
    >"$FRONTEND_LOG" 2>&1
) &
FRONTEND_PID=$!
FRONTEND_STARTED=1

wait_for_url_or_process "$FRONTEND_URL" "$FRONTEND_PID" 60 1 "frontend startup" "$FRONTEND_LOG"

if ! wait_for_url "$PROXY_USERS_URL" 60 1; then
  report_stage_failure "proxy reachability" "timed out waiting for proxied /service/* responses through ${FRONTEND_URL}"
  print_log_excerpt "frontend startup" "$FRONTEND_LOG"
  print_log_excerpt "backend startup" "$BACKEND_LOG"
  exit 1
fi

if ! USERS_RESPONSE="$(curl -fsS --connect-timeout 1 --max-time 5 "$PROXY_USERS_URL")"; then
  report_stage_failure "proxy reachability" "unable to fetch ${PROXY_USERS_URL}"
  print_log_excerpt "frontend startup" "$FRONTEND_LOG"
  print_log_excerpt "backend startup" "$BACKEND_LOG"
  exit 1
fi

if ! PROJECTS_RESPONSE="$(curl -fsS --connect-timeout 1 --max-time 5 "$PROXY_PROJECTS_URL")"; then
  report_stage_failure "proxy reachability" "unable to fetch ${PROXY_PROJECTS_URL}"
  print_log_excerpt "frontend startup" "$FRONTEND_LOG"
  print_log_excerpt "backend startup" "$BACKEND_LOG"
  exit 1
fi

if ! VALIDATION_OUTPUT="$(node - "$USERS_RESPONSE" "$PROJECTS_RESPONSE" <<'EOF' 2>&1
const [usersRaw, projectsRaw] = process.argv.slice(2)

function assertArray(label, raw) {
  let parsed
  try {
    parsed = JSON.parse(raw)
  } catch (error) {
    throw new Error(`${label} did not return valid JSON: ${error instanceof Error ? error.message : String(error)}`)
  }

  if (!Array.isArray(parsed)) {
    throw new Error(`${label} did not return a JSON array`)
  }
}

assertArray('user_service/listUsers', usersRaw)
assertArray('project_service/listProjects', projectsRaw)
EOF
)"; then
  report_stage_failure "response validation" "$VALIDATION_OUTPUT"
  exit 1
fi

log "Derived-app runtime smoke verification passed for $TARGET_DIR through frontend /service proxy."
