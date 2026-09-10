package com.restfulbooker.hooks;

import com.restfulbooker.context.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.restassured.response.Response;

public class Hooks {

    private final ScenarioContext scenarioContext;

    public Hooks(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @After
    public void attachApiResponseToReport(Scenario scenario) {

        Response response = scenarioContext.getResponse();

        if (response == null) {
            return;
        }

        scenario.log("Response status code: " + response.getStatusCode());

        String responseBody = response.asString();

        if (responseBody == null || responseBody.isBlank()) {
            responseBody = "Response body was empty";
        }

        scenario.attach(
                responseBody,
                "text/plain",
                "API Response"
        );
    }
}