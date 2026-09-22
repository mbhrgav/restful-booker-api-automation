package com.restfulbooker.stepdefinitions;

import com.restfulbooker.clients.CreateBookingClient;
import com.restfulbooker.config.ConfigManager;
import com.restfulbooker.context.ScenarioContext;
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

public class CreateBookingSteps {

    private static final String EXCEL_FILE_PATH =
            ConfigManager.getProperty("excel.file.path");

    private static final String CREATE_BOOKING_SHEET =
            ConfigManager.getProperty("excel.sheet.create");

    private final ScenarioContext scenarioContext;
    private final CreateBookingClient createBookingClient;

    private Booking bookingRequest;
    private Map<String, Object> invalidBookingRequest;
    private boolean emptyRequestBody;

    public CreateBookingSteps(
            ScenarioContext scenarioContext
    ) {
        this.scenarioContext = scenarioContext;
        this.createBookingClient =
                new CreateBookingClient();
    }

    @Given("I prepare a booking using Excel test case {string}")
    public void iPrepareABookingUsingExcelTestCase(
            String testCaseId
    ) {

        Map<String, String> excelData =
                ExcelReader.getRowData(
                        EXCEL_FILE_PATH,
                        CREATE_BOOKING_SHEET,
                        testCaseId
                );

        bookingRequest =
                BookingDataMapper.toBooking(excelData);

        invalidBookingRequest = null;
        emptyRequestBody = false;
    }

    @Given(
        "I prepare booking using Excel test case {string} after removing {string}"
    )
    public void iPrepareBookingAfterRemovingField(
            String testCaseId,
            String missingField
    ) {

        Map<String, String> excelData =
                ExcelReader.getRowData(
                        EXCEL_FILE_PATH,
                        CREATE_BOOKING_SHEET,
                        testCaseId
                );

        invalidBookingRequest =
                BookingDataMapper.toRequestMap(excelData);

        BookingDataMapper.removeField(
                invalidBookingRequest,
                missingField
        );

        bookingRequest = null;
        emptyRequestBody = false;
    }

    @Given("I prepare an empty booking request")
    public void iPrepareAnEmptyBookingRequest() {

        bookingRequest = null;
        invalidBookingRequest = null;
        emptyRequestBody = true;
    }

    @When("I send a POST request to create the booking")
    public void iSendAPostRequestToCreateTheBooking() {

        Response response;

        if (emptyRequestBody) {
            response = createBookingClient
                    .createBookingWithoutBody();

        } else if (bookingRequest != null) {
            response = createBookingClient
                    .createBooking(bookingRequest);

        } else if (invalidBookingRequest != null) {
            response = createBookingClient
                    .createBooking(invalidBookingRequest);

        } else {
            throw new IllegalStateException(
                    "Create booking request was not prepared"
            );
        }

        scenarioContext.setResponse(response);
    }

    @Then("the create booking response should contain a valid booking ID")
    public void createBookingResponseShouldContainValidBookingId() {

        Response response =
                scenarioContext.getResponse();

        Assert.assertNotNull(
                response,
                "Create booking response was not available"
        );

        Integer bookingId =
                response.jsonPath().getInt("bookingid");

        Assert.assertNotNull(
                bookingId,
                "Booking ID must not be null"
        );

        Assert.assertTrue(
                bookingId > 0,
                "Booking ID must be greater than zero"
        );

        scenarioContext.setBookingId(bookingId);
    }

    @Then("the created booking details should match the request")
    public void createdBookingDetailsShouldMatchTheRequest() {

        Response response =
                scenarioContext.getResponse();

        Assert.assertNotNull(
                response,
                "Create booking response was not available"
        );

        Assert.assertNotNull(
                bookingRequest,
                "Expected booking data was not available"
        );

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertEquals(
                response.jsonPath()
                        .getString("booking.firstname"),
                bookingRequest.getFirstname(),
                "Firstname did not match"
        );

        softAssert.assertEquals(
                response.jsonPath()
                        .getString("booking.lastname"),
                bookingRequest.getLastname(),
                "Lastname did not match"
        );

        softAssert.assertEquals(
                response.jsonPath()
                        .getInt("booking.totalprice"),
                bookingRequest.getTotalprice(),
                "Total price did not match"
        );

        softAssert.assertEquals(
                response.jsonPath()
                        .getBoolean("booking.depositpaid"),
                bookingRequest.isDepositpaid(),
                "Deposit-paid value did not match"
        );

        softAssert.assertEquals(
                response.jsonPath()
                        .getString(
                                "booking.bookingdates.checkin"
                        ),
                bookingRequest
                        .getBookingdates()
                        .getCheckin(),
                "Check-in date did not match"
        );

        softAssert.assertEquals(
                response.jsonPath()
                        .getString(
                                "booking.bookingdates.checkout"
                        ),
                bookingRequest
                        .getBookingdates()
                        .getCheckout(),
                "Check-out date did not match"
        );

        softAssert.assertEquals(
                response.jsonPath()
                        .getString("booking.additionalneeds"),
                bookingRequest.getAdditionalneeds(),
                "Additional needs did not match"
        );

        softAssert.assertAll();
    }
}