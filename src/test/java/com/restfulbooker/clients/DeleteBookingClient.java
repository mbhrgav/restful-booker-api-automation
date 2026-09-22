package com.restfulbooker.clients;

import com.restfulbooker.config.ConfigManager;
import com.restfulbooker.utils.RequestSpecificationFactory;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;


public class DeleteBookingClient {

    private static final String BOOKING_ENDPOINT =
            ConfigManager.getProperty("endpoint.booking")
                    + "/{bookingId}";

    public Response deleteBooking(
            Object bookingId,
            String token
    ) {

        RequestSpecification request = given()
                .spec(
                        RequestSpecificationFactory
                                .getDefaultRequestSpecification()
                )
                .pathParam("bookingId", bookingId);

        if (token != null && !token.isBlank()) {
            request.header(
                    "Cookie",
                    "token=" + token
            );
        }

        return request
                .when()
                .delete(BOOKING_ENDPOINT);
    }

    public Response deleteWithBasicAuth (Object bookingId)
    {
        RequestSpecification request = given()
                .spec(RequestSpecificationFactory.getDefaultRequestSpecification())
                .pathParams("bookingId", bookingId)
                .header("Authorization", ConfigManager.getProperty("basicAuth"));

        return request.when().delete(BOOKING_ENDPOINT);
    }
}