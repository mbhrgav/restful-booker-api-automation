package com.restfulbooker.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {
                "com.restfulbooker.stepdefinitions",
                "com.restfulbooker.hooks"
        },
        plugin = {
                "pretty",
                "json:target/cucumber-report.json",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        },
        monochrome = false
)
public class TestRunner extends AbstractTestNGCucumberTests {

    @BeforeSuite(alwaysRun = true)
    @Parameters("cucumber.filter.tags")
    public void configureCucumberTags(
            @Optional("") String cucumberTags) {

        if (cucumberTags != null && !cucumberTags.isBlank()) {
            System.setProperty(
                    "cucumber.filter.tags",
                    cucumberTags
            );

            System.out.println(
                    "Executing Cucumber scenarios with tags: "
                            + cucumberTags
            );
        }
    }
}