---
type: session
date: 2026-10-26
workitems: [ISSUE-50]
tags: [session, deploy, recommendations, seed]
status: closed
parent_workitem: [[ISSUE-50]]
parent_session: none
---

# Session: Issue 50 — recommendation seed + remote deploy issue

## Objetivo

Abrir issue no dash para deploy remoto (integração mobile) e adicionar lugares recomendados padrão via Flyway para validar a feature.

## Completado

- [x] GitHub issue #50 criada com critérios de aceite de deploy remoto
- [x] Flyway `V21__seed_default_recommended_places.sql` (11 lugares ativos Gramado/Canela, idempotente)
- [x] Garantir coluna `google_place_id` + unique quando possível
- [x] Estender `ACTIVE_END` do seed de teste para 2027-12-31
- [x] Work item + CURRENT_WORK atualizados

## Arquivos

- `src/main/resources/db/migration/V21__seed_default_recommended_places.sql`
- `src/test/java/br/voy/testsupport/RecommendedPlacesTestData.java`
- `src/test/resources/db/seed/recommended-places-issue-31.sql`
- `docs/obsidian/Work Items/Tasks/ISSUE-50.md`
- `docs/obsidian/CURRENT_WORK.md`

## Proximos passos

- Executar o deploy remoto (checklist da issue #50)
- Fechar manualmente a issue #49 (placeholder)
- Adicionar #50 ao Project board quando houver permissão `project`
