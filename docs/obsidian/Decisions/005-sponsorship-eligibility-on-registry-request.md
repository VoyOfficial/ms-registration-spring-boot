---
type: decision
date: 2026-10-12
status: accepted
tags: [decision, place, sponsorship]
---

# Decision 005: Sponsorship eligibility on registry request

## Context

Issue #27 requires rejecting sponsored place registration when the establishment is not approved or the subscription is inactive. This microservice has no subscription/billing aggregate yet.

## Decision

`POST /v1/places` requires `approved` and `subscriptionActive` on `PlaceRequest`. `PlaceRegistryService` validates them via `SponsorshipEligibility` before calling Google Places or persisting. Ineligible callers receive `403 Sponsorship Not Eligible`.

## Consequences

- Callers (BFF/admin/subscription service) must send eligibility flags until a dedicated subscription port exists.
- Flags are not persisted on `place`; they are registration-time authorization inputs only.
- Follow-up: replace request flags with `SponsorshipEligibilityPort` backed by a subscription service or local sponsor table.
