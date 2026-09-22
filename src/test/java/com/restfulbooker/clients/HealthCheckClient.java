package com.restfulbooker.clients;

import com.restfulbooker.utils.RequestSpecificationFactory;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class HealthCheckClient {

    private static final String HEALTH_ENDPOINT = "/ping";

    public Response checkHealth() {

        return given()
                .spec(
                        RequestSpecificationFactory
                                .getDefaultRequestSpecification()
                )
                .when()
                .get(HEALTH_ENDPOINT);
    }
}
