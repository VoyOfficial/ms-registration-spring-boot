# Place search stays within 5 km

**Date**: 2026-11-15
**Type**: Bug Fix
**Files Changed**: `SearchPlacesService.java`, `PlaceController.java`, `SearchPlacesServiceTest.java`

## Problem

`POST /v1/places/search` loaded every persisted place whose name contained the term, then applied ordination. `Mais avaliados` therefore returned the highest-rated name matches in the whole catalog. A Pizza search at Mountain View (`latitude=37.421998333333335`, `longitude=-122.084`) led with Gramado - RS places about 10,486 km away, and those rows filled the page so Google Text Search never ran.

`GET /v1/places` for the same coordinates already applies its radius in SQL. The pasted page (Googleplex, Shoreline Amphitheatre, Mountain View pizza and hotels) is entirely within 3.6 km.

### Root Cause

Search treated latitude and longitude as a distance label and as a Google location bias, not as a limit. Google Text Search `radius` biases results and can still return places outside that radius.

## Solution

`SearchPlacesService` drops database and Google places farther than 5 km (`DEFAULT_TEXT_SEARCH_RADIUS_METERS`) before ordination, pagination, and `saveIfAbsent`. The same 5 km value is still sent to Google as bias.

### Rationale

Ordination stays meaningful (highest rated, most commented, or closest) among places the user can actually be asking about. Nearby's contract is unchanged.
