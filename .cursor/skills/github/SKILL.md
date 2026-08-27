---
name: github
description: Issue, PR, and repository workflows integrated with GitHub via the gh CLI.
metadata:
  version: '1.0.0'
  status: active
  owner: squad-app
  tags: [github, issues, pr, gh-cli]
---

# Skill: GitHub Integration

GitHub integration for Issue management and PR automation using the `gh` CLI.

## When to use

- 📋 Create/manage Issues (feature, bug, task)
- 🔀 Create, review, or update Pull Requests
- 📝 Generate changelogs
- ✅ Validate implementation completeness before closing an Issue

## When not to use

- ❌ Local-only coding changes without any Issue/PR impact
- ❌ Pure codebase refactoring without tracking (use judgement — small refactors don't need an Issue)

## Inputs

- Issue numbers, branch names, repository context
- PR title/description context (Issue, sessions, commits)

## Outputs

- ✅ Created/updated Issues
- ✅ PR drafts with complete context and description
- ✅ Review comments and feedback
- ✅ Implementation completeness validation reports (via `spec-driven-eval`)

## Allowed tools

- `gh` CLI (issues, PRs, repo, api) via terminal
- `git` for branch/commit context
- Workspace read tools for context gathering (sessions, Work Items)

## Constraints and guardrails

- ⚠️ Default target/base branch: `main`
- ⚠️ Repository: `VoyOfficial/ms-registration-spring-boot`
- ⚠️ Always follow the [PR Context Gathering Workflow](references/pr-context-gathering-workflow.md) before creating any PR
- ⚠️ Only close an Issue after required tests pass (`mvn clean verify`) and (when relevant) spec-driven-eval score ≥90%
- ⚠️ Commit messages referenced/quoted in PR descriptions must follow the Conventional Commits format defined in `AGENTS.md`

## Operational patterns

### 1. Issue Management

- Create Issues (feature/bug/task) with clear title, description, and acceptance criteria
- Label Issues appropriately (`bug`, `feature`, `task`, `ready-for-review`, etc.)
- Link related Issues (parent/child via task lists or `Related to #n`)
- Update Issue status via labels/comments

```bash
gh issue create --title "..." --body "..." --label feature
gh issue view <n>
gh issue edit <n> --add-label ready-for-review
gh issue comment <n> --body "..."
```

### 2. Pull Request Automation

- Create PRs with auto-generated descriptions synthesizing full Issue context
- **Automatically infer PR type** (bug/feature/chore) from branch name or Issue labels
- Link the Issue (`Closes #<n>`)
- Set reviewers when applicable
- Review PR changes and provide feedback

**🚨 OBRIGATÓRIO**: Sempre seguir o [PR Context Gathering Workflow](references/pr-context-gathering-workflow.md) antes de criar qualquer PR.

**References**:

- [references/pr-context-gathering-workflow.md](references/pr-context-gathering-workflow.md) - **READ THIS FIRST**
- [references/pr-creation-guide.md](references/pr-creation-guide.md) - Template selection and structure
- [references/pr-review-guide.md](references/pr-review-guide.md) - Review comments and feedback

### 3. Implementation Completeness Validation

- Extract acceptance criteria from the Issue
- Run `spec-driven-eval` against implementation + tests
- Review grade report for gaps
- Only mark as closed/ready-for-review when score ≥90% or all P0/P1 criteria are MET

## Usage examples

### Issue Management

```markdown
/github criar Issue para endpoint de atualizacao de usuario
/github criar Issue de bug para validacao de CPF duplicado
/github atualizar Issue #12 para status ready-for-review
/github linkar Issue #12 a Issue pai #10
```

### Pull Request Automation

```markdown
# ⚠️ ALWAYS start with context gathering

/github criar PR completo (contexto + template)

# Example workflow (fully automatic):

1. Extract Issue number from branch
2. Read Issue from GitHub (gh issue view)
3. Discover and read ALL sessions in docs/obsidian/Sessions/
4. Read commit history
5. Infer PR type (bug/feature/chore) from branch/Issue labels
6. Synthesize COMPLETE task context in PR description
7. Create PR using gh pr create

# Manual override (specify template):

/github criar PR usando template bug
/github criar PR usando template feature
```

**Critical**: PRs must describe the **entire task**, not just the last session.

```markdown
/github create PR for current branch
/github review PR #12 and provide detailed feedback
/github update PR #12 with new reviewers from team
```

### Implementation Validation

```markdown
/github validate Issue #12 implementation completeness with spec-driven-eval
check if all acceptance criteria are met for Issue #12
```

## Detailed Guides

For comprehensive workflows and patterns, consult:

1. **[PR Context Gathering Workflow](references/pr-context-gathering-workflow.md)** ⭐ READ FIRST

   - Order of context collection (Issue → sessions → commits)
   - Discovering all related sessions (not just the last one)

2. **[PR Creation Guide](references/pr-creation-guide.md)**

   - Template selection (bug/feature/chore)
   - Auto-description generation
   - `gh pr create` usage

3. **[PR Review Guide](references/pr-review-guide.md)**
   - Review patterns
   - Feedback templates
   - Approval criteria

## Project Configuration

**GitHub Defaults**:

- Repository: `VoyOfficial/ms-registration-spring-boot`
- Default base branch: `main`
- CLI: `gh` (must be authenticated: `gh auth status`)

Always use these defaults when calling `gh` commands, unless the user specifies otherwise.
