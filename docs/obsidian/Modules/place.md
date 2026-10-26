---
type: module
date: 2026-10-12
status: in-progress
tags: [module, place]
---

# Place

Nearby: database first (filter by `google_types`), Google fallback, HMAC pagination tokens, page size 20. Recommendations: bounding box + haversine + ranking; active = `status` and date window; empty bbox expands radius. Default Gramado/Canela recommendations are bootstrapped by Flyway `V21` for remote/mobile validation ([[ISSUE-50]]).

`isSaved` is per-user (`user_saved_places`), not a column used by JPA on `place`. List distance is computed at read time.

Sponsored registry (`POST /v1/places`): resolves eligibility from `registration.sponsor` by normalized name and city (`approved` and `subscription_active`), enriches via Google Places (`getPlaceFromText`), rejects duplicates by `googlePlaceId`, persists recommendation window (`status=true`, start/end). Missing, unapproved, or inactive sponsors get 403. Spreadsheet import: `scripts/import-sponsors.py`.

## Related

- [[ISSUE-27]]
- [[ISSUE-43]]
- [[PR-38]]
- [[ISSUE-89]]
- [[2026-10-12-issue-27-sponsored-place-registry]]
- [[2026-10-19-issue-43-sponsor-eligibility-table]]
- [[2026-08-29-refactor-place-query-services]]
- [[2026-08-29-place-query-review-fixes]]
- [[Decisions/001-recommended-places-stay-in-domain]]
- [[Decisions/002-issaved-and-distance-not-on-place]]
- [[Decisions/003-keep-issaved-column-until-all-nodes-upgraded]]
- [[Decisions/004-merge-into-create-get-place-from-db]]
- [[Decisions/005-sponsorship-eligibility-on-registry-request]]
- [[Decisions/006-sponsor-table-eligibility]]
