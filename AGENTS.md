# Agent Instructions

Fonte unica do workflow: `.cursor/rules/harness-engineering.mdc`.

Este arquivo so aponta. Nao duplicar regras aqui.

## Fluxo

1. Session startup
2. Feedforward (desenhar a solucao inteira antes de codar)
3. Implementacao (testes obrigatorios; Spec Lean e o default)
4. Feedback (`./scripts/validate-dod.sh`)
5. Commit + session save

## Onde ler

- Workflow, commits e gates: `.cursor/rules/harness-engineering.mdc`
- Playbook de implementacao: `.cursor/rules/ms-registration.mdc`
- Arquitetura, convencoes e SOLID: carregam com `src/**/*.java`
- Testes: `.cursor/rules/testing-workflow.mdc`
- Spec Lean: `.cursor/rules/spec-lean.mdc`
- Subagentes: `/code-reviewer`, `/github`, `/qa`, `/feature`

## GitHub Project

Board da org: [Project #1](https://github.com/orgs/VoyOfficial/projects/1).

Issues e PRs: `.cursor/skills/github/SKILL.md`.
