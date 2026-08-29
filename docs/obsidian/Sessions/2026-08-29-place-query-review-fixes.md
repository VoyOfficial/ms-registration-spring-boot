---
type: session
date: 2026-08-29
workitems: [PR-38]
tags: [session, review, bugfix]
status: in-progress
parent_workitem: [[PR-38]]
parent_session: [[2026-08-29-refactor-place-query-services]]
---

# Session: review blockers and issue 89 for PR-38

## Objetivo

Revisar o PR #38, corrigir bloqueadores, responder a [[ISSUE-89]] (app) e registrar o alvo de merge.

## Links

- **Work Item**: [[PR-38]]
- **Sessions Anteriores**: [[2026-08-29-refactor-place-query-services]]
- **Modulos**: [[Modules/place]]
- **Decisoes**: [[Decisions/001-recommended-places-stay-in-domain]], [[Decisions/002-issaved-and-distance-not-on-place]], [[Decisions/003-keep-issaved-column-until-all-nodes-upgraded]], [[Decisions/004-merge-into-create-get-place-from-db]]
- **Issue app**: https://github.com/VoyOfficial/voy-app-react-native/issues/89
- **PR**: https://github.com/VoyOfficial/ms-registration-spring-boot/pull/38

## Contexto

Review do branch `cursor/refactor-place-query-services-e144` (base GitHub era `develop`). `create-get-place-from-db` e a branch de places mais alinhada ao `develop` atual. Follow-up de correcao apos o review.

## Completado

- [x] Review: API key em photo URL, cache sem `placeType`, page size 5 vs 20, paginacao nearby, HMAC unsigned
- [x] Nearby: `nearbyPageSize` 20, filtro `google_types` (V19), persist sync, token HMAC, sanitizar URL com `key=`
- [x] Recomendacoes: `status` + janela `start`/`end`; bbox vazia expande raio; 404 so no fim
- [x] Removida V18 (nao dropar `issaved` neste PR)
- [x] `mvn verify` (Surefire + Failsafe `PlacesApiClientIT`)
- [x] Respostas da issue #89 do app

## Decisoes tecnicas

- Ver [[Decisions/002-issaved-and-distance-not-on-place]]
- Ver [[Decisions/003-keep-issaved-column-until-all-nodes-upgraded]]
- Ver [[Decisions/004-merge-into-create-get-place-from-db]]

## Fora deste PR

- `isSaved` real no app: auth + endpoints save/remove (`CurrentUserHelper` ainda retorna `null`)
- Issue GitHub backend #27 (cadastro de patrocinados) e outra task

## Arquivos Modificados (follow-up)

| Arquivo | Mudanca |
| --- | --- |
| `GetNearbyPlacesService.java` | Tipo, page size, persist, token Google |
| `PaginationTokenEncoder.java` | Recusa token sem HMAC |
| `PlaceResponse.java` | Strip API key |
| `GetRecommendedPlacesService.java` | Filtro ativo + expansao de raio |
| `V18__remove_issaved_from_place.sql` | Removido do PR |
| `V19__add_google_types_to_place.sql` | Coluna `google_types` |

## Proximos passos

- [x] Merge PR #38 em `create-get-place-from-db`
- [x] Abrir PR de `create-get-place-from-db` para `develop` (#39)
- [ ] Review/merge do PR #39 em `develop`
- [ ] Follow-up `isSaved` com autenticacao (issue #89 do app)
