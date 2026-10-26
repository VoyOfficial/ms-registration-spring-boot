---
type: session
date: 2026-10-26
workitems: [ISSUE-50]
tags: [session, deploy, remote, mobile, tunnel]
status: closed
parent_workitem: [[ISSUE-50]]
parent_session: [[2026-10-26-issue-50-remote-deploy-recommendation-seed]]
---

# Session: Issue 50 — remote instance for mobile

## Objetivo

Publicar instância remota alcançável pelo app, com Flyway V21 (seed Gramado/Canela), health, recommendations e OpenAPI.

## Completado

- [x] Flyway credentials via `DATABASE_USER` / `DATABASE_PASSWORD` (+ `FLYWAY_ENABLE` default true)
- [x] `MANAGEMENT_PORT` configurável (single-port health for Render/tunnel)
- [x] `render.yaml` blueprint (web + Postgres)
- [x] `docs/remote-deploy.md` + `scripts/remote-tunnel.sh`
- [x] Rebuild 0.0.2, migrate V1–V21, seed 11 places
- [x] Smoke local + public trycloudflare tunnel

## Base URL (tunnel efêmero desta sessão)

`https://conventional-compensation-thumbnails-fast.trycloudflare.com/api/registration`

Durável: aplicar `render.yaml` no Render (ver docs).

## Arquivos

- `src/main/resources/application.yaml`
- `render.yaml`
- `docs/remote-deploy.md`
- `scripts/remote-tunnel.sh`
- `docker-compose.yml`
- `README.md`
