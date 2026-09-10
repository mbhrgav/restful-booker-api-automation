package com.restfulbooker.utils;

import com.restfulbooker.config.ConfigManager;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public final class RequestSpecificationFactory {

    private RequestSpecificationFactory() {
        // Prevent object creation
    }

    public static RequestSpecification
    getDefaultRequestSpecification() {

        return new RequestSpecBuilder()
                .setBaseUri(
                        ConfigManager.getProperty("base.url")
                )
                .setContentType(ContentType.JSON)
                .addHeader(
                        "Accept",
                        "application/json"
                )
                .addHeader(
                        "User-Agent",
                        "RestfulBookerApiAutomation/1.0"
                )
                .addHeader(
                        "Connection",
                        "close"
                )
                .build();
    }
}