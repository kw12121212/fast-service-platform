#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

usage() {
  cat <<'EOF'
Usage:
  ./scripts/platform-tool.sh assembly scaffold <node|java> <manifest-path> <absolute-output-dir>
  ./scripts/platform-tool.sh assembly verify
  ./scripts/platform-tool.sh assembly compatibility
  ./scripts/platform-tool.sh generated-app verify [generated-app-dir]
  ./scripts/platform-tool.sh generated-app verify-reference [generated-app-dir]
  ./scripts/platform-tool.sh generated-app verify-java [generated-app-dir]
  ./scripts/platform-tool.sh upgrade targets [generated-app-dir]
  ./scripts/platform-tool.sh upgrade evaluate <generated-app-dir>
  ./scripts/platform-tool.sh upgrade advisory [generated-app-dir]
  ./scripts/platform-tool.sh upgrade execute <generated-app-dir> [--apply]
EOF
}

group="${1:-}"
command="${2:-}"

if [[ -z "$group" || -z "$command" ]]; then
  usage >&2
  exit 1
fi

shift 2

case "$group/$command" in
  assembly/scaffold)
    implementation="${1:-}"
    manifest_path="${2:-}"
    output_dir="${3:-}"
    if [[ -z "$implementation" || -z "$manifest_path" || -z "$output_dir" ]]; then
      usage >&2
      exit 1
    fi
    case "$implementation" in
      node)
        node "$ROOT_DIR/scripts/scaffold-derived-app.mjs" --manifest "$manifest_path" --output "$output_dir"
        ;;
      java)
        "$ROOT_DIR/scripts/scaffold-derived-app-java.sh" "$manifest_path" "$output_dir"
        ;;
      *)
        printf 'Unknown assembly implementation: %s\n' "$implementation" >&2
        exit 1
        ;;
    esac
    ;;
  assembly/verify)
    "$ROOT_DIR/scripts/verify-app-assembly.sh"
    ;;
  assembly/compatibility)
    node "$ROOT_DIR/scripts/verify-app-assembly-compatibility.mjs"
    ;;
  generated-app/verify)
    target_dir="${1:-$(pwd)}"
    node "$ROOT_DIR/scripts/verify-derived-app.mjs" "$target_dir"
    ;;
  generated-app/verify-reference)
    target_dir="${1:-$(pwd)}"
    node "$ROOT_DIR/scripts/verify-derived-app.mjs" "$target_dir"
    ;;
  generated-app/verify-java)
    target_dir="${1:-$(pwd)}"
    "$ROOT_DIR/scripts/verify-derived-app-java.sh" "$target_dir"
    ;;
  upgrade/targets)
    if [[ $# -gt 0 ]]; then
      "$ROOT_DIR/scripts/list-platform-upgrade-targets.sh" "$1"
    else
      "$ROOT_DIR/scripts/list-platform-upgrade-targets.sh"
    fi
    ;;
  upgrade/evaluate)
    target_dir="${1:-}"
    if [[ -z "$target_dir" ]]; then
      usage >&2
      exit 1
    fi
    "$ROOT_DIR/scripts/evaluate-derived-app-upgrade.sh" "$target_dir"
    ;;
  upgrade/advisory)
    if [[ $# -gt 0 ]]; then
      "$ROOT_DIR/scripts/show-platform-release-advisory.sh" "$1"
    else
      "$ROOT_DIR/scripts/show-platform-release-advisory.sh"
    fi
    ;;
  upgrade/execute)
    target_dir="${1:-}"
    if [[ -z "$target_dir" ]]; then
      usage >&2
      exit 1
    fi
    shift
    "$ROOT_DIR/scripts/execute-derived-app-upgrade.sh" "$target_dir" "$@"
    ;;
  *)
    printf 'Unknown platform-tool command: %s %s\n' "$group" "$command" >&2
    usage >&2
    exit 1
    ;;
esac
