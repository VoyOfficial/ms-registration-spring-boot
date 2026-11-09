---
type: decision
date: 2026-11-08
status: accepted
tags: [decision, place, search, api-contract]
---

# Decision 009: Search reuses NearbyPlacesResponse / PlaceResponse

## Context

Issue [#54](https://github.com/VoyOfficial/ms-registration-spring-boot/issues/54) defines a mobile-facing search endpoint. The legacy mobile client (`RemoteSearchPlaces`) expects a raw array of `SearchPlaceModel` (`title`, `location`, `imageUri`, `myDistanceOfLocal`, …). Nearby and recommendations already return `NearbyPlacesResponse` with `PlaceResponse` items and `nextPageToken`.

`RemoteListPlaces` on the app already maps `PlaceResponse`-like fields onto the card model (`name`→`title`, `photo`/`photoReference`→`imageUri`, `address`→`location`, etc.).

Duplicating a search-specific DTO would fork OpenAPI and force two list shapes for the same cards.

## Decision

Search returns **`NearbyPlacesResponse` / `PlaceResponse`**, same as nearby and recommendations.

No backend DTO shaped like `SearchPlaceModel`. Field mapping for the app card is a mobile adapter concern (out of scope for #54 except documentation).

## Consequences

- One OpenAPI schema for place list cards across list endpoints.
- Mobile must update `RemoteSearchPlaces` (and URL) to unwrap `places` + map fields like `RemoteListPlaces`.
- Pagination stays on `nextPageToken` at the response root, not only as a query echo.

## Related

- [[Decisions/008-search-places-hybrid-source]]
- [[Decisions/001-recommended-places-stay-in-domain]]
- [[Modules/place]]
