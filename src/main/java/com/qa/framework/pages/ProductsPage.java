package com.qa.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ProductsPage extends BasePage {

    private final By allProductsTitle = By.cssSelector(".features_items h2.title");
    private final By searchBox = By.id("search_product");
    private final By searchButton = By.id("submit_search");
    private final By productNames = By.cssSelector(".features_items .productinfo p");

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    public ProductsPage open() {
        openPath("/products");
        return waitUntilLoaded();
    }

    public ProductsPage waitUntilLoaded() {
        waitForUrlContaining("/products");
        visible(searchBox);
        return this;
    }

    public ProductsPage search(String term) {
        type(searchBox, term);
        click(searchButton);
        waitForUrlContaining("search=");
        visible(allProductsTitle);
        return this;
    }

    /** "ALL PRODUCTS" before a search, "SEARCHED PRODUCTS" after one. */
    public String resultsTitle() {
        return text(allProductsTitle);
    }

    public List<String> productNames() {
        // An empty result list is valid, so read without waiting for at least one card.
        return driver.findElements(productNames).stream()
                .map(WebElement::getText)
                .map(String::trim)
                .toList();
    }

    public ProductDetailPage openProduct(String exactName) {
        By viewProduct = By.xpath("//div[contains(@class,'productinfo')][p[normalize-space()='" + exactName + "']]"
                + "/ancestor::div[contains(@class,'product-image-wrapper')]//div[contains(@class,'choose')]//a");
        click(viewProduct);
        return new ProductDetailPage(driver).waitUntilLoaded();
    }
}
