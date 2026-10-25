package com.qa.tests;

import com.qa.framework.pages.ProductsPage;
import com.qa.tests.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

public class ProductSearchTest extends BaseTest {

    @DataProvider(name = "searchTerms")
    public Object[][] searchTerms() {
        return new Object[][] {{"jeans"}, {"top"}, {"dress"}};
    }

    @Test(dataProvider = "searchTerms", groups = "regression", description = "Search returns matching products")
    public void searchReturnsMatchingProducts(String term) {
        ProductsPage products = new ProductsPage(driver()).open().search(term);

        Assert.assertEquals(products.resultsTitle().toLowerCase(), "searched products");
        List<String> names = products.productNames();
        Assert.assertFalse(names.isEmpty(), "Expected results for '" + term + "'");
        Assert.assertTrue(names.stream().anyMatch(n -> n.toLowerCase().contains(term)),
                "No product name contains '" + term + "': " + names);
    }

    @Test(groups = "regression", description = "A nonsense search returns no products")
    public void searchWithNoMatchReturnsNothing() {
        ProductsPage products = new ProductsPage(driver()).open().search("zzqx-no-such-product");
        Assert.assertTrue(products.productNames().isEmpty(), "Expected no results");
    }
}
