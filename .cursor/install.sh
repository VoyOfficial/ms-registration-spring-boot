#!/usr/bin/env bash
# Idempotent repository bootstrap for the ms-registration Spring Boot service.
# Installs the JDK 11 / Maven toolchain and a local PostgreSQL server, then
# compiles and tests the project so a runnable jar is available in target/.
set -euo pipefail

export DEBIAN_FRONTEND=noninteractive

# Spring Boot 2.7.5 (and its bundled Lombok/Hibernate) requires JDK 11.
# Maven is installed from apt because the repository's ./mvnw wrapper is
# missing its .mvn/wrapper metadata and cannot bootstrap on its own.
sudo apt-get update -y
sudo apt-get install -y --no-install-recommends \
  openjdk-11-jdk \
  maven \
  postgresql \
  postgresql-contrib

export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64

# Build and run the test suite (tests use an in-memory H2 database, so no
# running PostgreSQL instance is needed at this stage).
mvn -B clean package
