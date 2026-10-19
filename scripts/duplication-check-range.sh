#!/bin/sh
# Duplication gate for the push range only.
#
# Blocks duplication the branch introduces among the .java files in
# <merge-base>..HEAD. It does not fail on the historical duplication of src/.
# A repo-wide gate would push people to --no-verify, which the harness forbids.
#
# jscpd does not word-split one argument that contains spaces. The file list
# goes through xargs so each path is its own argv. xargs -r skips an empty list.
#
# Usage: ./scripts/duplication-check-range.sh [merge-base]

set -e

REPO_ROOT=$(git rev-parse --show-toplevel)
cd "$REPO_ROOT" || exit 1

resolve_merge_base() {
  upstream=$(git rev-parse --abbrev-ref --symbolic-full-name '@{u}' 2>/dev/null || true)
  if [ -n "$upstream" ]; then
    git merge-base HEAD "$upstream" 2>/dev/null || true
    return
  fi

  merge_base=""
  for candidate in $(git for-each-ref --format='%(refname:short)' refs/remotes/origin | grep -v 'HEAD$'); do
    candidate_base=$(git merge-base HEAD "$candidate" 2>/dev/null) || continue
    [ -z "$candidate_base" ] && continue
    if [ -z "$merge_base" ]; then
      merge_base="$candidate_base"
    elif git merge-base --is-ancestor "$merge_base" "$candidate_base" 2>/dev/null; then
      merge_base="$candidate_base"
    fi
  done
  printf '%s\n' "$merge_base"
}

merge_base="${1:-}"
if [ -z "$merge_base" ]; then
  merge_base=$(resolve_merge_base)
fi

if [ -z "$merge_base" ]; then
  echo "Duplication: sem merge-base (branch nova/sem upstream), pulando."
  exit 0
fi

list_file=$(mktemp)
trap 'rm -f "$list_file"' EXIT

git diff --name-only --diff-filter=ACMR "$merge_base" HEAD -- '*.java' | grep -E '\.java$' >"$list_file" || true

if [ ! -s "$list_file" ]; then
  echo "Duplication: nenhum .java no range ${merge_base}..HEAD, pulando."
  exit 0
fi

if ! command -v npx >/dev/null 2>&1; then
  echo "Push bloqueado: npx nao encontrado. O gate de duplicacao usa jscpd (Node)."
  echo "O projeto ja espera Node para o graphify (npx graphify)."
  exit 1
fi

echo "Duplication: jscpd nos .java do range (threshold 7%)."
if ! xargs -r npx --yes jscpd@4.0.5 --config "$REPO_ROOT/.jscpd.json" --format java --exitCode 1 <"$list_file"; then
  echo ""
  echo "Push bloqueado: duplicacao introduzida no range acima de 7%."
  echo "O gate olha so os .java do push, nao o src/ inteiro."
  echo "Inspecao manual: ./scripts/duplication-check-range.sh"
  exit 1
fi

echo "Duplication: ok"
exit 0
