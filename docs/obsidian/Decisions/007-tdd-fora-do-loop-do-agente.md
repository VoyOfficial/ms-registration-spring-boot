---
type: decision
date: 2026-10-19
status: accepted
tags: [decision, workflow, testing]
---

# Decision 007: TDD sai do loop do agente

## Context

O harness tratava RED → GREEN → REFACTOR como etapa obrigatoria de toda implementacao. No loop de um agente isso custa varias voltas de contexto, quebra entre modelos e nao entrega o beneficio que o TDD da para quem escreve o teste na mao. A referencia e o artigo "TDD in the agent loop" (Birgitta Bockeler / Martin Fowler).

O mesmo ajuste ja foi aplicado num projeto irmao com este workflow.

## Decision

- Testes continuam obrigatorios (unidade na camada certa + integracao). E2E continua suspenso ate existir framework.
- O agente desenha a solucao inteira no feedforward e so depois implementa.
- `tdd-workflow.mdc` foi substituido por `.cursor/rules/testing-workflow.mdc`.
- O ciclo RED → GREEN → REFACTOR deixa de ser gate.

## Consequences

- `./mvnw clean verify` e o sensor deterministico dos testes.
- Quem revisar nao cobra "teste primeiro". Cobra teste que prende o comportamento.
- O Spec Kit tradicional nao vira fluxo de TDD. O default de feature passa a ser Spec Lean.

## Related

- [[2026-10-19-workflow-lean-harness]]
- `.cursor/rules/testing-workflow.mdc`
- `.cursor/rules/harness-engineering.mdc`
