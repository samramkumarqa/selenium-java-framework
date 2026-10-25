package com.qa.framework.pages;

import com.qa.framework.models.Customer;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** "Enter Account Information" form shown after starting a signup. */
public class SignupPage extends BasePage {

    private final By mrTitle = By.id("id_gender1");
    private final By mrsTitle = By.id("id_gender2");
    private final By password = By.cssSelector("input[data-qa='password']");
    private final By day = By.cssSelector("select[data-qa='days']");
    private final By month = By.cssSelector("select[data-qa='months']");
    private final By year = By.cssSelector("select[data-qa='years']");
    private final By newsletter = By.id("newsletter");
    private final By specialOffers = By.id("optin");
    private final By firstName = By.cssSelector("input[data-qa='first_name']");
    private final By lastName = By.cssSelector("input[data-qa='last_name']");
    private final By company = By.cssSelector("input[data-qa='company']");
    private final By address1 = By.cssSelector("input[data-qa='address']");
    private final By address2 = By.cssSelector("input[data-qa='address2']");
    private final By country = By.cssSelector("select[data-qa='country']");
    private final By state = By.cssSelector("input[data-qa='state']");
    private final By city = By.cssSelector("input[data-qa='city']");
    private final By zipcode = By.cssSelector("input[data-qa='zipcode']");
    private final By mobile = By.cssSelector("input[data-qa='mobile_number']");
    private final By createAccountButton = By.cssSelector("button[data-qa='create-account']");

    public SignupPage(WebDriver driver) {
        super(driver);
    }

    public SignupPage waitUntilLoaded() {
        waitForUrlContaining("/signup");
        visible(createAccountButton);
        return this;
    }

    public ConfirmationPage createAccount(Customer c) {
        click("Mrs".equalsIgnoreCase(c.title()) ? mrsTitle : mrTitle);
        type(password, c.password());
        selectByValue(day, String.valueOf(c.birthDay()));
        selectByValue(month, String.valueOf(c.birthMonth()));
        selectByValue(year, String.valueOf(c.birthYear()));
        check(newsletter);
        check(specialOffers);

        type(firstName, c.firstName());
        type(lastName, c.lastName());
        type(company, c.company());
        type(address1, c.address1());
        type(address2, c.address2());
        selectByVisibleText(country, c.country());
        type(state, c.state());
        type(city, c.city());
        type(zipcode, c.zipcode());
        type(mobile, c.mobileNumber());

        click(createAccountButton);
        return new ConfirmationPage(driver, "account-created").waitUntilLoaded();
    }
}
