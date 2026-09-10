package com.restfulbooker.stepdefinitions;

import com.restfulbooker.clients.AuthClient;
import com.restfulbooker.config.ConfigManager;
import com.restfulbooker.context.ScenarioContext;
import com.restfulbooker.models.AuthRequest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;

public class AuthenticationSteps {

    private final ScenarioContext scenarioContext;
    private final AuthClient authClient;

    private AuthRequest authRequest;

    public AuthenticationSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
        this.authClient = new AuthClient();
    }

    @Given("I have valid authentication credentials")
    public void iHaveValidAuthenticationCredentials() {

        authRequest = new AuthRequest(
                ConfigManager.getProperty("username"),
                ConfigManager.getProperty("password")
        );
    }

    @Given("I have username {string} and password {string}")
    public void iHaveUsernameAndPassword(
            String username,
            String password
    ) {
        authRequest = new AuthRequest(username, password);
    }

    @When("I send a POST request to the authentication endpoint")
    public void iSendAPostRequestToTheAuthenticationEndpoint() {

        Assert.assertNotNull(
                authRequest,
                "Authentication request was not prepared"
        );

        Response response = authClient.createToken(authRequest);
        scenarioContext.setResponse(response);
    }

    @Then("the authentication response should contain a non-empty token")
    public void authenticationResponseShouldContainNonEmptyToken() {

        Response response = scenarioContext.getResponse();

        Assert.assertNotNull(
                response,
                "Authentication response was not available"
        );

        String token = response.jsonPath().getString("token");

        Assert.assertNotNull(
                token,
                "Authentication token must not be null"
        );

        Assert.assertFalse(
                token.isBlank(),
                "Authentication token must not be blank"
        );

        scenarioContext.setToken(token);
    }

    @Then("the authentication response reason should be {string}")
    public void authenticationResponseReasonShouldBe(
            String expectedReason
    ) {

        Response response = scenarioContext.getResponse();

        Assert.assertNotNull(
                response,
                "Authentication response was not available"
        );

        String actualReason =
                response.jsonPath().getString("reason");

        Assert.assertEquals(
                actualReason,
                expectedReason,
                "Authentication failure reason did not match"
        );
    }
}