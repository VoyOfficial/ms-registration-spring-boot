#!/usr/bin/env bash
# Expose the local API on a public trycloudflare.com URL for mobile smoke tests.
# Requires the app listening on API_PORT (default 8080) and cloudflared installed
# or downloadable. Prints the public base URL (includes /api/registration context).
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
API_PORT="${API_PORT:-8080}"
CLOUDFLARED_BIN="${CLOUDFLARED_BIN:-cloudflared}"
LOG_FILE="${REMOTE_TUNNEL_LOG:-/tmp/ms-registration-cloudflared.log}"

if ! command -v "${CLOUDFLARED_BIN}" >/dev/null 2>&1; then
  echo "cloudflared not on PATH; downloading static binary to /tmp/cloudflared..." >&2
  curl -fsSL \
    "https://github.com/cloudflare/cloudflared/releases/latest/download/cloudflared-linux-amd64" \
    -o /tmp/cloudflared
  chmod +x /tmp/cloudflared
  CLOUDFLARED_BIN=/tmp/cloudflared
fi

if ! curl -fsS "http://127.0.0.1:${API_PORT}/api/registration/swagger-ui/index.html" >/dev/null 2>&1; then
  echo "API not reachable on :${API_PORT}. Start the Spring Boot app first." >&2
  exit 1
fi

# shellcheck disable=SC2086
nohup "${CLOUDFLARED_BIN}" tunnel --url "http://127.0.0.1:${API_PORT}" >"${LOG_FILE}" 2>&1 &
echo $! > /tmp/ms-registration-cloudflared.pid

PUBLIC_HOST=""
for _ in $(seq 1 40); do
  PUBLIC_HOST="$(grep -oE 'https://[a-z0-9-]+\.trycloudflare\.com' "${LOG_FILE}" | head -n1 || true)"
  if [[ -n "${PUBLIC_HOST}" ]]; then
    break
  fi
  sleep 0.5
done

if [[ -z "${PUBLIC_HOST}" ]]; then
  echo "Failed to obtain trycloudflare URL. See ${LOG_FILE}" >&2
  exit 1
fi

BASE_URL="${PUBLIC_HOST}/api/registration"
# When MANAGEMENT_PORT equals API_PORT, health is on the API context-path.
HEALTH_URL="${BASE_URL}/actuator/portal-admin/health"
cat <<EOF
Remote tunnel is up.

Base URL (mobile): ${BASE_URL}
Health:            ${HEALTH_URL}
Swagger:           ${BASE_URL}/swagger-ui/index.html
OpenAPI:           ${BASE_URL}/v3/api-docs/v1
Recommendations:   ${BASE_URL}/v1/places/recommendations?latitude=-29.37855&longitude=-50.8744

PID: $(cat /tmp/ms-registration-cloudflared.pid)
Log: ${LOG_FILE}
EOF

# Persist for agents/docs without printing secrets.
mkdir -p "${ROOT_DIR}/.run"
printf '%s\n' "${BASE_URL}" > "${ROOT_DIR}/.run/remote-base-url.txt"
