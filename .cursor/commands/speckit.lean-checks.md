# /speckit.lean-checks

Deriva `checks.md` do plano aceito e implementa o bloco inteiro.

Nao usar este comando na mesma conversa que escreveu o `plan.md`. A implementacao comeca com contexto zerado (`.cursor/rules/spec-lean.mdc`). Se o plano foi produzido aqui, parar e pedir uma janela nova.

## Entrada

```text
$ARGUMENTS
```

Caminho do `plan.md`, slug em `specs/`, ou numero da Issue. Se vazio, usar o `specs/*/plan.md` da branch atual. Se houver mais de um, perguntar.

## Checks

Escrever `specs/<slug>/checks.md`:

```markdown
# Checks — <titulo>

## Coverage
Um item por criterio do plano. Nada do Criteria fica sem check. Nada entra aqui sem estar no Criteria.

- [ ] C1: <criterio>
  - Prova: `./mvnw test -Dtest=<Classe>` (ou o teste de integracao da camada)

## Handoff
- Plano: specs/<slug>/plan.md
- Landing: <lista curta de arquivos>
- Fora de escopo: <o que o Impact marcou como intocado>
- Verificador: `/speckit.verify` numa sessao que nao implementou este codigo
```

Cada prova aponta para um teste que falha se o comportamento sumir. Nao inventar E2E: Gate 2 esta suspenso.

## Implementar

1. Reler so `plan.md` e `checks.md`. Nao reler o transcript de planejamento.
2. Seguir `.cursor/rules/testing-workflow.mdc` e `.cursor/rules/ms-registration.mdc`.
3. Implementar o Landing. Testes nascem com o codigo. Sem ciclo RED/GREEN/REFACTOR.
4. Rodar as provas listadas em Coverage.
5. Nao declarar a tarefa pronta. Dizer que o proximo passo e `/speckit.verify` num verificador que nao e o autor.

Commits: `.cursor/rules/harness-engineering.mdc`.
