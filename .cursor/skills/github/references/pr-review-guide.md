# 💬 Comentários em Code Review

## 📝 Princípios

1. **Comentar na linha exata** do código (`gh pr review` / `gh api` para inline comments)
2. **Usar português** nos comentários
3. **Ser objetivo** - sem Javadoc excessivo
4. **Perguntar antes de criticar** - entender motivação

---

## 🏷️ Tipos de Comentários

### 1. Recursos/Performance

```markdown
**Sugestão - Possível N+1 query**

Esse `for` está chamando `userRepository.findById(...)` dentro do loop
(linha X), o que gera uma query por iteração.

Seria interessante considerar um `findAllById` (batch) ou um `JOIN FETCH`
na query JPA para evitar N+1.
```

**Quando usar:**

- N+1 queries em repositórios JPA
- Falta de `@Transactional` em operações multi-step
- Conexões/recursos não fechados (streams, `EntityManager` manual)

### 2. Clarificação de Código

```markdown
**Dúvida - Método `resolve`**

Não ficou claro onde e como esse método está sendo utilizado
e qual é o propósito do retorno `Optional<UserModel>`.

Poderia explicar o fluxo de uso?
```

**Quando usar:**

- Métodos/variáveis com propósito não claro
- Retornos `Optional`/`boolean` sem contexto
- Fluxos complexos entre camadas

### 3. Violação de Arquitetura (Clean Architecture / Ports & Adapters)

```markdown
**Bloqueante - Domain importando de infrastructure**

Este arquivo está em `domain/service/` mas importa
`UserJpaRepository` diretamente (linha X).

`domain` deve depender apenas de abstrações (`Repository` interface).
Injetar via `Repository` (porta) e mover o acesso JPA para
`infrastructure/repository/`.
```

**Quando usar:**

- `domain` importando classes de `infrastructure` (JPA, `@Entity`, `@Repository`)
- `Controller` com lógica de negócio (deveria delegar ao `UseCase`)
- `UseCase`/`Service` com mais de uma responsabilidade de negócio
- Ausência de mapeamento entre `Model` (JPA) e `Entity` (domínio)

### 4. Remoções Intencionais

```markdown
**Dúvida - Remoção da validação de CPF**

Foi removida a validação `@CPF` do `UserRequest`
que antes bloqueava CPFs inválidos na entrada.

Essa remoção foi intencional devido à migração da validação
para o `domain.service`? Se sim, faz sentido! Mas vale
confirmar que a mensagem de erro em `messages.properties`
ainda é usada.
```

**Quando usar:**

- Perguntar se remoção foi intencional
- Confirmar comportamento/contrato de API não foi quebrado

### 5. Sugestões de Refatoração

````markdown
**Sugestão de refatoração - Builder de teste duplicado**

Esse builder de `User` é bem detalhado (~30 linhas) e provavelmente
será necessário em outros arquivos de teste.

Sugestão: mover para `[Nome]Datas.java` compartilhado no pacote de testes.

Isso evitaria duplicação e facilitaria manutenção.

```java
// UserDatas.java
public static User validUser() {
    // ... código do builder
}
```
````

**Quando usar:**

- Test data builders duplicados → mover para `[Nome]Datas.java`
- Código repetido → extrair para helper/utilitário
- **SEMPRE incluir exemplo de código**

---

## 📋 Template Geral

```markdown
**[Tipo] - Título curto**

Descrição objetiva do ponto.

[Se for pergunta:]
**Qual foi a motivação para essa mudança?**

[Se for sugestão:]
```código
// Código sugerido
```
```

---

## ✅ Checklist de Review

Antes de finalizar review:

- [ ] Verificar possíveis N+1 queries e ausência de `@Transactional`
- [ ] Validar regra de dependência da Clean Architecture (`domain` puro, sem imports de `infrastructure`)
- [ ] Confirmar que mensagens de erro estão em `messages.properties` (i18n), não hardcoded
- [ ] Verificar se migrations Flyway acompanham mudanças de schema
- [ ] Questionar remoções de código/validações
- [ ] Sugerir centralização de test data builders (`[Nome]Datas.java`)
- [ ] Confirmar breaking changes de contrato de API com stakeholders relevantes

---

## 📊 Exemplo Real

**Contexto**: Linha 40-58 em `UserRegistryService.java`

```markdown
**Problema - Código duplicado em blocos catch**

(linha X-Y no arquivo UserRegistryService.java):

```java
catch (DataIntegrityViolationException e) {
    log.error("erro ao salvar usuario", e);
    throw new UserAlreadyExistsException(request.getCpf());
}
```

**Problema Identificado**:
Esse tratamento de exceção está duplicado em 3 métodos diferentes do Service.

**Sugestão**:

```java
private void handleSaveError(DataIntegrityViolationException e, String cpf) {
    log.error("erro ao salvar usuario cpf={}", cpf, e);
    throw new UserAlreadyExistsException(cpf);
}
```

**Motivo**:
Princípio DRY - código duplicado aumenta risco de bugs
quando apenas um dos blocos é atualizado.
```

## 🔧 Comandos úteis (`gh`)

```bash
# Ver PR e arquivos alterados
gh pr view <n> --json files,title,body

# Comentar de forma geral
gh pr comment <n> --body "..."

# Aprovar / solicitar mudanças
gh pr review <n> --approve --body "..."
gh pr review <n> --request-changes --body "..."
```
