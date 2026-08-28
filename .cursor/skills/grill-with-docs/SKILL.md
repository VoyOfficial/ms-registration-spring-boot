---
name: grill-with-docs
description: Interactive grilling session to align understanding before building. Documents terms in CONTEXT.md and hard decisions as ADRs.
disable-model-invocation: false
metadata:
  version: '1.0.0'
  status: active
  owner: squad-app
  tags: [planning, design, alignment, domain-modeling, adr]
---

# Skill: Grill with Docs

## When to use

**Uso Principal**: Clarificar Features vagas/incompletas

- Feature tem descrição básica mas falta detalhes técnicos
- Você não sabe responder perguntas-chave sobre a implementação
- Precisa descobrir requisitos técnicos, de negócio ou de contrato de API
- Novo endpoint/domínio não estabelecido (novas regras de negócio de cadastro)
- Refatoração arquitetural grande onde decisões precisam ser documentadas
- Necessidade de alinhar vocabulário técnico entre time
- Início de módulo ou funcionalidade greenfield

## When not to use

- Feature completo com todos os detalhes técnicos e de negócio
- Ajustes pequenos ou correções de bugs isolados
- Quando o domínio já está claro e documentado em CONTEXT.md
- Features pequenas com escopo bem definido e requisitos claros

## Inputs

- Issue ID ou link do GitHub (descrição básica)
- Descrição inicial da feature ou mudança
- Contexto de negócio ou requisitos de alto nível
- Arquivos relacionados ao domínio (opcional)
- Perguntas que você NÃO sabe responder mas precisa descobrir

## Outputs

- `CONTEXT.md`: Glossário de termos do domínio, atualizado incrementalmente
- `docs/adr/*.md`: Architecture Decision Records para decisões importantes e irreversíveis
- Alinhamento compartilhado entre você e o agente sobre vocabulário e decisões

## Allowed tools

- `read_file`: Para entender codebase existente
- `create_file`: Para criar CONTEXT.md e ADRs
- `replace_string_in_file`: Para atualizar CONTEXT.md incrementalmente
- `grep_search`: Para buscar uso de termos na codebase
- `semantic_search`: Para encontrar contexto relacionado
- `vscode_askQuestions`: Para fazer perguntas estruturadas

## Constraints and guardrails

- **Uma pergunta por vez**: Nunca fazer questionário com múltiplas perguntas de uma vez
- **Documentar imediatamente**: Termos resolvidos vão para CONTEXT.md na hora, não ao final
- **ADRs raros**: Apenas para decisões difíceis de reverter, surpreendentes ou com trade-offs reais
- **Responder do código quando possível**: Buscar na codebase antes de perguntar ao usuário
- **Manter glossário puro**: CONTEXT.md é vocabulário, não implementação ou spec
- **Respeitar dependências**: Resolver dependências entre decisões antes de avançar

## Operational patterns

### Standard workflow

**Fase 1: Descoberta inicial**

1. Receber descrição da feature/mudança
2. Identificar termos ambíguos ou não estabelecidos
3. Buscar na codebase existente para contexto

**Fase 2: Grilling iterativo (uma pergunta por vez)** 4. Fazer uma pergunta focada sobre termo ou decisão 5. Oferecer resposta recomendada baseada no código existente 6. Aguardar resposta do usuário 7. **Documentar imediatamente** em CONTEXT.md se termo for resolvido 8. Se decisão for hard/irreversível/trade-off → criar ADR 9. Repetir 4-8 até vocabulário estar claro

**Fase 3: Validação** 10. Revisar CONTEXT.md atualizado 11. Revisar ADRs criados (se houver) 12. Confirmar alinhamento antes de seguir para implementação

### Integration with Harness Engineering

```
grill-with-docs → Session startup → Feedforward → Implementation → Feedback → Commit
```

Use grill-with-docs **ANTES** do Session startup para features complexas.

### File structure

```
ms-registration-spring-boot/
├── CONTEXT.md                    # Glossário geral (raiz)
├── docs/
│   ├── adr/                      # ADRs gerados pelo grill
│   │   ├── 0001-record-template.md
│   │   ├── NNNN-decision-title.md
│   └── obsidian/                 # Mantém workflow atual
└── specs/                        # Mantém Spec Kit
```

