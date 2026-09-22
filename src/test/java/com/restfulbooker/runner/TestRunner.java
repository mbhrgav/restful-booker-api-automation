package com.restfulbooker.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import com.restfulbooker.clients.HealthCheckClient;
import io.restassured.response.Response;
import org.testng.Assert;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {
                "com.restfulbooker.stepdefinitions",
                "com.restfulbooker.hooks"
        },
        plugin = {
                "pretty",
                "html:target/cucumber-report.html",
                "json:target/cucumber-report.json"
        },
        monochrome = false
)
public class TestRunner extends AbstractTestNGCucumberTests {

    @BeforeSuite(alwaysRun = true)
    @Parameters("cucumber.filter.tags")
    public void configureTestSuite(
            @Optional("") String cucumberTags) {

        configureCucumberTags(cucumberTags);
        verifyApiIsAvailable();
    }

    private void verifyApiIsAvailable() {

        System.out.println(
                "Running pre-execution API health check..."
        );

        Response response;

        try {
            response = new HealthCheckClient()
                    .checkHealth();
        } catch (Exception exception) {

            Assert.fail(
                    "Test execution stopped because Restful Booker API "
                            + "could not be reached. Reason: "
                            + exception.getMessage(),
                    exception
            );

            return;
        }

        Assert.assertEquals(
                response.getStatusCode(),
                200,
                "Test execution stopped because the health-check API "
                        + "did not return status code 201. Response: "
                        + response.asString()
        );

        System.out.println(
                "Health check passed. Starting test execution..."
        );
    }

    public void configureCucumberTags(
            @Optional("") String cucumberTags) {

        if (cucumberTags != null && !cucumberTags.isBlank()) {
            System.setProperty(
                    "cucumber.filter.tags",
                    cucumberTags
            );

            System.out.println(
                    "Executing Cucumber scenarios with tags: "
                            + cucumberTags
            );
        }
    }
}