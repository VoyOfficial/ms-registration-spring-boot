---
title: API access token gate for /v1
date: 2026-11-17
status: closed
parent_session: none
---

# API access token

## Context

Serviço no Render público; qualquer um podia bater em `/v1/**` e gastar créditos Google Places.

## Decision

Shared secret via `Authorization: Bearer <API_ACCESS_TOKEN>`, filtro servlet em `infrastructure.config.security`, sem Spring Security. `REQUIRE_API_TOKEN=true` no Render; `false` em testes/local.

## Files

- `ApiAccessTokenFilter` / `ApiAccessTokenSecurityConfig`
- `application.yaml`, `render.yaml`, docs
- testes `ApiAccessTokenFilterTest`, `ApiAccessTokenMisconfiguredTest`

## Next

- Definir `API_ACCESS_TOKEN` no dashboard Render
- App mobile enviar o header Bearer
