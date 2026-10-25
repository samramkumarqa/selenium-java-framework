package com.qa.tests;

import com.qa.framework.models.CartItem;
import com.qa.framework.pages.CartPage;
import com.qa.framework.pages.ProductDetailPage;
import com.qa.framework.pages.ProductsPage;
import com.qa.tests.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CartTest extends BaseTest {

    private static final String PRODUCT = "Blue Top";

    @Test(groups = {"smoke", "regression"}, description = "Cart shows the right quantity and line total")
    public void cartCalculatesLineTotal() {
        ProductDetailPage detail = new ProductsPage(driver()).open()
                .search(PRODUCT)
                .openProduct(PRODUCT);
        int unitPrice = detail.unitPrice();

        CartItem item = detail.addToCart(3).viewCart().item(PRODUCT);

        Assert.assertEquals(item.quantity(), 3);
        Assert.assertEquals(item.unitPrice(), unitPrice);
        Assert.assertEquals(item.total(), unitPrice * 3);
    }

    @Test(groups = "regression", description = "Removing the only item empties the cart")
    public void removingItemEmptiesCart() {
        CartPage cart = new ProductsPage(driver()).open()
                .search(PRODUCT)
                .openProduct(PRODUCT)
                .addToCart(1)
                .viewCart();
        Assert.assertEquals(cart.items().size(), 1);

        cart.remove(PRODUCT).waitUntilEmpty();
        Assert.assertTrue(cart.isEmpty());
    }
}
