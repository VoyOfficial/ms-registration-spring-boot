# Agent Instructions (Resumo)

## 1) Workflow Padrão (Obrigatório)

Use Harness Engineering para qualquer tarefa de código neste repositório.

Fluxo base:

1. Session startup
2. Feedforward (contexto)
3. Implementação (TDD)
4. Feedback (validação)
5. Commit + session save

Referência detalhada: `.cursor/rules/harness-engineering.mdc`

## 2) Session Startup (sempre no primeiro prompt)

1. Rodar `git branch --show-current` e identificar a Issue relacionada (ex.: `feature/12-nome-da-feature`).
2. Ler `docs/obsidian/CURRENT_WORK.md`.
3. Consultar `docs/obsidian/Sessions/ISSUE-<n>-*.md` (ou `YYYY-MM-DD-*.md`).
4. Se houver Issue pai, consultar sessão relacionada.
5. Reportar em 1-2 linhas: task ativa + próximo passo.

Fonte da verdade de sessão: `docs/obsidian/Sessions/`

## 2.1) GitHub Project (tarefas e cards)

- **Board da org:** [Project #1](https://github.com/orgs/VoyOfficial/projects/1) (`VoyOfficial`, Projects v2)
- **Issues** = especificação da tarefa (critérios, discussão, links)
- **Project** = fila do time, status e visão de cards/colunas (backlog, em progresso, review, done)
- **Antes de iniciar trabalho:** consultar o board (Status, prioridade) e a Issue relacionada
- **Ao criar/atualizar Issue ou PR:** sincronizar no board (detalhes em `.cursor/skills/github/SKILL.md`)
- **Comandos úteis:** `gh project view 1 --owner VoyOfficial`, `gh project field-list 1 --owner VoyOfficial --format json`

## 3) Feedforward antes de codar

1. Carregar contexto da sessão atual.
2. Ler a Issue no GitHub (`gh issue view <n>`).
3. Validar critérios de aceite e contexto funcional.
4. Consultar rules relevantes em `.cursor/rules/`.
5. Confirmar padrões de Clean Architecture / Ports & Adapters (camadas `domain`/`application`/`infrastructure`).

### Diretriz de implementação

- Seguir YAGNI: implementar apenas o necessário para o requisito atual.
- Preferir soluções curtas e diretas, sem comprometer legibilidade e testabilidade.
- Respeitar a regra de dependência: `domain` nunca importa de `application`/`infrastructure`/Spring/JPA.

## 4) Qualidade e Gate de Conclusão

Ordem obrigatória de validação:

1. Unit tests (JUnit 5 + Mockito, camada `domain.service`)
2. Integration tests (`@WebMvcTest` + `MockMvc` para `application.controller`; `@DataJpaTest` para `infrastructure.repository`)
3. E2E (quando configurado no projeto — ainda não é gate hoje)

**Definition of Done (DoD) Harness:**

Antes de concluir qualquer task, executar validação dos gates ativos:

1. Gate 1: Código e Testes (`mvn clean verify` — unit + integration + compilação)
2. Gate 2: E2E (N/A por enquanto — reativar quando o projeto configurar um framework de E2E)
3. Gate 3: Code Review (via `/code-reviewer`)
4. Gate 4: Documentação Obsidian (YAML completo)
5. Gate 5: PR no GitHub

Referências:

- `.cursor/rules/definition-of-done.mdc` ⭐ DoD Harness
- `.cursor/rules/validation-pyramid.mdc`
- `.cursor/rules/tdd-workflow.mdc`
- `.cursor/rules/architecture.mdc`

## 5) Commits

O formato abaixo é **enforced automaticamente** por git hooks versionados em `.githooks/` (`commit-msg`, `pre-commit`, `pre-push`) - configurados automaticamente a cada `./mvnw` (ver `.cursor/rules/pre-commit-hooks.mdc`). Não é preciso validar manualmente antes de commitar, os hooks bloqueiam formato inválido, testes quebrando e falhas de build.

Padrão obrigatório: **Conventional Commits em inglês**, com escopo `modulo/arquivo`:

```
<type>(<module>/<file>): <description>
```

Tipos permitidos: `feat`, `fix`, `refactor`, `test`, `docs`, `style`, `chore`, `perf`

Regras:

1. Escopo = pacote/módulo + arquivo principal alterado (ex.: `user/userregistryservice`).
2. Descrição em inglês, minúscula, modo imperativo (ex.: "add", "fix", "extract").
3. Commits atômicos (1-3 arquivos quando possível, ordenados por dependência: `domain` → `infrastructure` → `application` → testes).
4. Não usar corpo de commit; apenas `-m` em uma linha.
5. NUNCA usar `--no-verify` ou pular hooks. Se o build/lint falhar, corrigir o código.
6. **Commitar** `docs/obsidian/` (Sessions, Work Items, Decisions, Modules, `CURRENT_WORK.md`). **NUNCA commitar**: estado local do editor (`docs/obsidian/.obsidian/`), `CONTEXT.md` na raiz, nem um `CURRENT_WORK.md` na raiz (o snapshot canônico é `docs/obsidian/CURRENT_WORK.md`).

Exemplos válidos:

- `feat(user/userregistryservice): validate duplicate cpf before persisting`
- `fix(user/usercontroller): return 404 when user is not found`
- `test(user/userregistryservice): add edge cases for invalid cpf`

Exemplos inválidos:

- `feat: adiciona validacao` (sem escopo, e em português)
- `TASK-123: fix bug` (formato antigo baseado em Azure DevOps)

## 6) Session Save

Ao fim de trabalho significativo:

1. Atualizar/criar sessão em `docs/obsidian/Sessions/` e **commitar** no mesmo fluxo da task (não deixar só local).
2. Atualizar `docs/obsidian/CURRENT_WORK.md` (Active / Blocked / Recently Completed) e commitar.
3. Incluir próximos passos objetivos.

**Consolidação de Sessões** (evitar granularidade excessiva):

- Uma sessão por **bloco de trabalho coeso** (4-8h), não por micro-fix
- Agrupar fixes relacionados
- Máximo ~5 sessões por Issue (exceto features muito grandes)
- Se criar sessão nova no mesmo dia, considerar atualizar a existente

Ao finalizar a task (normalmente abertura de PR):

1. Atualizar a Issue no GitHub para o status apropriado (ex.: label `ready-for-review`).
2. Atualizar todas as sessões da Issue para `status: closed`.
3. Garantir `parent_session` explícito em cada sessão (`[[...]]` ou `none`).

Referência detalhada: `.cursor/rules/memory-system.mdc`

## 7) Arquitetura

Clean Architecture / Ports & Adapters + princípios de DDD, sobre Spring Boot (Java). Cada módulo de domínio (ex.: `user`) é organizado em camadas `domain` (regras de negócio puras + `Entity`/`UseCase`/`Repository` interface), `application` (`Controller`, DTOs `Request`/`Response`, `UseCase` service) e `infrastructure` (JPA `Model`, `RelationalRepository`, config, exception handling). Ver `.cursor/rules/architecture.mdc` e `.cursor/rules/ms-registration.mdc`.

## 8) Rules Válidas

Todas em `.cursor/rules/`:

- `code-conventions.mdc` (always)
- `architecture.mdc` (always) ⭐ Clean Architecture / Ports & Adapters + DDD
- `ms-registration.mdc` (always) ⭐ playbook principal
- `solid-principles.mdc` (always)
- `definition-of-done.mdc`
- `documentation.mdc`
- `harness-engineering.mdc`
- `memory-system.mdc`
- `obsidian-integration.mdc`
- `pre-commit-hooks.mdc`
- `tdd-workflow.mdc`
- `validation-pyramid.mdc`

## 9) Skills e Subagentes

- Skills em `.cursor/skills/*/SKILL.md` (ex.: `github`, `session-management`, `spec-driven-eval`, `grill-with-docs`, `thermo-nuclear-code-quality-review`, `agents`). Veja `.cursor/skills/README.md` e `.cursor/skills/agents/SKILL.md` para o guia completo.
- Subagentes em `.cursor/agents/*.md`, invocáveis via `/nome`: `/code-reviewer`, `/github`, `/qa`, `/feature`.
- Comandos Spec Kit e outros workflows em `.cursor/commands/*.md`, invocáveis via `/nome` (ex.: `/speckit.specify`, `/speckit.plan`, `/feature-kickoff`).

<!-- SPECKIT START -->

Nenhuma feature em andamento via Spec Kit no momento. Ao rodar `/speckit.specify`, o contexto desta seção é atualizado automaticamente.

<!-- SPECKIT END -->
