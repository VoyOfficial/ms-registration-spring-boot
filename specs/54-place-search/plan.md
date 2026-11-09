# Busca de locais (Issue #54)

## Problem

O app mobile precisa buscar estabelecimentos por **nome**, com filtros multi-tipo, ordenação e paginação. Hoje o backend só tem nearby (por coordenadas) e recommendations; não há search por texto. O catálogo local é fino (seed V21 + o que o nearby persistiu), então busca só no DB devolveria quase nada. Após o grill (#54 / ADRs 008–009), a verdade desejada é: `POST /v1/places/search` híbrido (DB first + Google Text Search), resposta no mesmo envelope dos outros lists.

## Flow

1. Client chama `POST /api/registration/v1/places/search?place={termo}&latitude={lat}&longitude={lng}&nextPageToken={opcional}` com body `{ "types": ["Restaurantes", ...], "ordination": "Mais avaliados" }`.
2. `PlaceController` valida query obrigatória (`place`, `latitude`, `longitude`); body opcional (`types` / `ordination`).
3. Controller chama `SearchPlacesUseCase` com termo, coordenadas, types (labels PT), ordination, token.
4. `SearchPlacesService`:
   - Decodifica `nextPageToken` via `PaginationTokenEncoder` (offset DB + shown googlePlaceIds + google token), igual nearby.
   - Mapeia labels PT → `google_types` primários (mapa do grill). `types` vazio/ausente = sem filtro.
   - Busca no DB places cujo **nome** contém o termo (case-insensitive); aplica filtro de tipos; calcula distância com `GeoCalculator`; ordena (`Mais avaliados` / `Mais comentados` / default `Mais próximo`).
   - Preenche a página (page size alinhado ao nearby, ~20) a partir do offset, dedupe por `googlePlaceId`.
   - Se a página não encheu → `GooglePlacesPort.textSearch(...)` (novo); persiste via `PlacePersistService.saveIfAbsent`; completa a página; atualiza google token.
   - Monta próximo token HMAC se ainda houver mais DB/Google.
5. Controller mapeia `NearbyPlaces` → `NearbyPlacesResponse` / `PlaceResponse` (reuso; `distanceFromUserLocation` preenchido no domain).
6. Respostas:
   - Há itens → **200** + body.
   - Zero itens na 1ª busca (sem Continuação útil) → **204**.
   - Página seguinte com token válido mas sem itens → **200** `{ places: [], nextPageToken: null }`.
   - Query inválida (place/lat/lng ausentes) → **400** (`DefaultResponse` / Bean Validation).
   - Erros Google (denied / over query / invalid) → mesmos status já mapeados no `ExceptionHandlerAdvice` (403/429/422/…).

## Impact

**Muda**

- Passa a existir busca por nome paginada, filtrável e ordenável.
- Catálogo local cresce quando o Text Search traz places novos (mesmo padrão do nearby).
- OpenAPI/Swagger ganha a operação de search.

**Não muda**

- Contratos de nearby, recommendations, details e registry.
- Shape de card: continua `PlaceResponse` (sem DTO `SearchPlaceModel`).
- UI mobile e apontamento de URL (issue separada no app; app deve enviar lat/lng e adaptar o client).
- Cadastro/edição de places.

## Relations

| Peça | Reuso vs criação |
| --- | --- |
| Agregado `Place` / `NearbyPlaces` | **Reuso** |
| `PaginationTokenEncoder`, `GeoCalculator`, `PlacePersistService` | **Reuso** |
| `PlaceResponse`, `NearbyPlacesResponse` | **Reuso** (ADR 009) |
| `PlaceController` | **Editar** — novo método POST |
| `GooglePlacesPort` + `PlacesApiClient` + `GooglePlacesAdapter` (+ mock) | **Editar** — método Text Search listando resultados |
| `PlaceRepository` / JPA | **Editar** — query por nome (contains, CI) + filtro opcional por tipos |
| `SearchPlacesUseCase` + `SearchPlacesService` | **Criar** (espelho nearby; YAGNI) |
| Request body DTO | **Criar** (ex.: `SearchPlacesRequest` com `types`, `ordination`) |
| Mapa Filter PT → tipo | **Criar** util/enum de domínio pequeno (constantes do mapa acordado) |
| Flyway | **Nenhum** — usa tabela `place` / `google_types` existentes |
| Tabela | `registration.place` (leitura + insert via persist já existente) |

## Surface

- **Método/path:** `POST /v1/places/search` (context path `/api/registration`)
- **Query:** `place` (string, obrigatório), `latitude` (double, obrigatório), `longitude` (double, obrigatório), `nextPageToken` (string, opcional)
- **Body JSON:**
  ```json
  {
    "types": ["Restaurantes", "Lazer"],
    "ordination": "Mais próximo"
  }
  ```
  - `types`: lista de labels PT do mobile; `[]`/omitido = todos.
  - `ordination`: `Mais avaliados` | `Mais comentados` | `Mais próximo`; omitido/blank → `Mais próximo`.
- **Mapeamento Filter → google_types (v1):**  
  `Restaurantes`→`restaurant`; `Cafeteiras`→`cafe`; `Hotéis`→`lodging`; `Vida noturna`→`night_club`; `Lazer`→`park`; `Esporte`→`gym`; `Entretenimento`→`tourist_attraction`.
- **Response 200:** `NearbyPlacesResponse` (`places: PlaceResponse[]`, `nextPageToken`)
- **Response 204:** sem body (zero resultados)
- **Erros:** 400 validação; 403/422/429/500 alinhados às exceptions Google já existentes
- **i18n:** reutilizar chaves Google existentes onde couber; se precisar mensagem própria de search vazio, só se o 204 não bastar (preferir 204 sem body, como nearby zero-results)
- **Auth/`isSaved`:** v1 igual aos lists atuais (default `false` se o endpoint não enriquecer usuário; não bloqueia entrega)

## Landing

**domain**

- `SearchPlacesUseCase.java` (novo)
- `SearchPlacesService.java` (novo) — híbrido, ordenação, paginação
- Util/mapa de Filter labels → tipo (novo, mínimo)
- `GooglePlacesPort.java` — adicionar text search → `NearbyPlaces` (ou lista + token)
- `PlaceRepository.java` — `findPlacesByNameContaining(...)` (assinatura a fechar na implementação)

**infrastructure**

- `PlacesApiClient` — `textSearchQuery` (Places API Text Search) com location bias nas coordenadas do usuário
- `GooglePlacesAdapter` + `GooglePlacesAdapterMock` — implementar novo método
- `PlaceJpaRepository` + `RelationalPlaceRepository` — query ILIKE/contains no `name`, opcionalmente filtrando `google_types`

**application**

- `PlaceController` — `POST /search`
- `SearchPlacesRequest` (body)
- OpenAPI annotations no método
- Ajuste pontual no advice **só se** surgir exception nova de search vazio (evitar se 204 for decidido no controller)

**resources**

- `messages*.properties` — só se nova chave for inevitável

**testes**

- Unit: `SearchPlacesServiceTest` — match por nome, filtro types, cada ordination, híbrido quando DB incompleto, token inválido → 1ª página, página cheia sem Google
- Unit/adapter: mock Text Search no adapter se houver lógica
- Integration: `PlaceController` / IT — 200 com places, 204 vazio, 400 sem lat/lng ou sem `place`, filtro + ordenação smoke, OpenAPI path presente se o validator do projeto exigir

**fora do Landing**

- App React Native, seeds extras, migration, DTO `SearchPlaceModel`

## Criteria

1. `POST /v1/places/search` documentado no OpenAPI/Swagger com query `place`, `latitude`, `longitude`, `nextPageToken` e body `types`/`ordination`.
2. Busca por trecho de nome (CI) retorna places correspondentes do DB quando existem.
3. `types` com labels PT restringe aos `google_types` do mapa v1; `types` vazio/ausente não filtra por categoria.
4. `ordination` aplica rating desc, `userRatingsTotal` desc ou distância asc; default `Mais próximo` quando ausente.
5. Lat/lng obrigatórios: ausência → 400.
6. Página incompleta no DB dispara Text Search Google, persiste novos via `saveIfAbsent`, e completa a página sem duplicar `googlePlaceId`.
7. Paginação via `nextPageToken` (HMAC) funciona entre páginas.
8. Zero resultados → 204; fim de paginação vazio → 200 com `places: []` e `nextPageToken: null`.
9. Response usa `NearbyPlacesResponse` / `PlaceResponse` (inclui campos usáveis no card: name, address/city, rating, userRatingsTotal, photo, distanceFromUserLocation).
10. Testes unitários do service cobrem match, filtro, as três ordenações, híbrido e token; teste de integração cobre 200/204/400 principais.
