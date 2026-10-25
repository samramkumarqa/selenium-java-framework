package com.qa.framework.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qa.framework.config.Config;
import com.qa.framework.models.Customer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The practice store's public API, used to create and delete test accounts quickly.
 * Tests use it for setup and cleanup so the UI steps under test stay focused.
 *
 * Note: this API always answers HTTP 200; the real outcome is the "responseCode" in the JSON body.
 */
public class AutomationExerciseApi {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
    private final String baseUrl = Config.baseUrl() + "/api";

    public record ApiResult(int responseCode, String message) {
    }

    public ApiResult createAccount(Customer c) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("name", c.name());
        form.put("email", c.email());
        form.put("password", c.password());
        form.put("title", c.title());
        form.put("birth_date", String.valueOf(c.birthDay()));
        form.put("birth_month", String.valueOf(c.birthMonth()));
        form.put("birth_year", String.valueOf(c.birthYear()));
        form.put("firstname", c.firstName());
        form.put("lastname", c.lastName());
        form.put("company", c.company());
        form.put("address1", c.address1());
        form.put("address2", c.address2());
        form.put("country", c.country());
        form.put("zipcode", c.zipcode());
        form.put("state", c.state());
        form.put("city", c.city());
        form.put("mobile_number", c.mobileNumber());

        ApiResult result = send("POST", "/createAccount", form);
        if (result.responseCode() != 201) {
            throw new IllegalStateException("Could not create test account " + c.email() + ": " + result);
        }
        return result;
    }

    /** Deletes the account. Safe to call when it was already deleted (returns code 404). */
    public ApiResult deleteAccount(Customer c) {
        return send("DELETE", "/deleteAccount", Map.of("email", c.email(), "password", c.password()));
    }

    public boolean accountExists(Customer c) {
        return send("POST", "/verifyLogin", Map.of("email", c.email(), "password", c.password()))
                .responseCode() == 200;
    }

    private ApiResult send(String method, String path, Map<String, String> form) {
        String body = form.entrySet().stream()
                .map(e -> encode(e.getKey()) + "=" + encode(e.getValue()))
                .collect(Collectors.joining("&"));

        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .method(method, HttpRequest.BodyPublishers.ofString(body))
                .build();
        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode json = MAPPER.readTree(response.body());
            return new ApiResult(json.path("responseCode").asInt(response.statusCode()), json.path("message").asText());
        } catch (IOException e) {
            throw new UncheckedIOException(method + " " + path + " failed", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(method + " " + path + " interrupted", e);
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}
