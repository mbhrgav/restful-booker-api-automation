package com.restfulbooker.clients;

import com.restfulbooker.config.ConfigManager;
import com.restfulbooker.models.Booking;
import com.restfulbooker.utils.RequestSpecificationFactory;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class UpdateBookingClient {

    private static final String BOOKING_ENDPOINT =
            ConfigManager.getProperty("endpoint.booking")
                    + "/{bookingId}";

    public Response updateBooking(
            Object bookingId,
            String token,
            Booking booking
    ) {

        return sendUpdateRequest(
                bookingId,
                token,
                booking
        );
    }

    public Response updateBooking(
            Object bookingId,
            String token,
            Map<String, Object> booking
    ) {

        return sendUpdateRequest(
                bookingId,
                token,
                booking
        );
    }

    private Response sendUpdateRequest(
            Object bookingId,
            String token,
            Object requestBody
    ) {

        RequestSpecification request = given()
                .spec(
                        RequestSpecificationFactory
                                .getDefaultRequestSpecification()
                )
                .pathParam("bookingId", bookingId)
                .body(requestBody);

        if (token != null && !token.isBlank()) {
            request.header(
                    "Cookie",
                    "token=" + token
            );
        }

        return request
                .when()
                .put(BOOKING_ENDPOINT);
    }
}