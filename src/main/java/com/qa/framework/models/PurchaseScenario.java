package com.qa.framework.models;

/**
 * One row of purchase test data, from JSON or Google Sheets.
 *
 * @param id          identifier shown in reports, e.g. PUR-01
 * @param searchTerm  what to type in the product search box
 * @param productName exact product name to open from the results
 * @param quantity    how many to add to the cart
 * @param run         false to keep the row but skip it
 */
public record PurchaseScenario(String id, String searchTerm, String productName, int quantity, boolean run) {

    @Override
    public String toString() {
        return id + " (" + quantity + " x " + productName + ")";
    }
}
