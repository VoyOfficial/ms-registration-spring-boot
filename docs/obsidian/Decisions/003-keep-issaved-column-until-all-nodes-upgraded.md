---
type: decision
date: 2026-08-29
status: closed
tags: [decision, persistence, deploy]
---

# 003 - Keep issaved column until all nodes are upgraded

Do **not** ship `V18 DROP COLUMN isSaved` in PR #38.

New JPA no longer maps `isSaved` on `PlaceModel`. Old instances still do. Dropping the column during a rolling deploy breaks those old nodes.

Expand/contract: leave the unused `issaved` column in PostgreSQL. Drop it in a later migration after every instance runs this code. Keep V17 (`user_saved_places`).

## Related

- [[PR-38]]
- [[Decisions/002-issaved-and-distance-not-on-place]]
- [[2026-08-29-place-query-review-fixes]]
