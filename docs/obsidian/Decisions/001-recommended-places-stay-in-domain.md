---
type: decision
date: 2026-08-29
status: closed
tags: [decision, architecture]
---

# 001 - Recommended places stay in domain

`GetRecommendedPlacesUseCase` must return `Place` and `NearbyPlaces`, not application DTOs. Mapping to `PlaceResponse` happens in `PlaceController`.

## Why

Domain depending on `application.controller.response` was documented ArchUnit tech debt. Removing it lets the "domain must not depend on application" rule apply without exemptions.

## Related

- [[PR-38]]
- [[Modules/place]]
- [[2026-08-29-refactor-place-query-services]]
