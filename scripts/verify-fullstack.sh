#!/usr/bin/env bash
set -euo pipefail

source "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/_verify-lib.sh"

require_command curl
require_command node
ensure_frontend_deps
prepare_backend_runtime

SMOKE_DIR="$ROOT_DIR/target/ai-smoke"
BACKEND_BASE_DIR="$SMOKE_DIR/lealone-data"
BACKEND_LOG="$SMOKE_DIR/backend.log"
FRONTEND_LOG="$SMOKE_DIR/frontend.log"
FRONTEND_PORT="${FRONTEND_PORT:-4173}"
FRONTEND_URL="http://127.0.0.1:${FRONTEND_PORT}"
BACKEND_DIRECT_URL="http://127.0.0.1:8080/service/user_service/listUsers"
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

trap cleanup EXIT

if curl -fsS "$BACKEND_DIRECT_URL" >/dev/null 2>&1; then
  log "Reusing existing backend on 127.0.0.1:8080"
else
  log "Starting backend with demo data for smoke validation..."
  (
    cd "$ROOT_DIR/backend"
    java \
      -Dfsp.demo-data=true \
      -Dfsp.base-dir="$BACKEND_BASE_DIR" \
      -cp "target/classes:$(cat target/runtime-classpath.txt)" \
      com.fastservice.platform.backend.BackendApplication \
      >"$BACKEND_LOG" 2>&1
  ) &
  BACKEND_PID=$!
  BACKEND_STARTED=1
  wait_for_url "$BACKEND_DIRECT_URL" 90 1
fi

log "Starting frontend dev server on port $FRONTEND_PORT..."
(
  cd "$ROOT_DIR/frontend"
  ./node_modules/.bin/vite --host 127.0.0.1 --port "$FRONTEND_PORT" --strictPort \
    >"$FRONTEND_LOG" 2>&1
) &
FRONTEND_PID=$!
FRONTEND_STARTED=1
wait_for_url "$FRONTEND_URL" 60 1
wait_for_url "$FRONTEND_URL/service/user_service/listUsers" 60 1

log "Checking proxied /service/* responses..."
USERS_RESPONSE="$(curl -fsS "$FRONTEND_URL/service/user_service/listUsers")"
PROJECTS_RESPONSE="$(curl -fsS "$FRONTEND_URL/service/project_service/listProjects")"

node - "$USERS_RESPONSE" "$PROJECTS_RESPONSE" <<'EOF'
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

log "Full-stack smoke verification passed through frontend /service proxy."
