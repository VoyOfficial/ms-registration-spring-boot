---
type: session
date: 2026-11-14
workitems: []
tags: [session, bugfix, place, testing, dod]
status: closed
parent_workitem: none
parent_session: [[2026-11-14-nearby-list-and-search-speed]]
---

# Session: live Google IT and DoD for Places PRs

## Objetivo

Varios PRs seguidos corrigiram nearby 500 e busca lenta porque a suite mockada nao pegava o comportamento real do Google. Fechar o DoD de Places com IT ao vivo (SDK + HTTP na API local com mock off).

## Completado

- [x] `PlacesApiClientIT` cobre text search de pizza e download de uma foto de lista sob o teto de base64
- [x] `RenderPlacesSmokeIT` aceita API local via `RUN_PLACES_SMOKE_IT` + `PLACES_SMOKE_BASE_URL` (e ainda `RUN_RENDER_IT`)
- [x] `scripts/run-places-live-it.sh` documenta e executa o gate
- [x] PR template, DoD, testing-workflow, validation-pyramid e harness exigem live IT em PRs Places
- [x] Live smoke falhou em pizza search: foto Google acima do teto (ex.: Bom Apetitte). Fix: `Place.principalPhotos()` e search `normalizePlace` usam o mesmo teto da lista
- [x] Evidencia: API local com `ENABLE_MOCK_SERVICES=false` + script `--with-smoke`

## Arquivos Modificados

| Arquivo | Mudanca |
| --- | --- |
| `Place.java` | teto de foto na lista para path Google |
| `SearchPlacesService.java` | normalize usa `principalPhotos()` |
| `PlacePrincipalPhotosTest.java` | cobre o teto |
| `PlacesApiClientIT.java` | text search + foto lista live |
| `RenderPlacesSmokeIT.java` | local ou Render |
| `scripts/run-places-live-it.sh` | helper do gate |
| `.github/pull_request_template.md` | checkbox live Places |
| `.cursor/rules/definition-of-done.mdc` | Gate 2 Places |
| `.cursor/rules/testing-workflow.mdc` | excecao Places |
| `.cursor/rules/validation-pyramid.mdc` | camada live |
| `.cursor/rules/harness-engineering.mdc` | feedback Places |
| `scripts/validate-dod.sh` | aponta `--e2e-cmd` |
| `docs/obsidian/Modules/place.md` | documenta o gate |
