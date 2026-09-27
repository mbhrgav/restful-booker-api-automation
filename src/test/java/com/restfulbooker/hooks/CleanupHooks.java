package com.restfulbooker.hooks;

import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;
import com.restfulbooker.clients.DeleteBookingClient;
import com.restfulbooker.clients.GetBookingClient;
import com.restfulbooker.context.ScenarioContext;

import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CleanupHooks {

    private final ScenarioContext scenarioContext;

    private final DeleteBookingClient deleteBookingClient =
            new DeleteBookingClient();

    private final GetBookingClient getBookingClient =
            new GetBookingClient();

    public CleanupHooks(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @After(order = -100)
    public void cleanupCreatedBookings(Scenario scenario) {

        System.out.println(
                "[CLEANUP START] Scenario: " + scenario.getName()
                        + " | Registered booking IDs: "
                        + scenarioContext.getCreatedBookingIds()
        );

        scenario.log("Cleanup hook started: " + scenario.getName());

        Set<Integer> bookingIds =
                scenarioContext.getCreatedBookingIds();

        if (bookingIds.isEmpty()) {
            logCleanup(
                    scenario,
                    "Cleanup skipped: no created booking IDs were registered."
            );
            return;
        }

        boolean alreadyFailed = scenario.isFailed();

        List<String> failures = new ArrayList<>();

        for (Integer bookingId : bookingIds) {

            try {
                Response deleteResponse =
                        deleteBookingClient
                                .deleteWithBasicAuth(bookingId);

                int deleteStatus = deleteResponse.statusCode();

                /*
                 * Accept successful deletion or a possible
                 * already-deleted response.
                 *
                 * Restful Booker can return 405 for a missing ID.
                 * We must verify absence before accepting it.
                 */
                boolean shouldVerify =
                        (deleteStatus >= 200 && deleteStatus < 300)
                                || deleteStatus == 404
                                || deleteStatus == 405;

                if (!shouldVerify) {

                    String message =
                            "Cleanup failed for booking "
                                    + bookingId
                                    + ": DELETE returned "
                                    + deleteStatus;

                    failures.add(message);
                    logCleanup(scenario, message);
                    continue;
                }

                Response verificationResponse =
                        getBookingClient.getBookingById(bookingId);

                int getStatus = verificationResponse.statusCode();

                if (getStatus == 404) {

                    scenarioContext.removeCreatedBookingId(
                            bookingId
                    );

                    logCleanup(
                            scenario,
                            "Cleanup confirmed: booking "
                                    + bookingId
                                    + " is absent. DELETE status: "
                                    + deleteStatus
                                    + "; GET status: 404."
                    );

                } else {

                    String message =
                            "Cleanup could not confirm deletion of booking "
                                    + bookingId
                                    + ": DELETE returned "
                                    + deleteStatus
                                    + "; GET returned "
                                    + getStatus;

                    failures.add(message);
                    logCleanup(scenario, message);
                }

            } catch (Exception exception) {

                String message =
                        "Cleanup error for booking "
                                + bookingId
                                + ": "
                                + exception.getClass().getSimpleName();

                failures.add(message);
                logCleanup(scenario, message);
            }
        }

        /*
         * Try every registered booking before reporting failure.
         *
         * If the scenario already failed, retain that original
         * failure and keep cleanup errors in the logs.
         */
        if (!failures.isEmpty() && !alreadyFailed) {
            throw new AssertionError(
                    "Booking cleanup failed: "
                            + String.join("; ", failures)
            );
        }
    }

    private void logCleanup(Scenario scenario, String message) {
        scenario.log(message);
        System.out.println("[CLEANUP] " + message);
    }
}
