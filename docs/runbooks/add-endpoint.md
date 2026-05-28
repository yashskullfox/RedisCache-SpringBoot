You are adding a new REST endpoint to the RedisCache-SpringBoot service.
Follow the existing patterns exactly — do not introduce new frameworks or abstractions.

## What you need from the user (ask if not provided)

- **HTTP method** (GET / POST / PATCH / DELETE)
- **Path** (e.g., `/Account/{accountNumber}/balance`)
- **What it does** (brief description)
- **Request body shape** (if any)
- **Response shape**

## Steps to execute

1. **Read** `AGENTS.md` (endpoint matrix + conventions) and `docs/CURRENT-STATE.md` (DTO map).
2. **Read** the most similar existing controller and service files to match the pattern:
    - `src/main/java/com/search/api/DataController.java`
    - `src/main/java/com/search/CacheService.java`

3. **DTO** (if new request/response shape is needed):
    - Create in `src/main/java/com/search/api/`
    - Use Lombok `@Data` + `@NoArgsConstructor`
    - Validate inputs with `jakarta.validation` annotations (`@NotBlank`, `@NotNull`)
    - Keep DTOs as simple POJOs — no service logic

4. **Service interface** — add the method signature to `DataService.java` or `Service.java`
   (whichever matches the operation type: write vs. search).

5. **Service implementation** in `CacheService.java`:
    - Keep the implementation ≤ 30 lines
    - Follow existing cache key convention: `"Account_" + accountNumber`
    - Handle null/missing cache entries gracefully (return empty/default, not exception)

6. **Controller method** in the appropriate controller:
    - Use `@Valid` on `@RequestBody` params
    - Return `ResponseEntity<?>` for write operations, typed response for reads
    - No business logic — delegate to service immediately

7. **Unit test** in `src/test/java/com/search/api/<Controller>Test.java`:
    - Test happy path (2xx) and at least one error path (4xx or missing key)
    - Use `MockMvc` + `@MockitoBean` (not `@MockBean`)
    - Assert response status AND body — not just that the service was called

8. **Karate scenario** — add to the most relevant feature file in
   `src/test/resources/karate/` (or create a new `.feature` if it's a distinct domain):
    - Scenario: happy path
    - Scenario: invalid/missing input
    - Use `karate-config.js` base URL

9. **Run** `mvn clean verify -Dspring.profiles.active=build` and report result.

10. **Report**: files created/modified, curl examples for the new endpoint.

## Constraints

- No new dependencies unless essential (discuss first)
- Do not change existing endpoint paths or response shapes
- `CacheInfo.accoutnNumber` is a known typo — do not rename it
- All tests must use `-Dspring.profiles.active=build` (no Redis required)
