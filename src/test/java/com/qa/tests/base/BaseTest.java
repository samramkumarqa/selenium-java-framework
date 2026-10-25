package com.qa.tests.base;

import com.qa.framework.api.AutomationExerciseApi;
import com.qa.framework.driver.DriverFactory;
import com.qa.framework.pages.HomePage;
import com.qa.framework.reporting.ExtentManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Starts a fresh browser before each test and always closes it afterwards.
 * Reporting is wired in automatically through TestListener (registered in META-INF/services).
 */
public abstract class BaseTest {

    protected final AutomationExerciseApi api = new AutomationExerciseApi();

    @BeforeMethod(alwaysRun = true)
    public void startBrowser() {
        DriverFactory.start();
    }

    @AfterMethod(alwaysRun = true)
    public void closeBrowser() {
        DriverFactory.quit();
    }

    protected WebDriver driver() {
        return DriverFactory.getDriver();
    }

    protected HomePage openHome() {
        return new HomePage(driver()).open();
    }

    /** Adds a step line to the HTML report. */
    protected void step(String description) {
        if (ExtentManager.current() != null) {
            ExtentManager.current().info(description);
        }
    }
}
