package com.qa.framework.pages;

import com.qa.framework.pages.components.Header;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * The site's three confirmation screens share one layout: a heading plus a Continue button.
 * Pass the heading's data-qa value: "account-created", "order-placed" or "account-deleted".
 */
public class ConfirmationPage extends BasePage {

    private final By heading;
    private final By continueButton = By.cssSelector("a[data-qa='continue-button']");

    public ConfirmationPage(WebDriver driver, String headingDataQa) {
        super(driver);
        this.heading = By.cssSelector("h2[data-qa='" + headingDataQa + "']");
    }

    public ConfirmationPage waitUntilLoaded() {
        visible(heading);
        return this;
    }

    public String heading() {
        return text(heading);
    }

    public HomePage continueToHome() {
        click(continueButton);
        return new HomePage(driver).waitUntilLoaded();
    }

    public Header header() {
        return new Header(driver);
    }
}
