# Changelog

All notable changes to this project are documented here.
Format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).
Versioning follows [SemVer](https://semver.org/).

---

## [Unreleased]

---

## [1.2.0] — 2026-05-28

### Added — REST-MCP Bridge (Phases 1–4)

**MCP Layer**

- `McpConfig` — registers `WebMvcSseServerTransportProvider` + `McpSyncServer`; exposes `/mcp/sse` (SSE) and
  `/mcp/message` (JSON-RPC) endpoints
- `McpToolAdapter` — interface for auto-wiring tool specs into `McpSyncServer`
- `@Tool` annotation — retained for future annotation processing; runtime registration uses `McpToolAdapter.toolSpecs()`
- `AccountSearchTool` — `searchAccount` and `getAccountBalance` read tools; delegates to `SearchService`
- `AccountWriteTool` — `creditAccount`, `withdrawAccount`, `deleteAccount` write tools; delegates to `CacheService`;
  structured audit log on every call
- `McpInfoContributor` — contributes tool inventory to `GET /actuator/info` under `mcp.tools`
- `McpAuthFilter` — API-key guard (`X-MCP-API-Key` header); version negotiation (`Accept-MCP-Version`); adds
  `x-mcp-version: 1` to all `/mcp/**` responses; disabled when `mcp.auth.api-key` is empty

**Tests (23 new)**

- `AccountSearchToolTest` (4) — searchAccount hit/miss; getAccountBalance hit/miss
- `AccountWriteToolTest` (5) — credit, withdraw, no-overdraft, delete, invalid-account error path
- `McpRateLimiterTest` (2) — token-bucket consume + refill behavior
- `McpAuthFilterTest` (9) — missing/wrong key → 401; valid key passes; unsupported version → 400; version defaults;
  response header; no-auth when unconfigured; non-MCP path bypassed; 11th write call → 429
- `McpInfoContributorTest` (2) — `/actuator/info` lists tools + version field
- `McpIntegrationIT` (5) — full MCP protocol via `McpSyncClient`: list tools, searchAccount miss/hit, getAccountBalance
  hit; REST info endpoint

**Docs**

- `docs/REST-MCP-ROADMAP.md` — Phase 4 versioning policy updated: v0 sunset documented, 90-day deprecation window
  defined
- `one-requirement.md` — all T1.1–T4.4 checkboxes ticked; Deliverables + Milestones tables updated
- `application.properties` — `mcp.auth.api-key=` (empty = auth disabled locally)

### Notes

- **REST endpoints unchanged** — additive MCP layer only
- Auth is opt-in: set `mcp.auth.api-key` env var to enable the API-key guard
- `McpSyncServer` bean uses `destroyMethod = "close"` for clean JVM shutdown

### Rollback

- Remove `src/main/java/com/search/mcp/` package and `mcp-spring-webmvc` from `pom.xml`
- REST endpoints, CI, and release pipeline are unaffected

---

## [1.1.0] — 2026-05-28

### Added

- Spring Boot upgrade: 2.2.2 → 3.4.5 (Java 17)
- Karate E2E test suite (`account.feature`, `search.feature`) — 12 scenarios
- GitHub Actions CI: `ci.yml`, `e2e.yml`, `release.yml` (E2E-gated release)
- Spring Boot Actuator with `/actuator/health` endpoint
- `spring-boot-starter-validation` (jakarta namespace)
- `AGENTS.md` — AI agent instructions and conventions
- `docs/CURRENT-STATE.md` — endpoint inventory and DTO map
- `docs/REST-MCP-ROADMAP.md` — phased MCP migration plan
- `docs/runbooks/` — 5 operational runbooks
- `application-build.properties` — disables Redis health indicator in build profile

### Changed

- `javax.validation` → `jakarta.validation` across all DTOs
- WAR → JAR packaging
- `jedis` 3.2.0 → 5.2.0 (Spring Data Redis 3.x compatibility)
- `SpringBootRunner.sh` hardened with `set -euo pipefail`, Java 17 guard, clear output
- `spring.redis.*` → `spring.data.redis.*` properties
- `@Profile(value = {"build"})` simplified to `@Profile("build")`
- `@Profile` added to `RedisCacheConfig` (`"!build"`) to prevent bean conflict
- Constructor injection replaces `@Autowired` field injection in controllers
- Removed redundant `@Getter`/`@Setter` from `CacheInfo` (covered by `@Data`)
- `switch` in `CacheService` refactored to arrow-switch (Java 17)
- `Long.valueOf` → `Long.parseLong` in `CacheService`
- `DELETE /Account/{n}` — removed `consumes = APPLICATION_JSON` constraint
- `SearchController` — replaced local constant with `MediaType.APPLICATION_JSON_VALUE`

### Fixed

- Dead code: `if (cacheKey != null)` guard inside switch (cacheKey always non-null)
- Unused `List`/`ArrayList` imports and dead variable in `SearchService`
- Stale `@NotNull` on `int account` and `Long value` in `Result` (response DTO)
- `@NotNull` → `@NotBlank` on String-typed validation fields (`CacheData.value`, `PatchData.action`)
- Postman collection: corrected action names to match implementation (`Credit`, `Withdraw`, `Remove`)

### Removed

- Redundant `spring-boot-starter` and `spring-web` dependencies (covered by `spring-boot-starter-web`)
- Duplicate `lombok` dependency declaration
- Version pin on Lombok (now managed by Spring Boot BOM)

### Rollback Notes

> If v1.1.0 causes issues, revert to v1.0-SNAPSHOT by checking out the last commit before
> this release. Key breaking changes from the upgrade:
> - Java 17 is required; the app will not start on Java 8 or 11
> - `javax.*` imports replaced by `jakarta.*` — cannot run on old servlet containers
> - Packaging is JAR not WAR — deploy with `java -jar`, not a servlet container
> - `application.properties` keys changed: `spring.redis.*` → `spring.data.redis.*`

---

## [1.0-SNAPSHOT] — 2019 (original)

### Initial state

- Spring Boot 2.2.2, Java 8, WAR packaging
- Redis cache with Jedis 3.2.0
- `DataController` (POST/PATCH/DELETE `/Account/{n}`) and `SearchController` (POST `/`)
- In-memory fallback via `BuildCacheConfig` (`build` profile)
