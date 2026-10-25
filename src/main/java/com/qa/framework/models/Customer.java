package com.qa.framework.models;

/** A shopper account on the practice store. Built by {@link com.qa.framework.data.CustomerFactory}. */
public record Customer(
        String title,          // "Mr" or "Mrs"
        String name,
        String email,
        String password,
        int birthDay,
        int birthMonth,        // 1-12
        int birthYear,
        String firstName,
        String lastName,
        String company,
        String address1,
        String address2,
        String country,        // must be one of the site's dropdown values
        String state,
        String city,
        String zipcode,
        String mobileNumber) {
}
