---
type: session
date: 2026-10-12
workitems: [ISSUE-27]
tags: [session, place, sponsorship]
status: done
parent_workitem: [[ISSUE-27]]
---

# Session: Issue #27 sponsored place registry

## Objetivo

Completar o endpoint de cadastro de estabelecimentos patrocinados (issue #27): elegibilidade (aprovado + assinatura ativa), Google Places, dedupe por `googlePlaceId`, testes e documentação.

## Links

- **Issue**: https://github.com/VoyOfficial/ms-registration-spring-boot/issues/27
- **Branch**: `cursor/sponsored-place-registry-65c7`
- **Modulo**: [[Modules/place]]
- **Decisao**: [[Decisions/005-sponsorship-eligibility-on-registry-request]]

## Completado

- [x] `SponsorshipEligibility` + `SponsorshipNotEligibleException` (403)
- [x] `PlaceRegistryService` valida elegibilidade antes do Google Places
- [x] `PlaceRequest`: `approved`, `subscriptionActive`; `startRecommendation` null-safe
- [x] OpenAPI do `POST /v1/places` atualizado
- [x] Unit/controller tests (sucesso, duplicata, cidade, inelegivel, falha Google, payload invalido)

## Proximos passos

- [x] `mvn clean verify` (pre-push OK; live Places IT skipped sem billing na key)
- [x] PR #42 ligando `Closes #27`
- [ ] Review/merge do PR
- [ ] Follow-up: port de assinatura real (sem flags no request)
- [ ] Sync Status no Project board (token atual sem acesso ao Projects v2 da org)
