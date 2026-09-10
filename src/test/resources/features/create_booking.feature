@regression @createBooking
Feature: Create Booking API

  As an API consumer
  I want to create new bookings
  So that booking information can be stored in the system

  @positive @smoke
  Scenario Outline: Create bookings using valid Excel test data
    Given I prepare a booking using Excel test case "<testCaseId>"
    When I send a POST request to create the booking
    Then the response status code should be <expectedStatus>
    And the create booking response should contain a valid booking ID
    And the created booking details should match the request
    And the response should match the "create-booking-response-schema.json" JSON schema

    Examples:
      | testCaseId | expectedStatus |
      | CB_DATA_001 | 200            |
      | CB_DATA_002 | 200            |
      | CB_DATA_003 | 200            |

  @negative
  Scenario Outline: Attempt to create a booking after removing a mandatory field
    Given I prepare booking using Excel test case "CB_DATA_001" after removing "<missingField>"
    When I send a POST request to create the booking
    Then the response status code should be <expectedStatus>

    Examples:
      | missingField | expectedStatus |
      | firstname    | 500            |
      | lastname     | 500            |
      | totalprice   | 500            |
      | depositpaid  | 500            |
      | bookingdates | 500            |
      | checkin      | 500            |
      | checkout     | 500            |

  @negative
  Scenario: Attempt to create a booking using an empty request body
    Given I prepare an empty booking request
    When I send a POST request to create the booking
    Then the response status code should be 500