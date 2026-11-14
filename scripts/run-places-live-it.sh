#!/usr/bin/env bash
# Live Google Places verification for Places PRs.
#
# Gate intent: mocked unit/integration tests alone have shipped nearby 500s and
# multi-minute pizza search. Before merge, when this PR touches Google Places
# paths (adapter, client, nearby, search, list photos), run:
#
#   1) PlacesApiClientIT     — direct Google SDK calls
#   2) RenderPlacesSmokeIT   — HTTP against a running API with mock OFF
#
# Usage:
#   ./scripts/run-places-live-it.sh              # client IT only
#   ./scripts/run-places-live-it.sh --with-smoke # client IT + HTTP smoke
#
# Env:
#   PLACES_API_KEY          required (real key, not fake_key)
#   PLACES_SMOKE_BASE_URL   default http://localhost:8080
#   ENABLE_MOCK_SERVICES    must be false on the API under smoke test
#
# Exit 0 pass, 1 fail, 2 usage/config.

set -u

with_smoke=0

usage() {
  cat <<'EOF'
Usage: ./scripts/run-places-live-it.sh [--with-smoke]

  (default)     RUN_PLACES_LIVE_IT=true ./mvnw -Dtest=PlacesApiClientIT test
  --with-smoke  also RUN_PLACES_SMOKE_IT against PLACES_SMOKE_BASE_URL

Requires PLACES_API_KEY. For --with-smoke, start the local API first with
ENABLE_MOCK_SERVICES=false and the same PLACES_API_KEY.
EOF
}

while [ $# -gt 0 ]; do
  case "$1" in
    --with-smoke) with_smoke=1; shift ;;
    --help|-h) usage; exit 0 ;;
    *)
      echo "Unknown option: $1" >&2
      usage >&2
      exit 2
      ;;
  esac
done

REPO_ROOT=$(git rev-parse --show-toplevel 2>/dev/null || true)
if [ -z "$REPO_ROOT" ]; then
  echo "run-places-live-it: not a git repository." >&2
  exit 2
fi
cd "$REPO_ROOT"

if [ -z "${PLACES_API_KEY:-}" ] || [ "$PLACES_API_KEY" = "fake_key" ]; then
  echo "run-places-live-it: set a real PLACES_API_KEY (not fake_key)." >&2
  exit 2
fi

echo "=== PlacesApiClientIT (live Google) ==="
if ! RUN_PLACES_LIVE_IT=true ./mvnw -Dskip.git.hooks=true -Dtest=PlacesApiClientIT test; then
  echo "PlacesApiClientIT FAILED"
  exit 1
fi
echo "PlacesApiClientIT ok"

if [ "$with_smoke" -eq 1 ]; then
  base="${PLACES_SMOKE_BASE_URL:-http://localhost:8080}"
  echo "=== RenderPlacesSmokeIT against $base ==="
  if ! RUN_PLACES_SMOKE_IT=true PLACES_SMOKE_BASE_URL="$base" \
    ./mvnw -Dskip.git.hooks=true -Dtest=RenderPlacesSmokeIT test; then
    echo "RenderPlacesSmokeIT FAILED"
    exit 1
  fi
  echo "RenderPlacesSmokeIT ok"
fi

echo "Live Places IT: ok"
exit 0
