package com.qa.framework.pages;

import com.qa.framework.models.PaymentCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PaymentPage extends BasePage {

    private final By nameOnCard = By.cssSelector("input[data-qa='name-on-card']");
    private final By cardNumber = By.cssSelector("input[data-qa='card-number']");
    private final By cvc = By.cssSelector("input[data-qa='cvc']");
    private final By expiryMonth = By.cssSelector("input[data-qa='expiry-month']");
    private final By expiryYear = By.cssSelector("input[data-qa='expiry-year']");
    private final By payButton = By.cssSelector("button[data-qa='pay-button']");

    public PaymentPage(WebDriver driver) {
        super(driver);
    }

    public PaymentPage waitUntilLoaded() {
        waitForUrlContaining("/payment");
        visible(payButton);
        return this;
    }

    public ConfirmationPage pay(PaymentCard card) {
        type(nameOnCard, card.nameOnCard());
        type(cardNumber, card.number());
        type(cvc, card.cvc());
        type(expiryMonth, card.expiryMonth());
        type(expiryYear, card.expiryYear());
        click(payButton);
        return new ConfirmationPage(driver, "order-placed").waitUntilLoaded();
    }
}
