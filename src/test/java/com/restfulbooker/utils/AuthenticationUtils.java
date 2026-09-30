package com.restfulbooker.utils;

import com.restfulbooker.clients.AuthClient;
import com.restfulbooker.config.ConfigManager;
import com.restfulbooker.context.ScenarioContext;
import com.restfulbooker.models.AuthRequest;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.Locale;

public class AuthenticationUtils {

    private final ScenarioContext scenarioContext;
    private final AuthClient authClient;

    public AuthenticationUtils(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
        this.authClient = new AuthClient();
    }

    public String resolveToken(String authenticationType) {

        if (authenticationType == null) {
            throw new IllegalArgumentException(
                    "Authentication type must not be null"
            );
        }

        return switch (
                authenticationType.trim().toLowerCase(Locale.ROOT)
                ) {
            case "valid" -> generateValidToken();
            case "missing" -> null;
            case "invalid" -> "invalid-token";
            default -> throw new IllegalArgumentException(
                    "Unsupported authentication type: "
                            + authenticationType
            );
        };
    }

    public String generateValidToken() {

        AuthRequest authRequest = new AuthRequest(
                ConfigManager.getProperty("username"),
                ConfigManager.getProperty("password")
        );

        Response authResponse = authClient.createToken(authRequest);

        authResponse.then()
                .log()
                .ifValidationFails()
                .statusCode(200);

        String token = authResponse.jsonPath().getString("token");

        Assert.assertNotNull(
                token,
                "Authentication token must not be null"
        );

        Assert.assertFalse(
                token.isBlank(),
                "Authentication token must not be blank"
        );

        scenarioContext.setToken(token);

        return token;
    }
}
