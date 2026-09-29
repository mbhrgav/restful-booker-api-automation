package com.restfulbooker.stepdefinitions;

import com.restfulbooker.clients.UpdateBookingClient;
import com.restfulbooker.config.ConfigManager;
import com.restfulbooker.context.ScenarioContext;
import com.restfulbooker.models.Booking;
import com.restfulbooker.utils.BookingDataMapper;
import com.restfulbooker.utils.ExcelReader;
import com.restfulbooker.utils.BookingAssertions;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.Map;

public class UpdateBookingSteps {

    private static final String EXCEL_FILE_PATH =
            ConfigManager.getProperty("excel.file.path");

    private static final String UPDATE_BOOKING_SHEET =
            ConfigManager.getProperty("excel.sheet.update");

    private final ScenarioContext scenarioContext;
    private final UpdateBookingClient updateBookingClient;
    private final CommonSteps commonSteps;

    private Booking updateBookingRequest;
    private Map<String, Object> invalidUpdateRequest;

    public UpdateBookingSteps(
            ScenarioContext scenarioContext, CommonSteps commonSteps
    ) {
        this.scenarioContext = scenarioContext;
        this.updateBookingClient =
                new UpdateBookingClient();
        this.commonSteps = commonSteps;
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

        String token =  commonSteps.resolveToken(authenticationType);

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

    String token = commonSteps.generateValidToken();

    Response response = sendUpdateRequest(bookingId, token);

    if (response.statusCode() == 403) {
        String refreshedToken = commonSteps.generateValidToken();
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
                "Expected update data was not available for " + testCaseId
        );

        BookingAssertions.validateBookingDetails(
                scenarioContext.getResponse(),
                updateBookingRequest,
                ""
        );
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

}