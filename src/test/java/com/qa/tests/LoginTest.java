package com.qa.tests;

import com.qa.framework.data.CustomerFactory;
import com.qa.framework.models.Customer;
import com.qa.framework.pages.HomePage;
import com.qa.framework.pages.LoginPage;
import com.qa.tests.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "Registered user can log in and log out")
    public void validLoginShowsUserName() {
        Customer customer = CustomerFactory.newCustomer();
        api.createAccount(customer); // fast setup through the API; the UI login is what's under test
        try {
            LoginPage login = openHome().header().goToLogin();
            HomePage home = login.login(customer.email(), customer.password());
            Assert.assertEquals(home.header().waitForLoggedInUserName(), customer.name());

            step("Logged in, now logging out");
            LoginPage afterLogout = home.header().logout();
            Assert.assertEquals(afterLogout.header().loggedInUserName(), "", "User should be logged out");
        } finally {
            api.deleteAccount(customer);
        }
    }

    @Test(groups = "regression", description = "Wrong credentials show an error and do not log in")
    public void invalidLoginShowsError() {
        String error = new LoginPage(driver()).open()
                .loginExpectingError("nobody." + System.nanoTime() + "@example.com", "wrong-password");
        Assert.assertEquals(error, "Your email or password is incorrect!");
    }

    @Test(groups = "regression", description = "Signing up with an email that is already registered is rejected")
    public void signupWithExistingEmailIsRejected() {
        Customer existing = CustomerFactory.newCustomer();
        api.createAccount(existing);
        try {
            String error = new LoginPage(driver()).open().signupExpectingError("Someone Else", existing.email());
            Assert.assertEquals(error, "Email Address already exist!");
        } finally {
            api.deleteAccount(existing);
        }
    }
}
