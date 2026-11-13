---
type: session
date: 2026-11-09
workitems: [ISSUE-54]
tags: [session, place, search, lean-checks]
status: open
parent_workitem: [[ISSUE-54]]
parent_session: none
---

# Session: Issue 54 — lean-checks busca de locais

## Objetivo

Derivar `checks.md` do plano aceito e implementar o Landing de `POST /v1/places/search`.

## Completado

- [x] `specs/54-place-search/checks.md` (C1–C10)
- [x] Domain: `SearchPlacesUseCase` / `SearchPlacesService` / filter+ordination utils / port+repo contracts
- [x] Infrastructure: Text Search client/adapters, name-contains JPA query
- [x] Application: `SearchPlacesRequest`, `PlaceController` POST `/search`, OpenAPI
- [x] Provas: `SearchPlacesServiceTest` (11), search cases em `PlaceControllerTest` (4), `OpenApiDocsSmokeTest`

## Próximo passo

`/speckit.verify` numa sessão que **não** implementou este código.

## Arquivos principais

- `specs/54-place-search/checks.md`
- `src/main/java/br/voy/domain/service/SearchPlacesService.java`
- `src/main/java/br/voy/application/controller/PlaceController.java`
- `src/test/java/br/voy/domain/service/SearchPlacesServiceTest.java`
