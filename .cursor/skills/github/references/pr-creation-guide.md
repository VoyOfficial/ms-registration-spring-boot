# 🚀 Criar Pull Request

## 🚨 REGRA PRINCIPAL

**SEMPRE use o template oficial do repositorio.**

❌ NUNCA crie descrições do zero ou use formatos alternativos ("O QUE, POR QUE, COMO") sem necessidade
✅ SEMPRE use o template disponível em `.github/pull_request_template.md`

---

## ⚠️ OBRIGATÓRIO: Workflow de Coleta de Contexto

**ANTES de criar qualquer PR**, siga o workflow completo de coleta de contexto:

📚 **[PR Context Gathering Workflow](./pr-context-gathering-workflow.md)**

Este workflow garante que você:

1. ✅ Leia a **Issue completa** no GitHub
2. ✅ Descubra e leia **TODAS as sessões** relacionadas à task (não só a última)
3. ✅ Consulte o **histórico de commits** completo
4. ✅ Sintetize **TODO o trabalho realizado** no PR (não apenas uma sessão isolada)

**Regra de Ouro**: Um PR resolve uma **TAREFA completa**, não uma sessão isolada.

---

## 🎯 Template Atual do Projeto

Hoje o repositório tem um único template em `.github/pull_request_template.md`, usado automaticamente pelo `gh pr create` (sem precisar de `--template`).

Estrutura atual:

```markdown
### What?

[O que foi feito]

### Why?

[Por que foi feito]

### How?

[Como foi implementado]

### Testing?

[Como foi testado]

### Screenshots (optional)

[Screenshots, se aplicável]

### Anything Else?

[Observações adicionais]
```

Preencha cada seção com o contexto coletado (Issue + sessions + commits). Não invente uma estrutura alternativa enquanto este for o único template do repositório.

> Se o time decidir criar templates específicos por tipo (bug/feature/chore), criar arquivos em `.github/PULL_REQUEST_TEMPLATE/{bug,feature,chore}.md` e atualizar esta seção com a lógica de inferência de tipo abaixo.

## 🎯 Inferir Tipo de PR (para quando existirem múltiplos templates)

Caso o repositório passe a ter múltiplos templates, o agente deve inferir automaticamente o tipo baseado na branch:

### 1. Por Prefixo da Branch (Prioridade 1)

```
bugfix/*   → bug
hotfix/*   → bug
feature/*  → feature
chore/*    → chore
refactor/* → chore
test/*     → chore
docs/*     → chore
perf/*     → chore
style/*    → chore
```

### 2. Por Labels da Issue (Prioridade 2)

Se a branch não seguir convenção, usar as labels da Issue (`gh issue view <n> --json labels`).

### 3. Por Análise do Título (Prioridade 3)

- Título contém `["fix", "bug", "crash"]` → bug
- Título contém `["feat", "add", "new"]` → feature
- Título contém `["refactor", "test", "chore", "docs"]` → chore

### 4. Fallback

Se nenhuma inferência funcionar, perguntar ao usuário qual tipo usar.

---

## ✅ Checklist PRÉ-CRIAÇÃO (Automático)

O agente GitHub executa TODOS os passos automaticamente:

### 1. Context Gathering

- [ ] Extrair número da Issue da branch
- [ ] Ler Issue no GitHub (`gh issue view <n>`)
- [ ] Descobrir TODAS as sessões da task
- [ ] Ler histórico de commits completo
- [ ] Ler CURRENT_WORK.md

### 2. Validação de Código

- [ ] Verificar se compila sem erros (`mvn -q clean compile`)
- [ ] Garantir que testes estão presentes e passando (`mvn clean verify`)
- [ ] Validar convenção de commits (`type(module/file): description`)

### 3. Criação do PR

- [ ] Popular template com contexto completo
- [ ] Vincular Issue (`Closes #<n>`)
- [ ] Adicionar reviewers (se aplicável)
- [ ] Criar PR via `gh pr create`

---

## 🔧 Criar PR via `gh` CLI

### Configuração do Repositório

```
Owner: VoyOfficial
Repository: ms-registration-spring-boot
Base branch: main
```

### Comando

```bash
gh pr create \
  --title "feat(user/registry): add duplicate CPF validation" \
  --body "$(cat <<'EOF'
### What?
[Resumo do que foi implementado, cobrindo TODA a Issue]

### Why?
[Contexto/motivação vindo da Issue]

### How?
[Abordagem técnica - camadas domain/application/infrastructure afetadas, migrations Flyway se houver]

### Testing?
[Testes unit/integration adicionados]

### Screenshots (optional)
[Se aplicável]

### Anything Else?
Closes #<n>
EOF
)" \
  --base main \
  --reviewer <username-opcional>
```

### Verificação pós-criação

```bash
gh pr view --web
```
