package com.restfulbooker.stepdefinitions;

import com.restfulbooker.clients.AuthClient;
import com.restfulbooker.clients.DeleteBookingClient;
import com.restfulbooker.clients.GetBookingClient;
import com.restfulbooker.config.ConfigManager;
import com.restfulbooker.context.ScenarioContext;
import com.restfulbooker.models.AuthRequest;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;

public class DeleteBookingSteps {

    private final ScenarioContext scenarioContext;
    private final DeleteBookingClient deleteBookingClient;
    private final GetBookingClient getBookingClient;
    private final AuthClient authClient;

    public DeleteBookingSteps(
            ScenarioContext scenarioContext
    ) {
        this.scenarioContext = scenarioContext;
        this.deleteBookingClient =
                new DeleteBookingClient();
        this.getBookingClient =
                new GetBookingClient();
        this.authClient = new AuthClient();
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
                resolveToken(authenticationType);

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
                            generateValidToken()
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
                        generateValidToken()
                );

        if (response.statusCode() == 403) {
            response = deleteBookingClient
                    .deleteBooking(
                            bookingId,
                            generateValidToken()
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
                        generateValidToken()
                );

        if (response.statusCode() == 403) {
            response = deleteBookingClient
                    .deleteBooking(
                            bookingId,
                            generateValidToken()
                    );
        }

        scenarioContext.setResponse(response);
    }

    private String resolveToken(
            String authenticationType
    ) {

        return switch (
                authenticationType.trim().toLowerCase()
        ) {
            case "valid" -> generateValidToken();
            case "missing" -> null;
            case "invalid" -> "invalid-token";
            default -> throw new IllegalArgumentException(
                    "Unsupported authentication type: "
                            + authenticationType
            );
        };
    }

    private String generateValidToken() {

        AuthRequest authRequest = new AuthRequest(
                ConfigManager.getProperty("username"),
                ConfigManager.getProperty("password")
        );

        Response authResponse =
                authClient.createToken(authRequest);

        authResponse.then()
                .log()
                .ifValidationFails()
                .statusCode(200);

        String token =
                authResponse.jsonPath().getString("token");

        Assert.assertNotNull(
                token,
                "Authentication token must not be null"
        );

        Assert.assertFalse(
                token.isBlank(),
                "Authentication token must not be blank"
        );

        scenarioContext.setToken(token);

        return token;
    }
}