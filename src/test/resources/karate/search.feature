Feature: Search API – cache lookup flows

  Background:
    * url baseUrl

  Scenario: Search returns data for a seeded account
    # Seed
    Given path '/Account/4001'
    And request { type: 'investment', value: '10000' }
    When method POST
    Then status 200
    # Search
    Given path '/'
    And request { account: '4001' }
    When method POST
    Then status 200
    And match response.account == 4001
    And match response.type == 'investment'
    And match response.value == 10000

  Scenario: Search unknown account returns 200 with empty/default result
    Given path '/'
    And request { account: '99999' }
    When method POST
    Then status 200
    And match response.value == '#null'

  Scenario: Search after delete returns empty result
    Given path '/Account/4002'
    And request { type: 'savings', value: '500' }
    When method POST
    Then status 200
    Given path '/Account/4002'
    When method DELETE
    Then status 200
    Given path '/'
    And request { account: '4002' }
    When method POST
    Then status 200
    And match response.value == '#null'

  Scenario: Health endpoint is reachable
    Given url baseUrl + '/actuator/health'
    When method GET
    Then status 200
    And match response.status == 'UP'
