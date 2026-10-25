package com.qa.framework.pages;

import com.qa.framework.utils.Prices;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductDetailPage extends BasePage {

    private final By name = By.cssSelector(".product-information h2");
    private final By price = By.cssSelector(".product-information span span");
    private final By quantity = By.id("quantity");
    private final By addToCartButton = By.cssSelector(".product-information button.cart");
    private final By addedModal = By.cssSelector("#cartModal .modal-content");
    private final By viewCartInModal = By.cssSelector("#cartModal a[href='/view_cart']");

    public ProductDetailPage(WebDriver driver) {
        super(driver);
    }

    public ProductDetailPage waitUntilLoaded() {
        waitForUrlContaining("/product_details/");
        visible(name);
        return this;
    }

    public String name() {
        return text(name);
    }

    public int unitPrice() {
        return Prices.parse(text(price));
    }

    public ProductDetailPage addToCart(int qty) {
        type(quantity, String.valueOf(qty));
        click(addToCartButton);
        visible(addedModal);
        return this;
    }

    public CartPage viewCart() {
        click(viewCartInModal);
        return new CartPage(driver).waitUntilLoaded();
    }
}
