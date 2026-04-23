#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$ROOT_DIR/scripts/_verify-lib.sh"

FIXTURE_DIR="$ROOT_DIR/docs/ai/tests"
SCHEMA_DIR="$ROOT_DIR/docs/ai/schemas"
DESCRIPTOR_DIR="$ROOT_DIR/docs/ai/management-modules"

FIXTURE_INPUT="$FIXTURE_DIR/e2e-fixture.solution-input.json"
FIXTURE_PLAN="$FIXTURE_DIR/e2e-fixture.solution-to-manifest-plan.json"
FIXTURE_RECOMMENDATION="$FIXTURE_DIR/e2e-fixture.solution-to-manifest-recommendation.json"
FIXTURE_MANIFEST="$FIXTURE_DIR/e2e-fixture.manifest.json"
DESCRIPTOR_LEAVE_REQUEST="$DESCRIPTOR_DIR/leave-request.management-module.json"

SKIP_SMOKE=0
PASSED=0
FAILED=0
STAGE_RESULTS=()

for arg in "$@"; do
  case "$arg" in
    --skip-smoke) SKIP_SMOKE=1 ;;
    *) printf 'Unknown argument: %s\n' "$arg" >&2; exit 1 ;;
  esac
done

require_command node
require_command java
require_command "$MVN_BIN"
require_command bun

stage_pass() {
  local stage="$1"
  PASSED=$((PASSED + 1))
  STAGE_RESULTS+=("PASS: $stage")
  log "STAGE PASS: $stage"
}

stage_fail() {
  local stage="$1"
  local message="$2"
  FAILED=$((FAILED + 1))
  STAGE_RESULTS+=("FAIL: $stage — $message")
  printf '[e2e] STAGE FAIL: %s — %s\n' "$stage" "$message" >&2
}

validate_schema() {
  local label="$1"
  local data_path="$2"
  local schema_path="$3"

  node - "$data_path" "$schema_path" <<'SCRIPT'
const fs = require('fs');
const path = require('path');
let Ajv2020;
try {
  Ajv2020 = require('ajv/dist/2020');
} catch {
  const homeDir = require('os').homedir();
  const nvmDir = path.join(homeDir, '.nvm/versions/node', process.version);
  Ajv2020 = require(path.join(nvmDir, 'lib/node_modules/@google/gemini-cli/node_modules/ajv/dist/2020'));
}
const [dataPath, schemaPath] = process.argv.slice(2);
const data = JSON.parse(fs.readFileSync(dataPath, 'utf8'));
const schema = JSON.parse(fs.readFileSync(schemaPath, 'utf8'));
const ajv = new Ajv2020();
const valid = ajv.validate(schema, data);
if (!valid) {
  process.stderr.write(ajv.errors.map(e => `${e.instancePath} ${e.message}`).join('; ') + '\n');
  process.exit(1);
}
SCRIPT
}

OUTPUT_DIR=""
cleanup() {
  if [[ -n "$OUTPUT_DIR" && -d "$OUTPUT_DIR" ]]; then
    log "Cleaning up temporary output directory: $OUTPUT_DIR"
    rm -rf "$OUTPUT_DIR"
  fi
}
trap cleanup EXIT

OUTPUT_DIR="$(mktemp -d "${TMPDIR:-/tmp}/fsp-e2e-pipeline-XXXXXX")"

log "=== E2E Solution Pipeline Validation ==="
log "Fixture directory: $FIXTURE_DIR"
log "Output directory: $OUTPUT_DIR"
if [[ "$SKIP_SMOKE" -eq 1 ]]; then
  log "Smoke stage: SKIP (--skip-smoke)"
fi
log ""

# Stage 1: Validate solution input
STAGE="solution-input-validation"
log "Stage 1/8: Validating solution input..."
if validate_schema "$STAGE" "$FIXTURE_INPUT" "$SCHEMA_DIR/ai-solution-input.schema.json"; then
  stage_pass "$STAGE"
else
  stage_fail "$STAGE" "solution input failed schema validation"
  exit 1
fi

# Stage 2: Validate planning artifact
STAGE="planning-validation"
log "Stage 2/8: Validating planning artifact..."
if validate_schema "$STAGE" "$FIXTURE_PLAN" "$SCHEMA_DIR/solution-to-manifest-planning.schema.json"; then
  stage_pass "$STAGE"
else
  stage_fail "$STAGE" "planning artifact failed schema validation"
  exit 1
fi

# Stage 3: Validate recommendation artifact
STAGE="recommendation-validation"
log "Stage 3/8: Validating recommendation artifact..."
if validate_schema "$STAGE" "$FIXTURE_RECOMMENDATION" "$SCHEMA_DIR/solution-to-manifest-recommendation.schema.json"; then
  stage_pass "$STAGE"
else
  stage_fail "$STAGE" "recommendation artifact failed schema validation"
  exit 1
fi

# Stage 4: Validate descriptor-driven management module
STAGE="descriptor-validation"
log "Stage 4/8: Validating leave-request descriptor..."
if validate_schema "$STAGE" "$DESCRIPTOR_LEAVE_REQUEST" "$SCHEMA_DIR/descriptor-driven-management-module.schema.json"; then
  stage_pass "$STAGE"
else
  stage_fail "$STAGE" "leave-request descriptor failed schema validation"
  exit 1
fi

# Stage 5: Validate manifest
STAGE="manifest-validation"
log "Stage 5/8: Validating assembly manifest..."
if validate_schema "$STAGE" "$FIXTURE_MANIFEST" "$SCHEMA_DIR/app-manifest.schema.json"; then
  stage_pass "$STAGE"
else
  stage_fail "$STAGE" "manifest failed schema validation"
  exit 1
fi

# Stage 6: Assembly
STAGE="assembly"
log "Stage 6/8: Running assembly scaffold..."
if "$ROOT_DIR/scripts/platform-tool.sh" assembly scaffold "$FIXTURE_MANIFEST" "$OUTPUT_DIR"; then
  stage_pass "$STAGE"
else
  stage_fail "$STAGE" "assembly scaffold failed"
  exit 1
fi

# Stage 7: Generated-app verification
STAGE="generated-app-verification"
log "Stage 7/8: Running generated-app verification..."
if "$ROOT_DIR/scripts/platform-tool.sh" generated-app verify "$OUTPUT_DIR"; then
  stage_pass "$STAGE"
else
  stage_fail "$STAGE" "generated-app verification failed"
  exit 1
fi

# Stage 8: Runtime smoke
if [[ "$SKIP_SMOKE" -eq 1 ]]; then
  STAGE="runtime-smoke"
  log "Stage 8/8: Skipping runtime smoke (--skip-smoke)."
  STAGE_RESULTS+=("SKIP: $STAGE — skipped by --skip-smoke flag")
else
  STAGE="runtime-smoke"
  log "Stage 8/8: Running runtime smoke..."
  if "$ROOT_DIR/scripts/platform-tool.sh" generated-app smoke "$OUTPUT_DIR"; then
    stage_pass "$STAGE"
  else
    stage_fail "$STAGE" "runtime smoke failed"
    exit 1
  fi
fi

# Summary
log ""
log "=== E2E Pipeline Summary ==="
for result in "${STAGE_RESULTS[@]}"; do
  log "  $result"
done
log ""
log "Results: $PASSED passed, $FAILED failed"

if [[ "$FAILED" -gt 0 ]]; then
  exit 1
fi

log "E2E solution pipeline validation passed."
