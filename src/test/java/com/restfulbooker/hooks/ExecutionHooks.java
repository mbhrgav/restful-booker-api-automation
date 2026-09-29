package com.restfulbooker.hooks;

import com.restfulbooker.context.ScenarioContext;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;

import java.time.Instant;

public class ExecutionHooks {

    private final ScenarioContext scenarioContext;

    public ExecutionHooks(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Before(order = -1000)
    public void logScenarioStart(Scenario scenario) {
        logExecution(scenario, "START");
    }

    // Runs before the response-report and cleanup hooks.
    @After(order = 1000)
    public void logScenarioResult(Scenario scenario) {
        logExecution(scenario, "STEPS FINISHED");
    }

    private void logExecution(Scenario scenario, String stage) {

        String message =
                "[" + stage + "]"
                        + " Time=" + Instant.now()
                        + " Thread=" + Thread.currentThread().getId()
                        + " Scenario=" + scenario.getName()
                        + " Location=" + scenario.getUri()
                        + ":" + scenario.getLine()
                        + " BookingId=" + scenarioContext.getBookingId()
                        + " RegisteredIds="
                        + scenarioContext.getCreatedBookingIds();

        System.out.println(message);
        scenario.log(message);
    }
}
