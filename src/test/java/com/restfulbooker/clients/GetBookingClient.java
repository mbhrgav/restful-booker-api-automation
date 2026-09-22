package com.restfulbooker.clients;

import com.restfulbooker.config.ConfigManager;
import com.restfulbooker.utils.RequestSpecificationFactory;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class GetBookingClient {

    private static final String BOOKING_ENDPOINT =
            ConfigManager.getProperty("endpoint.booking");

    public Response getAllBookingIds() {

        return given()
                .spec(
                        RequestSpecificationFactory
                                .getDefaultRequestSpecification()
                )
                .when()
                .get(BOOKING_ENDPOINT);
    }

    public Response getBookingIdsWithFilters(
            Map<String, ?> filters
    ) {

        return given()
                .spec(
                        RequestSpecificationFactory
                                .getDefaultRequestSpecification()
                )
                .queryParams(filters)
                .when()
                .get(BOOKING_ENDPOINT);
    }

    public Response getBookingById(int bookingId) {

        return getBookingById(
                String.valueOf(bookingId)
        );
    }

    public Response getBookingById(String bookingId) {

        return given()
                .spec(
                        RequestSpecificationFactory
                                .getDefaultRequestSpecification()
                )
                .pathParam("bookingId", bookingId)
                .when()
                .get(BOOKING_ENDPOINT + "/{bookingId}");
    }
}