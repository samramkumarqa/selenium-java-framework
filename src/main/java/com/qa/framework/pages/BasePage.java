package com.qa.framework.pages;

import com.qa.framework.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Shared, wait-aware actions for every page object.
 * Every interaction waits for the element to be ready, so tests never need Thread.sleep.
 */
public abstract class BasePage {

    private static final String VIGNETTE_FRAGMENT = "#google_vignette";

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(Config.getInt("timeout.seconds")));
    }

    protected void openPath(String path) {
        driver.get(Config.baseUrl() + path);
        dismissAdInterstitial();
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected List<WebElement> allVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected void click(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        scrollIntoView(element);
        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            // A sticky banner or ad iframe is on top; a JS click still exercises the real handler.
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
        dismissAdInterstitial();
    }

    protected void type(By locator, String text) {
        WebElement element = visible(locator);
        scrollIntoView(element);
        element.clear();
        element.sendKeys(text);
    }

    protected void selectByValue(By locator, String value) {
        new Select(visible(locator)).selectByValue(value);
    }

    protected void selectByVisibleText(By locator, String text) {
        new Select(visible(locator)).selectByVisibleText(text);
    }

    protected void check(By locator) {
        WebElement element = visible(locator);
        if (!element.isSelected()) {
            click(locator);
        }
    }

    protected String text(By locator) {
        return visible(locator).getText().trim();
    }

    protected boolean isDisplayed(By locator) {
        List<WebElement> found = driver.findElements(locator);
        return !found.isEmpty() && found.get(0).isDisplayed();
    }

    protected void waitForUrlContaining(String fragment) {
        wait.until(ExpectedConditions.urlContains(fragment));
        dismissAdInterstitial();
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    /**
     * Google's full-screen "vignette" ad adds #google_vignette to the URL and covers the page.
     * Reloading the same URL without the fragment removes it.
     */
    protected void dismissAdInterstitial() {
        String url = driver.getCurrentUrl();
        if (url != null && url.contains(VIGNETTE_FRAGMENT)) {
            driver.navigate().to(url.replace(VIGNETTE_FRAGMENT, ""));
        }
    }
}
