package com.qa.tests;

import com.qa.framework.data.CustomerFactory;
import com.qa.framework.data.TestDataSource;
import com.qa.framework.data.TestDataSources;
import com.qa.framework.models.CartItem;
import com.qa.framework.models.Customer;
import com.qa.framework.models.PurchaseScenario;
import com.qa.framework.pages.CartPage;
import com.qa.framework.pages.CheckoutPage;
import com.qa.framework.pages.ConfirmationPage;
import com.qa.framework.pages.HomePage;
import com.qa.framework.pages.ProductDetailPage;
import com.qa.tests.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * End-to-end purchase, driven by test data from JSON or Google Sheets:
 * register -> search -> add to cart -> checkout -> pay -> order placed -> delete account.
 */
public class PurchaseFlowTest extends BaseTest {

    @DataProvider(name = "purchases")
    public Object[][] purchases() {
        TestDataSource source = TestDataSources.current();
        System.out.println("[data] Purchase scenarios from " + source.name());
        return source.enabledPurchaseScenarios().stream()
                .map(s -> new Object[] {s})
                .toArray(Object[][]::new);
    }

    @Test(dataProvider = "purchases", groups = {"smoke", "regression", "e2e"},
            description = "New customer buys a product end to end")
    public void newCustomerCanPlaceOrder(PurchaseScenario scenario) {
        Customer customer = CustomerFactory.newCustomer();
        try {
            step("Register " + customer.email());
            HomePage home = openHome()
                    .header().goToLogin()
                    .startSignup(customer.name(), customer.email())
                    .createAccount(customer)
                    .continueToHome();
            Assert.assertEquals(home.header().waitForLoggedInUserName(), customer.name());

            step("Search '" + scenario.searchTerm() + "' and add " + scenario.quantity() + " x " + scenario.productName());
            ProductDetailPage product = home.header().goToProducts()
                    .search(scenario.searchTerm())
                    .openProduct(scenario.productName());
            Assert.assertEquals(product.name(), scenario.productName());
            int unitPrice = product.unitPrice();

            CartPage cart = product.addToCart(scenario.quantity()).viewCart();
            CartItem item = cart.item(scenario.productName());
            Assert.assertEquals(item.quantity(), scenario.quantity(), "cart quantity");
            Assert.assertEquals(item.total(), unitPrice * scenario.quantity(), "cart line total");

            step("Check out and pay");
            CheckoutPage checkout = cart.proceedToCheckout();
            String address = checkout.deliveryAddress();
            Assert.assertTrue(address.contains(customer.address1()), "Delivery address should show " + customer.address1());
            Assert.assertTrue(address.contains(customer.city()), "Delivery address should show " + customer.city());

            ConfirmationPage orderPlaced = checkout
                    .placeOrder("Automated order " + scenario.id())
                    .pay(CustomerFactory.testCard(customer));
            Assert.assertEquals(orderPlaced.heading().toLowerCase(), "order placed!");

            step("Delete the account through the UI");
            ConfirmationPage deleted = orderPlaced.continueToHome().header().deleteAccount();
            Assert.assertEquals(deleted.heading().toLowerCase(), "account deleted!");
        } finally {
            // If the test failed midway, don't leave the account behind. A 404 here just means it's already gone.
            api.deleteAccount(customer);
        }
    }
}
