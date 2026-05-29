# RedisCache-SpringBoot

Spring Boot 3.4.5 service that stores account data in a cache and supports both:
1. **REST clients** (stable existing endpoints)
2. **MCP clients** (AI-agent tool calls over `/mcp/**`)

> **Current version:** `1.2.0` &nbsp;|&nbsp; **Java:** 17 &nbsp;|&nbsp; **Default local profile:** `build` (in-memory cache, no Redis required)

---

## What this project demonstrates

This repo shows a practical migration pattern:

- Keep the Java service contract stable
- Add Redis-backed caching for production
- Keep local/test runs fast with in-memory cache
- Expose the same business capabilities as MCP tools (additive, non-breaking)

---

## How we converted the Java application to Redis-backed caching

### Before → After

| Area | Before (legacy baseline) | After (current state) |
|------|---------------------------|------------------------|
| Runtime | Older Spring Boot generation | Spring Boot `3.4.5` + Java `17` |
| Validation | `javax.*` | `jakarta.*` |
| Packaging | WAR-oriented | Runnable JAR |
| Cache backend | Ad-hoc/legacy setup | Spring Cache + profile-based backend |
| Production cache | Not standardized | Redis via `RedisCacheManager` + Jedis |
| Local/test cache | Mixed local setups | `build` profile with `ConcurrentMapCacheManager` |
| Release safety | Limited automation | Unit + Karate E2E + CI gates |

### Key implementation choices

1. **Single cache contract:** `CacheConfig` defines one cache name: `AccountCache`.
2. **Profile-based cache backend:**  
   - `BuildCacheConfig` (`@Profile("build")`) uses in-memory `ConcurrentMapCacheManager`.  
   - `RedisCacheConfig` (`@Profile("!build")`) uses Redis with `JedisConnectionFactory`.
3. **Production TTL policy:** `AccountCache` entries expire after **1 hour** in Redis.
4. **Zero REST contract breakage:** Controllers and endpoint paths stayed stable while internals were modernized.
5. **Safe rollout path:** Test profile defaults to in-memory cache so CI/local runs are deterministic without external Redis.

For the full migration narrative (files touched, rationale, and verification approach), see:
`docs/JAVA-APP-TO-REDIS-CONVERSION.md`.

---

## Quick start

```bash
# Run locally without Redis (in-memory cache)
./SpringBootRunner.sh

# Unit tests (build profile)
mvn clean test -Dspring.profiles.active=build

# Full verification (unit + integration + Karate)
mvn clean verify -Dspring.profiles.active=build
```

---

## Run with real Redis

```bash
# Start Redis (example)
redis-server --port 6379

# Run app with non-build profile so RedisCacheConfig is active
mvn spring-boot:run -Dspring-boot.run.profiles=redis \
  -Dspring-boot.run.arguments="--redis.url=localhost --redis.port=6379"
```

When not using the `build` profile, the app wires `RedisCacheConfig` and stores cache entries in Redis.

---

## API surface

| Method | Path | Purpose |
|--------|------|---------|
| POST | `/Account/{accountNumber}` | Add account if missing |
| PATCH | `/Account/{accountNumber}` | `Credit` / `Withdraw` / `Remove` |
| DELETE | `/Account/{accountNumber}` | Remove account |
| POST | `/` | Search account |
| GET | `/actuator/health` | Health endpoint |
| GET | `/actuator/info` | Service + MCP tool inventory |
| GET | `/mcp/sse` | MCP SSE transport |
| POST | `/mcp/message` | MCP JSON-RPC endpoint |

---

## Documentation map

- `docs/JAVA-APP-TO-REDIS-CONVERSION.md` — complete migration story (Java app → Redis-backed service)
- `docs/CURRENT-STATE.md` — endpoint/DTO/current behavior snapshot
- `docs/REST-MCP-ROADMAP.md` — MCP phase history and versioning policy
- `one-requirement.md` — full acceptance criteria and delivery checklist
- `AGENTS.md` — contributor/agent engineering conventions

Contact: skullfox@hackermail.com
