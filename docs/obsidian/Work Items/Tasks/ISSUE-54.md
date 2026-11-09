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

## Status

Implementação via `/speckit.lean-checks` em andamento; próximo passo obrigatório: `/speckit.verify` em verificador que não é o autor.
