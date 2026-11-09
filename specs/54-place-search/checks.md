# Checks — Busca de locais

## Coverage
Um item por criterio do plano. Nada do Criteria fica sem check. Nada entra aqui sem estar no Criteria.

- [ ] C1: `POST /v1/places/search` documentado no OpenAPI/Swagger com query `place`, `latitude`, `longitude`, `nextPageToken` e body `types`/`ordination`.
  - Prova: `./mvnw test -Dtest=OpenApiDocsSmokeTest`

- [ ] C2: Busca por trecho de nome (CI) retorna places correspondentes do DB quando existem.
  - Prova: `./mvnw test -Dtest=SearchPlacesServiceTest#mustReturnDbPlacesMatchingNameCaseInsensitive`

- [ ] C3: `types` com labels PT restringe aos `google_types` do mapa v1; `types` vazio/ausente nao filtra por categoria.
  - Prova: `./mvnw test -Dtest=SearchPlacesServiceTest#mustFilterByPortugueseTypeLabels,SearchPlacesServiceTest#mustNotFilterWhenTypesAbsentOrEmpty`

- [ ] C4: `ordination` aplica rating desc, `userRatingsTotal` desc ou distancia asc; default `Mais proximo` quando ausente.
  - Prova: `./mvnw test -Dtest=SearchPlacesServiceTest#mustOrderByMostRated,SearchPlacesServiceTest#mustOrderByMostCommented,SearchPlacesServiceTest#mustOrderByClosestByDefault`

- [ ] C5: Lat/lng obrigatorios: ausencia → 400.
  - Prova: `./mvnw test -Dtest=PlaceSearchControllerTest#mustReturn400WhenSearchMissingLatitudeOrLongitude`

- [ ] C6: Pagina incompleta no DB dispara Text Search Google, persiste novos via `saveIfAbsent`, e completa a pagina sem duplicar `googlePlaceId`.
  - Prova: `./mvnw test -Dtest=SearchPlacesServiceTest#mustSupplementFromGoogleTextSearchWithoutDuplicates`

- [ ] C7: Paginacao via `nextPageToken` (HMAC) funciona entre paginas.
  - Prova: `./mvnw test -Dtest=SearchPlacesServiceTest#mustPaginateAcrossPagesWithToken`

- [ ] C8: Zero resultados → 204; fim de paginacao vazio → 200 com `places: []` e `nextPageToken: null`.
  - Prova: `./mvnw test -Dtest=PlaceSearchControllerTest#mustReturn204WhenSearchHasZeroResults,PlaceSearchControllerTest#mustReturn200EmptyWhenSearchContinuationHasNoItems`

- [ ] C9: Response usa `NearbyPlacesResponse` / `PlaceResponse` (name, address/city, rating, userRatingsTotal, photo, distanceFromUserLocation).
  - Prova: `./mvnw test -Dtest=PlaceSearchControllerTest#mustReturn200SearchWithNearbyPlacesResponseShape`

- [ ] C10: Testes unitarios do service cobrem match, filtro, as tres ordenacoes, hibrido e token; teste de integracao cobre 200/204/400 principais.
  - Prova: `./mvnw test -Dtest=SearchPlacesServiceTest,PlaceSearchControllerTest`

## Handoff
- Plano: specs/54-place-search/plan.md
- Landing: SearchPlacesUseCase, SearchPlacesService, PlaceSearchFilterMapper, PlaceSearchOrdination, GooglePlacesPort.textSearch, PlaceRepository.findPlacesByNameContaining, PlacesApiClient.textSearchQuery, GooglePlacesAdapter(+Mock), PlaceJpaRepository/RelationalPlaceRepository, SearchPlacesRequest, PlaceController POST /search, OpenApiDocsSmokeTest, SearchPlacesServiceTest, PlaceSearchControllerTest
- Fora de escopo: app React Native, seeds extras, Flyway, DTO SearchPlaceModel, contratos nearby/recommendations/details/registry
- Verificador: `/speckit.verify` numa sessao que nao implementou este codigo
