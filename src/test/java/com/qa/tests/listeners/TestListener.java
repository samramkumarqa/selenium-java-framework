package com.qa.tests.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.qa.framework.config.Config;
import com.qa.framework.driver.DriverFactory;
import com.qa.framework.models.PurchaseScenario;
import com.qa.framework.models.TestResultRecord;
import com.qa.framework.reporting.ExtentManager;
import com.qa.framework.reporting.ResultRecorder;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Hooks every test into the HTML report and the results log.
 * On failure it attaches a screenshot; failure callbacks run before @AfterMethod closes the browser.
 */
public class TestListener implements ITestListener, ISuiteListener {

    private static final String RUN_ID = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = ExtentManager.start(displayName(result), result.getMethod().getDescription());
        test.assignCategory(result.getMethod().getGroups());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentManager.current().pass("Passed");
        finish(result, "PASS", "");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = ExtentManager.current();
        test.fail(result.getThrowable());
        screenshotBase64().ifPresent(png ->
                test.fail("Screenshot at failure",
                        MediaEntityBuilder.createScreenCaptureFromBase64String(png).build()));
        finish(result, "FAIL", firstLine(result.getThrowable()));
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        // A skip can happen before onTestStart (e.g. a failed @BeforeMethod), so make sure a node exists.
        ExtentTest test = ExtentManager.current() != null
                ? ExtentManager.current()
                : ExtentManager.start(displayName(result), result.getMethod().getDescription());
        test.skip(result.getThrowable() == null ? "Skipped" : firstLine(result.getThrowable()));
        finish(result, "SKIP", firstLine(result.getThrowable()));
    }

    @Override
    public void onFinish(ISuite suite) {
        ExtentManager.flush();
        ResultRecorder.publish();
        System.out.println("[report] " + ExtentManager.REPORT_PATH + " | " + ResultRecorder.CSV_PATH);
    }

    private void finish(ITestResult result, String status, String message) {
        ResultRecorder.record(new TestResultRecord(
                RUN_ID,
                result.getTestClass().getRealClass().getSimpleName() + "." + result.getMethod().getMethodName(),
                dataId(result),
                Config.get("browser"),
                status,
                result.getEndMillis() - result.getStartMillis(),
                message));
        ExtentManager.end();
    }

    private static String displayName(ITestResult result) {
        String id = dataId(result);
        String method = result.getMethod().getMethodName();
        return id.isEmpty() ? method : method + " [" + id + "]";
    }

    /** A short label for data-driven runs: the scenario id, or the raw parameter values. */
    private static String dataId(ITestResult result) {
        Object[] params = result.getParameters();
        if (params == null || params.length == 0) {
            return "";
        }
        if (params[0] instanceof PurchaseScenario scenario) {
            return scenario.id();
        }
        return Arrays.stream(params).map(String::valueOf).collect(Collectors.joining(", "));
    }

    private static java.util.Optional<String> screenshotBase64() {
        if (!DriverFactory.hasDriver()) {
            return java.util.Optional.empty();
        }
        try {
            return java.util.Optional.of(((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.BASE64));
        } catch (RuntimeException e) {
            return java.util.Optional.empty();
        }
    }

    private static String firstLine(Throwable t) {
        if (t == null || t.getMessage() == null) {
            return t == null ? "" : t.getClass().getSimpleName();
        }
        return t.getMessage().lines().findFirst().orElse("");
    }
}
