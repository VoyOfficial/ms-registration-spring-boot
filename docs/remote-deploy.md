# Remote deploy — mobile integration (#50)

Shared backend so `voy-app-react-native` can exercise recommendations, nearby,
sponsored registry, and OpenAPI against a real base URL.

## Option A — Render Blueprint (durable)

1. Open [Render Blueprints](https://dashboard.render.com/blueprints) and connect
   this repository (`render.yaml` at the root).
2. Apply the blueprint (web service + Postgres). The blueprint uses the smallest
   paid Postgres plan (`basic_256mb`); free Postgres is no longer offered on
   Render — swap the DB for Neon/another host if you prefer.
3. Set `PLACES_API_KEY` on the web service (required for Google Places calls;
   recommendations seed works without live Places traffic).
4. Wait for the first deploy (Flyway runs V1–V21, including the Gramado/Canela
   recommendation seed). Free web services may cold-start after idle time.

Base URL shape:

```text
https://<service>.onrender.com/api/registration
```

## Option B — Quick public tunnel (ephemeral)

Use when you already have the API + Postgres running locally (or in a Cloud Agent):

```bash
export API_PORT=8080
export MANAGEMENT_PORT=8080   # optional; puts health on the API port
./scripts/remote-tunnel.sh
```

The script prints a `https://*.trycloudflare.com/api/registration` base URL.
It dies when the process or machine stops — fine for a QA window, not for a
stable shared env.

## Environment

| Variable | Notes |
|----------|--------|
| `DATABASE_*` | Host, port, db, user, password, schema (`registration`) |
| `PLACES_API_KEY` | Google Places |
| `PAGINATION_SECRET_KEY` | Cursor encryption for nearby pagination |
| `API_PORT` | Default `8080` |
| `MANAGEMENT_PORT` | Default `2022`. Set equal to `API_PORT` on single-port hosts |
| `PROFILE_ACTIVE` | `dev` for remote integration |

## Smoke checks

Replace `$BASE` with the remote base URL (`…/api/registration`).

```bash
# Health (single-port / Render)
curl -sS "$HOST/api/registration/actuator/portal-admin/health"
# or dual-port local: curl -sS "http://localhost:2022/actuator/portal-admin/health"

curl -sS "$BASE/v1/places/recommendations?latitude=-29.37855&longitude=-50.8744"
curl -sS -o /dev/null -w '%{http_code}\n' "$BASE/swagger-ui/index.html"
curl -sS -o /dev/null -w '%{http_code}\n' "$BASE/v3/api-docs/v1"
```

Expect recommendations JSON with active Gramado/Canela places (not empty 404).

## Mobile handoff

Point the app registration API client at:

```text
$BASE
```

Example paths the app should call:

- `GET $BASE/v1/places/recommendations?latitude=…&longitude=…`
- Swagger: `$BASE/swagger-ui/index.html`
- OpenAPI: `$BASE/v3/api-docs/v1`
