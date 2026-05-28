You are smoke-testing all endpoints of the locally running RedisCache-SpringBoot app.

## Pre-flight check

1. Verify the app is running:
   ```bash
   curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health
   ```
   If not 200, tell the user to start the app with `./SpringBootRunner.sh` and stop.

## Smoke test matrix

Run each curl in order. Use a shared test account number (e.g., `99901`).

```bash
BASE="http://localhost:8080"
ACCT=99901

# 1. Health check
curl -s -w "\n[%{http_code}]" "$BASE/actuator/health"

# 2. Add account (POST)
curl -s -w "\n[%{http_code}]" -X POST "$BASE/Account/$ACCT" \
  -H "Content-Type: application/json" \
  -d '{"type":"savings","value":"1000"}'

# 3. Search account (POST /)
curl -s -w "\n[%{http_code}]" -X POST "$BASE/" \
  -H "Content-Type: application/json" \
  -d "{\"account\":\"$ACCT\"}"

# 4. Credit account (PATCH)
curl -s -w "\n[%{http_code}]" -X PATCH "$BASE/Account/$ACCT" \
  -H "Content-Type: application/json" \
  -d '{"action":"Credit","value":500}'

# 5. Search again — verify value updated
curl -s -w "\n[%{http_code}]" -X POST "$BASE/" \
  -H "Content-Type: application/json" \
  -d "{\"account\":\"$ACCT\"}"

# 6. Withdraw (PATCH)
curl -s -w "\n[%{http_code}]" -X PATCH "$BASE/Account/$ACCT" \
  -H "Content-Type: application/json" \
  -d '{"action":"Withdraw","value":200}'

# 7. Delete account
curl -s -w "\n[%{http_code}]" -X DELETE "$BASE/Account/$ACCT" \
  -H "Content-Type: application/json"

# 8. Search after delete — expect empty/zeroed result
curl -s -w "\n[%{http_code}]" -X POST "$BASE/" \
  -H "Content-Type: application/json" \
  -d "{\"account\":\"$ACCT\"}"

# 9. Negative: add account with missing value field
curl -s -w "\n[%{http_code}]" -X POST "$BASE/Account/$ACCT" \
  -H "Content-Type: application/json" \
  -d '{"type":"savings"}'
```

## Expected results

| # | Endpoint                     | Expected status | Key assertion                  |
|---|------------------------------|-----------------|--------------------------------|
| 1 | GET /actuator/health         | 200             | `{"status":"UP"}`              |
| 2 | POST /Account/99901          | 200             | "Added into Data"              |
| 3 | POST / (search)              | 200             | `value: 1000`, `type: savings` |
| 4 | PATCH Credit                 | 200             | "is updated"                   |
| 5 | POST / (search after credit) | 200             | `value: 1500`                  |
| 6 | PATCH Withdraw               | 200             | "is updated"                   |
| 7 | DELETE                       | 200             | "Removed Account"              |
| 8 | POST / (search after delete) | 200             | `value: 0` or null fields      |
| 9 | POST missing value           | 400             | validation error               |

## Report format

```
Smoke Test Results — <timestamp>   App: localhost:8080
────────────────────────────────────────────────────────
 # │ Method  │ Path                   │ Status │ Result
───┼─────────┼────────────────────────┼────────┼────────
 1 │ GET     │ /actuator/health       │ 200    │ ✅ PASS
 2 │ POST    │ /Account/99901         │ 200    │ ✅ PASS
...
────────────────────────────────────────────────────────
Summary: X/9 passed
```

Flag any result where status or body does not match the expected table above.
For failures, include the raw response body.
