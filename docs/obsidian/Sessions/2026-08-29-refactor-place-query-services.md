---
type: session
date: 2026-08-29
workitems: [PR-38]
tags: [session, refactor, performance]
status: closed
parent_workitem: [[PR-38]]
parent_session: none
---

# Session: refactor nearby and recommendation list queries

## Objetivo

Speed up get-place-from-DB (nearby) and recommended-places request paths while keeping existing tests and ArchUnit rules green. Open a PR to `develop`.

## Links

- **Work Item**: [[PR-38]]
- **Sessions Anteriores**: none
- **Modulos**: [[Modules/place]]
- **Decisoes**: [[Decisions/001-recommended-places-stay-in-domain]]

## Completado

- [x] Return domain `Place` / `NearbyPlaces` from `GetRecommendedPlacesUseCase` instead of application DTOs
- [x] Batch saved-place lookups via `findSavedPlaceIdsByUser`
- [x] Skip photo hydration on list queries (`PlaceModel.toListDomain`, lazy photos)
- [x] Extract `GeoCalculator` and `CurrentUserPort`
- [x] Keep expansion, pagination, and Google fallback behavior
- [x] `./mvnw clean verify` and PR #38 against `develop`

## Arquivos Modificados

| Arquivo | Mudanca |
| --- | --- |
| `GetRecommendedPlacesService.java` | Shared search loop, scored places, batch isSaved |
| `GetRecommendedPlacesUseCase.java` | Domain return types |
| `GetNearbyPlacesService.java` | Clearer DB-first then Google flow |
| `PlaceModel.java` | Lazy photos + `toListDomain()` |
| `RelationalPlaceRepository.java` | List mapping without photos |
| `PlaceController.java` | Map domain to `PlaceResponse` |
| `ArchitectureFitnessTest.java` | Drop GetRecommendedPlaces application-layer exemption |

## Proximos passos

- Continuacao: [[2026-08-29-place-query-review-fixes]]
- [ ] Merge em `create-get-place-from-db`, depois PR para `develop`
