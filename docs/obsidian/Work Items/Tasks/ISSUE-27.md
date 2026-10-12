---
type: task
date: 2026-10-12
status: in-progress
tags: [task, backend, place]
github: https://github.com/VoyOfficial/ms-registration-spring-boot/issues/27
---

# ISSUE-27: Cadastro de Estabelecimentos Patrocinados

## Goal

Endpoint REST to register sponsored places: validate name/city/ranking, require approval + active subscription, enrich via Google Places, dedupe by googlePlaceId, persist to PostgreSQL.

## Branch

`cursor/sponsored-place-registry-65c7`

## Session

[[2026-10-12-issue-27-sponsored-place-registry]]

## Acceptance

- [x] Endpoint documented and reachable (`POST /v1/places`)
- [x] Google Places enrichment
- [x] No duplicate googlePlaceId
- [x] Reject not approved / inactive subscription (403)
- [x] Relevant fields stored
- [x] Unit + controller tests
