---
type: decision
date: 2026-10-19
status: accepted
tags: [decision, place, sponsorship]
---

# Decision 006: Sponsor table as the MVP eligibility source

## Context

[[Decisions/005-sponsorship-eligibility-on-registry-request]] keeps eligibility off the client request and behind `SponsorshipEligibilityPort`. The first adapter was fail-closed, so `POST /v1/places` always returned 403. Issue #43 needs a real MVP source without a billing service.

## Decision

Eligibility is loaded from `registration.sponsor`.

- Lookup key is `name` + `city` after trim, collapsed whitespace, and lowercasing. Accents stay as written.
- A row is eligible only when `approved` and `subscription_active` are both true.
- A missing row is ineligible.
- Ops loads the table from a spreadsheet with `scripts/import-sponsors.py`. The API does not read Excel.

## Consequences

- `FailClosedSponsorshipEligibilityAdapter` is removed. `TableSponsorshipEligibilityAdapter` is the only `SponsorshipEligibilityPort` bean.
- `PlaceRequest` still has no approval or subscription flags.
- Re-importing the same normalized name and city updates the existing row.

## Related

- [[ISSUE-43]]
- [[ISSUE-27]]
- [[Modules/place]]
- [[Decisions/005-sponsorship-eligibility-on-registry-request]]
