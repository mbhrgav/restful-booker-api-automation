@health
Feature: Restful Booker API health check

  As an API consumer
  I want to verify the health of the Restful Booker API
  So that I know whether the service is available

  @smoke
  Scenario: Verify that the Restful Booker API is running
    Given the Restful Booker API base URL is configured
    When I send a GET request to the health check endpoint
    Then the response status code should be 201