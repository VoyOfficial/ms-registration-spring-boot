# 📚 PR Context Gathering Workflow

> **Workflow obrigatório para coleta de contexto antes de criar PRs**

## 🚨 Regra de Ouro

**Um PR resolve uma TAREFA completa (Issue), não uma sessão isolada.**

---

## 📋 Ordem de Prioridade

```
1️⃣ Issue no GitHub
2️⃣ Commits da branch (git log)
3️⃣ TODAS as sessões relacionadas (docs/obsidian/Sessions/)
```

---

## 🔄 Workflow em 7 Passos

### 1. Extrair numero da Issue da Branch

```bash
git branch --show-current
```

Branches seguem o padrao `feature/<n>-slug` ou `bugfix/<n>-slug`. Se o numero nao estiver no nome, perguntar ao usuario.

### 2. Ler a Issue no GitHub

```bash
gh issue view <n>
```

Extrair: título, descrição, acceptance criteria, labels, Issue pai (se referenciada no corpo).

### 3. Ler Work Item Local (Obsidian)

```
docs/obsidian/Work Items/{Bugs|Features|Tasks}/ISSUE-<n>.md
```

Buscar seção `## Sessions` para lista de sessões relacionadas.

### 4. Descobrir TODAS as Sessões

**CRÍTICO**: Não parar na última sessão!

**Método 1** (preferido): Ler seção `## Sessions` do work item local.

**Método 2** (fallback):

```bash
ls docs/obsidian/Sessions/ | grep "ISSUE-<n>"
```

### 5. Ler TODAS as Sessões

Usar `read_file` em cada sessão descoberta. Extrair:

- Objetivo da implementação
- Arquivos modificados (por camada: domain/application/infrastructure)
- Decisões técnicas
- Testes criados (unit/integration, migrations Flyway)

### 6. Ler Histórico de Commits

```bash
git log --oneline --no-merges origin/main..HEAD
```

### 7. Ler `docs/obsidian/CURRENT_WORK.md`

Highlights da tarefa e validação cruzada.

---

## 🎯 Checklist de Validação

Antes de criar o PR:

- [ ] Issue do GitHub lida
- [ ] Work item local (Obsidian) lido
- [ ] TODAS as sessões descobertas e lidas
- [ ] Histórico de commits consultado
- [ ] `docs/obsidian/CURRENT_WORK.md` consultado
- [ ] PR sintetiza TODO o trabalho (não só última sessão)
- [ ] Contadores (testes, arquivos, migrations) consolidados de todas as sessões

---

## 🔗 Referências

- [PR Creation Guide](./pr-creation-guide.md) - Templates e uso do `gh pr create`
- `.cursor/rules/memory-system.mdc` - Estrutura de sessões
- `.cursor/rules/obsidian-integration.mdc` - Padrões de documentação
