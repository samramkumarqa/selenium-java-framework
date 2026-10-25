package com.qa.framework.pages;

import com.qa.framework.pages.components.Header;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** The combined "Login to your account" and "New User Signup!" page. */
public class LoginPage extends BasePage {

    private final By loginEmail = By.cssSelector("input[data-qa='login-email']");
    private final By loginPassword = By.cssSelector("input[data-qa='login-password']");
    private final By loginButton = By.cssSelector("button[data-qa='login-button']");
    private final By loginError = By.cssSelector("form[action='/login'] p");

    private final By signupName = By.cssSelector("input[data-qa='signup-name']");
    private final By signupEmail = By.cssSelector("input[data-qa='signup-email']");
    private final By signupButton = By.cssSelector("button[data-qa='signup-button']");
    private final By signupError = By.cssSelector("form[action='/signup'] p");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        openPath("/login");
        return waitUntilLoaded();
    }

    public LoginPage waitUntilLoaded() {
        visible(loginButton);
        visible(signupButton);
        return this;
    }

    public HomePage login(String email, String password) {
        submitLogin(email, password);
        return new HomePage(driver).waitUntilLoaded();
    }

    /** Submits credentials that are expected to fail and returns the error shown. */
    public String loginExpectingError(String email, String password) {
        submitLogin(email, password);
        return text(loginError);
    }

    public SignupPage startSignup(String name, String email) {
        submitSignup(name, email);
        return new SignupPage(driver).waitUntilLoaded();
    }

    /** Submits a signup that is expected to be rejected and returns the error shown. */
    public String signupExpectingError(String name, String email) {
        submitSignup(name, email);
        return text(signupError);
    }

    public Header header() {
        return new Header(driver);
    }

    private void submitLogin(String email, String password) {
        type(loginEmail, email);
        type(loginPassword, password);
        click(loginButton);
    }

    private void submitSignup(String name, String email) {
        type(signupName, name);
        type(signupEmail, email);
        click(signupButton);
    }
}
