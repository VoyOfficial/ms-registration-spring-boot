# Skills Index

Skills disponíveis para os agentes neste projeto. Cada skill vive em `.cursor/skills/<nome>/SKILL.md`.

| Skill | Quando usar |
|-------|-------------|
| `agents` | Descobrir qual subagente (`/qa`, `/github`, `/code-reviewer`, `/feature`) usar para uma tarefa. |
| `github` | Criar/gerenciar Issues e PRs no GitHub via `gh` CLI. |
| `grill-with-docs` | Clarificar features vagas antes de implementar; documenta em `CONTEXT.md`/ADRs. |
| `session-management` | Retomar/salvar progresso de trabalho em `docs/obsidian/Sessions/`. |
| `spec-driven-eval` | Avaliar o quão completa está uma implementação frente a um PRD/spec. |
| `thermo-nuclear-code-quality-review` | Padrão de review extremamente rigoroso, usado pelo `/code-reviewer`. |

Consulte `.cursor/agents/*.md` para os subagentes que usam essas skills, e `AGENTS.md` na raiz para o workflow geral.
