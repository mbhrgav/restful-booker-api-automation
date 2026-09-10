package com.restfulbooker.clients;

import com.restfulbooker.models.Booking;
import com.restfulbooker.utils.RequestSpecificationFactory;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class CreateBookingClient {

    private static final String BOOKING_ENDPOINT =
            "/booking";

    public Response createBooking(Booking booking) {

        return given()
                .spec(
                        RequestSpecificationFactory
                                .getDefaultRequestSpecification()
                )
                .body(booking)
                .when()
                .post(BOOKING_ENDPOINT);
    }

    public Response createBooking(
            Map<String, Object> bookingRequest
    ) {

        return given()
                .spec(
                        RequestSpecificationFactory
                                .getDefaultRequestSpecification()
                )
                .body(bookingRequest)
                .when()
                .post(BOOKING_ENDPOINT);
    }

    public Response createBookingWithoutBody() {

        return given()
                .spec(
                        RequestSpecificationFactory
                                .getDefaultRequestSpecification()
                )
                .when()
                .post(BOOKING_ENDPOINT);
    }
}