### When to create ADR vs just glossary entry

**ADR (Architecture Decision Record)** - Criar apenas quando:

- Decisão é difícil de reverter (mudança de biblioteca, padrão arquitetural, estratégia de migração de dados)
- Resultado não é óbvio sem contexto (por que escolhemos X em vez de Y?)
- Há trade-offs reais que o time precisa entender

**CONTEXT.md entry** - Para todo o resto:

- Definições de termos de domínio
- Vocabulário compartilhado
- Conceitos de negócio
- Nomes canônicos de entidades

### Example flow

```
User: Vamos implementar validação de usuario duplicado no cadastro

Agent: [busca na codebase por "duplicado" / "already exists"]
       Encontrei UserAlreadyExistsException, mas ela hoje só considera
       o CPF. É esse o único identificador único de negócio, ou e-mail
       também deveria ser considerado duplicado?

       Sugestão: Pelo domínio de cadastro, CPF é o identificador
       canônico; e-mail costuma ser secundário.

User: Isso, só CPF por enquanto.

Agent: [atualiza CONTEXT.md]
       ✓ Adicionado termo "Identificador único de negócio (CPF)" ao glossário

       Próxima pergunta: Quando o CPF já existe, a API deve retornar
       409 Conflict ou 422 Unprocessable Entity?

       Sugestão: Baseado no ExceptionHandlerAdvice atual, exceptions de
       regra de negócio já mapeiam para 409/422 - qual delas melhor
       representa "conflito de recurso já existente"?

[continua uma pergunta por vez até clarificar tudo]
```

## Checklist de sucesso

- [ ] Faz uma pergunta por vez e aguarda resposta
- [ ] Termos são escritos em CONTEXT.md assim que resolvidos
- [ ] Busca no código antes de perguntar ao usuário
- [ ] ADRs são criados apenas para decisões hard/irreversíveis
- [ ] Glossário (CONTEXT.md) permanece puro - só vocabulário
- [ ] **YAGNI Check**: Para cada Service/UseCase/abstração planejada, perguntar "Existe consumidor conhecido AGORA?"

## YAGNI Enforcement

Durante o grill, validar cada abstração proposta:

**Perguntas obrigatórias antes de planejar Services/UseCases/interfaces**:

1. "Quem vai consumir isso imediatamente?"
2. "Se a resposta for 'talvez no futuro', não criar ainda"
3. "Podemos resolver com código mais simples primeiro?"

**Exemplo de YAGNI aplicado**:

```
User: Vamos criar uma interface genérica NotificationSender para notificar usuarios

Agent: Antes de criar a interface:
- Quem vai consumir essa notificação imediatamente (e-mail, SMS, push)?
- Quantos canais de notificação precisamos AGORA?
- Podemos resolver isso com um Service concreto (ex.: EmailNotificationService)
  primeiro e extrair a interface só quando houver um segundo canal?

[Se apenas e-mail é necessário hoje, implementar EmailNotificationService direto.
Extrair Port/interface só quando 2+ implementações existirem]
```

## Integration with existing workflow

Para usar em conjunto com o Harness Engineering:

1. **Feature complexa nova**:

   ```
   /grill-with-docs → [alinhamento completo] → Session startup → Implementation
   ```

2. **Feature simples ou bug fix**:

   ```
   Session startup → Implementation (pular grill)
   ```

3. **Refatoração grande**:
   ```
   /grill-with-docs → Review CONTEXT.md/ADRs → Session startup → Refactor
   ```

## Restrictions

- **Não substituir** Spec Kit - use grill para vocabulário/alinhamento, depois use Spec Kit para especificação técnica
- **Não substituir** Work Items - grill não cria tasks, apenas clarifica domínio
- **Não substituir** Session memory - grill não documenta progresso de implementação

## References

- Source: https://github.com/mattpocock/skills/tree/main/skills/engineering/grill-with-docs
- Blog: https://www.aihero.dev/grill-with-docs
- Related skills: `grilling` (sem docs), `domain-modeling` (só glossário), `wayfinder` (projetos grandes)
