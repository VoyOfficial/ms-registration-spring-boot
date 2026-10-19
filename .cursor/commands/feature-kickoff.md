# Feature Kickoff Checklist

Use este prompt ao iniciar uma nova feature. Ele guia o workflow correto baseado na complexidade.

## Informações da Feature

**Issue**: #{issue_number}
**Descrição**: {description}

---

## 1. Análise de Complexidade

Responda SIM ou NÃO:

- [ ] Feature tem descrição vaga ou incompleta?
- [ ] Existem termos de domínio não claros?
- [ ] Envolve mais de 3 endpoints/casos de uso?
- [ ] Requer decisões arquiteturais (patterns, libs, nova tabela/migration, novo bounded context)?
- [ ] É funcionalidade greenfield (não existe similar)?

**Se 2+ respostas SIM** → Obrigatório usar a skill `grill-with-docs` antes de continuar

---

## 2. Pre-Implementation (apenas se passou pelo Grill ou feature simples)

- [ ] `CONTEXT.md` atualizado com termos do domínio
- [ ] ADRs criados para decisões irreversíveis (se houver)
- [ ] Domínio/agregado identificado (pacotes `domain`, `application`, `infrastructure`)
- [ ] **Default:** `/speckit.lean-plan` revisado por humano, depois `/speckit.lean-checks` numa janela de contexto nova
- [ ] **Só se a feature for muito grande ou multi-módulo:** Spec Kit tradicional (`/speckit.specify` → `/speckit.plan` → `/speckit.tasks`)

---

## 3. YAGNI Validation

Para cada UseCase/Service/endpoint planejado:

| Item         | Consumidores AGORA | Criar?   |
| ------------ | ------------------- | -------- |
| {item1}      | {count}              | {yes/no} |
| {item2}      | {count}              | {yes/no} |

**Regra**: Se não há consumidor conhecido agora, não criar a abstração ainda.

---

## 4. Session Strategy

- [ ] Definir sessões por work chunk (4-8h), não por fix
- [ ] Planejar máximo ~5 sessões para a Issue
- [ ] Primeiro nome de sessão: `ISSUE-<n>-YYYY-MM-DD-phase1-description.md`

---

## Ready to Start?

Se todos os checks acima estão OK:

```bash
git branch --show-current
```

E seguir Session Startup normal (`.cursor/rules/harness-engineering.mdc`).
