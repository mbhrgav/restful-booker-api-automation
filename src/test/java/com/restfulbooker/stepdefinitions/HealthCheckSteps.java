package com.restfulbooker.stepdefinitions;

import com.restfulbooker.config.ConfigManager;
import com.restfulbooker.context.ScenarioContext;
import com.restfulbooker.utils.RequestSpecificationFactory;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;

import static io.restassured.RestAssured.given;

public class HealthCheckSteps {

    private final ScenarioContext scenarioContext;

    public HealthCheckSteps(
            ScenarioContext scenarioContext
    ) {
        this.scenarioContext = scenarioContext;
    }

    @Given("the Restful Booker API base URL is configured")
    public void theRestfulBookerApiBaseUrlIsConfigured() {

        String baseUrl =
                ConfigManager.getProperty("base.url");

        Assert.assertNotNull(
                baseUrl,
                "Base URL must not be null"
        );

        Assert.assertFalse(
                baseUrl.isBlank(),
                "Base URL must not be blank"
        );
    }

    @When("I send a GET request to the health check endpoint")
    public void iSendAGetRequestToTheHealthCheckEndpoint() {

        Response response = given()
                .spec(
                        RequestSpecificationFactory
                                .getDefaultRequestSpecification()
                )
                .when()
                .get("/ping");

        scenarioContext.setResponse(response);
    }
}