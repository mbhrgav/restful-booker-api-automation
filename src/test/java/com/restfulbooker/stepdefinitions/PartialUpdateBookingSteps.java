package com.restfulbooker.stepdefinitions;

import com.restfulbooker.clients.AuthClient;
import com.restfulbooker.clients.PartialUpdateBookingClient;
import com.restfulbooker.config.ConfigManager;
import com.restfulbooker.context.ScenarioContext;
import com.restfulbooker.models.AuthRequest;
import com.restfulbooker.utils.ExcelReader;
import com.restfulbooker.utils.PartialUpdateDataMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.util.Map;

public class PartialUpdateBookingSteps {

    private static final String EXCEL_FILE_PATH =
            ConfigManager.getProperty("excel.file.path");

    private static final String PARTIAL_UPDATE_SHEET =
            ConfigManager.getProperty(
                    "excel.sheet.partial.update"
            );

    private final ScenarioContext scenarioContext;
    private final PartialUpdateBookingClient partialUpdateClient;
    private final AuthClient authClient;

    private Map<String, Object> partialUpdateRequest;

    public PartialUpdateBookingSteps(
            ScenarioContext scenarioContext
    ) {
        this.scenarioContext = scenarioContext;
        this.partialUpdateClient =
                new PartialUpdateBookingClient();
        this.authClient = new AuthClient();
    }

    @Given(
        "I prepare a partial update using Excel test case {string}"
    )
    public void iPreparePartialUpdate(
            String testCaseId
    ) {

        Map<String, String> excelData =
                ExcelReader.getRowData(
                        EXCEL_FILE_PATH,
                        PARTIAL_UPDATE_SHEET,
                        testCaseId
                );

        partialUpdateRequest =
                PartialUpdateDataMapper
                        .toPartialUpdateMap(excelData);

        Assert.assertFalse(
                partialUpdateRequest.isEmpty(),
                "Partial update request must not be empty"
        );
    }

    @When(
        "I send a PATCH request using {string} authentication"
    )
    public void iSendPatchRequestUsingAuthentication(
            String authenticationType
    ) {

        Integer bookingId =
                scenarioContext.getBookingId();

        Assert.assertNotNull(
                bookingId,
                "Booking ID was not available for partial update"
        );

        String token =
                resolveToken(authenticationType);

        Response response = partialUpdateClient
                .partiallyUpdateBooking(
                        bookingId,
                        token,
                        partialUpdateRequest
                );

        if (
                authenticationType.equalsIgnoreCase("valid")
                && response.statusCode() == 403
        ) {
            response = partialUpdateClient
                    .partiallyUpdateBooking(
                            bookingId,
                            generateValidToken(),
                            partialUpdateRequest
                    );
        }

        scenarioContext.setResponse(response);
    }

    @When(
        "I send a PATCH request for booking ID {string} using valid authentication"
    )
    public void iSendPatchRequestForBookingId(
            String bookingId
    ) {

        Response response = partialUpdateClient
                .partiallyUpdateBooking(
                        bookingId,
                        generateValidToken(),
                        partialUpdateRequest
                );

        if (response.statusCode() == 403) {
            response = partialUpdateClient
                    .partiallyUpdateBooking(
                            bookingId,
                            generateValidToken(),
                            partialUpdateRequest
                    );
        }

        scenarioContext.setResponse(response);
    }

    @Then(
        "the partial update response should match Excel test case {string}"
    )
    public void partialUpdateResponseShouldMatchExcelTestCase(
            String testCaseId
    ) {

        Assert.assertNotNull(
                partialUpdateRequest,
                "Partial update data was not available for "
                        + testCaseId
        );

        Response response =
                scenarioContext.getResponse();

        Assert.assertNotNull(
                response,
                "Partial update response was not available"
        );

        SoftAssert softAssert = new SoftAssert();

        assertTopLevelField(
                softAssert,
                response,
                "firstname"
        );

        assertTopLevelField(
                softAssert,
                response,
                "lastname"
        );

        assertTopLevelField(
                softAssert,
                response,
                "totalprice"
        );

        assertTopLevelField(
                softAssert,
                response,
                "depositpaid"
        );

        assertTopLevelField(
                softAssert,
                response,
                "additionalneeds"
        );

        assertBookingDate(
                softAssert,
                response,
                "checkin"
        );

        assertBookingDate(
                softAssert,
                response,
                "checkout"
        );

        softAssert.assertAll();
    }

    private void assertTopLevelField(
            SoftAssert softAssert,
            Response response,
            String fieldName
    ) {

        if (!partialUpdateRequest.containsKey(fieldName)) {
            return;
        }

        Object expectedValue =
                partialUpdateRequest.get(fieldName);

        Object actualValue =
                response.jsonPath().get(fieldName);

        softAssert.assertEquals(
                actualValue,
                expectedValue,
                "Updated " + fieldName + " did not match"
        );
    }

    private void assertBookingDate(
            SoftAssert softAssert,
            Response response,
            String dateField
    ) {

        Object bookingDatesObject =
                partialUpdateRequest.get("bookingdates");

        if (!(bookingDatesObject instanceof Map<?, ?>)) {
            return;
        }

        Map<?, ?> bookingDates =
                (Map<?, ?>) bookingDatesObject;

        if (!bookingDates.containsKey(dateField)) {
            return;
        }

        Object expectedValue =
                bookingDates.get(dateField);

        Object actualValue = response
                .jsonPath()
                .get("bookingdates." + dateField);

        softAssert.assertEquals(
                actualValue,
                expectedValue,
                "Updated " + dateField + " did not match"
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