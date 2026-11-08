---
type: decision
date: 2026-11-08
status: accepted
tags: [decision, place, search]
---

# Decision 008: Search places uses hybrid data source (DB first + Google Text Search)

## Context

Issue [#54](https://github.com/VoyOfficial/ms-registration-spring-boot/issues/54) adds place search by name with filters, ordering, and `nextPageToken` pagination.

The database today is thin for text search: Flyway `V21` seeds 11 Gramado/Canela recommendations, and the catalog otherwise grows only when nearby (or registry) persists Google results. A DB-only search would often return empty or near-empty pages for real user queries.

`GooglePlacesPort` already supports Nearby Search and Find Place From Text (single candidate for registry). It does **not** yet expose a list Text Search API.

Nearby already established the pattern: database first, Google fallback, persist via `PlacePersistService`, HMAC `nextPageToken`.

## Decision

v1 **Busca de locais** uses a **fonte híbrida**:

1. Query persisted places by name (and filters/ordination) first.
2. If the page is not full, call Google Places **Text Search** to complement results.
3. Persist newly seen Google places (`saveIfAbsent`) so the catalog grows.
4. Reuse the existing pagination-token style where practical (offset / shown ids / Google token).

DB-only and Google-only were rejected for cold-start UX and inconsistency with nearby, respectively.

## Consequences

- New method on `GooglePlacesPort` (+ adapter/client) for Text Search listing.
- Search shares persistence and pagination vocabulary with nearby, but has its own use case/service.
- Google quota/latency apply when the local catalog misses.
- Filter label → Google type mapping must be defined before the Google leg is useful.

## Related

- [[ISSUE-54]] (GitHub #54)
- [[Modules/place]]
- [[Decisions/004-merge-into-create-get-place-from-db]]
