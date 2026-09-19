package com.restfulbooker.clients;

import com.restfulbooker.models.AuthRequest;
import com.restfulbooker.utils.RequestSpecificationFactory;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthClient {

    private static final String AUTH_ENDPOINT = "/auth";

    public Response createToken(AuthRequest authRequest) {

        return given()
                .spec(RequestSpecificationFactory
                        .getDefaultRequestSpecification())
                .body(authRequest)
                .log().all()
                .when()
                .post(AUTH_ENDPOINT);
    }
}