@regression @deleteBooking
Feature: Delete Booking API

  As an authorised API consumer
  I want to delete existing bookings
  So that unwanted booking records are removed

  @positive @smoke
  Scenario Outline: Delete an existing booking using valid authentication
    Given an existing booking is created using Excel test case "<createDataId>"
    When I send a DELETE request using "valid" authentication
    Then the response status code should be 201
    And the deleted booking should no longer be available

    Examples:
      | createDataId |
      | CB_DATA_001  |
      | CB_DATA_002  |

  @negative
  Scenario Outline: Attempt to delete a booking without valid authentication
    Given an existing booking is created using Excel test case "CB_DATA_001"
    When I send a DELETE request using "<authenticationType>" authentication
    Then the response status code should be <expectedStatus>

    Examples:
      | authenticationType | expectedStatus |
      | missing            | 403            |
      | invalid            | 403            |

  @negative
  Scenario: Attempt to delete a booking that does not exist
    When I send a DELETE request for booking ID "999999999" using valid authentication
    Then the response status code should be 405

  @negative
  Scenario: Attempt to delete the same booking twice
    Given an existing booking is created using Excel test case "CB_DATA_003"
    When I send a DELETE request using "valid" authentication
    Then the response status code should be 201
    When I send a DELETE request again using valid authentication
    Then the response status code should be 405