---
type: session
date: 2026-11-09
workitems: [ISSUE-54]
tags: [session, place, search, verify-fix]
status: closed
parent_workitem: [[ISSUE-54]]
parent_session: [[2026-11-09-issue-54-place-search-lean-checks]]
---

# Session: Issue 54 — corrigir blockers do `/speckit.verify`

## Objetivo

Corrigir High (token inválido → Google), Reuse-First (utils com 1 caller), blank `place` → 400, e Landing de `Place.java`.

## Completado

- [x] `decodePagination` devolve `googleNextPageToken = null` no catch
- [x] `mustTreatInvalidTokenAsFirstPage` verifica `textSearch(..., isNull())`
- [x] `PlaceSearchFilterMapper` / `PlaceSearchOrdination` package-private em `domain.service`
- [x] Controller rejeita `place` blank com 400 + teste
- [x] Landing/checks atualizados com `Place.java` address fallback

## Próximo passo

PR #56 merged into `develop` (2026-11-13). Remaining outside this session: optional `/speckit.verify` in a non-author session + close GitHub issue #54.
