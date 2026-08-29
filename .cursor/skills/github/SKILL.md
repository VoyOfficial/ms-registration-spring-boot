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
- 🗂️ Sync Issues/PRs to the org Project board (Projects v2) and move them across Status columns
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
- ✅ Issues/PRs synced as items on the org Project board, with Status kept up to date
- ✅ PR drafts with complete context and description
- ✅ Review comments and feedback
- ✅ Implementation completeness validation reports (via `spec-driven-eval`)

## Allowed tools

- `gh` CLI (issues, PRs, repo, project, api) via terminal
- `git` for branch/commit context
- Workspace read tools for context gathering (sessions, Work Items)

## Constraints and guardrails

- ⚠️ Default target/base branch: `main`
- ⚠️ Repository: `VoyOfficial/ms-registration-spring-boot`
- ⚠️ Project board: org Project `#1` (`https://github.com/orgs/VoyOfficial/projects/1`), owner `VoyOfficial` — every Issue/PR created by this skill must be added to it
- ⚠️ Never hardcode Status option names — run `gh project field-list` first, since column/option names can be renamed on the board without notice
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

**After every `gh issue create`, sync it to the board immediately** (see section 2 below).

### 2. Project Board Sync (Projects v2)

- Every Issue/PR created or picked up by this skill must have a matching item on org Project `#1`
- Never assume Status option names — always discover them first with `field-list` (they can differ from what's shown in a stale context)
- Move the item's Status as the work progresses (e.g. when a branch is created, when the PR opens, when it's ready for review, when it's merged/closed)

```bash
# Add a newly created Issue (or an existing PR) to the board
gh project item-add 1 --owner VoyOfficial --url https://github.com/VoyOfficial/ms-registration-spring-boot/issues/<n>

# Discover the board's fields and valid Status options before setting one
gh project field-list 1 --owner VoyOfficial --format json

# Move the item to a Status column (use an option name returned above, e.g. "In Progress", "In Review", "Done")
gh project item-edit 1 --owner VoyOfficial --url https://github.com/VoyOfficial/ms-registration-spring-boot/issues/<n> --field "Status" --value "<option-name>"

# Sanity check: open the board in the browser
gh project view 1 --owner VoyOfficial --web
```

### 3. Pull Request Automation

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

**When the PR is created, also add it to the board and set Status to "In Review" (or the equivalent option discovered via `field-list`).**

### 4. Implementation Completeness Validation

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

### Project Board Sync

```markdown
/github adicionar Issue #12 ao board
/github mover Issue #12 para "In Progress" no board
/github sincronizar board com as Issues abertas
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
- Project board: org Project `#1` (`https://github.com/orgs/VoyOfficial/projects/1`), owner `VoyOfficial`
- CLI: `gh` (must be authenticated: `gh auth status`; needs the `project` scope to read/write board items — `gh auth refresh -s project` if `item-add`/`item-edit` return a permission error)

Always use these defaults when calling `gh` commands, unless the user specifies otherwise.
