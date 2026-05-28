# RedisCache-SpringBoot

Spring Boot 3.4.5 + Redis cache service — modernized REST API with CI/CD and a REST-MCP Northstar roadmap.

> **Current version:** `1.1.0` &nbsp;|&nbsp; **Java:** 17 &nbsp;|&nbsp; **Profile:** `build` (in-memory cache, no Redis
> needed for tests)

---

## Quick Start

```bash
# No Redis required — uses in-memory cache
./SpringBootRunner.sh

# Unit tests
mvn clean test -Dspring.profiles.active=build

# Full E2E (Karate)
mvn clean verify -Dspring.profiles.active=build
```

## About the Service

In-memory / Redis-backed cache for account data. Uses `CacheManager` (profile-switched: in-memory for `build`, Jedis for
production) to manage an `AccountCache` bucket with 1-hour TTL.

### Endpoints

| Method | Path                       | Description                                       |
|--------|----------------------------|---------------------------------------------------|
| POST   | `/Account/{accountNumber}` | Add account to cache                              |
| PATCH  | `/Account/{accountNumber}` | Update account (`Credit` / `Withdraw` / `Remove`) |
| DELETE | `/Account/{accountNumber}` | Evict account from cache                          |
| POST   | `/`                        | Search account by number                          |
| GET    | `/actuator/health`         | Health check                                      |

### Add account

```
POST /Account/1234
Content-Type: application/json

{ "type": "Saving", "value": "100" }

→ 200  "Account 1234 Added into Data"
```

### Search account

```
POST /
Content-Type: application/json

{ "account": "1234" }

→ 200
{
  "account": 1234,
  "type": "Saving",
  "value": 100,
  "lastModification": "2024-12-20T02:40:24.312Z"
}
```

### Update (Credit / Withdraw)

```
PATCH /Account/1234
Content-Type: application/json

{ "action": "Credit", "value": 200 }

→ 200  "Account 1234 is updated"
```

### Delete

```
DELETE /Account/1234

→ 200  "Removed Account 1234"
```

---

## Architecture

See `AGENTS.md` for full source layout, conventions, and MCP roadmap guidance.  
See `docs/REST-MCP-ROADMAP.md` for the phased plan toward REST-MCP bridge.

## Prerequisites (production with Redis)

- Java 17+
- Maven 3.8+
- Redis 7+ on `localhost:6379` (only needed for `redis` profile)

Contact: skullfox@hackermail.com
