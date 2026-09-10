@regression @getBooking
Feature: Get Booking APIs

  As an API consumer
  I want to retrieve booking information
  So that I can verify the bookings stored in the system

  @positive @smoke
  Scenario: Retrieve all booking IDs
    When I send a GET request to retrieve all booking IDs
    Then the response status code should be 200
    And the response should contain a list of booking IDs
    And the response should match the "booking-ids-response-schema.json" JSON schema

  @positive @smoke
  Scenario Outline: Retrieve an existing booking by its ID
    Given an existing booking is created using Excel test case "<testCaseId>"
    When I send a GET request for the created booking ID
    Then the response status code should be 200
    And the returned booking details should match Excel test case "<testCaseId>"
    And the response should match the "booking-response-schema.json" JSON schema

    Examples:
      | testCaseId |
      | CB_DATA_001 |
      | CB_DATA_002 |

  @positive
  Scenario Outline: Filter bookings using configurable query parameters
    Given an existing booking is created using Excel test case "<testCaseId>"
    When I filter bookings using "<filters>"
    Then the response status code should be 200
    And the filtered response should contain the created booking ID
    And the response should match the "booking-ids-response-schema.json" JSON schema

    Examples:
      | testCaseId | filters            |
      | CB_DATA_001 | firstname          |
      | CB_DATA_002 | lastname           |
      | CB_DATA_003 | firstname,lastname |

  @negative
  Scenario Outline: Attempt to retrieve a booking using an invalid or unavailable ID
    When I send a GET request for booking ID "<bookingId>"
    Then the response status code should be 404

    Examples:
      | bookingId |
      | 999999999 |
      | invalid   |
      | -1        |