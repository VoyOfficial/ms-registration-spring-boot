---
type: workitem
id: ISSUE-54
title: Busca de locais (search places)
status: in-progress
github: https://github.com/VoyOfficial/ms-registration-spring-boot/issues/54
tags: [place, search, api]
---

# ISSUE-54 — Busca de locais

## Objetivo

`POST /v1/places/search` híbrido (DB first + Google Text Search), filtros PT, ordenação, paginação HMAC, resposta `NearbyPlacesResponse`.

## Plano / Checks

- [[../specs/54-place-search/plan.md]] (no repo: `specs/54-place-search/plan.md`)
- `specs/54-place-search/checks.md`

## Decisões

- [[Decisions/008-search-places-hybrid-source]]
- [[Decisions/009-search-reuses-place-response]]

## Delivery

- PR: https://github.com/VoyOfficial/ms-registration-spring-boot/pull/56 (merged into `develop`, 2026-11-13)

## Status

Implementação shipped via PR #56. Remaining: optional `/speckit.verify` in a non-author session + close GitHub issue #54 (agent tokens may lack `issues:write`).
