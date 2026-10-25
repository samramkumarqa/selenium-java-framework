package com.qa.framework.pages;

import com.qa.framework.pages.components.Header;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {

    private final By logo = By.cssSelector("#header .logo img");
    private final By featuredItems = By.cssSelector(".features_items");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage open() {
        openPath("/");
        return waitUntilLoaded();
    }

    public HomePage waitUntilLoaded() {
        visible(logo);
        visible(featuredItems);
        return this;
    }

    public Header header() {
        return new Header(driver);
    }
}
