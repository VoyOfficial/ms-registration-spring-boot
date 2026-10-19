---
type: session
date: 2026-10-19
workitems: []
tags: [session, workflow, harness]
status: closed
parent_workitem: none
parent_session: none
---

# Session: Workflow lean no harness

## Work Item

- Nenhum. Melhoria de workflow a partir do historico de um projeto irmao com o mesmo harness.

## Contexto

O historico `workflow-ai-melhorias-aplicadas` fechou quatro frentes: contexto enxuto, Spec Lean, validacao deterministica e review em camadas com Reuse-First. Este repositorio ainda tinha Spec Kit tradicional, TDD obrigatorio no loop do agente e rules grandes em `alwaysApply`.

## O que foi feito

- Contexto always-on medido em caracteres (`AGENTS.md` + rules com `alwaysApply: true`): 48.676 → 16.019 (**-67%**). `architecture`, `solid-principles` e `code-conventions` passam a carregar com `src/**/*.java`. `AGENTS.md` virou ponteiro. `harness-engineering.mdc` e a fonte unica.
- TDD saiu do loop do agente. Testes continuam obrigatorios. Decisao: [[Decisions/007-tdd-fora-do-loop-do-agente]]. `tdd-workflow.mdc` virou `testing-workflow.mdc`.
- Spec Lean e o default: `/speckit.lean-plan`, `/speckit.lean-checks`, `/speckit.verify`. Spec Kit tradicional permanece para feature grande.
- `scripts/validate-dod.sh` cobre os 5 gates. Gate 2 (E2E) fica suspenso: nao ha UI web, Playwright nao se aplica, e nao ha framework E2E.
- Pre-push roda jscpd so nos `.java` do range do push (threshold 7%). Lista de arquivos vai por `xargs`, porque o jscpd nao separa um unico argumento com espacos.
- Hook `stop` (`.cursor/hooks/spec-lean-verify-gate.py`) pede `/speckit.verify` quando ha `checks.md` e Java no range. Quieto em planejamento, discussao e docs.
- `/code-reviewer` ganhou Reuse-First bloqueante: arvore atual, arquivos novos, placement bidirecional.
- Agentes nao foram convertidos em skills. Eles ja carregam sob demanda (`/nome`). A troca seria cosmetica.

## Decisoes tecnicas

- [[Decisions/007-tdd-fora-do-loop-do-agente]]
- Duplicacao historica do `src/` nao bloqueia push. So a duplicacao que o range introduz.
- Cobertura JaCoCo do repo inteiro nao falha o Gate 1. O criterio de codigo novo fica no `/code-reviewer`.

## Arquivos modificados

- `.cursor/rules/harness-engineering.mdc`, `testing-workflow.mdc`, `spec-lean.mdc`
- `.cursor/commands/speckit.lean-plan.md`, `speckit.lean-checks.md`, `speckit.verify.md`
- `.cursor/agents/code-reviewer.md`
- `.cursor/hooks.json`, `.cursor/hooks/spec-lean-verify-gate.py`
- `scripts/validate-dod.sh`, `scripts/duplication-check-range.sh`, `.jscpd.json`
- `.githooks/pre-push`

## Proximos passos

- Nenhum. Spec Lean passa a ser o fluxo default na proxima feature.
