# /speckit.verify

Verificador independente. Nunca o autor da implementacao.

Se esta conversa escreveu ou editou o codigo sob revisao, parar. Pedir uma janela nova (ou deixar o hook `.cursor/hooks/spec-lean-verify-gate.py` abrir o follow-up). Nao "revisar de memoria".

Readonly: nao editar codigo, nao commitar, nao "ja corrigir".

## Entrada

```text
$ARGUMENTS
```

Base opcional. Resolver nesta ordem:

1. `gh pr view --json baseRefName -q .baseRefName` se houver PR da branch.
2. Upstream (`@{u}`), se existir.
3. O merge-base mais proximo entre `origin/develop` e `origin/main`.

## Escopo

1. Ler `specs/**/checks.md` da feature (Coverage + Handoff + Landing do `plan.md`).
2. `git diff <base>...HEAD` e `git log <base>..HEAD --oneline`. Nao confiar em resumo.
3. Para cada check: prova citada existe e cobre o claim, ou o check falha.
4. Arquivo Java alterado fora do Landing do plano: marcar como regressao ou escopo excedente. Nao assumir que e intencional.
5. Delegar a passagem estrutural ao `/code-reviewer` contra a mesma base. Nao reescrever o checklist do reviewer aqui. O Reuse-First gate do reviewer faz parte do veredito.
6. Nao aprovar com High de corretude, check sem prova, ou arquivo fora do Landing sem justificativa no plano.

## Saida

```markdown
**Verificador:** sessao independente (nao-autor)
**Range:** <base>..<head>
**Checks:** N cobertos / M falhos
**Fora do Landing:** <arquivos ou nenhum>
**Code review:** veredito do /code-reviewer
**Status:** aprovado / mudanças necessárias
```

Se falhar, listar o check e o arquivo. Quem implementou corrige numa outra sessao e o verify roda de novo.
