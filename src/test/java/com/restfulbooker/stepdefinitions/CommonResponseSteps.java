package com.restfulbooker.stepdefinitions;

import com.restfulbooker.context.ScenarioContext;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.testng.Assert;

public class CommonResponseSteps {

    private final ScenarioContext scenarioContext;

    public CommonResponseSteps(
            ScenarioContext scenarioContext
    ) {
        this.scenarioContext = scenarioContext;
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(
            int expectedStatusCode
    ) {

        Response response =
                scenarioContext.getResponse();

        Assert.assertNotNull(
                response,
                "API response was not available"
        );

        response.then()
                .log()
                .ifValidationFails()
                .statusCode(expectedStatusCode);
    }
}