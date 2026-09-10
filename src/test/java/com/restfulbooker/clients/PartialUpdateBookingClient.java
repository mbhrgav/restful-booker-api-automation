package com.restfulbooker.clients;

import com.restfulbooker.utils.RequestSpecificationFactory;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class PartialUpdateBookingClient {

    private static final String BOOKING_ENDPOINT =
            "/booking/{bookingId}";

    public Response partiallyUpdateBooking(
            Object bookingId,
            String token,
            Map<String, Object> requestBody
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
                .patch(BOOKING_ENDPOINT);
    }
}