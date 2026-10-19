#!/usr/bin/env bash
# Definition of Done for ms-registration-spring-boot.
#
# Gate 1  tests          ./mvnw clean verify
# Gate 2  E2E            suspended (no framework). Passes with an explicit note.
# Gate 3  code review    oriented: /code-reviewer. Pass --review-done after it.
# Gate 4  Obsidian       ISSUE id in frontmatter of a Work Item and a Session
# Gate 5  PR             gh pr for the current branch. Pass --skip-pr before opening it.
#
# Exit 0  active gates passed
# Exit 1  a gate failed
# Exit 2  bad usage

set -u

skip_tests=0
skip_e2e=0
skip_review=0
review_done=0
skip_pr=0
require_pr=0
task=""
e2e_cmd=""

usage() {
  cat <<'EOF'
Usage: ./scripts/validate-dod.sh --task ISSUE-<n> [options]

Options:
  --task ISSUE-<n>   Issue id (also accepts 43 or #43). Required unless --skip-docs.
  --skip-tests       Skip Gate 1 (./mvnw clean verify).
  --skip-e2e         Explicit no-op for Gate 2. Same result as the default.
  --e2e-cmd <cmd>    Run this command as Gate 2 when an E2E framework exists.
  --skip-review      Skip the oriented code-review checkpoint.
  --review-done      Mark Gate 3 done after /code-reviewer approved.
  --skip-pr          Skip the PR check (use before the PR exists).
  --require-pr       Fail Gate 5 when the branch has no PR (default once --skip-pr is absent).
  --skip-docs        Skip Gate 4.
  --help             Show this help.

Exit codes: 0 pass, 1 gate failed, 2 usage error.
EOF
}

skip_docs=0

while [ $# -gt 0 ]; do
  case "$1" in
    --task)
      task="${2:-}"
      shift 2
      ;;
    --skip-tests) skip_tests=1; shift ;;
    --skip-e2e) skip_e2e=1; shift ;;
    --e2e-cmd)
      e2e_cmd="${2:-}"
      shift 2
      ;;
    --skip-review) skip_review=1; shift ;;
    --review-done) review_done=1; shift ;;
    --skip-pr) skip_pr=1; shift ;;
    --require-pr) require_pr=1; shift ;;
    --skip-docs) skip_docs=1; shift ;;
    --help|-h) usage; exit 0 ;;
    *)
      echo "Opcao desconhecida: $1" >&2
      usage >&2
      exit 2
      ;;
  esac
done

REPO_ROOT=$(git rev-parse --show-toplevel 2>/dev/null || true)
if [ -z "$REPO_ROOT" ]; then
  echo "validate-dod: nao e um repositorio git." >&2
  exit 2
fi
cd "$REPO_ROOT"

failed=0

normalize_task() {
  raw="$1"
  raw="${raw#\#}"
  case "$raw" in
    ISSUE-*|issue-*) raw=$(printf '%s' "$raw" | tr '[:lower:]' '[:upper:]') ;;
    ''|*[!0-9]*) ;;
    *) raw="ISSUE-$raw" ;;
  esac
  printf '%s\n' "$raw"
}

note() { printf '%s\n' "$1"; }

gate_fail() {
  note "$1"
  failed=1
}

note "=== Gate 1: Testes (./mvnw clean verify) ==="
if [ "$skip_tests" -eq 1 ]; then
  note "  pulado (--skip-tests)"
else
  if ./mvnw -q clean verify; then
    note "  ok"
    if [ -f target/site/jacoco/index.html ]; then
      note "  jacoco: target/site/jacoco/index.html (cobertura de codigo novo e criterio do /code-reviewer, nao um fail do repo inteiro)"
    fi
  else
    gate_fail "  FALHOU"
  fi
fi

note "=== Gate 2: E2E ==="
if [ -n "$e2e_cmd" ]; then
  if bash -c "$e2e_cmd"; then
    note "  ok ($e2e_cmd)"
  else
    gate_fail "  FALHOU ($e2e_cmd)"
  fi
