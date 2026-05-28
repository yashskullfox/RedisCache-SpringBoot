# Current State Notes

## Project Overview

Spring Boot REST API backed by Redis (Jedis) for in-memory caching of account data.

## Endpoint Inventory

| Method | Path                       | Controller         | Description                             |
|--------|----------------------------|--------------------|-----------------------------------------|
| POST   | `/Account/{accountNumber}` | `DataController`   | Add account data to cache               |
| PATCH  | `/Account/{accountNumber}` | `DataController`   | Update account (Credit/Withdraw/Remove) |
| DELETE | `/Account/{accountNumber}` | `DataController`   | Remove account from cache               |
| POST   | `/`                        | `SearchController` | Search cache by account number          |
| GET    | `/actuator/health`         | Spring Actuator    | Health check                            |

## DTO Map

| Class       | Role                                                             |
|-------------|------------------------------------------------------------------|
| `CacheData` | POST request body – `type` (String), `value` (String, required)  |
| `PatchData` | PATCH request body – `action` (String, required), `value` (Long) |
| `Request`   | Search request – `account` (String)                              |
| `Result`    | Search response – `account`, `type`, `value`, `lastModification` |
| `CacheInfo` | Internal cache object stored under key `Account_{number}`        |

## Cache Flows

- **Write** (`addDataToCache`): only writes if key does not exist (idempotent add)
- **Credit** (`updateDataInCache`): adds to stored value
- **Withdraw**: subtracts; floor at current value if result < 0
- **Remove** (PATCH action): evicts key
- **Delete** endpoint: evicts key directly
- **Search**: cache miss returns empty `Result` (zero/null fields)

## Build / Run

```bash
# Local (no Redis, uses in-memory ConcurrentMap):
./SpringBootRunner.sh

# Unit tests:
mvn clean test -Dspring.profiles.active=build

# E2E (Karate):
mvn clean verify -Dspring.profiles.active=build
```

## Known Issues Fixed in v1.1.0

- Upgraded from Spring Boot 2.2.2 → 3.4.5 (Java 17)
- Migrated `javax.validation` → `jakarta.validation`
- Fixed malformed `SpringBootRunner.sh` (dangling backslash)
- Updated `spring.redis.*` → `spring.data.redis.*` properties
- Changed WAR → JAR packaging
- Added test infrastructure (JUnit 5 + Karate)
