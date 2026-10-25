package com.qa.framework.models;

/** One row of the shopping cart, with prices in rupees. */
public record CartItem(String name, int unitPrice, int quantity, int total) {
}
