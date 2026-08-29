---
type: module
date: 2026-08-29
status: in-progress
tags: [module, place]
---

# Place

Nearby: database first (filter by `google_types`), Google fallback, HMAC pagination tokens, page size 20. Recommendations: bounding box + haversine + ranking; active = `status` and date window; empty bbox expands radius.

`isSaved` is per-user (`user_saved_places`), not a column used by JPA on `place`. List distance is computed at read time.

## Related

- [[PR-38]]
- [[ISSUE-89]]
- [[2026-08-29-refactor-place-query-services]]
- [[2026-08-29-place-query-review-fixes]]
- [[Decisions/001-recommended-places-stay-in-domain]]
- [[Decisions/002-issaved-and-distance-not-on-place]]
- [[Decisions/003-keep-issaved-column-until-all-nodes-upgraded]]
- [[Decisions/004-merge-into-create-get-place-from-db]]
