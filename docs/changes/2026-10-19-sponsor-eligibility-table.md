# Sponsor table eligibility

**Date**: 2026-10-19
**Type**: New Feature
**Files Changed**: `SponsorshipEligibilityPort` adapter, `registration.sponsor`, Flyway `V20__create_table_sponsor.sql`

## Problem

`POST /v1/places` resolved sponsorship through `SponsorshipEligibilityPort`, but the only adapter was fail-closed. Every registry call returned 403 until a real source existed.

### Symptoms

- Eligible establishments could not be registered.
- The client was correctly blocked from sending `approved` or `subscriptionActive`.

### Root Cause

Issue #27 shipped the port without a billing service. Issue #43 asks for a spreadsheet-backed Postgres table instead.

## Solution

### Changes Made

`TableSponsorshipEligibilityAdapter` reads `registration.sponsor` by a normalized name and city. Eligible means `approved` and `subscription_active`. A missing row stays ineligible.

**Rationale**: Ops can import a sheet without adding a billing service or trusting the caller.

### Architecture / Pattern Applied

`domain` keeps `Sponsor` and `SponsorRepository`. `infrastructure` maps `SponsorModel`, normalizes the lookup key, and implements the existing port. `application` is unchanged aside from the OpenAPI description. `PlaceRequest` still has no eligibility flags.

### Impact

**Affected components**:

- `POST /v1/places`
- Flyway schema `registration.sponsor`

**Breaking Changes**: No. Calls that were fail-closed now return 201 when a matching approved, active sponsor exists.

## Validation

### How to Test

1. Import a sponsor with `scripts/import-sponsors.py`.
2. `POST /v1/places` with the same name and city.
3. Expect 201 when both flags are true, and 403 otherwise.

### Automated Tests

- [x] Unit tests for the normalizer, repository, and adapter
- [x] Integration test: seeded sponsor returns 201; missing, unapproved, and inactive return 403
- [ ] E2E tests needed (only if E2E is already configured for this project)

## References

- https://github.com/VoyOfficial/ms-registration-spring-boot/issues/43
- `docs/sponsor-import.md`
- `docs/obsidian/Decisions/006-sponsor-table-eligibility.md`
