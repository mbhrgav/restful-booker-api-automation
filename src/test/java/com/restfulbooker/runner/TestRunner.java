package com.restfulbooker.runner;

import com.restfulbooker.config.ConfigManager;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import com.restfulbooker.clients.HealthCheckClient;
import io.restassured.response.Response;
import org.testng.Assert;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.service.ExtentService;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {
                "com.restfulbooker.stepdefinitions",
                "com.restfulbooker.hooks"
        },
        plugin = {
                "pretty",
                "json:target/cucumber-report.json",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
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

        ExtentTest healthCheckReport =
                ExtentService.getInstance()
                        .createTest(
                                "Pre-Execution API Health Check"
                        );

        System.out.println(
                "Running pre-execution API health check..."
        );

        Response response;

        try {
            response = new HealthCheckClient()
                    .checkHealth();

        } catch (Exception exception) {

            String failureMessage =
                    "Restful Booker API could not be reached. Reason: "
                            + exception.getMessage();

            healthCheckReport.fail(failureMessage);

            ExtentService.flush();

            Assert.fail(
                    failureMessage,
                    exception
            );

            return;
        }
        int expectedStatusCode = Integer.parseInt(
                ConfigManager.getProperty(
                        "health.expected.status"
                )
        );
        int actualStatusCode =
                response.getStatusCode();

        if (actualStatusCode != expectedStatusCode) {

            String failureMessage =
                    "Health check failed. Expected status code: "
                            + expectedStatusCode
                            + ", but actual status code was: "
                            + actualStatusCode
                            + ". Response body: "
                            + response.asString();

            healthCheckReport.fail(failureMessage);

            ExtentService.flush();

            Assert.fail(failureMessage);
        }

        String successMessage =
                "Health check passed. API returned status code: "
                        + actualStatusCode;

        healthCheckReport.pass(successMessage);

        ExtentService.flush();

        System.out.println(successMessage);
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