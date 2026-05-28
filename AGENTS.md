# AGENTS.md – RedisCache-SpringBoot

Agent instructions for AI assistants working in this repo. Read this before making changes.

---

## What This Repo Is

Sample Spring Boot 3.4.5 + Redis cache service demonstrating the **Walmart Turing** pattern:
turn any backend REST repo into an MCP-capable service so AI agents can call it directly
via the Model Context Protocol — without breaking existing REST consumers.

Current state: REST baseline is production-ready (v1.1.0). Active work is Phase 1 of the
REST-MCP bridge (see `docs/REST-MCP-ROADMAP.md`).

---

## Quick Navigation

| Need                                   | Go here                    |
|----------------------------------------|----------------------------|
| Endpoint contracts + DTO map           | `docs/CURRENT-STATE.md`    |
| MCP phases 1-4 detail                  | `docs/REST-MCP-ROADMAP.md` |
| Full requirement + acceptance criteria | `one-requirement.md`       |
| CI pipeline config                     | `.github/workflows/`       |

---

## Build & Test Commands

```bash
# Unit tests (no Redis required — uses in-memory cache)
mvn clean test -Dspring.profiles.active=build

# E2E / Karate (starts embedded server, runs feature files)
mvn clean verify -Dspring.profiles.active=build

# Run app locally (no Redis)
./SpringBootRunner.sh

# Package without running tests
mvn clean package -DskipTests
```

All test commands must stay green before merging. Never skip `-Dspring.profiles.active=build`
for local runs — without it the app tries to connect to a real Redis instance.

---

## Endpoint Matrix

| Method | Path                       | Controller         | Service method        | Notes                                   |
|--------|----------------------------|--------------------|-----------------------|-----------------------------------------|
| POST   | `/Account/{accountNumber}` | `DataController`   | `addDataToCache`      | Idempotent — no-op if key exists        |
| PATCH  | `/Account/{accountNumber}` | `DataController`   | `updateDataInCache`   | Actions: `Credit`, `Withdraw`, `Remove` |
| DELETE | `/Account/{accountNumber}` | `DataController`   | `removeDataFromCache` | Evicts key; no-op if missing            |
| POST   | `/`                        | `SearchController` | `search`              | Cache miss returns zeroed `Result`      |
| GET    | `/actuator/health`         | Spring Actuator    | —                     | Always UP in build profile              |

---

## DTO Contracts

| Class       | Package | Fields                                                     | Used as             |
|-------------|---------|------------------------------------------------------------|---------------------|
| `CacheData` | `api`   | `type: String`, `value: String @NotBlank`                  | POST /Account body  |
| `PatchData` | `api`   | `action: String @NotBlank`, `value: Long`                  | PATCH /Account body |
| `Request`   | `api`   | `account: String`                                          | POST / body         |
| `Result`    | `api`   | `account`, `type`, `value`, `lastModification`             | Search response     |
| `CacheInfo` | root    | `accoutnNumber` (sic), `type`, `value`, `lastModification` | Stored in cache     |

> `CacheInfo.accoutnNumber` is a known typo — **do not rename** (breaks serialized cache entries).

---

## Key Source Layout

```
src/main/java/com/search/
├── api/
│   ├── DataController.java      ← POST/PATCH/DELETE /Account/{n}
│   ├── SearchController.java    ← POST /
│   ├── DataService.java         ← write interface
│   ├── Service.java             ← search interface
│   ├── CacheData.java / PatchData.java / Request.java / Result.java
├── config/
│   ├── CacheConfig.java         ← profile-switched cache bean factory
│   ├── BuildCacheConfig.java    ← ConcurrentMapCache (build/test profile)
│   ├── RedisCacheConfig.java    ← JedisConnectionFactory (prod profile)
├── CacheService.java            ← implements DataService + Service
├── SearchService.java           ← thin search façade (delegates to CacheService)
└── SearchApplication.java
```

---

## When You Add a New Endpoint

1. Add the method to the relevant interface (`DataService` or `Service`) first.
2. Implement in `CacheService` — keep each cache operation ≤ 30 lines.
3. Wire in the controller; keep controllers free of business logic.
4. Add a `@Valid` DTO if the endpoint takes a request body.
5. Write a unit test in `src/test/.../api/<Controller>Test.java`.
6. Add or extend a Karate `.feature` file in `src/test/resources/karate/`.
7. Run `mvn clean verify -Dspring.profiles.active=build` before pushing.

---

## When You Expose a Service Method as an MCP Tool (Turing Pattern)

This is the active Phase 1 work. Follow the pattern in `docs/REST-MCP-ROADMAP.md`:

1. **Read-only first.** Start with `searchAccount` / `getAccountBalance` — no auth needed.
2. **Adapter class** goes in `src/main/java/com/search/mcp/` (create the package).
3. **Never modify existing service signatures.** The MCP adapter calls services; services
   do not know about MCP.
4. **Tool schema** must have: `name` (camelCase), `description` (≥ 1 sentence), `inputSchema`
   matching the service method parameters.
5. **Contract test required** — add an MCP client integration test alongside unit tests.
6. **REST endpoint stays unchanged** — verify with existing Karate suite after adding MCP layer.

Recommended SDK: `io.modelcontextprotocol:sdk` (pinned version). See roadmap Phase 2 task list
for dependency coordinates.

---

## Test Conventions

- Unit tests: `src/test/.../` matching the source package, suffix `Test.java`.
- Karate E2E: `src/test/resources/karate/*.feature` — one feature file per endpoint group.
- Test profile activates `BuildCacheConfig` (in-memory) — no Redis, no Docker required.
- Mock with `@MockitoBean` (Spring Boot 3.4+) not `@MockBean`.
- Assert **outcomes** (response body, status code, cache state) — not method invocations.

---

## Commit & Branch Conventions

```
feat: <what>          # new capability
fix: <what>           # bug fix
refactor: <what>      # no behavior change
test: <what>          # test-only change
docs: <what>          # docs-only change
mcp: <what>           # MCP adapter / tool work (Phase 1+)
```

Branch naming: `feature/<ticket>-<slug>`, `fix/<ticket>-<slug>`, `mcp/phase-<n>-<slug>`.

---

## Runbooks

Markdown runbooks in `docs/runbooks/` — usable by any developer, AI agent, or CLI tool.

| Runbook                  | What it does                                                                     |
|--------------------------|----------------------------------------------------------------------------------|
| `scaffold-mcp-tool.md`   | Generate `@Tool` wrapper + schema + contract test for a service method           |
| `add-endpoint.md`        | Scaffold controller + DTO + unit test + Karate feature (this repo's conventions) |
| `run-karate.md`          | Run Karate E2E suite and summarize pass/fail by scenario                         |
| `endpoint-smoke-test.md` | Curl all endpoints against the running local app and report status               |
| `release-gate-check.md`  | Verify E2E green, tag checklist, and prepare release notes                       |
