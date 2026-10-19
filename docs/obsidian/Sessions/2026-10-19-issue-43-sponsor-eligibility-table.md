---
type: session
date: 2026-10-19
workitems: [ISSUE-43]
tags: [session, place, sponsorship]
status: closed
parent_workitem: [[ISSUE-43]]
parent_session: [[2026-10-12-issue-27-sponsored-place-registry]]
---

# Session: Issue #43 sponsor table eligibility

## Work Item

- Primary: [[ISSUE-43]]

## Contexto

Substituir o adapter fail-closed por uma tabela `registration.sponsor` carregada a partir da planilha de ops.

## O que foi feito

- Migration Flyway `V20__create_table_sponsor.sql`
- `Sponsor` + `SponsorRepository` + `TableSponsorshipEligibilityAdapter`
- Lookup por nome e cidade normalizados; elegivel so com `approved` e `subscription_active`
- Remocao de `FailClosedSponsorshipEligibilityAdapter`
- Script `scripts/import-sponsors.py`
- Testes unitarios e integracao (201 com seed, 403 sem match / nao aprovado / assinatura inativa)

## Decisoes tecnicas

- [[Decisions/006-sponsor-table-eligibility]]
- Acentos nao sao removidos, para o Java e o script usarem a mesma chave
- O request continua sem flags de elegibilidade

## Arquivos modificados

- `src/main/java/br/voy/domain/entity/Sponsor.java`
- `src/main/java/br/voy/domain/repository/SponsorRepository.java`
- `src/main/java/br/voy/infrastructure/adapters/TableSponsorshipEligibilityAdapter.java`
- `src/main/java/br/voy/infrastructure/model/SponsorModel.java`
- `src/main/java/br/voy/infrastructure/repository/relational/RelationalSponsorRepository.java`
- `src/main/resources/db/migration/V20__create_table_sponsor.sql`
- `src/test/java/br/voy/application/controller/PlaceRegistrySponsorEligibilityIntegrationTest.java`

## Proximos passos

- Review do PR
- Sync do Project board (o token desta run nao resolveu o Project v2 da org)
