---
name: agents
description: Agent roles, scope boundaries, and capability matrix for the code-reviewer, github, qa, and feature subagents.
metadata:
  version: '1.0.0'
  status: active
  owner: squad-app
  tags: [agents, governance, responsibilities, routing]
---

# Skill: Agent Roles

## When to use

- Clarify which subagent should handle a task.
- Define responsibilities and boundaries before starting work.
- Prevent role overlap and duplicated effort.

## When not to use

- Single-task technical execution where the right subagent is already clear.
- Low-level implementation details of a specific domain skill.

## Inputs

- Task type (feature, bug, test, PR, architecture, docs).
- Needed output (code, plan, tests, documentation, PR/Issue action).

## Outputs

- Recommended subagent.
- Scope of work and explicit non-scope.
- Suggested handoff sequence (if multi-agent).

## Allowed tools

- Task tool / natural-language subagent delegation ("use the qa subagent to...").
- Read-only workspace tools for context lookup.

## Constraints and guardrails

- Subagents are invoked with `/name` (e.g. `/qa`) or by naturally mentioning them in the request; they are not a different LLM identity, just a scoped persona with its own instructions.
- Keep responsibility boundaries explicit.
- For end-to-end implementation with no specialized persona needed, just work directly in the main agent.

## Agent matrix

- `/qa` (`.cursor/agents/qa.md`): test strategy, execution, bug analysis, spec-driven evaluation. Covers unit (`domain.service`), integration (`@WebMvcTest`, `@DataJpaTest`), and validators.
- `/github` (`.cursor/agents/github.md`): GitHub Issues, PRs, changelog generation.
- `/code-reviewer` (`.cursor/agents/code-reviewer.md`, uses the `thermo-nuclear-code-quality-review` skill): extremely strict code review focused on structural quality, Clean Architecture/Ports & Adapters compliance, maintainability, abstraction quality, and preventing spaghetti growth. Blocks file-size explosions, unnecessary complexity, and missed simplification opportunities.
- `/feature` (`.cursor/agents/feature.md`): full feature lifecycle from an open GitHub Issue to PR, orchestrating the other subagents/skills.
- Architecture (Clean Architecture/SOLID review) and documentation/changelog work do not have a dedicated subagent file today — do this directly in the main agent using the `architecture`/`solid-principles` rules and the `documentation` rule.

## Usage examples

```markdown
/qa criar teste unitario para o Service de registro de usuario
/qa executar spec-driven-eval para a Issue #12 e validar 100% da implementacao
/github criar PR usando o template de feature
/code-reviewer revisar branch feature/12-cadastro-usuario contra main usando o padrao thermo-nuclear
/code-reviewer auditoria estrutural profunda das mudancas da branch atual
/feature 12
```

## Quality validation with spec-driven-eval

Use the `spec-driven-eval` skill to validate implementation completeness:

- **When**: Before completing a feature/Issue, during PR review, or benchmarking implementations.
- **Who**: Typically the `/qa` subagent, but can be invoked from any context.
- **Output**: Scored report showing % completion per acceptance criterion, test coverage, and final grade.
- **Requires**: PRD/spec with acceptance criteria, implementation code, and tests (unit + integration).

```markdown
# Invoke explicitly by name
/qa run spec-driven-eval for current implementation against Issue #12 spec
```

## Thermo-nuclear code quality review

Use the `thermo-nuclear-code-quality-review` skill (via the `/code-reviewer` subagent) for extremely strict structural quality audits:

- **When**: PR review, deep refactoring validation, architecture quality gates, or when preventing tech debt accumulation.
- **Who**: `/code-reviewer` subagent (configured with the thermo-nuclear-code-quality-review skill).
- **Output**: Detailed structural analysis with blocking issues for: file-size explosion (>1000 lines), spaghetti growth, missed simplification opportunities ("code judo"), unnecessary abstractions, and Clean Architecture boundary violations.
- **Philosophy**: Be ambitious about structural simplification. Don't just check if code works - push for code that makes the codebase **meaningfully better**.

**Key Blockers:**

- Files crossing 1000 lines without strong justification
- Ad-hoc conditionals scattered in unrelated flows
- Missed opportunities for dramatic simplification
- Unnecessary wrapper/abstraction layers
- Logic in wrong architectural layer (`domain` importing from `infrastructure`/`application`, business logic in `Controller`)
- Hardcoded values (URLs, error messages that should be in `messages.properties`)

```markdown
# Invoke explicitly by name
/code-reviewer review branch feature/12-cadastro-usuario against main
/code-reviewer thermo-nuclear audit of current changes
```

**Note:** This is a **stricter** review standard than normal code review. Use when quality bar must be exceptionally high or when preventing architectural decay.

## Handoff pattern

**Standard flow:**

- Implementation -> QA validation (including spec-driven-eval) -> **Code Review** -> GitHub PR delivery -> Documentation finalization.

**With thermo-nuclear code review (high-quality bar):**

- Implementation -> QA validation -> **`/code-reviewer` (thermo-nuclear)** -> GitHub PR delivery -> Documentation finalization.

**Notes:**

- Code review can happen in parallel with QA validation for faster feedback.
- Use thermo-nuclear review for: major features, refactorings, architecture-critical changes, or quality gate enforcement.
- `/feature` runs this entire handoff chain autonomously for a GitHub Issue.
