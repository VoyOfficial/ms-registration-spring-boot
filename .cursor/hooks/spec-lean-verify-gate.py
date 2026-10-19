#!/usr/bin/env python3
"""Stop hook: ask for an independent /speckit.verify after a Lean implementation.

Silent for planning, discussion, and docs. Fires once per conversation
(loop_limit 1) when a specs/**/checks.md exists and the branch range or the
working tree contains Java.
"""

from __future__ import annotations

import json
import subprocess
import sys
from pathlib import Path


FOLLOWUP = (
    "Esta branch tem checks.md e Java no range. Rode /speckit.verify agora, "
    "como verificador independente: voce nao e o autor e nao edita codigo. "
    "Cubra cada check e delegue a estrutura ao /code-reviewer. "
    "Se este turno foi so planejamento, discussao ou documentacao, nao altere "
    "codigo e encerre sem implementar."
)


def emit(message: str | None = None) -> None:
    payload = {"followup_message": message} if message else {}
    json.dump(payload, sys.stdout)
    sys.stdout.write("\n")


def git(root: Path, *args: str) -> str:
    result = subprocess.run(
        ["git", *args],
        cwd=root,
        check=False,
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        return ""
    return result.stdout


def merge_base(root: Path) -> str:
    upstream = git(root, "rev-parse", "--abbrev-ref", "--symbolic-full-name", "@{u}").strip()
    if upstream:
        return git(root, "merge-base", "HEAD", upstream).strip()

    best = ""
    refs = git(root, "for-each-ref", "--format=%(refname:short)", "refs/remotes/origin")
    for ref in refs.splitlines():
        if ref.endswith("HEAD"):
            continue
        candidate = git(root, "merge-base", "HEAD", ref).strip()
        if not candidate:
            continue
        if not best:
            best = candidate
            continue
        ancestor = subprocess.run(
            ["git", "merge-base", "--is-ancestor", best, candidate],
            cwd=root,
            check=False,
            capture_output=True,
        )
        if ancestor.returncode == 0:
            best = candidate
    return best


def java_paths(text: str) -> list[str]:
    return [line for line in text.splitlines() if line.endswith(".java")]


def java_in_range(root: Path) -> bool:
    base = merge_base(root)
    chunks = []
    if base:
        chunks.append(git(root, "diff", "--name-only", "--diff-filter=ACMR", f"{base}..HEAD"))
    chunks.append(git(root, "diff", "--name-only", "--diff-filter=ACMR"))
    chunks.append(git(root, "diff", "--cached", "--name-only", "--diff-filter=ACMR"))
    return any(java_paths(chunk) for chunk in chunks)


def checks_exist(root: Path) -> bool:
    specs = root / "specs"
    if not specs.is_dir():
        return False
    return any(specs.glob("**/checks.md"))


def main() -> int:
    raw = sys.stdin.read()
    try:
        data = json.loads(raw) if raw.strip() else {}
    except json.JSONDecodeError:
        emit()
        return 0

    if data.get("status") != "completed":
        emit()
        return 0
    try:
        loop_count = int(data.get("loop_count") or 0)
    except (TypeError, ValueError):
        loop_count = 0
    if loop_count > 0:
        emit()
        return 0

    roots = data.get("workspace_roots") or []
    root = Path(roots[0]).resolve() if roots else Path.cwd()
    if not (root / ".git").exists():
        probe = subprocess.run(
            ["git", "rev-parse", "--show-toplevel"],
            cwd=root,
            check=False,
            capture_output=True,
            text=True,
        )
        if probe.returncode != 0:
            emit()
            return 0
        root = Path(probe.stdout.strip())

    if not checks_exist(root) or not java_in_range(root):
        emit()
        return 0

    emit(FOLLOWUP)
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except Exception:
        emit()
        raise SystemExit(0)
