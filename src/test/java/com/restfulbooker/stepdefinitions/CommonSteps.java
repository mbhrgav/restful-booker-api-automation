package com.restfulbooker.stepdefinitions;

import io.cucumber.java.en.Then;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import com.restfulbooker.models.Booking;
import com.restfulbooker.clients.AuthClient;
import com.restfulbooker.config.ConfigManager;
import com.restfulbooker.context.ScenarioContext;
import com.restfulbooker.models.AuthRequest;
import io.restassured.response.Response;
import org.testng.Assert;
import com.restfulbooker.clients.CreateBookingClient;
import com.restfulbooker.utils.BookingDataMapper;
import com.restfulbooker.utils.ExcelReader;
import io.cucumber.java.en.Given;
import java.util.Map;
import java.util.Locale;

public class CommonSteps {

    private final ScenarioContext scenarioContext;
    private final AuthClient authClient;

    private final CreateBookingClient createBookingClient;

    public CommonSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
        this.authClient = new AuthClient();
        this.createBookingClient = new CreateBookingClient();
    }

    public String resolveToken(String authenticationType) {

        if (authenticationType == null) {
            throw new IllegalArgumentException(
                    "Authentication type must not be null"
            );
        }

        return switch (
                authenticationType.trim().toLowerCase(Locale.ROOT)
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

    public String generateValidToken() {

        AuthRequest authRequest = new AuthRequest(
                ConfigManager.getProperty("username"),
                ConfigManager.getProperty("password")
        );

        Response authResponse = authClient.createToken(authRequest);

        authResponse.then()
                .log()
                .ifValidationFails()
                .statusCode(200);

        String token = authResponse.jsonPath().getString("token");

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

    @Given("an existing booking is created using Excel test case {string}")
    public void existingBookingIsCreatedUsingExcelTestCase(
            String testCaseId
    ) {

        Map<String, String> excelData = ExcelReader.getRowData(
                ConfigManager.getProperty("excel.file.path"),
                ConfigManager.getProperty("excel.sheet.create"),
                testCaseId
        );

        Booking booking = BookingDataMapper.toBooking(excelData);

        scenarioContext.setExpectedBooking(booking);

        Response createResponse =
                createBookingClient.createBooking(booking);

        scenarioContext.setResponse(createResponse);

        // Register before assertions so cleanup can run after a failure.
        scenarioContext.registerCreatedBooking(createResponse);

        createResponse.then()
                .log()
                .ifValidationFails()
                .statusCode(200);

        Integer bookingId = createResponse.jsonPath()
                .getObject("bookingid", Integer.class);

        Assert.assertNotNull(
                bookingId,
                "Created booking ID must not be null"
        );

        Assert.assertTrue(
                bookingId > 0,
                "Created booking ID must be greater than zero"
        );

        scenarioContext.setBookingId(bookingId);
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatusCode) {

        Response response = scenarioContext.getResponse();

        Assert.assertNotNull(
                response,
                "API response was not available"
        );

        response.then()
                .log()
                .ifValidationFails()
                .statusCode(expectedStatusCode);
    }

    @Then("the response should match the {string} JSON schema")
    public void responseShouldMatchJsonSchema(String schemaFileName) {

        Response response = scenarioContext.getResponse();

        Assert.assertNotNull(
                response,
                "API response was not available for schema validation"
        );

        response.then()
                .body(
                        matchesJsonSchemaInClasspath(
                                "schemas/" + schemaFileName
                        )
                );
    }

}