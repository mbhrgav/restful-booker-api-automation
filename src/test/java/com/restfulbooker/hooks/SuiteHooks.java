package com.restfulbooker.hooks;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.service.ExtentService;
import com.restfulbooker.clients.HealthCheckClient;
import com.restfulbooker.config.ConfigManager;
import io.cucumber.java.BeforeAll;
import io.restassured.response.Response;

public class SuiteHooks {

    @BeforeAll
    public static void verifyApiIsAvailable() {

        ExtentTest healthReport = ExtentService.getInstance()
                .createTest("Pre-Execution API Health Check");

        System.out.println(
                "[HEALTH CHECK] Checking API before scenario execution..."
        );

        try {
            int expectedStatus = Integer.parseInt(
                    ConfigManager.getProperty("health.expected.status")
            );

            Response response = new HealthCheckClient().checkHealth();

            int actualStatus = response.statusCode();

            if (actualStatus != expectedStatus) {
                throw new IllegalStateException(
                        "Health check failed. Expected status: "
                                + expectedStatus
                                + ", actual status: "
                                + actualStatus
                );
            }

            String message =
                    "Health check passed. API returned status: "
                            + actualStatus;

            healthReport.pass(message);
            System.out.println("[HEALTH CHECK] " + message);

        } catch (Exception exception) {

            String message =
                    "Pre-execution health check failed: "
                            + exception.getMessage();

            healthReport.fail(message);
            System.err.println("[HEALTH CHECK] " + message);

            throw new IllegalStateException(
                    "Scenario execution stopped because the "
                            + "pre-execution health check failed.",
                    exception
            );

        } finally {
            // Save the health-check result even when startup fails.
            ExtentService.flush();
        }
    }

    public static void configureCucumberTags(String cucumberTags) {

        if (cucumberTags != null && !cucumberTags.isBlank()) {

            System.setProperty(
                    "cucumber.filter.tags",
                    cucumberTags.trim()
            );

            System.out.println(
                    "Executing Cucumber scenarios with tags: "
                            + cucumberTags.trim()
            );
        }
    }
}
