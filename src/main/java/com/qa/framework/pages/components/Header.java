package com.qa.framework.pages.components;

import com.qa.framework.pages.BasePage;
import com.qa.framework.pages.CartPage;
import com.qa.framework.pages.ConfirmationPage;
import com.qa.framework.pages.LoginPage;
import com.qa.framework.pages.ProductsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Top navigation, present on every page. */
public class Header extends BasePage {

    private final By loginLink = By.cssSelector("#header a[href='/login']");
    private final By productsLink = By.cssSelector("#header a[href='/products']");
    private final By cartLink = By.cssSelector("#header a[href='/view_cart']");
    private final By logoutLink = By.cssSelector("#header a[href='/logout']");
    private final By deleteAccountLink = By.cssSelector("#header a[href='/delete_account']");
    private final By loggedInAs = By.xpath("//*[@id='header']//a[contains(normalize-space(), 'Logged in as')]/b");

    public Header(WebDriver driver) {
        super(driver);
    }

    public LoginPage goToLogin() {
        click(loginLink);
        return new LoginPage(driver).waitUntilLoaded();
    }

    public ProductsPage goToProducts() {
        click(productsLink);
        return new ProductsPage(driver).waitUntilLoaded();
    }

    public CartPage goToCart() {
        click(cartLink);
        return new CartPage(driver).waitUntilLoaded();
    }

    /** The name shown in "Logged in as NAME", or an empty string when nobody is logged in. */
    public String loggedInUserName() {
        return isDisplayed(loggedInAs) ? text(loggedInAs) : "";
    }

    public String waitForLoggedInUserName() {
        return text(loggedInAs);
    }

    public LoginPage logout() {
        click(logoutLink);
        return new LoginPage(driver).waitUntilLoaded();
    }

    public ConfirmationPage deleteAccount() {
        click(deleteAccountLink);
        return new ConfirmationPage(driver, "account-deleted").waitUntilLoaded();
    }
}
