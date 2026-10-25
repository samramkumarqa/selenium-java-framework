package com.qa.framework.data;

import com.qa.framework.models.Customer;
import com.qa.framework.models.PaymentCard;
import net.datafaker.Faker;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/** Generates unique, realistic customers so tests never depend on pre-existing accounts. */
public final class CustomerFactory {

    /** Countries offered by the store's signup dropdown. */
    private static final List<String> COUNTRIES =
            List.of("India", "United States", "Canada", "Australia", "New Zealand", "Singapore");

    private static final Faker FAKER = new Faker(Locale.ENGLISH);

    private CustomerFactory() {
    }

    public static Customer newCustomer() {
        String firstName = FAKER.name().firstName();
        String lastName = FAKER.name().lastName();
        String unique = UUID.randomUUID().toString().substring(0, 8);

        return new Customer(
                FAKER.options().option("Mr", "Mrs"),
                firstName + " " + lastName,
                ("qa." + firstName + "." + unique + "@example.com").toLowerCase(),
                "Pw-" + UUID.randomUUID(),
                FAKER.number().numberBetween(1, 29),
                FAKER.number().numberBetween(1, 13),
                FAKER.number().numberBetween(1970, 2004),
                firstName,
                lastName,
                FAKER.company().name(),
                FAKER.address().streetAddress(),
                FAKER.address().secondaryAddress(),
                FAKER.options().nextElement(COUNTRIES),
                FAKER.address().state(),
                FAKER.address().city(),
                FAKER.address().zipCode(),
                FAKER.phoneNumber().subscriberNumber(10));
    }

    /** A dummy card in the well-known Visa test format; the practice store never charges anything. */
    public static PaymentCard testCard(Customer customer) {
        return new PaymentCard(customer.firstName() + " " + customer.lastName(),
                "4111111111111111", "123", "12", "2030");
    }
}
