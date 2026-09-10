package com.restfulbooker.stepdefinitions;

import com.restfulbooker.clients.AuthClient;
import com.restfulbooker.clients.UpdateBookingClient;
import com.restfulbooker.config.ConfigManager;
import com.restfulbooker.context.ScenarioContext;
import com.restfulbooker.models.AuthRequest;
import com.restfulbooker.models.Booking;
import com.restfulbooker.utils.BookingDataMapper;
import com.restfulbooker.utils.ExcelReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.util.Map;

public class UpdateBookingSteps {

    private static final String EXCEL_FILE_PATH =
            "testdata/BookingTestData.xlsx";

    private static final String UPDATE_BOOKING_SHEET =
            "UpdateBooking";

    private final ScenarioContext scenarioContext;
    private final UpdateBookingClient updateBookingClient;
    private final AuthClient authClient;

    private Booking updateBookingRequest;
    private Map<String, Object> invalidUpdateRequest;

    public UpdateBookingSteps(
            ScenarioContext scenarioContext
    ) {
        this.scenarioContext = scenarioContext;
        this.updateBookingClient =
                new UpdateBookingClient();
        this.authClient = new AuthClient();
    }

    @Given(
        "I prepare a full booking update using Excel test case {string}"
    )
    public void iPrepareFullBookingUpdate(
            String testCaseId
    ) {

        Map<String, String> excelData =
                ExcelReader.getRowData(
                        EXCEL_FILE_PATH,
                        UPDATE_BOOKING_SHEET,
                        testCaseId
                );

        updateBookingRequest =
                BookingDataMapper.toBooking(excelData);

        invalidUpdateRequest = null;
    }

    @Given(
        "I prepare a full booking update using Excel test case {string} after removing {string}"
    )
    public void iPrepareFullBookingUpdateAfterRemovingField(
            String testCaseId,
            String missingField
    ) {

        Map<String, String> excelData =
                ExcelReader.getRowData(
                        EXCEL_FILE_PATH,
                        UPDATE_BOOKING_SHEET,
                        testCaseId
                );

        invalidUpdateRequest =
                BookingDataMapper.toRequestMap(excelData);

        BookingDataMapper.removeField(
                invalidUpdateRequest,
                missingField
        );

        updateBookingRequest = null;
    }

    @When(
        "I send a PUT request using {string} authentication"
    )
    public void iSendPutRequestUsingAuthentication(
            String authenticationType
    ) {

        Integer bookingId =
                scenarioContext.getBookingId();

        Assert.assertNotNull(
                bookingId,
                "Booking ID was not available for update"
        );

        String token =
                resolveToken(authenticationType);

        Response response = sendUpdateRequest(
                bookingId,
                token
        );

        scenarioContext.setResponse(response);
    }

   @When(
    "I send a PUT request for booking ID {string} using valid authentication"
)
public void iSendPutRequestForBookingId(
        String bookingId
) {

    String token = generateValidToken();

    Response response = sendUpdateRequest(
            bookingId,
            token
    );

    if (response.statusCode() == 403) {

        String refreshedToken = generateValidToken();

        response = sendUpdateRequest(
                bookingId,
                refreshedToken
        );
    }

    scenarioContext.setResponse(response);
}

    @Then(
        "the updated booking details should match Excel test case {string}"
    )
    public void updatedBookingDetailsShouldMatchExcelTestCase(
            String testCaseId
    ) {

        Assert.assertNotNull(
                updateBookingRequest,
                "Expected update data was not available for "
                        + testCaseId
        );

        Response response =
                scenarioContext.getResponse();

        Assert.assertNotNull(
                response,
                "Update booking response was not available"
        );

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertEquals(
                response.jsonPath().getString("firstname"),
                updateBookingRequest.getFirstname(),
                "Updated firstname did not match"
        );

        softAssert.assertEquals(
                response.jsonPath().getString("lastname"),
                updateBookingRequest.getLastname(),
                "Updated lastname did not match"
        );

        softAssert.assertEquals(
                response.jsonPath().getInt("totalprice"),
                updateBookingRequest.getTotalprice(),
                "Updated total price did not match"
        );

        softAssert.assertEquals(
                response.jsonPath().getBoolean("depositpaid"),
                updateBookingRequest.isDepositpaid(),
                "Updated deposit-paid value did not match"
        );

        softAssert.assertEquals(
                response.jsonPath()
                        .getString("bookingdates.checkin"),
                updateBookingRequest
                        .getBookingdates()
                        .getCheckin(),
                "Updated check-in date did not match"
        );

        softAssert.assertEquals(
                response.jsonPath()
                        .getString("bookingdates.checkout"),
                updateBookingRequest
                        .getBookingdates()
                        .getCheckout(),
                "Updated check-out date did not match"
        );

        softAssert.assertEquals(
                response.jsonPath()
                        .getString("additionalneeds"),
                updateBookingRequest.getAdditionalneeds(),
                "Updated additional needs did not match"
        );

        softAssert.assertAll();
    }

    private Response sendUpdateRequest(
            Object bookingId,
            String token
    ) {

        if (updateBookingRequest != null) {
            return updateBookingClient.updateBooking(
                    bookingId,
                    token,
                    updateBookingRequest
            );
        }

        if (invalidUpdateRequest != null) {
            return updateBookingClient.updateBooking(
                    bookingId,
                    token,
                    invalidUpdateRequest
            );
        }

        throw new IllegalStateException(
                "Update booking request was not prepared"
        );
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

        String token = authResponse
                .jsonPath()
                .getString("token");

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