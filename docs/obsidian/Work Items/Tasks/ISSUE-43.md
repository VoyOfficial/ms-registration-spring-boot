---
type: task
date: 2026-10-17
status: todo
tags: [task, backend, place, sponsorship]
github: https://github.com/VoyOfficial/ms-registration-spring-boot/issues/43
---

# ISSUE-43: Fonte de elegibilidade de patrocínio (tabela MVP)

## Goal

Replace `FailClosedSponsorshipEligibilityAdapter` with a table-backed `SponsorshipEligibilityPort` for MVP (planilha → import → Postgres).

## Parent

[[ISSUE-27]] / PR #42

## Acceptance

- Flyway table for sponsors
- Lookup by name+city; eligible = approved && subscription_active
- 201 when eligible, 403 otherwise
- Unit + integration tests
- Import process documented

## Links

- https://github.com/VoyOfficial/ms-registration-spring-boot/issues/43
