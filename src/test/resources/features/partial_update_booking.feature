@regression @partialUpdateBooking
Feature: Partial Update Booking API

  As an authorised API consumer
  I want to partially update an existing booking
  So that only selected booking fields are changed

  @positive @smoke
  Scenario Outline: Partially update an existing booking using selected fields
    Given an existing booking is created using Excel test case "<createDataId>"
    And I prepare a partial update using Excel test case "<partialUpdateDataId>"
    When I send a PATCH request using "valid" authentication
    Then the response status code should be <expectedStatus>
    And the partial update response should match Excel test case "<partialUpdateDataId>"
    And the response should match the "booking-response-schema.json" JSON schema

    Examples:
      | createDataId | partialUpdateDataId | expectedStatus |
      | CB_DATA_001  | PB_DATA_001         | 200            |
      | CB_DATA_001  | PB_DATA_002         | 200            |
      | CB_DATA_002  | PB_DATA_003         | 200            |
      | CB_DATA_003  | PB_DATA_004         | 200            |

  @negative
  Scenario Outline: Attempt to partially update a booking without valid authentication
    Given an existing booking is created using Excel test case "CB_DATA_001"
    And I prepare a partial update using Excel test case "PB_DATA_001"
    When I send a PATCH request using "<authenticationType>" authentication
    Then the response status code should be <expectedStatus>

    Examples:
      | authenticationType | expectedStatus |
      | missing            | 403            |
      | invalid            | 403            |

  @negative
  Scenario: Attempt to partially update a booking that does not exist
    Given I prepare a partial update using Excel test case "PB_DATA_002"
    When I send a PATCH request for booking ID "999999999" using valid authentication
    Then the response status code should be 405