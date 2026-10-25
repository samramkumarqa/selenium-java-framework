package com.qa.framework.utils;

/** Parses the store's price labels, e.g. "Rs. 1500" -> 1500. */
public final class Prices {

    private Prices() {
    }

    public static int parse(String label) {
        String digits = label.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            throw new IllegalArgumentException("No price found in '" + label + "'");
        }
        return Integer.parseInt(digits);
    }
}
