package com.restfulbooker.stepdefinitions;

import com.restfulbooker.clients.DeleteBookingClient;
import com.restfulbooker.clients.GetBookingClient;
import com.restfulbooker.context.ScenarioContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;
import com.restfulbooker.utils.AuthenticationUtils;

public class DeleteBookingSteps {

    private final ScenarioContext scenarioContext;
    private final DeleteBookingClient deleteBookingClient;
    private final GetBookingClient getBookingClient;
    private final AuthenticationUtils authenticationUtils;

    public DeleteBookingSteps(
            ScenarioContext scenarioContext
    ) {
        this.scenarioContext = scenarioContext;
        this.deleteBookingClient =
                new DeleteBookingClient();
        this.getBookingClient =
                new GetBookingClient();
        this.authenticationUtils =
                new AuthenticationUtils(scenarioContext);
    }

    @When(
        "I send a DELETE request using {string} authentication"
    )
    public void iSendDeleteRequestUsingAuthentication(
            String authenticationType
    ) {

        Integer bookingId =
                scenarioContext.getBookingId();

        Assert.assertNotNull(
                bookingId,
                "Booking ID was not available for deletion"
        );

        String token =
                authenticationUtils.resolveToken(authenticationType);

        Response response = deleteBookingClient
                .deleteBooking(
                        bookingId,
                        token
                );

        if (
                authenticationType.equalsIgnoreCase("valid")
                && response.statusCode() == 403
        ) {
            response = deleteBookingClient
                    .deleteBooking(
                            bookingId,
                            authenticationUtils.generateValidToken()
                    );
        }

        scenarioContext.setResponse(response);
    }

    @When(
        "I send a DELETE request for booking ID {string} using valid authentication"
    )
    public void iSendDeleteRequestForBookingId(
            String bookingId
    ) {

        Response response = deleteBookingClient
                .deleteBooking(
                        bookingId,
                        authenticationUtils.generateValidToken()
                );

        if (response.statusCode() == 403) {
            response = deleteBookingClient
                    .deleteBooking(
                            bookingId,
                            authenticationUtils.generateValidToken()
                    );
        }

        scenarioContext.setResponse(response);
    }

    @Then("the deleted booking should no longer be available")
    public void deletedBookingShouldNoLongerBeAvailable() {

        Integer bookingId =
                scenarioContext.getBookingId();

        Assert.assertNotNull(
                bookingId,
                "Deleted booking ID was not available"
        );

        Response getResponse =
                getBookingClient.getBookingById(bookingId);

        getResponse.then()
                .log()
                .ifValidationFails()
                .statusCode(404);
    }

    @When(
        "I send a DELETE request again using valid authentication"
    )
    public void iSendDeleteRequestAgainUsingValidAuthentication() {

        Integer bookingId =
                scenarioContext.getBookingId();

        Assert.assertNotNull(
                bookingId,
                "Booking ID was not available for deletion"
        );

        Response response = deleteBookingClient
                .deleteBooking(
                        bookingId,
                        authenticationUtils.generateValidToken()
                );

        if (response.statusCode() == 403) {
            response = deleteBookingClient
                    .deleteBooking(
                            bookingId,
                            authenticationUtils.generateValidToken()
                    );
        }

        scenarioContext.setResponse(response);
    }

    @When("I send a delete request using valid basic auth")
    public void iSendADeleteRequestUsingValidBasicAuth()
    {
        int bookingId = scenarioContext.getBookingId();
        Response deleteResponse = deleteBookingClient.deleteWithBasicAuth(bookingId);
        scenarioContext.setResponse(deleteResponse);
    }

}