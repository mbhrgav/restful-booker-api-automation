@regression @authentication
Feature: Restful Booker authentication

  As an authorised API consumer
  I want to generate an authentication token
  So that I can update and delete bookings

  @smoke @positive
  Scenario: Generate token using valid credentials
    Given I have valid authentication credentials
    When I send a POST request to the authentication endpoint
    Then the response status code should be 200
    And the authentication response should contain a non-empty token
    And the response should match the "auth-response-schema.json" JSON schema

  @negative
  Scenario Outline: Reject invalid authentication credentials
    Given I have username "<username>" and password "<password>"
    When I send a POST request to the authentication endpoint
    Then the response status code should be 200
    And the authentication response reason should be "Bad credentials"

    Examples:
      | username    | password      |
      | admin       | wrongPassword |
      | invalidUser | password123   |