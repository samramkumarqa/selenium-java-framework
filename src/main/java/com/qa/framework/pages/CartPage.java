package com.qa.framework.pages;

import com.qa.framework.models.CartItem;
import com.qa.framework.pages.components.Header;
import com.qa.framework.utils.Prices;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class CartPage extends BasePage {

    private final By rows = By.cssSelector("#cart_info_table tbody tr");
    private final By emptyCartMessage = By.id("empty_cart");
    private final By proceedToCheckout = By.cssSelector("a.check_out");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public CartPage open() {
        openPath("/view_cart");
        return waitUntilLoaded();
    }

    public CartPage waitUntilLoaded() {
        waitForUrlContaining("/view_cart");
        // Either items or the "Cart is empty!" message, depending on state.
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(rows),
                ExpectedConditions.visibilityOfElementLocated(emptyCartMessage)));
        return this;
    }

    public List<CartItem> items() {
        return driver.findElements(rows).stream().map(CartPage::toItem).toList();
    }

    public CartItem item(String productName) {
        return items().stream()
                .filter(i -> i.name().equals(productName))
                .findFirst()
                .orElseThrow(() -> new AssertionError("'" + productName + "' is not in the cart: " + items()));
    }

    public CartPage remove(String productName) {
        By deleteButton = By.xpath("//tr[.//td[contains(@class,'cart_description')]//a[normalize-space()='"
                + productName + "']]//a[contains(@class,'cart_quantity_delete')]");
        WebElement row = visible(deleteButton);
        click(deleteButton);
        wait.until(ExpectedConditions.stalenessOf(row));
        return this;
    }

    public boolean isEmpty() {
        return isDisplayed(emptyCartMessage);
    }

    public CartPage waitUntilEmpty() {
        visible(emptyCartMessage);
        return this;
    }

    /** Requires a logged-in user; guests get a "login to checkout" dialog instead. */
    public CheckoutPage proceedToCheckout() {
        click(proceedToCheckout);
        return new CheckoutPage(driver).waitUntilLoaded();
    }

    public Header header() {
        return new Header(driver);
    }

    private static CartItem toItem(WebElement row) {
        String name = row.findElement(By.cssSelector(".cart_description h4 a")).getText().trim();
        int price = Prices.parse(row.findElement(By.cssSelector(".cart_price p")).getText());
        int qty = Integer.parseInt(row.findElement(By.cssSelector(".cart_quantity button")).getText().trim());
        int total = Prices.parse(row.findElement(By.cssSelector(".cart_total_price")).getText());
        return new CartItem(name, price, qty, total);
    }
}
