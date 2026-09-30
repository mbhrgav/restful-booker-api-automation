package com.restfulbooker.stepdefinitions;


import com.restfulbooker.clients.GetBookingClient;
import com.restfulbooker.context.ScenarioContext;
import com.restfulbooker.models.Booking;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;
import com.restfulbooker.utils.BookingAssertions;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GetBookingSteps {

    private final ScenarioContext scenarioContext;
    private final GetBookingClient getBookingClient;


    public GetBookingSteps(ScenarioContext scenarioContext)
    {
        this.scenarioContext = scenarioContext;
        this.getBookingClient = new GetBookingClient();
    }

    @When("I send a GET request to retrieve all booking IDs")
    public void iSendAGetRequestToRetrieveAllBookingIds() {

        Response response =
                getBookingClient.getAllBookingIds();

        scenarioContext.setResponse(response);
    }

    @Then("the response should contain a list of booking IDs")
    public void responseShouldContainListOfBookingIds() {

        Response response =
                scenarioContext.getResponse();

        Assert.assertNotNull(
                response,
                "Get booking IDs response was not available"
        );

        List<Map<String, Object>> bookingIds =
                response.jsonPath().getList("$");

        Assert.assertNotNull(
                bookingIds,
                "Booking ID list must not be null"
        );

        Assert.assertFalse(
                bookingIds.isEmpty(),
                "Booking ID list must not be empty"
        );

        for (Map<String, Object> booking : bookingIds) {

            Assert.assertTrue(
                    booking.containsKey("bookingid"),
                    "Response item did not contain bookingid"
            );

            Assert.assertNotNull(
                    booking.get("bookingid"),
                    "Booking ID must not be null"
            );
        }
    }

    @When("I send a GET request for the created booking ID")
    public void iSendAGetRequestForCreatedBookingId() {

        Integer bookingId =
                scenarioContext.getBookingId();

        Assert.assertNotNull(
                bookingId,
                "Created booking ID was not available"
        );

        Response response =
                getBookingClient.getBookingById(bookingId);

        scenarioContext.setResponse(response);
    }

    @Then("the returned booking details should match Excel test case {string}")
    public void returnedBookingDetailsShouldMatchExcelTestCase(
            String testCaseId
    ) {

        Booking expectedBooking =
                scenarioContext.getExpectedBooking();

        Assert.assertNotNull(
                expectedBooking,
                "Expected booking data was not available for " + testCaseId
        );

        BookingAssertions.validateBookingDetails(
                scenarioContext.getResponse(),
                expectedBooking,
                ""
        );
    }

    @When("I filter bookings using {string}")
    public void iFilterBookingsUsing(String filterNames) {

        Booking expectedBooking = scenarioContext.getExpectedBooking();

        Assert.assertNotNull(
                expectedBooking,
                "Expected booking data was not available"
        );

        Map<String, Object> filters =
                new LinkedHashMap<>();

        String[] requestedFilters =
                filterNames.split(",");

        for (String requestedFilter : requestedFilters) {

            String filter =
                    requestedFilter.trim().toLowerCase();

            switch (filter) {

                case "firstname" ->
                        filters.put(
                                "firstname",
                                expectedBooking.getFirstname()
                        );

                case "lastname" ->
                        filters.put(
                                "lastname",
                                expectedBooking.getLastname()
                        );

                case "checkin" ->
                        filters.put(
                                "checkin",
                                expectedBooking
                                        .getBookingdates()
                                        .getCheckin()
                        );

                case "checkout" ->
                        filters.put(
                                "checkout",
                                expectedBooking
                                        .getBookingdates()
                                        .getCheckout()
                        );

                default -> throw new IllegalArgumentException(
                        "Unsupported booking filter: "
                                + requestedFilter
                );
            }
        }

        Response response =
                getBookingClient
                        .getBookingIdsWithFilters(filters);

        scenarioContext.setResponse(response);
    }

    @Then(
        "the filtered response should contain the created booking ID"
    )
    public void filteredResponseShouldContainCreatedBookingId() {

        Integer expectedBookingId =
                scenarioContext.getBookingId();

        Assert.assertNotNull(
                expectedBookingId,
                "Created booking ID was not available"
        );

        Response response =
                scenarioContext.getResponse();

        Assert.assertNotNull(
                response,
                "Filtered response was not available"
        );

        List<Integer> bookingIds =
                response.jsonPath()
                        .getList("bookingid");

        Assert.assertNotNull(
                bookingIds,
                "Filtered booking ID list must not be null"
        );

        Assert.assertTrue(
                bookingIds.contains(expectedBookingId),
                "Filtered response did not contain booking ID: "
                        + expectedBookingId
        );
    }

    @When("I send a GET request for booking ID {string}")
    public void iSendAGetRequestForBookingId(
            String bookingId
    ) {

        Response response =
                getBookingClient.getBookingById(bookingId);

        scenarioContext.setResponse(response);
    }
}