package com.qa.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutPage extends BasePage {

    private final By deliveryAddress = By.id("address_delivery");
    private final By comment = By.cssSelector("textarea[name='message']");
    private final By placeOrder = By.cssSelector("a[href='/payment']");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public CheckoutPage waitUntilLoaded() {
        waitForUrlContaining("/checkout");
        visible(deliveryAddress);
        return this;
    }

    public String deliveryAddress() {
        return text(deliveryAddress);
    }

    public PaymentPage placeOrder(String orderComment) {
        type(comment, orderComment);
        click(placeOrder);
        return new PaymentPage(driver).waitUntilLoaded();
    }
}
