---
type: session
date: 2026-11-15
workitems: []
tags: [session, bugfix, place, search]
status: closed
parent_workitem: none
parent_session: [[2026-11-14-places-live-it-dod]]
---

# Session: search must stay inside 5 km

## Contexto

`POST /v1/places/search?place=Pizza` with `ordination: Mais avaliados` at Mountain View (`37.422, -122.084`) returned Gramado pizzas about 10,486 km away. The name query loaded every persisted match, then rating order filled the page before Google Text Search ran.

`GET /v1/places` for the same coordinates returned Mountain View and Palo Alto places, all within 3.6 km of the point. That list is the database cache inside the nearby radius (pizza-heavy because earlier searches persisted those rows). It is not the same bug.

## O que foi feito

- [x] Search keeps a place only when haversine distance is at most 5 km, for database rows and Google Text Search rows
- [x] Ordination (`Mais avaliados`, `Mais comentados`, `Mais próximo`) runs on that set
- [x] A full page of distant name matches no longer skips Google
- [x] Distant Google hits are not persisted

## Decisoes tecnicas

- 5 km is the radius already sent as Text Search bias and the nearby default. Google's text-search radius does not restrict results, so the service applies the cap after distance is calculated.
- Nearby behavior stays database-first inside its own radius.

## Arquivos modificados

- `SearchPlacesService.java`
- `PlaceController.java` (OpenAPI description)
- `SearchPlacesServiceTest.java`
- `specs/54-place-search/plan.md`
- `docs/obsidian/Modules/place.md`

## Validacao

- `./mvnw -Dtest=SearchPlacesServiceTest test`

## Proximos passos

- Nenhum no codigo. Deploy para o Render para o mobile passar a ver o raio.
