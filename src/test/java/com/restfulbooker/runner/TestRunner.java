package com.restfulbooker.runner;

import com.restfulbooker.hooks.SuiteHooks;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.DataProvider;

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
        }
)
public class TestRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }

    @BeforeSuite(alwaysRun = true)
    @Parameters("cucumber.filter.tags")
    public void configureTestSuite(
            @Optional("") String cucumberTags) {

        SuiteHooks.configureCucumberTags(cucumberTags);
    }

}