else
  note "  suspenso: sem framework E2E. Playwright nao se aplica (servico sem UI web)."
  if [ "$skip_e2e" -eq 1 ]; then
    note "  --skip-e2e explicito"
  fi
fi

note "=== Gate 3: Code review (orientado) ==="
if [ "$skip_review" -eq 1 ]; then
  note "  pulado (--skip-review)"
elif [ "$review_done" -eq 1 ]; then
  note "  marcado (--review-done). Conferir o veredito do /code-reviewer."
else
  gate_fail "  pendente: rode /code-reviewer contra a base do PR e chame de novo com --review-done"
fi

note "=== Gate 4: Docs Obsidian ==="
if [ "$skip_docs" -eq 1 ]; then
  note "  pulado (--skip-docs)"
else
  if [ -z "$task" ]; then
    echo "validate-dod: --task e obrigatorio para o Gate 4 (ou passe --skip-docs)." >&2
    exit 2
  fi
  task_id=$(normalize_task "$task")
  case "$task_id" in
    ISSUE-[0-9]*) ;;
    *)
      echo "validate-dod: task invalida '$task'. Use ISSUE-<n>, <n> ou #<n>." >&2
      exit 2
      ;;
  esac

  # Match the issue id in frontmatter, the github issues/pull URL, or the H1.
  # Work items in this repo keep the id in the title and the github link;
  # sessions keep it in workitems/parent_workitem. Body mentions do not count.
  frontmatter_hits() {
    root="$1"
    id="$2"
    number="${id#ISSUE-}"
    find "$root" -name '*.md' -type f 2>/dev/null | while read -r file; do
      awk -v id="$id" -v number="$number" '
        NR == 1 && $0 == "---" { in_fm = 1; next }
        in_fm && $0 == "---" { in_fm = 0; next }
        in_fm || $0 ~ /^# / {
          if ($0 ~ ("(^|[^0-9])" id "([^0-9]|$)")) found = 1
          if (number ~ /^[0-9]+$/ && $0 ~ ("/(issues|pull)/" number "([^0-9]|$)")) found = 1
        }
        END { exit found ? 0 : 1 }
      ' "$file" && printf '%s\n' "$file"
    done
  }

  work_hits=$(frontmatter_hits "docs/obsidian/Work Items" "$task_id" || true)
  session_hits=$(frontmatter_hits "docs/obsidian/Sessions" "$task_id" || true)

  if [ -n "$work_hits" ]; then
    note "  work item: $(printf '%s\n' "$work_hits" | head -n 1)"
  else
    gate_fail "  FALHOU: nenhum Work Item com $task_id no frontmatter"
  fi
  if [ -n "$session_hits" ]; then
    note "  session: $(printf '%s\n' "$session_hits" | head -n 1)"
  else
    gate_fail "  FALHOU: nenhuma Session com $task_id no frontmatter"
  fi
  if [ -f docs/obsidian/CURRENT_WORK.md ] && grep -q "$task_id" docs/obsidian/CURRENT_WORK.md; then
    note "  CURRENT_WORK.md cita $task_id"
  else
    note "  aviso: CURRENT_WORK.md nao cita $task_id"
  fi
fi

note "=== Gate 5: PR ==="
if [ "$skip_pr" -eq 1 ]; then
  note "  pulado (--skip-pr). Crie o PR com /github antes de fechar a task."
else
  branch=$(git branch --show-current)
  if pr_url=$(gh pr view --json url -q .url 2>/dev/null) && [ -n "$pr_url" ]; then
    note "  ok $pr_url"
  else
    gate_fail "  FALHOU: branch '$branch' sem PR. Use /github ou --skip-pr ate abrir o PR."
  fi
  if [ "$require_pr" -eq 1 ] && [ "$failed" -eq 0 ]; then
    note "  --require-pr satisfeito"
  fi
fi

note ""
if [ "$failed" -ne 0 ]; then
  note "DoD: FALHOU"
  exit 1
fi
note "DoD: gates ativos ok"
exit 0
