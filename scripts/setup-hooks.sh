#!/usr/bin/env sh
# Configures git to use the versioned hooks in .githooks/ (harness).
# Runs automatically on every `./mvnw` invocation (see maven-antrun-plugin in
# pom.xml), but can also be run manually, e.g. right after cloning the repo.

set -e

REPO_ROOT=$(git rev-parse --show-toplevel)
cd "$REPO_ROOT"

git config core.hooksPath .githooks
chmod +x .githooks/* 2>/dev/null || true

echo "Git hooks configurados (core.hooksPath=.githooks)."
