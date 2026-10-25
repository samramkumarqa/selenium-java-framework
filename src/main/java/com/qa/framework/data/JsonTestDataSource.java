package com.qa.framework.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qa.framework.models.PurchaseScenario;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;

/** Reads purchase scenarios from a JSON file on the test classpath. */
public class JsonTestDataSource implements TestDataSource {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final String resourcePath;

    public JsonTestDataSource(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public String name() {
        return "JSON (" + resourcePath + ")";
    }

    @Override
    public List<PurchaseScenario> purchaseScenarios() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalStateException("Test data file not found on classpath: " + resourcePath);
            }
            return MAPPER.readValue(in, new TypeReference<List<PurchaseScenario>>() { });
        } catch (IOException e) {
            throw new UncheckedIOException("Could not parse " + resourcePath, e);
        }
    }
}
