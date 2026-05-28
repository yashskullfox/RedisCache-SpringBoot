#!/usr/bin/env bash
# =============================================================================
# SpringBootRunner.sh – local development startup script
#
# Prerequisites:
#   - Java 17+  (check: java -version)
#   - Maven 3.8+ (check: mvn -version)
#   - Redis running on localhost:6379  *only* when using the 'redis' profile
#     (default profile is 'build' which uses in-memory cache – no Redis needed)
#
# Usage:
#   ./SpringBootRunner.sh                     # default build profile (no Redis)
#   SPRING_PROFILES_ACTIVE=redis ./SpringBootRunner.sh   # real Redis
# =============================================================================

set -euo pipefail

PROFILE="${SPRING_PROFILES_ACTIVE:-build}"
APP_PORT="${SERVER_PORT:-8080}"

echo "=================================================="
echo "  RedisCache-SpringBoot  |  profile=${PROFILE}  |  port=${APP_PORT}"
echo "=================================================="

# Verify Java 17+
JAVA_MAJOR=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [[ "${JAVA_MAJOR}" -lt 17 ]]; then
  echo "ERROR: Java 17+ is required (found major version ${JAVA_MAJOR})."
  exit 1
fi

export MAVEN_OPTS="-Xmx512m -Xms256m"

echo "Building project..."
mvn clean package -DskipTests -q

echo "Starting application on port ${APP_PORT} with profile '${PROFILE}'..."
java -jar \
  -Dspring.profiles.active="${PROFILE}" \
  -Dserver.port="${APP_PORT}" \
  target/search-controller-1.1.0.jar

