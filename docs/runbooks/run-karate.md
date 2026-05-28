You are running and summarizing the Karate E2E test suite for RedisCache-SpringBoot.

## Steps to execute

1. **Run** the full Karate suite:
   ```bash
   mvn clean verify -Dspring.profiles.active=build 2>&1 | tee /tmp/karate-run.log
   ```

2. **Check exit code.** If non-zero, the suite has failures — proceed to step 4.

3. **Parse results** from:
    - `target/failsafe-reports/` — XML surefire/failsafe output
    - `target/karate-reports/` — HTML/JSON Karate report (if present)
    - `/tmp/karate-run.log` — raw Maven output

4. **Summarize** in this format:

   ```
   Karate Results — <timestamp>
   ─────────────────────────────
   Scenarios : <total> | Passed: <n> | Failed: <n> | Skipped: <n>

   PASSED:
     ✅ account.feature / Add account to cache
     ✅ account.feature / Update account – Credit
     ...

   FAILED:
     ❌ search.feature / Search by account number
        Request : POST / {"account":"12345"}
        Expected: 200 {"account":"12345", ...}
        Actual  : 500 {"error":"..."}
        Log hint: <relevant log line>

   Build: PASS | FAIL
   ```

5. **If failures exist:**
    - Read the relevant `.feature` file to understand the scenario intent.
    - Read the relevant service/controller code for the failing path.
    - Diagnose the root cause (config issue, assertion mismatch, timing, etc.).
    - Propose a fix with the exact file + line to change.
    - Do NOT auto-apply fixes unless the user asks.

6. **Report** the summary table to the user.

## Constraints

- Always use `-Dspring.profiles.active=build` — no external Redis required
- Do not modify test code during this skill — diagnose only
- If Maven is not on PATH, check `./mvnw` wrapper as fallback
