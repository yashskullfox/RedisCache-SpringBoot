Feature: Account Cache API – CRUD flows

  Background:
    * url baseUrl

  # ─────────────────────────────────────────────────────────────────────────────
  # POST /Account/{accountNumber} – create / add to cache
  # ─────────────────────────────────────────────────────────────────────────────

  Scenario: Add a new account to cache (happy path)
    Given path '/Account/1001'
    And request { type: 'savings', value: '5000' }
    When method POST
    Then status 200
    And match response contains '1001'

  Scenario: Add account – duplicate should not overwrite (idempotent)
    Given path '/Account/1002'
    And request { type: 'savings', value: '1000' }
    When method POST
    Then status 200
    # Second POST with different value – cache should still hold original
    Given path '/Account/1002'
    And request { type: 'savings', value: '9999' }
    When method POST
    Then status 200

  Scenario: Add account – missing required field 'value' returns 400
    Given path '/Account/1003'
    And request { type: 'savings' }
    When method POST
    Then status 400

  # ─────────────────────────────────────────────────────────────────────────────
  # PATCH /Account/{accountNumber} – update value in cache
  # ─────────────────────────────────────────────────────────────────────────────

  Scenario: Credit an existing account
    # Seed
    Given path '/Account/2001'
    And request { type: 'checking', value: '2000' }
    When method POST
    Then status 200
    # Credit
    Given path '/Account/2001'
    And request { action: 'Credit', value: 500 }
    When method PATCH
    Then status 200
    And match response contains '2001'

  Scenario: Withdraw from an existing account
    Given path '/Account/2002'
    And request { type: 'checking', value: '1000' }
    When method POST
    Then status 200
    Given path '/Account/2002'
    And request { action: 'Withdraw', value: 300 }
    When method PATCH
    Then status 200

  Scenario: Patch account – missing required field 'action' returns 400
    Given path '/Account/2003'
    And request { value: 100 }
    When method PATCH
    Then status 400

  # ─────────────────────────────────────────────────────────────────────────────
  # DELETE /Account/{accountNumber} – remove from cache
  # ─────────────────────────────────────────────────────────────────────────────

  Scenario: Delete an existing account
    Given path '/Account/3001'
    And request { type: 'savings', value: '750' }
    When method POST
    Then status 200
    Given path '/Account/3001'
    When method DELETE
    Then status 200
    And match response contains '3001'

  Scenario: Delete a non-existent account returns 200 (no-op)
    Given path '/Account/3999'
    When method DELETE
    Then status 200
