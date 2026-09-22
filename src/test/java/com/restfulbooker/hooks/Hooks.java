package com.restfulbooker.hooks;

import com.restfulbooker.context.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.restassured.response.Response;
import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;

public class Hooks {

    private final ScenarioContext scenarioContext;

    public Hooks(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @After
    public void attachApiResponseToReport() {

        Response response = scenarioContext.getResponse();

        if (response == null) {
            return;
        }

        String responseBody = response.asPrettyString();

        if (responseBody == null || responseBody.isBlank()) {
            responseBody = "Response body was empty";
        } else {
            responseBody = maskSensitiveData(responseBody);
        }

        ExtentCucumberAdapter.addTestStepLog(
                "<b>Response Status Code:</b> "
                        + response.getStatusCode()
                        + "<br><br>"
                        + "<b>API Response:</b>"
                        + "<pre>"
                        + escapeHtml(responseBody)
                        + "</pre>"
        );
    }

    private String maskSensitiveData(String responseBody) {

        return responseBody.replaceAll(
                "(?i)(\"(?:token|password)\"\\s*:\\s*\")[^\"]*(\")",
                "$1********$2"
        );
    }

    private String escapeHtml(String value) {

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}