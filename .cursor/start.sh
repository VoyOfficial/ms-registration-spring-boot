#!/usr/bin/env bash
# Per-boot runtime initialization: bring up the local PostgreSQL server and
# ensure the database/credentials the application expects exist. This script is
# idempotent and safe to run on every environment start.
set -euo pipefail

# Start the PostgreSQL cluster (version-agnostic). "start" is a no-op if it is
# already running.
sudo service postgresql start

# Wait until PostgreSQL is accepting connections.
for _ in $(seq 1 30); do
  if pg_isready -q; then
    break
  fi
  sleep 1
done

# Configure the credentials and database used by application-local.properties
# (DATABASE_USER=postgres / DATABASE_PASSWORD=password / DATABASE_DB=voy).
sudo -u postgres psql -c "ALTER USER postgres WITH PASSWORD 'password';"
sudo -u postgres psql -tc "SELECT 1 FROM pg_database WHERE datname='voy'" \
  | grep -q 1 || sudo -u postgres psql -c "CREATE DATABASE voy;"

echo "PostgreSQL is ready and the 'voy' database exists."
