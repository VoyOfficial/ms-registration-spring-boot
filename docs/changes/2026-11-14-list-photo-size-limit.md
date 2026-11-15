# Align the nearby list photo size limit

**Date**: 2026-11-14
**Type**: Bug Fix
**Files Changed**: `PlacePhotoRef`, `PlacePhotoJpaRepository`, `RelationalPlaceRepository`, `PlaceNearbyPersistenceIntegrationTest`

## Problem

The nearby list skipped photos in two different units that shared one number, 150000.

### Symptoms

- A stored image of 150KB could pass the SQL filter and then disappear from the list after the bytes were loaded.
- The photo query returned `Object[]`, so a column shift failed only at runtime.

### Root Cause

`image_base64` is `bytea`. `length(image_base64)` counts stored bytes. `String.length()` on the response counts base64 characters, which is about 4/3 of the raw image. The same constant was applied to both, so a raw image between 113KB and 150KB was loaded and then dropped.

## Solution

### Changes Made

**`RelationalPlaceRepository`**: the SQL filter stays at 150000 stored bytes. The in-memory check uses the largest base64 string that size can produce, 200000 characters. A row the query accepts is a row the list can return. A larger payload is still dropped before it is serialized.

**`PlacePhotoJpaRepository`**: the list query returns `PlacePhotoRef` (`id`, `placeId`, `photoReference`, `principalPhoto`) instead of `Object[]`.

**Rationale**: one stored-byte ceiling stops the multi-megabyte rows that made `GET /v1/places` return 500. The character ceiling matches that byte ceiling, so the two checks no longer disagree.

### Architecture / Pattern Applied

The change stays in `infrastructure`. The domain `Place` model is unchanged. The projection is a persistence read model, not a domain type.

### Impact

**Affected components**:

- `GET /v1/places`
- Recommendation and name search lists that share `toListWithPrincipalPhoto`

**Breaking Changes**: No. Photos at or under 150KB stored are returned. Photos above that stay off the list, and the place itself is still returned.

## Validation

### How to Test

1. Store a place whose principal photo decodes to 150000 bytes.
2. `GET /api/registration/v1/places` returns 200 and that photo.
3. Store a photo of 150001 bytes.
4. The same call returns 200, the place, and no photo.

### Automated Tests

- [x] Integration tests verified (`PlaceNearbyPersistenceIntegrationTest`)
- [ ] E2E tests needed (only if E2E is already configured for this project)

## References

- Pull request #63
