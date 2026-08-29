---
type: decision
date: 2026-08-29
status: in-progress
tags: [decision, git]
---

# 004 - Merge PR-38 into create-get-place-from-db first

PR #38 (`cursor/refactor-place-query-services-e144`) was opened against `develop`, but it forked from `create-get-place-from-db` and that branch is ahead of `develop` for places work.

Stack:

1. Merge #38 into `create-get-place-from-db`
2. Open a PR from `create-get-place-from-db` to `develop`

Do not merge #38 straight to `develop` without that rebase/retarget.

## Related

- [[PR-38]]
- [[2026-08-29-place-query-review-fixes]]
