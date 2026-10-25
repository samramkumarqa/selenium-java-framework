package com.qa.framework.models;

/** Dummy card details. The practice store accepts any values; no real payment happens. */
public record PaymentCard(String nameOnCard, String number, String cvc, String expiryMonth, String expiryYear) {
}
