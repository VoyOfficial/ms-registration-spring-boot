---
type: task
date: 2026-10-17
status: closed
tags: [task, backend, place, sponsorship]
github: https://github.com/VoyOfficial/ms-registration-spring-boot/issues/43
---

# ISSUE-43: Fonte de elegibilidade de patrocínio (tabela MVP)

## Goal

Replace `FailClosedSponsorshipEligibilityAdapter` with a table-backed `SponsorshipEligibilityPort` for MVP (planilha → import → Postgres).

## Parent

[[ISSUE-27]]

## Branch

`cursor/sponsor-eligibility-table-b636`

## Sessions

- [[2026-10-19-issue-43-sponsor-eligibility-table]]

## Decisoes

- [[Decisions/006-sponsor-table-eligibility]]
- [[Decisions/005-sponsorship-eligibility-on-registry-request]]

## Modulos

- [[Modules/place]]

## Acceptance

- [x] Flyway table for sponsors
- [x] Lookup by name+city; eligible = approved && subscription_active
- [x] 201 when eligible, 403 otherwise
- [x] Request does not carry approved / subscriptionActive
- [x] Unit + integration tests
- [x] Import process documented
