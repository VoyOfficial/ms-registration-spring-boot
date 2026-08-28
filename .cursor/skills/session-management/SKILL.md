---
name: session-management
description: Harness-oriented session continuity, context capture, and resumability across work sessions.
metadata:
  version: '1.0.0'
  status: active
  owner: squad-app
  tags: [harness, memory, context, continuity]
---

# Skill: Session Management

## When to use

- Resume prior work on a GitHub Issue.
- Save session progress, decisions, and next steps.
- Keep durable context for team continuity.

## When not to use

- One-off trivial edits without context carry-over.

## Inputs

- Active branch/Issue.
- Session notes and related work items.

## Outputs

- Updated session note.
- Clear next-step handoff.
- Updated active work snapshot.

## Allowed tools

- Workspace file read/write tools for notes.
- Git branch/status for task context.

## Constraints and guardrails

- Use `docs/obsidian/Sessions/` as source of truth.
- Keep notes concise and actionable.
- Record decisions and blockers explicitly.

## Usage examples

```markdown
resume work on Issue #42
criar session note sobre fluxo de busca de lugares
o que eu estava trabalhando na última sessão?
```
