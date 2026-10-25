package com.qa.framework.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.qa.framework.config.Config;

/** One HTML report per run (target/extent-report/index.html), one ExtentTest per test thread. */
public final class ExtentManager {

    public static final String REPORT_PATH = "target/extent-report/index.html";

    private static ExtentReports reports;
    private static final ThreadLocal<ExtentTest> CURRENT = new ThreadLocal<>();

    private ExtentManager() {
    }

    public static synchronized ExtentReports reports() {
        if (reports == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_PATH);
            spark.config().setDocumentTitle("Selenium Test Report");
            spark.config().setReportName("AutomationExercise regression");
            spark.config().setTheme(Theme.STANDARD);

            reports = new ExtentReports();
            reports.attachReporter(spark);
            reports.setSystemInfo("Browser", Config.get("browser"));
            reports.setSystemInfo("Headless", Config.get("headless"));
            reports.setSystemInfo("Base URL", Config.baseUrl());
        }
        return reports;
    }

    public static ExtentTest start(String name, String description) {
        ExtentTest test = reports().createTest(name, description);
        CURRENT.set(test);
        return test;
    }

    /** The report node for the running test, for logging steps from tests. May be null outside a test. */
    public static ExtentTest current() {
        return CURRENT.get();
    }

    public static void end() {
        CURRENT.remove();
    }

    public static synchronized void flush() {
        if (reports != null) {
            reports.flush();
        }
    }
}
