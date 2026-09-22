package com.restfulbooker.stepdefinitions;

import com.restfulbooker.clients.AuthClient;
import com.restfulbooker.clients.CreateBookingClient;
import com.restfulbooker.clients.DeleteBookingClient;
import com.restfulbooker.clients.GetBookingClient;
import com.restfulbooker.config.ConfigManager;
import com.restfulbooker.context.ScenarioContext;
import com.restfulbooker.models.AuthRequest;
import com.restfulbooker.models.Booking;
import com.restfulbooker.utils.BookingDataMapper;
import com.restfulbooker.utils.ExcelReader;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.util.Map;

public class DeleteBookingSteps {

    private final ScenarioContext scenarioContext;
    private final DeleteBookingClient deleteBookingClient;
    private final GetBookingClient getBookingClient;
    private final AuthClient authClient;
    private static final String EXCEL_FILE_PATH =
            ConfigManager.getProperty("excel.file.path");
    private static final String CREATE_BOOKING_SHEET =
            ConfigManager.getProperty("excel.sheet.create");
    private final CreateBookingClient createBookingClient;
    private final SoftAssert softAssert;

    public DeleteBookingSteps(
            ScenarioContext scenarioContext
    ) {
        this.scenarioContext = scenarioContext;
        this.deleteBookingClient =
                new DeleteBookingClient();
        this.getBookingClient =
                new GetBookingClient();
        this.authClient = new AuthClient();
        this.createBookingClient = new CreateBookingClient();
        this.softAssert = new SoftAssert();
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

    @Given("New booking is created using excel test case {string}")
    public void newBookingIsCreatedUsingExcelTestCase(String testCaseId)
    {
        Map<String,String> excelData = ExcelReader.getRowData(EXCEL_FILE_PATH,CREATE_BOOKING_SHEET, testCaseId);

        Booking createBooking = BookingDataMapper.toBooking(excelData);

        Response response = createBookingClient.createBooking(createBooking);
        Assert.assertNotNull(response, "Assert cant be null");
        softAssert.assertEquals(response.getStatusCode(), 200, "Status code is not 200");
        int bookingId = response.jsonPath().getInt("bookingid");
        scenarioContext.setBookingId(bookingId);
    }

    @When("I send a delete request using valid basic auth")
    public void iSendADeleteRequestUsingValidBasicAuth()
    {
        int bookingId = scenarioContext.getBookingId();
        Response deleteResponse = deleteBookingClient.deleteWithBasicAuth(bookingId);
        scenarioContext.setResponse(deleteResponse);
    }

    @Then("The response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatusCode)
    {
     Response deleteResponse = scenarioContext.getResponse();
     deleteResponse.then().log().ifValidationFails().statusCode(expectedStatusCode);
    }

    @Then("Deleted booking should not be present")
    public void deletedBookingShouldNotBePresent()
    {
       int bookingId = scenarioContext.getBookingId();
       Response getresponse = getBookingClient.getBookingById(bookingId);
       scenarioContext.setResponse(getresponse);
       getresponse.then().log().ifValidationFails().statusCode(404);
    }
}