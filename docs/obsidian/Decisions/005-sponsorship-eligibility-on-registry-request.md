---
type: decision
date: 2026-10-16
status: accepted
tags: [decision, place, sponsorship]
---

# Decision 005: Sponsorship eligibility via domain port

## Context

Issue #27 requires rejecting sponsored place registration when the establishment is not approved or the subscription is inactive. This microservice has no subscription/billing aggregate yet. Client-attested request flags were rejected by Gate 3 (`/code-reviewer`) as insecure.

## Decision

`PlaceRegistryService` resolves eligibility through `SponsorshipEligibilityPort.resolveForRegistration(name, city)` before Google Places or persistence. The default infrastructure adapter `FailClosedSponsorshipEligibilityAdapter` always returns ineligible until a subscription-backed implementation exists. Ineligible callers receive `403 Sponsorship Not Eligible`.

## Consequences

- Production registry stays fail-closed until a real adapter (subscription MS or sponsor table) is wired.
- Tests stub/mock `SponsorshipEligibilityPort` for eligible and ineligible paths.
- `PlaceRequest` carries only place fields (`name`, `city`, `ranking`, optional dates).

## Update (2026-10-19)

The fail-closed adapter is replaced by the sponsor table. The port, the 403, and the ban on client-attested flags stay in force. See [[Decisions/006-sponsor-table-eligibility]].
