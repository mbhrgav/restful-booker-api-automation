@regression @updateBooking
Feature: Full Update Booking API

  As an authorised API consumer
  I want to fully update an existing booking
  So that all booking information can be replaced

  @positive @smoke
  Scenario Outline: Fully update an existing booking using valid data
    Given an existing booking is created using Excel test case "<createDataId>"
    And I prepare a full booking update using Excel test case "<updateDataId>"
    When I send a PUT request using "valid" authentication
    Then the response status code should be <expectedStatus>
    And the updated booking details should match Excel test case "<updateDataId>"
    And the response should match the "booking-response-schema.json" JSON schema

    Examples:
      | createDataId | updateDataId | expectedStatus |
      | CB_DATA_001  | UB_DATA_001  | 200            |
      | CB_DATA_002  | UB_DATA_002  | 200            |

  @negative
  Scenario Outline: Attempt to update a booking without valid authentication
    Given an existing booking is created using Excel test case "CB_DATA_001"
    And I prepare a full booking update using Excel test case "UB_DATA_001"
    When I send a PUT request using "<authenticationType>" authentication
    Then the response status code should be <expectedStatus>

    Examples:
      | authenticationType | expectedStatus |
      | missing            | 403            |
      | invalid            | 403            |

  @negative
  Scenario Outline: Attempt to update a booking after removing a mandatory field
    Given an existing booking is created using Excel test case "CB_DATA_001"
    And I prepare a full booking update using Excel test case "UB_DATA_001" after removing "<missingField>"
    When I send a PUT request using "valid" authentication
    Then the response status code should be <expectedStatus>

    Examples:
      | missingField | expectedStatus |
      | firstname    | 400            |
      | lastname     | 400            |
      | totalprice   | 400            |
      | depositpaid  | 400            |
      | bookingdates | 400            |
      | checkin      | 400            |
      | checkout     | 400            |

  @negative
  Scenario: Attempt to update a booking that does not exist
    Given I prepare a full booking update using Excel test case "UB_DATA_003"
    When I send a PUT request for booking ID "999999999" using valid authentication
    Then the response status code should be 405