# Spec-Driven Eval Integration — PR Workflow

## Overview

Integre `spec-driven-eval` no workflow de PR para garantir implementações 100% completas antes de merge.

## PR Review Checklist Enhancement

Adicione ao checklist de review de PR:

```markdown
## Quality Gates

- [ ] Unit tests passando (`mvn test`)
- [ ] Integration tests passando (`mvn verify` — `@WebMvcTest` / `@DataJpaTest`)
- [ ] **Spec-driven-eval score ≥ 90%** (para P0/P1 stories)
- [ ] Sem UNMET checks em critérios P0
- [ ] Migrations Flyway (se houver) aplicadas com sucesso
```

> Nao ha gate de E2E ainda neste projeto (ver `.cursor/rules/validation-pyramid.mdc`). Quando um framework de E2E for adotado (ex.: Testcontainers), adicionar `E2E tests passando` a este checklist.

## Automation Opportunity

### GitHub Actions Integration

Potencial automação (futura):

```yaml
# .github/workflows/spec-eval.yml
name: Spec-Driven Evaluation

on:
  pull_request:
    types: [opened, synchronize]

jobs:
  evaluate:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Run spec-driven-eval
        run: |
          # Extract Issue number from branch name
          # Run evaluation
          # Post results as PR comment
```

## Manual PR Review Process

### 1. Before Creating PR

```bash
# Na feature branch
/qa run spec-driven-eval for current branch vs main
```

### 2. Interpreting Results in PR Context

**Grade Report Example**:

```markdown
Final I-score: 95% (19/20 MET)
Final T-score: 85% (17/20 MET)
Combined: 90%

UNMET Implementation Checks:

- Story P1, AC2, I-check 3: Error message not localized (messages.properties)

UNMET Test Checks:

- Story P0, AC1, T-check 2: Missing integration assertion for user persisted state (@DataJpaTest)
- Story P1, AC3, T-check 1: Missing unit test for validator de CPF duplicado
```

**Action Items**:

1. Fix UNMET P0 checks immediately (blocker)
2. Add P1 missing tests before merge (required)
3. Create follow-up Issue for P2 gaps (optional)

### 3. PR Description Template Addition

Adicione seção ao PR template:

```markdown
## Spec-Driven Evaluation

**Issue**: #<n>

**Evaluation Results**:

- Final I-score: XX%
- Final T-score: XX%
- Combined: XX%

**UNMET Checks**:

- [ ] List any UNMET checks and remediation plan
- [ ] Or mark N/A if all MET

**Evaluation Report**: [Link to report in Obsidian]
```

## Agent Invocation Patterns

### During PR Creation

```markdown
/github criar PR para a branch atual, incluir resultados do spec-driven-eval
```

### During PR Review

```markdown
/qa validate completeness of Issue #12 implementation against baseline
```

### After Addressing Review Comments

```markdown
/qa re-run spec-driven-eval to confirm all gaps addressed
```

## Quality Standards by Priority

### P0 Stories (Crítico)

- **Requirement**: 100% I-score + 100% T-score
- **Blocker**: Any UNMET check bloqueia merge
- **Rationale**: Core business functionality (ex.: cadastro/validação de usuário); zero tolerance for gaps

### P1 Stories (Importante)

- **Requirement**: ≥ 90% Combined score
- **Blocker**: < 85% bloqueia merge
- **Rationale**: Significant features; minimal gaps tolerated

### P2 Stories (Desejável)

- **Requirement**: ≥ 70% Combined score (best effort)
- **Blocker**: Não bloqueia merge
- **Rationale**: Nice-to-have; can be partially implemented

## Common PR Rejection Reasons

### 1. Incomplete Implementation (Low I-score)

```markdown
**Reviewer Comment**:
Spec-driven-eval shows 65% I-score. Missing implementations:

- AC2, I-check 3: Error handling for duplicate CPF not mapped to 409
- AC4, I-check 1: Missing Flyway migration for new column

Please implement missing checks before re-review.
```

### 2. Insufficient Test Coverage (Low T-score)

```markdown
**Reviewer Comment**:
T-score 70% — missing critical integration tests:

- AC1: No @DataJpaTest validating persisted state
- AC3: Error scenario not covered by @WebMvcTest

Add integration tests per validation pyramid guidelines.
```

### 3. Scope Creep (Low S-score)

```markdown
**Reviewer Comment**:
Spec-driven-eval flagged out-of-scope additions:

- Implemented endpoint de exportação de usuários (explicitamente OOS no PRD)

Please remove or justify scope addition with product owner approval.
```

## Continuous Improvement

### Tracking Trends

Manter histórico de scores por feature:

```markdown
| Issue | Combined | I-score | T-score | Issues                     |
| ----- | -------- | ------- | ------- | --------------------------- |
| #12   | 92%      | 95%     | 88%     | Missing 2 integration tests |
| #13   | 88%      | 90%     | 85%     | Error handling incomplete   |
| #14   | 96%      | 98%     | 94%     | ✅ Excellent                |
```

### Team Goals

- **Sprint Goal**: Average Combined ≥ 90%
- **Monthly Goal**: 80% of PRs primeira review com grade satisfatória
- **Quality Metric**: Zero P0 UNMET checks reaching production

## References

- Main skill: [../SKILL.md](../SKILL.md)
- PR creation guide: `.cursor/skills/github/references/pr-creation-guide.md`
- Code review guide: `.cursor/skills/github/references/pr-review-guide.md`
