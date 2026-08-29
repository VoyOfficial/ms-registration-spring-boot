---
type: decision
date: 2026-08-29
status: closed
tags: [decision, place]
---

# 002 - isSaved and distanceOfLocal do not belong on place

From [voy-app-react-native#89](https://github.com/VoyOfficial/voy-app-react-native/issues/89):

- **isSaved on `place`**: no. Saved is per user. Persist in `user_saved_places` (V17). API `isSaved` is derived at read time when a user is authenticated.
- **distanceOfLocal on `place`**: no as a stored place attribute. Distance depends on the requester; list endpoints compute haversine at read time. The column may stay unused/nullable until a later cleanup.

## Related

- [[PR-38]]
- [[Modules/place]]
- [[2026-08-29-place-query-review-fixes]]
- [[Decisions/003-keep-issaved-column-until-all-nodes-upgraded]]
