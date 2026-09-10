package com.restfulbooker.stepdefinitions;

import com.restfulbooker.context.ScenarioContext;
import io.cucumber.java.en.Then;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class SchemaValidationSteps {

    private final ScenarioContext scenarioContext;

    public SchemaValidationSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Then("the response should match the {string} JSON schema")
    public void responseShouldMatchJsonSchema(String schemaFileName) {

        if (scenarioContext.getResponse() == null) {
            throw new IllegalStateException(
                    "API response was not available for schema validation"
            );
        }

        scenarioContext.getResponse()
                .then()
                .body(matchesJsonSchemaInClasspath(
                        "schemas/" + schemaFileName
                ));
    }
}