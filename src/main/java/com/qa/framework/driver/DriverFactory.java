package com.qa.framework.driver;

import com.qa.framework.config.Config;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * Creates one WebDriver per test thread so tests can run in parallel safely.
 * Driver binaries are resolved automatically by Selenium Manager, so none are committed to the repo.
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    /**
     * The practice site serves heavy ads, including full-page "vignette" interstitials
     * that cover the page mid-test. Chromium browsers simply fail to resolve the ad hosts.
     */
    private static final String BLOCK_AD_HOSTS = "--host-resolver-rules="
            + "MAP *.googlesyndication.com ~NOTFOUND, "
            + "MAP *.doubleclick.net ~NOTFOUND, "
            + "MAP *.googleadservices.com ~NOTFOUND, "
            + "MAP *.google-analytics.com ~NOTFOUND, "
            + "MAP *.adtrafficquality.google ~NOTFOUND, "
            + "MAP fundingchoicesmessages.google.com ~NOTFOUND";

    private DriverFactory() {
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver not started for this thread. Call DriverFactory.start() first.");
        }
        return driver;
    }

    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    public static WebDriver start() {
        String browser = Config.get("browser").toLowerCase();
        boolean headless = Config.getBool("headless");

        WebDriver driver = switch (browser) {
            case "chrome" -> {
                ChromeOptions options = new ChromeOptions();
                applyChromiumDefaults(options, headless);
                yield new ChromeDriver(options);
            }
            case "edge" -> {
                EdgeOptions options = new EdgeOptions();
                applyChromiumDefaults(options, headless);
                yield new EdgeDriver(options);
            }
            case "firefox" -> {
                FirefoxOptions options = new FirefoxOptions();
                options.setPageLoadStrategy(PageLoadStrategy.EAGER);
                if (headless) {
                    options.addArguments("-headless");
                }
                yield new FirefoxDriver(options);
            }
            default -> throw new IllegalArgumentException(
                    "Unsupported browser '" + browser + "'. Use chrome, edge or firefox.");
        };

        // A fixed size behaves the same headed and headless (maximize does nothing in headless mode).
        driver.manage().window().setSize(new Dimension(1440, 900));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(Config.getInt("page.load.timeout.seconds")));
        DRIVER.set(driver);
        return driver;
    }

    public static void quit() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER.remove();
            }
        }
    }

    private static void applyChromiumDefaults(ChromiumOptions<?> options, boolean headless) {
        // EAGER: continue once the DOM is ready instead of waiting for every ad and tracker to load.
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.addArguments("--disable-notifications", "--disable-search-engine-choice-screen", BLOCK_AD_HOSTS);
        if (headless) {
            options.addArguments("--headless=new");
        }
    }
}
