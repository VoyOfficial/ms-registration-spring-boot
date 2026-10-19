# /speckit.lean-plan

Gera um plano unico para revisao humana. Nao implementa. Nao cria `research.md`, `data-model.md`, `contracts/` nem `tasks.md`.

Spec Kit tradicional (`/speckit.specify` em diante) fica reservado a feature muito grande ou multi-agregado. Ver `.cursor/rules/spec-lean.mdc`.

## Entrada

```text
$ARGUMENTS
```

Considerar o texto do usuario antes de seguir. Se vier um numero, tratar como GitHub Issue (`gh issue view <n>`).

## Passos

1. Session startup curto: branch atual, `docs/obsidian/CURRENT_WORK.md`, sessao da Issue se existir.
2. Ler a Issue (titulo, descricao, criterios de aceite) ou o pedido do usuario.
3. Procurar reuso em `src/main/java` antes de propor tipo novo (UseCase, Service, Validator, Repository, config).
4. Escolher o slug: `specs/<issue>-<kebab>` (ex.: `specs/43-sponsor-eligibility`). Se o diretorio ja tiver um `plan.md` tradicional preenchido, nao sobrescrever: perguntar.
5. Escrever `specs/<slug>/plan.md` com as secoes abaixo, em portugues, concreto para este servico. Sem catalogo de componentes e sem template do Spec Kit.

## plan.md

```markdown
# <titulo>

## Problem
Quem sofre, o que falha hoje, o que passa a ser verdade.

## Flow
Passos do request ao efeito (HTTP → Controller → UseCase → Repository → resposta). Incluir erro e status HTTP.

## Impact
O que muda de comportamento. O que explicitamente nao muda.

## Relations
Agregados, portas e tabelas que esta mudanca toca. Reuso (tipo existente) vs criacao.

## Surface
Contrato externo: metodo, path, request, response, status, chaves i18n. `nenhum` se for so interno.

## Landing
Arquivos por camada (`domain`, `application`, `infrastructure`), migration Flyway se houver, testes por camada. So o que este plano vai criar ou editar.

## Criteria
Lista verificavel. Cada item vira um check em `/speckit.lean-checks`.
```

## Parar

Entregar o caminho do arquivo e pedir revisao humana. Nao editar `src/`. Nao rodar `/speckit.lean-checks` nesta conversa.
